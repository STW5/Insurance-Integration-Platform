package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecutorRegistry {
    private final List<InterfaceExecutor> executors;

    public ExecutorRegistry(List<InterfaceExecutor> executors) {
        this.executors = executors;
    }

    public InterfaceExecutor find(ProtocolType protocolType) {
        return executors.stream()
                .filter(executor -> executor.supports(protocolType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 프로토콜: " + protocolType));
    }
}
