package com.stw.insuranceintegrationplatform.dashboard.presentation;

public record ProtocolExecutionStat(
        long total,
        long success,
        long failed
) {
}
