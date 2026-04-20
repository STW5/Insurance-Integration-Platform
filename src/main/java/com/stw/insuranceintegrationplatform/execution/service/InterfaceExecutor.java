package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;

public interface InterfaceExecutor {
    boolean supports(ProtocolType protocolType);

    ExecutionResult execute(InterfaceDefinitionEntity definition, String requestSummary, boolean testExecution);
}
