package com.stw.insuranceintegrationplatform.execution.presentation;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionTriggerType;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/executions")
public class ExecutionController {
    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/interfaces/{interfaceCode}")
    public ResponseEntity<ExecutionHistoryResponse> execute(
            @PathVariable String interfaceCode,
            @RequestBody(required = false) ExecuteInterfaceRequest request
    ) {
        boolean testExecution = request != null && request.testExecution();
        String summary = request != null ? request.requestSummary() : "";
        return ResponseEntity.ok(executionService.execute(interfaceCode, testExecution, summary));
    }

    @GetMapping("/histories")
    public ResponseEntity<ExecutionHistoryPageResponse> histories(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String interfaceCode,
            @RequestParam(required = false) ExecutionStatus executionStatus,
            @RequestParam(required = false) ExecutionTriggerType triggerType,
            @RequestParam(required = false) Boolean reprocessed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "false") boolean failuresOnly
    ) {
        ExecutionStatus effectiveStatus = failuresOnly ? ExecutionStatus.FAILED : executionStatus;
        return ResponseEntity.ok(executionService.searchHistories(
                from,
                to,
                interfaceCode,
                effectiveStatus,
                triggerType,
                reprocessed,
                page,
                size,
                sortBy,
                direction
        ));
    }

    @GetMapping("/histories/{historyId}")
    public ResponseEntity<ExecutionHistoryResponse> history(@PathVariable long historyId) {
        return ResponseEntity.ok(executionService.getHistory(historyId));
    }

    @PostMapping("/histories/{historyId}/reprocess")
    public ResponseEntity<ExecutionHistoryResponse> reprocess(
            @PathVariable long historyId,
            @RequestBody(required = false) ReprocessExecutionRequest request
    ) {
        String requestSummary = request != null ? request.requestSummary() : null;
        return ResponseEntity.ok(executionService.reprocess(historyId, requestSummary));
    }
}
