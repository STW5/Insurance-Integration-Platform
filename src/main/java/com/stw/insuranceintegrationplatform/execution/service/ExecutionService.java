package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionTriggerType;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryPageResponse;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.execution.repository.ExecutionHistoryRepository;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutionService {
    private final InterfaceService interfaceService;
    private final ExecutionHistoryRepository historyRepository;
    private final ExecutorRegistry executorRegistry;
    private final ExecutionLockManager executionLockManager;

    public ExecutionService(
            InterfaceService interfaceService,
            ExecutionHistoryRepository historyRepository,
            ExecutorRegistry executorRegistry,
            ExecutionLockManager executionLockManager
    ) {
        this.interfaceService = interfaceService;
        this.historyRepository = historyRepository;
        this.executorRegistry = executorRegistry;
        this.executionLockManager = executionLockManager;
    }

    public ExecutionHistoryResponse execute(String interfaceCode, boolean testExecution, String requestSummary) {
        InterfaceDefinitionEntity definition = interfaceService.getByCode(interfaceCode);
        ExecutionTriggerType trigger = testExecution ? ExecutionTriggerType.TEST : ExecutionTriggerType.MANUAL;
        return doExecute(definition, trigger, false, requestSummary);
    }

    public ExecutionHistoryResponse executeScheduled(String interfaceCode, String requestSummary) {
        InterfaceDefinitionEntity definition = interfaceService.getByCode(interfaceCode);
        return doExecute(definition, ExecutionTriggerType.SCHEDULED, false, requestSummary);
    }

    public ExecutionHistoryResponse reprocess(long failedHistoryId, String requestSummaryOverride) {
        ExecutionHistoryEntity failedHistory = historyRepository.findById(failedHistoryId)
                .filter(h -> h.getExecutionStatus() == ExecutionStatus.FAILED)
                .orElseThrow(() -> new IllegalArgumentException("실패 이력을 찾을 수 없습니다: " + failedHistoryId));

        InterfaceDefinitionEntity definition = interfaceService.getByCode(failedHistory.getInterfaceCode());
        String requestSummary = resolveReprocessRequestSummary(failedHistory, failedHistoryId, requestSummaryOverride);
        return doExecute(definition, ExecutionTriggerType.REPROCESS, true, requestSummary);
    }

    public ExecutionHistoryResponse getHistory(long historyId) {
        ExecutionHistoryEntity history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("실행 이력을 찾을 수 없습니다: " + historyId));
        return toResponse(history);
    }

    public List<ExecutionHistoryResponse> listHistories(boolean failuresOnly) {
        List<ExecutionHistoryEntity> source = failuresOnly
                ? historyRepository.findByExecutionStatusOrderByStartedAtDesc(ExecutionStatus.FAILED)
                : historyRepository.findAllByOrderByStartedAtDesc();
        return source.stream().map(this::toResponse).toList();
    }

    public ExecutionHistoryPageResponse searchHistories(
            LocalDateTime from,
            LocalDateTime to,
            String interfaceCode,
            ExecutionStatus executionStatus,
            ExecutionTriggerType triggerType,
            Boolean reprocessed,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Specification<ExecutionHistoryEntity> spec = Specification.unrestricted();

        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startedAt"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("startedAt"), to));
        }
        if (interfaceCode != null && !interfaceCode.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("interfaceCode"), interfaceCode));
        }
        if (executionStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("executionStatus"), executionStatus));
        }
        if (triggerType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("triggerType"), triggerType));
        }
        if (reprocessed != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("reprocessed"), reprocessed));
        }

        String validSort = switch (sortBy) {
            case "startedAt", "endedAt", "historyId", "executionStatus", "interfaceCode" -> sortBy;
            default -> "startedAt";
        };

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 200),
                Sort.by(direction, validSort)
        );

        Page<ExecutionHistoryEntity> result = historyRepository.findAll(spec, pageable);
        return new ExecutionHistoryPageResponse(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    public long totalBetween(LocalDateTime from, LocalDateTime to) {
        return historyRepository.countByStartedAtBetween(from, to);
    }

    public long successBetween(LocalDateTime from, LocalDateTime to) {
        return historyRepository.countByStartedAtBetweenAndExecutionStatus(from, to, ExecutionStatus.SUCCESS);
    }

    public long failureBetween(LocalDateTime from, LocalDateTime to) {
        return historyRepository.countByStartedAtBetweenAndExecutionStatus(from, to, ExecutionStatus.FAILED);
    }

    public List<ExecutionHistoryResponse> recentFailuresBetween(LocalDateTime from, LocalDateTime to, int limit) {
        return historyRepository.findByExecutionStatusAndStartedAtBetweenOrderByStartedAtDesc(ExecutionStatus.FAILED, from, to)
                .stream()
                .limit(Math.max(1, limit))
                .map(this::toResponse)
                .toList();
    }

    public List<ExecutionHistoryResponse> historiesBetween(LocalDateTime from, LocalDateTime to) {
        return historyRepository.findByStartedAtBetween(from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ExecutionHistoryResponse doExecute(
            InterfaceDefinitionEntity definition,
            ExecutionTriggerType triggerType,
            boolean reprocessed,
            String requestSummary
    ) {
        if (!definition.isActive() && triggerType != ExecutionTriggerType.TEST) {
            throw new IllegalArgumentException("비활성화된 인터페이스는 실행할 수 없습니다: " + definition.getInterfaceCode());
        }

        boolean acquired = executionLockManager.tryAcquire(definition.getInterfaceCode());
        if (!acquired) {
            throw new IllegalStateException("이미 실행 중인 인터페이스입니다: " + definition.getInterfaceCode());
        }

        try {
            ExecutionHistoryEntity history = new ExecutionHistoryEntity();
            history.setInterfaceCode(definition.getInterfaceCode());
            history.setTriggerType(triggerType);
            history.setReprocessed(reprocessed);
            history.setStartedAt(LocalDateTime.now());
            history.setRequestSummary(requestSummary == null ? "" : requestSummary);

            int maxAttempts = triggerType == ExecutionTriggerType.TEST ? 1 : Math.max(1, definition.getRetryCount() + 1);
            int attempts = 0;
            ExecutionResult result = null;

            for (int i = 1; i <= maxAttempts; i++) {
                attempts = i;
                result = executorRegistry.find(definition.getProtocolType())
                        .execute(definition, requestSummary, triggerType == ExecutionTriggerType.TEST);
                if (result.isSuccess()) {
                    break;
                }
            }

            if (result == null) {
                throw new IllegalStateException("실행 결과가 생성되지 않았습니다.");
            }

            String errorMessage = result.errorMessage();
            if (!result.isSuccess() && errorMessage != null && !errorMessage.isBlank()) {
                errorMessage = errorMessage + " (attempt " + attempts + "/" + maxAttempts + ")";
            }

            history.complete(
                    LocalDateTime.now(),
                    result.executionStatus(),
                    result.processedCount(),
                    attempts,
                    errorMessage,
                    result.responseSummary()
            );

            ExecutionHistoryEntity saved = historyRepository.save(history);

            definition.applyExecutionResult(result.isSuccess(), saved.getEndedAt());
            interfaceService.save(definition);

            return toResponse(saved);
        } finally {
            executionLockManager.release(definition.getInterfaceCode());
        }
    }

    private ExecutionHistoryResponse toResponse(ExecutionHistoryEntity history) {
        return new ExecutionHistoryResponse(
                history.getHistoryId(),
                history.getInterfaceCode(),
                history.getTriggerType(),
                history.isReprocessed(),
                history.getStartedAt(),
                history.getEndedAt(),
                history.getExecutionStatus(),
                history.getProcessedCount(),
                history.getAttemptCount(),
                history.getErrorMessage(),
                history.getRequestSummary(),
                history.getResponseSummary()
        );
    }

    private String resolveReprocessRequestSummary(
            ExecutionHistoryEntity failedHistory,
            long failedHistoryId,
            String requestSummaryOverride
    ) {
        if (requestSummaryOverride != null && !requestSummaryOverride.isBlank()) {
            return requestSummaryOverride;
        }
        String originalSummary = failedHistory.getRequestSummary();
        if (originalSummary != null && !originalSummary.isBlank()) {
            return originalSummary + " | reprocessFrom=" + failedHistoryId;
        }
        return "재처리 from historyId=" + failedHistoryId;
    }

    public long totalToday() {
        return totalBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
    }

    public long successToday() {
        return successBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
    }

    public long failureToday() {
        return failureBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
    }
}
