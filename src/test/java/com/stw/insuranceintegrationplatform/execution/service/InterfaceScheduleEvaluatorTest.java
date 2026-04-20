package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterfaceScheduleEvaluatorTest {

    private final InterfaceScheduleEvaluator evaluator = new InterfaceScheduleEvaluator();

    @Test
    void shouldSkipManualSchedule() {
        InterfaceDefinitionEntity entity = new InterfaceDefinitionEntity();
        entity.setExecutionSchedule("manual");

        InterfaceScheduleEvaluator.ScheduleDecision decision = evaluator.decide(entity, LocalDateTime.now());

        assertFalse(decision.due());
    }

    @Test
    void shouldRunFixedScheduleWhenDue() {
        InterfaceDefinitionEntity entity = new InterfaceDefinitionEntity();
        entity.setExecutionSchedule("fixed:1");
        entity.setNextScheduledAt(LocalDateTime.now().minusMinutes(1));

        InterfaceScheduleEvaluator.ScheduleDecision decision = evaluator.decide(entity, LocalDateTime.now());

        assertTrue(decision.due());
    }
}
