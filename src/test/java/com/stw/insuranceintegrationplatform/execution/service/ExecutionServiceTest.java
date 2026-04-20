package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.presentation.ExecutionHistoryResponse;
import com.stw.insuranceintegrationplatform.execution.repository.ExecutionHistoryRepository;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.DirectionType;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock
    private InterfaceService interfaceService;
    @Mock
    private ExecutionHistoryRepository executionHistoryRepository;
    @Mock
    private ExecutorRegistry executorRegistry;
    @Mock
    private InterfaceExecutor interfaceExecutor;
    @Mock
    private ExecutionLockManager executionLockManager;

    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionService = new ExecutionService(interfaceService, executionHistoryRepository, executorRegistry, executionLockManager);
    }

    @Test
    void shouldExecuteSuccess() {
        InterfaceDefinitionEntity def = sampleInterface("IF-REST-001", ProtocolType.REST, "mock://success");

        when(interfaceService.getByCode("IF-REST-001")).thenReturn(def);
        when(executionLockManager.tryAcquire("IF-REST-001")).thenReturn(true);
        when(executorRegistry.find(ProtocolType.REST)).thenReturn(interfaceExecutor);
        when(interfaceExecutor.execute(any(), any(), any(Boolean.class)))
                .thenReturn(new ExecutionResult(ExecutionStatus.SUCCESS, 1, null, "ok"));
        when(executionHistoryRepository.save(any())).thenAnswer(invocation -> {
            ExecutionHistoryEntity e = invocation.getArgument(0);
            e.setHistoryId(1L);
            return e;
        });

        ExecutionHistoryResponse response = executionService.execute("IF-REST-001", false, "run");

        assertEquals(ExecutionStatus.SUCCESS, response.executionStatus());
        assertEquals(1L, response.historyId());
    }


    @Test
    void shouldSearchHistoriesWithPaging() {
        ExecutionHistoryEntity row = new ExecutionHistoryEntity();
        row.setHistoryId(10L);
        row.setInterfaceCode("IF-REST-001");
        row.setExecutionStatus(ExecutionStatus.SUCCESS);
        row.setStartedAt(LocalDateTime.now());
        row.setAttemptCount(1);

        when(executionHistoryRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "startedAt")), 1));

        var result = executionService.searchHistories(
                null,
                null,
                "IF-REST-001",
                ExecutionStatus.SUCCESS,
                null,
                null,
                0,
                20,
                "startedAt",
                Sort.Direction.DESC
        );

        assertEquals(1, result.content().size());
        assertEquals(1L, result.totalElements());
    }

    @Test
    void shouldReprocessFailedHistory() {
        InterfaceDefinitionEntity def = sampleInterface("IF-BATCH-001", ProtocolType.BATCH, "batch://fail");
        ExecutionHistoryEntity failed = new ExecutionHistoryEntity();
        failed.setHistoryId(7L);
        failed.setInterfaceCode("IF-BATCH-001");
        failed.setExecutionStatus(ExecutionStatus.FAILED);

        when(executionHistoryRepository.findById(7L)).thenReturn(Optional.of(failed));
        when(executionLockManager.tryAcquire("IF-BATCH-001")).thenReturn(true);
        when(interfaceService.getByCode("IF-BATCH-001")).thenReturn(def);
        when(executorRegistry.find(ProtocolType.BATCH)).thenReturn(interfaceExecutor);
        when(interfaceExecutor.execute(any(), any(), any(Boolean.class)))
                .thenReturn(new ExecutionResult(ExecutionStatus.SUCCESS, 3, null, "reprocessed"));
        when(executionHistoryRepository.save(any())).thenAnswer(invocation -> {
            ExecutionHistoryEntity e = invocation.getArgument(0);
            e.setHistoryId(8L);
            return e;
        });

        ExecutionHistoryResponse response = executionService.reprocess(7L);

        assertTrue(response.reprocessed());
        assertEquals("IF-BATCH-001", response.interfaceCode());
    }

    @Test
    void shouldGetHistoryById() {
        ExecutionHistoryEntity row = new ExecutionHistoryEntity();
        row.setHistoryId(33L);
        row.setInterfaceCode("IF-REST-033");
        row.setExecutionStatus(ExecutionStatus.SUCCESS);
        row.setStartedAt(LocalDateTime.now());
        row.setAttemptCount(1);

        when(executionHistoryRepository.findById(33L)).thenReturn(Optional.of(row));

        ExecutionHistoryResponse result = executionService.getHistory(33L);

        assertEquals(33L, result.historyId());
        assertEquals("IF-REST-033", result.interfaceCode());
    }

    @Test
    void shouldThrowWhenHistoryNotFound() {
        when(executionHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> executionService.getHistory(999L));

        assertTrue(ex.getMessage().contains("실행 이력을 찾을 수 없습니다"));
    }



    @Test
    void shouldBlockWhenInterfaceAlreadyRunning() {
        InterfaceDefinitionEntity def = sampleInterface("IF-REST-009", ProtocolType.REST, "mock://success");
        when(interfaceService.getByCode("IF-REST-009")).thenReturn(def);
        when(executionLockManager.tryAcquire("IF-REST-009")).thenReturn(false);

        var ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> executionService.execute("IF-REST-009", false, "run"));

        assertTrue(ex.getMessage().contains("이미 실행 중"));
    }

    private InterfaceDefinitionEntity sampleInterface(String code, ProtocolType protocolType, String endpoint) {
        InterfaceDefinitionEntity e = new InterfaceDefinitionEntity();
        e.setInterfaceCode(code);
        e.setInterfaceName("name");
        e.setTargetInstitution("기관");
        e.setProtocolType(protocolType);
        e.setDirectionType(DirectionType.SEND);
        e.setEndpointOrPath(endpoint);
        e.setExecutionSchedule("manual");
        e.setTimeoutSeconds(10);
        e.setRetryCount(1);
        e.setActive(true);
        e.setHealthStatus(InterfaceHealthStatus.NORMAL);
        e.setLastExecutedAt(LocalDateTime.now());
        e.setLastExecutionSuccess(true);
        return e;
    }
}
