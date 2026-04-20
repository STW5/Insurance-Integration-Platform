package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestInterfaceExecutor implements InterfaceExecutor {
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean supports(ProtocolType protocolType) {
        return protocolType == ProtocolType.REST;
    }

    @Override
    public ExecutionResult execute(InterfaceDefinitionEntity definition, String requestSummary, boolean testExecution) {
        String endpoint = definition.getEndpointOrPath();

        if (testExecution) {
            return new ExecutionResult(ExecutionStatus.SUCCESS, 1, null, "REST 테스트 실행 성공");
        }

        if (endpoint.startsWith("mock://")) {
            if (endpoint.contains("fail")) {
                return new ExecutionResult(ExecutionStatus.FAILED, 0, "mock failure", "REST 모의 실패");
            }
            return new ExecutionResult(ExecutionStatus.SUCCESS, 1, null, "REST 모의 성공");
        }

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return new ExecutionResult(ExecutionStatus.SUCCESS, 1, null, "HTTP " + response.getStatusCode().value());
            }
            return new ExecutionResult(ExecutionStatus.FAILED, 0, "HTTP status: " + response.getStatusCode().value(), "비정상 응답");
        } catch (RestClientException exception) {
            return new ExecutionResult(ExecutionStatus.FAILED, 0, exception.getMessage(), "REST 호출 실패");
        }
    }
}
