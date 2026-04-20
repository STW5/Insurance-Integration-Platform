package com.stw.insuranceintegrationplatform.dashboard.presentation;

import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        DashboardPeriod period,
        LocalDateTime from,
        LocalDateTime to,
        long totalExecutions,
        long successCount,
        long failureCount,
        long failedInterfaceCount,
        Map<ProtocolType, Long> protocolStatus,
        List<ExecutionHistoryResponse> recentFailures
) {
}
