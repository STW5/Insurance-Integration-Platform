package com.stw.insuranceintegrationplatform.dashboard.service;

import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardPeriod;
import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardResponse;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import com.stw.insuranceintegrationplatform.execution.service.InterfaceExecutionCount;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.InterfaceSummaryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ExecutionService executionService;
    @Mock
    private InterfaceService interfaceService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardMetricsForToday() {
        when(executionService.totalBetween(any(), any())).thenReturn(2L);
        when(executionService.successBetween(any(), any())).thenReturn(1L);
        when(executionService.failureBetween(any(), any())).thenReturn(1L);
        when(executionService.recentFailuresBetween(any(), any(), any(Integer.class))).thenReturn(List.of());
        when(executionService.groupedCountsBetween(any(), any())).thenReturn(List.of(
                groupedCount("IF-1", ExecutionStatus.SUCCESS, 1L),
                groupedCount("IF-2", ExecutionStatus.FAILED, 1L)
        ));

        when(interfaceService.list()).thenReturn(List.of(
                new InterfaceSummaryResponse("IF-1", "name1", "기관1", ProtocolType.REST, InterfaceHealthStatus.NORMAL, null, true, true),
                new InterfaceSummaryResponse("IF-2", "name2", "기관2", ProtocolType.BATCH, InterfaceHealthStatus.FAILED, null, false, true)
        ));

        DashboardResponse response = dashboardService.getDashboard(DashboardPeriod.TODAY, null, null);

        assertEquals(DashboardPeriod.TODAY, response.period());
        assertEquals(2L, response.totalExecutions());
        assertEquals(1L, response.failedInterfaceCount());
        assertEquals(2, response.protocolStatus().size());
        assertEquals(1L, response.protocolStatus().get(ProtocolType.REST).success());
        assertEquals(1L, response.protocolStatus().get(ProtocolType.BATCH).failed());
    }

    @Test
    void shouldFailWhenCustomPeriodWithoutRange() {
        assertThrows(IllegalArgumentException.class,
                () -> dashboardService.getDashboard(DashboardPeriod.CUSTOM, null, null));
    }

    @Test
    void shouldAcceptCustomPeriodWithRange() {
        when(executionService.totalBetween(any(), any())).thenReturn(0L);
        when(executionService.successBetween(any(), any())).thenReturn(0L);
        when(executionService.failureBetween(any(), any())).thenReturn(0L);
        when(executionService.recentFailuresBetween(any(), any(), any(Integer.class))).thenReturn(List.of());
        when(executionService.groupedCountsBetween(any(), any())).thenReturn(List.of());
        when(interfaceService.list()).thenReturn(List.of());

        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now();
        DashboardResponse response = dashboardService.getDashboard(DashboardPeriod.CUSTOM, from, to);

        assertEquals(DashboardPeriod.CUSTOM, response.period());
        assertEquals(from, response.from());
        assertEquals(to, response.to());
    }

    private InterfaceExecutionCount groupedCount(String interfaceCode, ExecutionStatus status, long count) {
        return new InterfaceExecutionCount(interfaceCode, status, count);
    }
}
