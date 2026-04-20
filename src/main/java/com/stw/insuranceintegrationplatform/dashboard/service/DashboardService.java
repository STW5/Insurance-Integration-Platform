package com.stw.insuranceintegrationplatform.dashboard.service;

import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardResponse;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final ExecutionService executionService;
    private final InterfaceService interfaceService;

    public DashboardService(ExecutionService executionService, InterfaceService interfaceService) {
        this.executionService = executionService;
        this.interfaceService = interfaceService;
    }

    public DashboardResponse getDashboard() {
        List<ExecutionHistoryResponse> failures = executionService.listHistories(true);
        Map<ProtocolType, Long> protocolMap = new EnumMap<>(ProtocolType.class);

        interfaceService.list().forEach(summary -> protocolMap.merge(summary.protocolType(), 1L, Long::sum));

        long failedInterfaceCount = interfaceService.list().stream()
                .filter(summary -> summary.healthStatus() == InterfaceHealthStatus.FAILED)
                .count();

        return new DashboardResponse(
                executionService.totalToday(),
                executionService.successToday(),
                executionService.failureToday(),
                failedInterfaceCount,
                protocolMap,
                failures.stream().limit(10).toList()
        );
    }
}
