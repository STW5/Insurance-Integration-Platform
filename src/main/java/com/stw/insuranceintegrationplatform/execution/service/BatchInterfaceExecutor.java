package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import org.springframework.stereotype.Component;

@Component
public class BatchInterfaceExecutor implements InterfaceExecutor {
    @Override
    public boolean supports(ProtocolType protocolType) {
        return protocolType == ProtocolType.BATCH;
    }

    @Override
    public ExecutionResult execute(InterfaceDefinitionEntity definition, String requestSummary, boolean testExecution) {
        if (testExecution) {
            return new ExecutionResult(ExecutionStatus.SUCCESS, 10, null, "BATCH 테스트 완료");
        }

        if (definition.getEndpointOrPath().contains("fail")) {
            return new ExecutionResult(ExecutionStatus.FAILED, 0, "batch simulation failure", "BATCH 실패");
        }

        int processed = Math.max(1, definition.getRetryCount() + 5);
        return new ExecutionResult(ExecutionStatus.SUCCESS, processed, null, "BATCH 성공");
    }
}
