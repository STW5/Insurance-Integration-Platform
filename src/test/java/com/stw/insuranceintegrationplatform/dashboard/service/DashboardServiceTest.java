package com.stw.insuranceintegrationplatform.dashboard.service;

import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardResponse;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.InterfaceSummaryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldReturnDashboardMetrics() {
        when(executionService.totalToday()).thenReturn(2L);
        when(executionService.successToday()).thenReturn(1L);
        when(executionService.failureToday()).thenReturn(1L);
        when(executionService.listHistories(true)).thenReturn(List.of());

        when(interfaceService.list()).thenReturn(List.of(
                new InterfaceSummaryResponse("IF-1", "name1", "기관1", ProtocolType.REST, InterfaceHealthStatus.NORMAL, null, true, true),
                new InterfaceSummaryResponse("IF-2", "name2", "기관2", ProtocolType.BATCH, InterfaceHealthStatus.FAILED, null, false, true)
        ));

        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(2L, response.totalExecutionsToday());
        assertEquals(1L, response.failedInterfaceCount());
        assertEquals(2, response.protocolStatus().size());
    }
}
