package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;

public record InterfaceExecutionCount(
        String interfaceCode,
        ExecutionStatus executionStatus,
        long totalCount
) {
}
