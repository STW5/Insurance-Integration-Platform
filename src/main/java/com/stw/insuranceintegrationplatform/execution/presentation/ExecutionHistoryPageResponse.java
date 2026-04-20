package com.stw.insuranceintegrationplatform.execution.presentation;

import java.util.List;

public record ExecutionHistoryPageResponse(
        List<ExecutionHistoryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
