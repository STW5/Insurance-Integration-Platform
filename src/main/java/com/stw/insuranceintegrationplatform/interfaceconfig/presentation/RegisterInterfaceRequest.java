package com.stw.insuranceintegrationplatform.interfaceconfig.presentation;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.DirectionType;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterInterfaceRequest(
        @NotBlank String interfaceCode,
        @NotBlank String interfaceName,
        @NotBlank String targetInstitution,
        @NotNull ProtocolType protocolType,
        @NotNull DirectionType directionType,
        @NotBlank String endpointOrPath,
        @NotBlank String executionSchedule,
        @Min(1) int timeoutSeconds,
        @Min(0) int retryCount,
        boolean active
) {
}
