package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionTriggerType;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.execution.repository.ExecutionHistoryRepository;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutionService {
    private final InterfaceService interfaceService;
    private final ExecutionHistoryRepository historyRepository;
    private final ExecutorRegistry executorRegistry;

    public ExecutionService(
            InterfaceService interfaceService,
            ExecutionHistoryRepository historyRepository,
            ExecutorRegistry executorRegistry
    ) {
        this.interfaceService = interfaceService;
        this.historyRepository = historyRepository;
        this.executorRegistry = executorRegistry;
    }

    public ExecutionHistoryResponse execute(String interfaceCode, boolean testExecution, String requestSummary) {
        InterfaceDefinitionEntity definition = interfaceService.getByCode(interfaceCode);
        return doExecute(definition, testExecution ? ExecutionTriggerType.TEST : ExecutionTriggerType.MANUAL, false, requestSummary);
    }

    public ExecutionHistoryResponse reprocess(long failedHistoryId) {
        ExecutionHistoryEntity failedHistory = historyRepository.findById(failedHistoryId)
                .filter(h -> h.getExecutionStatus() == ExecutionStatus.FAILED)
                .orElseThrow(() -> new IllegalArgumentException("실패 이력을 찾을 수 없습니다: " + failedHistoryId));

        InterfaceDefinitionEntity definition = interfaceService.getByCode(failedHistory.getInterfaceCode());
        return doExecute(definition, ExecutionTriggerType.REPROCESS, true, "재처리 from historyId=" + failedHistoryId);
    }

    public List<ExecutionHistoryResponse> listHistories(boolean failuresOnly) {
        List<ExecutionHistoryEntity> source = failuresOnly
                ? historyRepository.findByExecutionStatusOrderByStartedAtDesc(ExecutionStatus.FAILED)
                : historyRepository.findAllByOrderByStartedAtDesc();
        return source.stream().map(this::toResponse).toList();
    }

    private ExecutionHistoryResponse doExecute(
            InterfaceDefinitionEntity definition,
            ExecutionTriggerType triggerType,
            boolean reprocessed,
            String requestSummary
    ) {
        ExecutionHistoryEntity history = new ExecutionHistoryEntity();
        history.setInterfaceCode(definition.getInterfaceCode());
        history.setTriggerType(triggerType);
        history.setReprocessed(reprocessed);
        history.setStartedAt(LocalDateTime.now());
        history.setRequestSummary(requestSummary == null ? "" : requestSummary);

        ExecutionResult result = executorRegistry.find(definition.getProtocolType())
                .execute(definition, requestSummary, triggerType == ExecutionTriggerType.TEST);

        history.complete(
                LocalDateTime.now(),
                result.executionStatus(),
                result.processedCount(),
                result.errorMessage(),
                result.responseSummary()
        );

        ExecutionHistoryEntity saved = historyRepository.save(history);

        definition.applyExecutionResult(result.isSuccess(), saved.getEndedAt());
        interfaceService.save(definition);

        return toResponse(saved);
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
                history.getErrorMessage(),
                history.getRequestSummary(),
                history.getResponseSummary()
        );
    }

    public long totalToday() {
        return historyRepository.findByStartedAtBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()).size();
    }

    public long successToday() {
        return historyRepository.findByStartedAtBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()).stream()
                .filter(h -> h.getExecutionStatus() == ExecutionStatus.SUCCESS)
                .count();
    }

    public long failureToday() {
        return historyRepository.findByStartedAtBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()).stream()
                .filter(h -> h.getExecutionStatus() == ExecutionStatus.FAILED)
                .count();
    }
}
