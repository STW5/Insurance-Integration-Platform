package com.stw.insuranceintegrationplatform.execution.presentation;

public record ExecuteInterfaceRequest(
        boolean testExecution,
        String requestSummary
) {
}
