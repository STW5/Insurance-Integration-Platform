package com.stw.insuranceintegrationplatform.execution.repository;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistoryEntity, Long>, JpaSpecificationExecutor<ExecutionHistoryEntity> {
    List<ExecutionHistoryEntity> findAllByOrderByStartedAtDesc();

    List<ExecutionHistoryEntity> findByExecutionStatusOrderByStartedAtDesc(ExecutionStatus executionStatus);

    List<ExecutionHistoryEntity> findByStartedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByStartedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByStartedAtBetweenAndExecutionStatus(LocalDateTime from, LocalDateTime to, ExecutionStatus executionStatus);

    List<ExecutionHistoryEntity> findByExecutionStatusAndStartedAtBetweenOrderByStartedAtDesc(
            ExecutionStatus executionStatus,
            LocalDateTime from,
            LocalDateTime to
    );
}
