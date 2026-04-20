package com.stw.insuranceintegrationplatform.execution.presentation;

import com.stw.insuranceintegrationplatform.common.presentation.GlobalExceptionHandler;
import com.stw.insuranceintegrationplatform.config.SecurityConfig;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionTriggerType;
import com.stw.insuranceintegrationplatform.execution.service.ExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExecutionController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ExecutionControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExecutionService executionService;

    @Test
    void shouldAllowAdminToGetHistoryDetail() throws Exception {
        when(executionService.getHistory(1L)).thenReturn(sample(1L));

        mockMvc.perform(get("/api/executions/histories/1")
                        .with(httpBasic("admin", "admin1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.historyId").value(1))
                .andExpect(jsonPath("$.interfaceCode").value("IF-REST-001"));
    }

    @Test
    void shouldBlockOperatorFromHistoryDetail() throws Exception {
        mockMvc.perform(get("/api/executions/histories/1")
                        .with(httpBasic("operator", "operator1234")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldPassOverrideSummaryOnReprocess() throws Exception {
        when(executionService.reprocess(7L, "manual-override")).thenReturn(sample(8L));

        mockMvc.perform(post("/api/executions/histories/7/reprocess")
                        .with(httpBasic("admin", "admin1234"))
                        .contentType("application/json")
                        .content("{\"requestSummary\":\"manual-override\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.historyId").value(8));

        verify(executionService).reprocess(7L, "manual-override");
    }

    private ExecutionHistoryResponse sample(long id) {
        return new ExecutionHistoryResponse(
                id,
                "IF-REST-001",
                ExecutionTriggerType.MANUAL,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                ExecutionStatus.SUCCESS,
                1,
                1,
                null,
                "req",
                "res"
        );
    }
}
