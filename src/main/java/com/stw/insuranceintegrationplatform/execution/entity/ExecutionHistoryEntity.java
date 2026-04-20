package com.stw.insuranceintegrationplatform.execution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "execution_histories")
public class ExecutionHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "interface_code", nullable = false, length = 64)
    private String interfaceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 20)
    private ExecutionTriggerType triggerType;

    @Column(name = "reprocessed", nullable = false)
    private boolean reprocessed;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status", length = 20)
    private ExecutionStatus executionStatus;

    @Column(name = "processed_count")
    private int processedCount;

    @Column(name = "attempt_count")
    private int attemptCount;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "request_summary", length = 2000)
    private String requestSummary;

    @Column(name = "response_summary", length = 2000)
    private String responseSummary;

    public ExecutionHistoryEntity() {
    }

    public void complete(
            LocalDateTime endedAt,
            ExecutionStatus executionStatus,
            int processedCount,
            int attemptCount,
            String errorMessage,
            String responseSummary
    ) {
        this.endedAt = endedAt;
        this.executionStatus = executionStatus;
        this.processedCount = processedCount;
        this.attemptCount = attemptCount;
        this.errorMessage = errorMessage;
        this.responseSummary = responseSummary;
    }

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
    public String getInterfaceCode() { return interfaceCode; }
    public void setInterfaceCode(String interfaceCode) { this.interfaceCode = interfaceCode; }
    public ExecutionTriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(ExecutionTriggerType triggerType) { this.triggerType = triggerType; }
    public boolean isReprocessed() { return reprocessed; }
    public void setReprocessed(boolean reprocessed) { this.reprocessed = reprocessed; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public ExecutionStatus getExecutionStatus() { return executionStatus; }
    public void setExecutionStatus(ExecutionStatus executionStatus) { this.executionStatus = executionStatus; }
    public int getProcessedCount() { return processedCount; }
    public void setProcessedCount(int processedCount) { this.processedCount = processedCount; }
    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getRequestSummary() { return requestSummary; }
    public void setRequestSummary(String requestSummary) { this.requestSummary = requestSummary; }
    public String getResponseSummary() { return responseSummary; }
    public void setResponseSummary(String responseSummary) { this.responseSummary = responseSummary; }
}
