package com.stw.insuranceintegrationplatform.interfaceconfig.presentation;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;

import java.time.LocalDateTime;

public record InterfaceSummaryResponse(
        String interfaceCode,
        String interfaceName,
        String targetInstitution,
        ProtocolType protocolType,
        InterfaceHealthStatus healthStatus,
        LocalDateTime lastExecutedAt,
        boolean lastExecutionSuccess,
        boolean active
) {
}
