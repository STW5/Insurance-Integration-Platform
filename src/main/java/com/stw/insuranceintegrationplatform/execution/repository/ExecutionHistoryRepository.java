package com.stw.insuranceintegrationplatform.execution.repository;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistoryEntity, Long> {
    List<ExecutionHistoryEntity> findAllByOrderByStartedAtDesc();

    List<ExecutionHistoryEntity> findByExecutionStatusOrderByStartedAtDesc(ExecutionStatus executionStatus);

    List<ExecutionHistoryEntity> findByStartedAtBetween(LocalDateTime from, LocalDateTime to);
}
