package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;

public record ExecutionResult(
        ExecutionStatus executionStatus,
        int processedCount,
        String errorMessage,
        String responseSummary
) {
    public boolean isSuccess() {
        return executionStatus == ExecutionStatus.SUCCESS;
    }
}
