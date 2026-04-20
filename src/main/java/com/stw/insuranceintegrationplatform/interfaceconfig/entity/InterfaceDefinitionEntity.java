package com.stw.insuranceintegrationplatform.interfaceconfig.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "interface_definitions")
public class InterfaceDefinitionEntity {
    @Id
    @Column(name = "interface_code", nullable = false, length = 64)
    private String interfaceCode;

    @Column(name = "interface_name", nullable = false, length = 200)
    private String interfaceName;

    @Column(name = "target_institution", nullable = false, length = 200)
    private String targetInstitution;

    @Enumerated(EnumType.STRING)
    @Column(name = "protocol_type", nullable = false, length = 20)
    private ProtocolType protocolType;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction_type", nullable = false, length = 20)
    private DirectionType directionType;

    @Column(name = "endpoint_or_path", nullable = false, length = 500)
    private String endpointOrPath;

    @Column(name = "execution_schedule", nullable = false, length = 120)
    private String executionSchedule;

    @Column(name = "timeout_seconds", nullable = false)
    private int timeoutSeconds;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false, length = 20)
    private InterfaceHealthStatus healthStatus;

    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;

    @Column(name = "last_execution_success", nullable = false)
    private boolean lastExecutionSuccess;

    @Column(name = "next_scheduled_at")
    private LocalDateTime nextScheduledAt;

    public InterfaceDefinitionEntity() {
    }

    public void applyExecutionResult(boolean success, LocalDateTime executedAt) {
        this.lastExecutionSuccess = success;
        this.lastExecutedAt = executedAt;
        if (this.active) {
            this.healthStatus = success ? InterfaceHealthStatus.NORMAL : InterfaceHealthStatus.FAILED;
        }
    }

    public void applyActivation(boolean active) {
        this.active = active;
        this.healthStatus = active ? InterfaceHealthStatus.NORMAL : InterfaceHealthStatus.STOPPED;
    }

    public String getInterfaceCode() { return interfaceCode; }
    public void setInterfaceCode(String interfaceCode) { this.interfaceCode = interfaceCode; }
    public String getInterfaceName() { return interfaceName; }
    public void setInterfaceName(String interfaceName) { this.interfaceName = interfaceName; }
    public String getTargetInstitution() { return targetInstitution; }
    public void setTargetInstitution(String targetInstitution) { this.targetInstitution = targetInstitution; }
    public ProtocolType getProtocolType() { return protocolType; }
    public void setProtocolType(ProtocolType protocolType) { this.protocolType = protocolType; }
    public DirectionType getDirectionType() { return directionType; }
    public void setDirectionType(DirectionType directionType) { this.directionType = directionType; }
    public String getEndpointOrPath() { return endpointOrPath; }
    public void setEndpointOrPath(String endpointOrPath) { this.endpointOrPath = endpointOrPath; }
    public String getExecutionSchedule() { return executionSchedule; }
    public void setExecutionSchedule(String executionSchedule) { this.executionSchedule = executionSchedule; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public InterfaceHealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(InterfaceHealthStatus healthStatus) { this.healthStatus = healthStatus; }
    public LocalDateTime getLastExecutedAt() { return lastExecutedAt; }
    public void setLastExecutedAt(LocalDateTime lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; }
    public boolean isLastExecutionSuccess() { return lastExecutionSuccess; }
    public void setLastExecutionSuccess(boolean lastExecutionSuccess) { this.lastExecutionSuccess = lastExecutionSuccess; }
    public LocalDateTime getNextScheduledAt() { return nextScheduledAt; }
    public void setNextScheduledAt(LocalDateTime nextScheduledAt) { this.nextScheduledAt = nextScheduledAt; }
}
