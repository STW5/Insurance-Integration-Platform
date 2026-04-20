package com.stw.insuranceintegrationplatform.execution.presentation;

import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<ExecutionHistoryResponse>> histories(@RequestParam(defaultValue = "false") boolean failuresOnly) {
        return ResponseEntity.ok(executionService.listHistories(failuresOnly));
    }

    @PostMapping("/histories/{historyId}/reprocess")
    public ResponseEntity<ExecutionHistoryResponse> reprocess(@PathVariable long historyId) {
        return ResponseEntity.ok(executionService.reprocess(historyId));
    }
}
