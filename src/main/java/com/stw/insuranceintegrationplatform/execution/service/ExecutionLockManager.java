package com.stw.insuranceintegrationplatform.execution.service;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExecutionLockManager {
    private final Set<String> runningInterfaceCodes = ConcurrentHashMap.newKeySet();

    public boolean tryAcquire(String interfaceCode) {
        return runningInterfaceCodes.add(interfaceCode);
    }

    public void release(String interfaceCode) {
        runningInterfaceCodes.remove(interfaceCode);
    }
}
