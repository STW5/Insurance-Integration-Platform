package com.stw.insuranceintegrationplatform.execution.service;

import com.stw.insuranceintegrationplatform.interfaceconfig.entity.InterfaceDefinitionEntity;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class InterfaceScheduleEvaluator {

    public ScheduleDecision decide(InterfaceDefinitionEntity definition, LocalDateTime now) {
        String schedule = definition.getExecutionSchedule();
        if (schedule == null || schedule.isBlank() || "manual".equalsIgnoreCase(schedule.trim())) {
            return ScheduleDecision.skip();
        }

        LocalDateTime next = definition.getNextScheduledAt();

        if (schedule.startsWith("fixed:")) {
            int minutes = Integer.parseInt(schedule.substring("fixed:".length()).trim());
            return fixedDecision(now, next, minutes);
        }

        if (schedule.startsWith("every-") && schedule.endsWith("m")) {
            int minutes = Integer.parseInt(schedule.substring("every-".length(), schedule.length() - 1));
            return fixedDecision(now, next, minutes);
        }

        if (schedule.startsWith("daily-")) {
            LocalTime t = LocalTime.parse(schedule.substring("daily-".length()));
            LocalDateTime candidate = now.toLocalDate().atTime(t);
            LocalDateTime scheduled = next != null ? next : (candidate.isAfter(now) ? candidate : candidate.plusDays(1));
            boolean due = !scheduled.isAfter(now);
            LocalDateTime nextScheduled = due ? scheduled.plusDays(1) : scheduled;
            return new ScheduleDecision(due, nextScheduled);
        }

        if (schedule.startsWith("cron:")) {
            String expr = schedule.substring("cron:".length()).trim();
            CronExpression cron = CronExpression.parse(expr);
            LocalDateTime scheduled = next != null ? next : cron.next(now.minusSeconds(1));
            boolean due = scheduled != null && !scheduled.isAfter(now);
            LocalDateTime nextScheduled = due ? cron.next(now) : scheduled;
            return new ScheduleDecision(due, nextScheduled);
        }

        throw new IllegalArgumentException("지원하지 않는 실행 주기 형식: " + schedule);
    }

    private ScheduleDecision fixedDecision(LocalDateTime now, LocalDateTime next, int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("fixed/every 분 단위는 1 이상이어야 합니다.");
        }
        LocalDateTime scheduled = next != null ? next : now;
        boolean due = !scheduled.isAfter(now);
        LocalDateTime nextScheduled = due ? now.plusMinutes(minutes) : scheduled;
        return new ScheduleDecision(due, nextScheduled);
    }

    public record ScheduleDecision(boolean due, LocalDateTime nextScheduledAt) {
        public static ScheduleDecision skip() {
            return new ScheduleDecision(false, null);
        }
    }
}
