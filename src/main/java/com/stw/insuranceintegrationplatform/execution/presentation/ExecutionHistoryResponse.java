package com.stw.insuranceintegrationplatform.execution.presentation;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionTriggerType;

import java.time.LocalDateTime;

public record ExecutionHistoryResponse(
        long historyId,
        String interfaceCode,
        ExecutionTriggerType triggerType,
        boolean reprocessed,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        ExecutionStatus executionStatus,
        int processedCount,
        String errorMessage,
        String requestSummary,
        String responseSummary
) {
}
