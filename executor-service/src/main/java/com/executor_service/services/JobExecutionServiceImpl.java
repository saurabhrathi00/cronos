package com.executor_service.services;

import com.executor_service.exceptions.JobValidationException;
import com.executor_service.models.JobEvent;
import com.executor_service.models.dao.JobEntity;
import com.executor_service.models.enums.JobStatus;
import com.executor_service.repository.JobRepository;
import com.executor_service.services.registry.JobExecutorRegistry;
import com.executor_service.utils.CronUtils;
import com.executor_service.validators.JobExecutionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobExecutionServiceImpl implements JobExecutionService {

    private final JobRepository jobRepository;
    private final ClockService clockService;
    private final JobExecutionValidator jobExecutionValidator;
    private final JobExecutorRegistry jobExecutorRegistry;


    @Override
    @Transactional
    public void execute(JobEvent event) {

        JobEntity job = jobRepository
                .findByJobIdForUpdate(event.getJobId())
                .orElseThrow(() ->
                        new JobValidationException(
                                "Job not found: " + event.getJobId()
                        )
                );

        jobExecutionValidator.validate(job, event);
        log.info("Executing job jobId={} attempt={}", job.getJobId(), event.getAttempt());
        jobExecutorRegistry
                .getExecutor(job)
                .execute(job);

        Instant now = clockService.now();

        if (job.isRecurring()) {
            try {
                Instant nextRunAt =
                        CronUtils.computeNextRun(
                                job.getCronExpression(),
                                now
                        );
                job.setNextRunAt(nextRunAt);
                job.setStatus(JobStatus.SCHEDULED);
                job.setAttempts(0); // reset attempts for next execution
            } catch (IllegalArgumentException | IllegalStateException ex) {
                throw new JobValidationException("Failed to compute next execution time for recurring job", ex);
            }
        } else {
            job.setStatus(JobStatus.COMPLETED);
        }
        job.setUpdatedAt(now);
        // Transaction will commit here , JPA will flush changes
    }

    @Override
    @Transactional
    public void markFailed(JobEvent event, Exception cause) {
        JobEntity job = jobRepository
                .findByJobIdForUpdate(event.getJobId())
                .orElse(null);

        if (job == null) {
            log.warn("markFailed: job not found jobId={}", event.getJobId());
            return;
        }

        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.FAILED) {
            log.info("markFailed ignored, job already terminal jobId={} status={}",job.getJobId(), job.getStatus()
            );
            return;
        }
        job.setStatus(JobStatus.FAILED);
        job.setFailureReason(cause.getMessage());
        job.setFailedAt(clockService.now());
    }
}

