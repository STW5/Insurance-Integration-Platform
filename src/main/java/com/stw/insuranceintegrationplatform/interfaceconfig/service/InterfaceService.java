package com.stw.insuranceintegrationplatform.interfaceconfig.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.InterfaceSummaryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.RegisterInterfaceRequest;
import com.stw.insuranceintegrationplatform.interfaceconfig.repository.InterfaceDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterfaceService {
    private final InterfaceDefinitionRepository interfaceRepository;

    public InterfaceService(InterfaceDefinitionRepository interfaceRepository) {
        this.interfaceRepository = interfaceRepository;
    }

    public InterfaceSummaryResponse register(RegisterInterfaceRequest request) {
        if (interfaceRepository.findById(request.interfaceCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 인터페이스 코드입니다: " + request.interfaceCode());
        }

        InterfaceDefinitionEntity entity = new InterfaceDefinitionEntity();
        entity.setInterfaceCode(request.interfaceCode());
        entity.setInterfaceName(request.interfaceName());
        entity.setTargetInstitution(request.targetInstitution());
        entity.setProtocolType(request.protocolType());
        entity.setDirectionType(request.directionType());
        entity.setEndpointOrPath(request.endpointOrPath());
        entity.setExecutionSchedule(request.executionSchedule());
        entity.setTimeoutSeconds(request.timeoutSeconds());
        entity.setRetryCount(request.retryCount());
        entity.setActive(request.active());
        entity.setHealthStatus(request.active() ? InterfaceHealthStatus.NORMAL : InterfaceHealthStatus.STOPPED);
        entity.setLastExecutionSuccess(false);

        InterfaceDefinitionEntity saved = interfaceRepository.save(entity);
        return toSummary(saved);
    }

    public List<InterfaceSummaryResponse> list() {
        return interfaceRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    public InterfaceDefinitionEntity getByCode(String interfaceCode) {
        return interfaceRepository.findById(interfaceCode)
                .orElseThrow(() -> new IllegalArgumentException("인터페이스를 찾을 수 없습니다: " + interfaceCode));
    }

    public InterfaceDefinitionEntity save(InterfaceDefinitionEntity entity) {
        return interfaceRepository.save(entity);
    }

    private InterfaceSummaryResponse toSummary(InterfaceDefinitionEntity entity) {
        return new InterfaceSummaryResponse(
                entity.getInterfaceCode(),
                entity.getInterfaceName(),
                entity.getTargetInstitution(),
                entity.getProtocolType(),
                entity.getHealthStatus(),
                entity.getLastExecutedAt(),
                entity.isLastExecutionSuccess(),
                entity.isActive()
        );
    }
}
