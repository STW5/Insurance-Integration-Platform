package com.stw.insuranceintegrationplatform.dashboard.presentation;

import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalExecutionsToday,
        long successCountToday,
        long failureCountToday,
        long failedInterfaceCount,
        Map<ProtocolType, Long> protocolStatus,
        List<ExecutionHistoryResponse> recentFailures
) {
}
