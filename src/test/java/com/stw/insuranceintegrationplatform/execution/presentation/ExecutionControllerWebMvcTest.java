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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
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
    void shouldAllowOperatorToListHistoriesWithPaging() throws Exception {
        when(executionService.searchHistories(any(), any(), nullable(String.class), any(), any(), any(), anyInt(), anyInt(), anyString(), any()))
                .thenReturn(new ExecutionHistoryPageResponse(List.of(sample(1L)), 0, 20, 1L, 1, true));

        mockMvc.perform(get("/api/executions/histories")
                        .with(httpBasic("operator", "operator1234"))
                        .param("page", "0")
                        .param("size", "20")
                        .param("failuresOnly", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].historyId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldRequireAuthenticationForHistoryList() throws Exception {
        mockMvc.perform(get("/api/executions/histories"))
                .andExpect(status().isUnauthorized());
    }

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

    @Test
    void shouldApplyFailuresOnlyFilterAsFailedStatus() throws Exception {
        when(executionService.searchHistories(any(), any(), nullable(String.class), any(), any(), any(), anyInt(), anyInt(), anyString(), any()))
                .thenReturn(new ExecutionHistoryPageResponse(List.of(), 0, 20, 0L, 0, true));

        mockMvc.perform(get("/api/executions/histories")
                        .with(httpBasic("operator", "operator1234"))
                        .param("failuresOnly", "true"))
                .andExpect(status().isOk());

        verify(executionService).searchHistories(any(), any(), nullable(String.class), org.mockito.ArgumentMatchers.eq(ExecutionStatus.FAILED), any(), any(), anyInt(), anyInt(), anyString(), any());
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
