package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import org.springframework.stereotype.Component;

@Component
public class MockProtocolExecutor implements InterfaceExecutor {
    @Override
    public boolean supports(ProtocolType protocolType) {
        return protocolType == ProtocolType.SOAP
                || protocolType == ProtocolType.MQ
                || protocolType == ProtocolType.SFTP
                || protocolType == ProtocolType.FTP;
    }

    @Override
    public ExecutionResult execute(InterfaceDefinitionEntity definition, String requestSummary, boolean testExecution) {
        if (definition.getEndpointOrPath().contains("fail")) {
            return new ExecutionResult(ExecutionStatus.FAILED, 0, "mock protocol failure", definition.getProtocolType() + " 모의 실패");
        }
        return new ExecutionResult(ExecutionStatus.SUCCESS, 1, null, definition.getProtocolType() + " 모의 성공");
    }
}
