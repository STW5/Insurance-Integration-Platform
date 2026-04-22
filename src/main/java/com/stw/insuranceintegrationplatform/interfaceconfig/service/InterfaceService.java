package com.stw.insuranceintegrationplatform.interfaceconfig.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.InterfaceSummaryResponse;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.RegisterInterfaceRequest;
import com.stw.insuranceintegrationplatform.interfaceconfig.presentation.UpdateInterfaceRequest;
import com.stw.insuranceintegrationplatform.interfaceconfig.repository.InterfaceDefinitionRepository;
import org.springframework.data.jpa.domain.Specification;
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
        entity.applyActivation(request.active());
        entity.setLastExecutionSuccess(false);

        InterfaceDefinitionEntity saved = interfaceRepository.save(entity);
        return toSummary(saved);
    }

    public InterfaceSummaryResponse update(String interfaceCode, UpdateInterfaceRequest request) {
        InterfaceDefinitionEntity entity = getByCode(interfaceCode);
        entity.setInterfaceName(request.interfaceName());
        entity.setTargetInstitution(request.targetInstitution());
        entity.setProtocolType(request.protocolType());
        entity.setDirectionType(request.directionType());
        entity.setEndpointOrPath(request.endpointOrPath());
        entity.setExecutionSchedule(request.executionSchedule());
        entity.setTimeoutSeconds(request.timeoutSeconds());
        entity.setRetryCount(request.retryCount());
        entity.applyActivation(request.active());

        if (!request.active()) {
            entity.setNextScheduledAt(null);
        }

        return toSummary(interfaceRepository.save(entity));
    }

    public List<InterfaceSummaryResponse> list() {
        return list(null, null, null, null);
    }

    public InterfaceSummaryResponse get(String interfaceCode) {
        return toSummary(getByCode(interfaceCode));
    }

    public List<InterfaceSummaryResponse> list(ProtocolType protocolType, String targetInstitution, InterfaceHealthStatus healthStatus, Boolean active) {
        Specification<InterfaceDefinitionEntity> spec = Specification.unrestricted();

        if (protocolType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("protocolType"), protocolType));
        }
        if (targetInstitution != null && !targetInstitution.isBlank()) {
            String keyword = "%" + targetInstitution.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("targetInstitution")), keyword));
        }
        if (healthStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("healthStatus"), healthStatus));
        }
        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }

        return interfaceRepository.findAll(spec).stream()
                .map(this::toSummary)
                .toList();
    }

    public List<InterfaceDefinitionEntity> listActiveEntities() {
        return interfaceRepository.findByActiveTrue();
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
