package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import com.stw.insuranceintegrationplatform.interfaceconfig.service.InterfaceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InterfaceScheduler {
    private final InterfaceService interfaceService;
    private final ExecutionService executionService;
    private final InterfaceScheduleEvaluator scheduleEvaluator;

    public InterfaceScheduler(
            InterfaceService interfaceService,
            ExecutionService executionService,
            InterfaceScheduleEvaluator scheduleEvaluator
    ) {
        this.interfaceService = interfaceService;
        this.executionService = executionService;
        this.scheduleEvaluator = scheduleEvaluator;
    }

    @Scheduled(cron = "${scheduler.interface.cron:0 * * * * *}")
    public void runScheduledInterfaces() {
        LocalDateTime now = LocalDateTime.now();

        for (InterfaceDefinitionEntity definition : interfaceService.listActiveEntities()) {
            try {
                InterfaceScheduleEvaluator.ScheduleDecision decision = scheduleEvaluator.decide(definition, now);
                if (!decision.due()) {
                    if (decision.nextScheduledAt() != null && definition.getNextScheduledAt() == null) {
                        definition.setNextScheduledAt(decision.nextScheduledAt());
                        interfaceService.save(definition);
                    }
                    continue;
                }

                executionService.executeScheduled(definition.getInterfaceCode(), "scheduled-run");
                definition.setNextScheduledAt(decision.nextScheduledAt());
                interfaceService.save(definition);
            } catch (Exception ignored) {
                // 프로토타입 단계: 스케줄러 루프 보호를 위해 인터페이스 단위 예외는 삼키고 다음 대상 계속 처리
            }
        }
    }
}
