package com.stw.insuranceintegrationplatform.execution.repository;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select e.interfaceCode as interfaceCode, e.executionStatus as executionStatus, count(e) as totalCount
            from ExecutionHistoryEntity e
            where e.startedAt between :from and :to
            group by e.interfaceCode, e.executionStatus
            """)
    List<ExecutionGroupedCountProjection> countGroupedByInterfaceAndStatus(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
