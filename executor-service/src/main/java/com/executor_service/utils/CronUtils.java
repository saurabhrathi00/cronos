package com.executor_service.utils;


import com.executor_service.exceptions.JobValidationException;
import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CronUtils {

    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Computes the next execution time for a cron expression.
     *
     * @param cronExpression cron string (Spring-compatible)
     * @param fromInstant    reference time (usually now)
     * @return next execution time as Instant (UTC)
     */
    public static Instant computeNextRun(String cronExpression, Instant fromInstant) {

        if (cronExpression == null || cronExpression.isBlank()) {
            throw new IllegalArgumentException("cronExpression must be provided");
        }

        try {
            CronExpression cron = CronExpression.parse(cronExpression);

            ZonedDateTime from = ZonedDateTime.ofInstant(fromInstant, UTC);
            ZonedDateTime next = cron.next(from);

            if (next == null) {
                throw new IllegalStateException(
                        "Cron expression does not produce any future execution time");
            }

            return next.toInstant();

        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Invalid cron expression: " + cronExpression);
        }
    }
}
