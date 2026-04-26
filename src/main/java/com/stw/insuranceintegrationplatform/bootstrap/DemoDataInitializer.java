package com.stw.insuranceintegrationplatform.bootstrap;

import com.stw.insuranceintegrationplatform.execution.entity.ExecutionHistoryEntity;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionStatus;
import com.stw.insuranceintegrationplatform.execution.entity.ExecutionTriggerType;
import com.stw.insuranceintegrationplatform.execution.repository.ExecutionHistoryRepository;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.DirectionType;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceHealthStatus;
import com.stw.insuranceintegrationplatform.interfaceconfig.entity.ProtocolType;
import com.stw.insuranceintegrationplatform.interfaceconfig.repository.InterfaceDefinitionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("docker")
public class DemoDataInitializer implements CommandLineRunner {

    private final InterfaceDefinitionRepository interfaceRepository;
    private final ExecutionHistoryRepository historyRepository;

    public DemoDataInitializer(
            InterfaceDefinitionRepository interfaceRepository,
            ExecutionHistoryRepository historyRepository
    ) {
        this.interfaceRepository = interfaceRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public void run(String... args) {
        if (interfaceRepository.count() > 0 || historyRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        InterfaceDefinitionEntity rest = newInterface(
                "IF-REST-FSS-001", "금감원 계약조회 REST", "금감원", ProtocolType.REST, DirectionType.SEND,
                "https://fss.example/api/contracts", "manual", true, InterfaceHealthStatus.NORMAL, now.minusMinutes(40), true
        );
        InterfaceDefinitionEntity soap = newInterface(
                "IF-SOAP-PARTNER-002", "제휴사 정산 SOAP", "제휴사A", ProtocolType.SOAP, DirectionType.SEND,
                "https://partner.example/soap/settlement", "daily-02:00", true, InterfaceHealthStatus.FAILED, now.minusHours(2), false
        );
        InterfaceDefinitionEntity mq = newInterface(
                "IF-MQ-CORE-003", "코어 수납 MQ 수신", "보험코어", ProtocolType.MQ, DirectionType.RECEIVE,
                "MQ://core/payment", "every-10m", true, InterfaceHealthStatus.NORMAL, now.minusMinutes(10), true
        );
        InterfaceDefinitionEntity batch = newInterface(
                "IF-BATCH-RECON-004", "일마감 대사 배치", "내부정산", ProtocolType.BATCH, DirectionType.SEND,
                "batch://reconciliation", "cron:0 0 1 * * *", true, InterfaceHealthStatus.NORMAL, now.minusDays(1), true
        );
        InterfaceDefinitionEntity sftp = newInterface(
                "IF-SFTP-CLAIM-005", "청구자료 SFTP 송신", "제휴병원", ProtocolType.SFTP, DirectionType.SEND,
                "/outbound/claim", "fixed:60m", true, InterfaceHealthStatus.FAILED, now.minusHours(3), false
        );

        interfaceRepository.saveAll(List.of(rest, soap, mq, batch, sftp));

        historyRepository.saveAll(List.of(
                success("IF-REST-FSS-001", ExecutionTriggerType.MANUAL, now.minusMinutes(40), 120, "FSS 조회 완료"),
                failed("IF-SOAP-PARTNER-002", ExecutionTriggerType.SCHEDULED, now.minusHours(2), 0, "SOAP timeout"),
                success("IF-MQ-CORE-003", ExecutionTriggerType.SCHEDULED, now.minusMinutes(10), 57, "MQ 수신 완료"),
                success("IF-BATCH-RECON-004", ExecutionTriggerType.SCHEDULED, now.minusDays(1), 904, "일마감 완료"),
                failed("IF-SFTP-CLAIM-005", ExecutionTriggerType.MANUAL, now.minusHours(3), 0, "SFTP 인증 실패"),
                success("IF-SFTP-CLAIM-005", ExecutionTriggerType.REPROCESS, now.minusHours(1), 33, "재처리 송신 완료")
        ));
    }

    private InterfaceDefinitionEntity newInterface(
            String code,
            String name,
            String institution,
            ProtocolType protocol,
            DirectionType direction,
            String endpoint,
            String schedule,
            boolean active,
            InterfaceHealthStatus health,
            LocalDateTime lastExecutedAt,
            boolean success
    ) {
        InterfaceDefinitionEntity entity = new InterfaceDefinitionEntity();
        entity.setInterfaceCode(code);
        entity.setInterfaceName(name);
        entity.setTargetInstitution(institution);
        entity.setProtocolType(protocol);
        entity.setDirectionType(direction);
        entity.setEndpointOrPath(endpoint);
        entity.setExecutionSchedule(schedule);
        entity.setTimeoutSeconds(30);
        entity.setRetryCount(2);
        entity.applyActivation(active);
        entity.setHealthStatus(health);
        entity.setLastExecutedAt(lastExecutedAt);
        entity.setLastExecutionSuccess(success);
        return entity;
    }

    private ExecutionHistoryEntity success(
            String code,
            ExecutionTriggerType triggerType,
            LocalDateTime startedAt,
            int processedCount,
            String responseSummary
    ) {
        ExecutionHistoryEntity entity = new ExecutionHistoryEntity();
        entity.setInterfaceCode(code);
        entity.setTriggerType(triggerType);
        entity.setReprocessed(triggerType == ExecutionTriggerType.REPROCESS);
        entity.setStartedAt(startedAt);
        entity.setRequestSummary("demo request");
        entity.complete(
                startedAt.plusSeconds(2),
                ExecutionStatus.SUCCESS,
                processedCount,
                1,
                null,
                responseSummary
        );
        return entity;
    }

    private ExecutionHistoryEntity failed(
            String code,
            ExecutionTriggerType triggerType,
            LocalDateTime startedAt,
            int processedCount,
            String errorMessage
    ) {
        ExecutionHistoryEntity entity = new ExecutionHistoryEntity();
        entity.setInterfaceCode(code);
        entity.setTriggerType(triggerType);
        entity.setReprocessed(false);
        entity.setStartedAt(startedAt);
        entity.setRequestSummary("demo request");
        entity.complete(
                startedAt.plusSeconds(5),
                ExecutionStatus.FAILED,
                processedCount,
                3,
                errorMessage,
                "demo failure"
        );
        return entity;
    }
}
