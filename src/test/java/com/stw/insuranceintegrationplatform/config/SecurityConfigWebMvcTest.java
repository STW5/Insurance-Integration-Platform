package com.stw.insuranceintegrationplatform.config;

import com.stw.insuranceintegrationplatform.dashboard.presentation.DashboardController;
import com.stw.insuranceintegrationplatform.dashboard.service.DashboardService;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionController;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.InterfaceController;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {InterfaceController.class, ExecutionController.class, DashboardController.class})
@Import(SecurityConfig.class)
class SecurityConfigWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterfaceService interfaceService;

    @MockBean
    private ExecutionService executionService;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/interfaces"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowOperatorToReadInterfaces() throws Exception {
        when(interfaceService.list(null, null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/interfaces")
                        .with(httpBasic("operator", "operator1234")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldBlockOperatorFromManualExecution() throws Exception {
        mockMvc.perform(post("/api/executions/interfaces/IF-REST-001")
                        .with(httpBasic("operator", "operator1234"))
                        .contentType("application/json")
                        .content("{\"testExecution\":false,\"requestSummary\":\"run\"}"))
                .andExpect(status().isForbidden());
    }
}