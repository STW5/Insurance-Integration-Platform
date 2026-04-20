package com.stw.insuranceintegrationplatform.interfaceconfig.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.DirectionType;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.InterfaceSummaryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.repository.InterfaceDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterfaceServiceTest {

    @Mock
    private InterfaceDefinitionRepository interfaceRepository;

    private InterfaceService interfaceService;

    @BeforeEach
    void setUp() {
        interfaceService = new InterfaceService(interfaceRepository);
    }

    @Test
    void shouldGetInterfaceSummaryByCode() {
        InterfaceDefinitionEntity entity = sample("IF-REST-001");
        when(interfaceRepository.findById("IF-REST-001")).thenReturn(Optional.of(entity));

        InterfaceSummaryResponse result = interfaceService.get("IF-REST-001");

        assertEquals("IF-REST-001", result.interfaceCode());
        assertEquals(ProtocolType.REST, result.protocolType());
        assertEquals(InterfaceHealthStatus.NORMAL, result.healthStatus());
    }

    @Test
    void shouldThrowWhenInterfaceNotFound() {
        when(interfaceRepository.findById("NOT-FOUND")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> interfaceService.get("NOT-FOUND"));

        assertEquals("인터페이스를 찾을 수 없습니다: NOT-FOUND", ex.getMessage());
    }

    private InterfaceDefinitionEntity sample(String code) {
        InterfaceDefinitionEntity entity = new InterfaceDefinitionEntity();
        entity.setInterfaceCode(code);
        entity.setInterfaceName("테스트 인터페이스");
        entity.setTargetInstitution("기관");
        entity.setProtocolType(ProtocolType.REST);
        entity.setDirectionType(DirectionType.SEND);
        entity.setEndpointOrPath("http://localhost/mock");
        entity.setExecutionSchedule("manual");
        entity.setTimeoutSeconds(10);
        entity.setRetryCount(1);
        entity.setHealthStatus(InterfaceHealthStatus.NORMAL);
        entity.applyActivation(true);
        entity.setLastExecutionSuccess(true);
        return entity;
    }
}
