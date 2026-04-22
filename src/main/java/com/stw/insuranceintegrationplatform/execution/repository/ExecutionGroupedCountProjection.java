package com.stw.insuranceintegrationplatform.execution.repository;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;

public interface ExecutionGroupedCountProjection {
    String getInterfaceCode();

    ExecutionStatus getExecutionStatus();

    long getTotalCount();
}
