package com.stw.insuranceintegrationplatform.dashboard.service;

import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardPeriod;
import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardResponse;
import com.stw.insuranceintegrationplatform.dashboard.presentation.ProtocolExecutionStat;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import com.stw.insuranceintegrationplatform.execution.service.InterfaceExecutionCount;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public DashboardResponse getDashboard(DashboardPeriod period, LocalDateTime from, LocalDateTime to) {
        Range range = resolveRange(period, from, to);
        var interfaceSummaries = interfaceService.list();
        List<InterfaceExecutionCount> groupedCounts = executionService.groupedCountsBetween(range.from(), range.to());
        Map<String, ProtocolType> protocolByCode = interfaceSummaries.stream()
                .collect(java.util.stream.Collectors.toMap(
                        summary -> summary.interfaceCode(),
                        summary -> summary.protocolType()
                ));

        Map<ProtocolType, ProtocolExecutionStat> protocolMap = buildProtocolMap(groupedCounts, protocolByCode);

        long failedInterfaceCount = interfaceSummaries.stream()
                .filter(summary -> summary.healthStatus() == InterfaceHealthStatus.FAILED)
                .count();

        List<ExecutionHistoryResponse> failures = executionService.recentFailuresBetween(range.from(), range.to(), 10);

        return new DashboardResponse(
                period,
                range.from(),
                range.to(),
                executionService.totalBetween(range.from(), range.to()),
                executionService.successBetween(range.from(), range.to()),
                executionService.failureBetween(range.from(), range.to()),
                failedInterfaceCount,
                protocolMap,
                failures
        );
    }

    private Map<ProtocolType, ProtocolExecutionStat> buildProtocolMap(
            List<InterfaceExecutionCount> groupedCounts,
            Map<String, ProtocolType> protocolByCode
    ) {
        Map<ProtocolType, MutableProtocolStat> mutableMap = new EnumMap<>(ProtocolType.class);

        groupedCounts.forEach(groupedCount -> {
            ProtocolType protocolType = protocolByCode.get(groupedCount.interfaceCode());
            if (protocolType == null) {
                return;
            }

            MutableProtocolStat stat = mutableMap.computeIfAbsent(protocolType, k -> new MutableProtocolStat());
            stat.total += groupedCount.totalCount();

            switch (groupedCount.executionStatus()) {
                case SUCCESS -> stat.success += groupedCount.totalCount();
                case FAILED -> stat.failed += groupedCount.totalCount();
                default -> {
                }
            }
        });

        Map<ProtocolType, ProtocolExecutionStat> result = new EnumMap<>(ProtocolType.class);
        mutableMap.forEach((protocol, stat) ->
                result.put(protocol, new ProtocolExecutionStat(stat.total, stat.success, stat.failed)));
        return result;
    }

    private Range resolveRange(DashboardPeriod period, LocalDateTime from, LocalDateTime to) {
        LocalDateTime now = LocalDateTime.now();

        return switch (period) {
            case TODAY -> new Range(LocalDate.now().atStartOfDay(), now);
            case DAYS_7 -> new Range(now.minusDays(7), now);
            case DAYS_30 -> new Range(now.minusDays(30), now);
            case CUSTOM -> {
                if (from == null || to == null) {
                    throw new IllegalArgumentException("CUSTOM 기간은 from/to가 모두 필요합니다.");
                }
                if (from.isAfter(to)) {
                    throw new IllegalArgumentException("from은 to보다 늦을 수 없습니다.");
                }
                yield new Range(from, to);
            }
        };
    }

    private record Range(LocalDateTime from, LocalDateTime to) {
    }

    private static class MutableProtocolStat {
        private long total;
        private long success;
        private long failed;
    }
}
