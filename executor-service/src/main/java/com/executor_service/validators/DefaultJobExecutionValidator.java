package com.executor_service.validators;

import com.executor_service.configurations.ServiceConfiguration;
import com.executor_service.exceptions.JobValidationException;
import com.executor_service.models.JobEvent;
import com.executor_service.models.dao.JobEntity;
import com.executor_service.models.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DefaultJobExecutionValidator implements JobExecutionValidator {

    private final ServiceConfiguration serviceConfiguration;

    @Override
    public void validate(JobEntity job, JobEvent event) {

        if (job.getStatus() != JobStatus.RUNNING) {
            throw new JobValidationException(
                    "Job is not in RUNNING state. jobId=" +
                            job.getJobId() +
                            ", status=" + job.getStatus()
            );
        }

        if (event.getAttempt() != job.getAttempts()) {
            throw new JobValidationException(
                    "Stale JobEvent received. jobId=" +
                            job.getJobId() +
                            ", expectedAttempt=" + job.getAttempts() +
                            ", receivedAttempt=" + event.getAttempt()
            );
        }

        if (job.getNextRunAt() != null &&
                job.getNextRunAt().isAfter(Instant.now())) {

            throw new JobValidationException(
                    "Job executed before nextRunAt. jobId=" +
                            job.getJobId() +
                            ", nextRunAt=" + job.getNextRunAt()
            );
        }

        int maxAttempts = serviceConfiguration.getRetry().getMaxAttempts();
        if (event.getAttempt() >= maxAttempts) {
            throw new JobValidationException(
                    "Max retry attempts exceeded for jobId=" + event.getJobId()
            );
        }
    }
}

