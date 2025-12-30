package com.job_service.services;

import com.job_service.exceptions.BadRequestException;
import com.job_service.models.dao.JobEntity;
import com.job_service.models.enums.ExecutionMode;
import com.job_service.models.enums.JobStatus;
import com.job_service.models.enums.JobType;
import com.job_service.models.requests.CreateJobRequest;
import com.job_service.models.responses.CreateJobResponse;
import com.job_service.models.responses.JobResponse;
import com.job_service.repository.JobRepository;
import com.job_service.repository.specification.JobSpecifications;
import com.job_service.utils.CronUtils;
import com.job_service.utils.JobIdGenerator;
import com.job_service.validators.JobValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobValidator jobValidator;
    private final ClockService clockService;
    private final JobRepository jobRepository;

    public CreateJobResponse createJob(CreateJobRequest request) throws BadRequestException {
        jobValidator.validate(request);
        Instant nextRunAt = computeNextRunAt(request);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        JobEntity job = JobEntity.builder()
                .jobId(JobIdGenerator.generate())
                .jobType(request.getJobType())
                .executionMode(request.getExecutionMode())
                .cronExpression(request.getCronExpression())
                .payload(request.getPayload())
                .status(JobStatus.SCHEDULED)
                .createdBy(currentUser)
                .nextRunAt(nextRunAt)
                .build();

        jobRepository.save(job);

        return CreateJobResponse.builder()
                .jobId(job.getJobId())
                .status(job.getStatus().name())
                .nextRunAt(job.getNextRunAt())
                .build();
    }

    public Page<JobResponse> getJobs(JobStatus status,JobType jobType,Instant from,Instant to,int page,int size) {
        jobValidator.validateGetJobs(status, jobType, from, to, page, size);
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        Page<JobEntity> pageResult = jobRepository.findAll(
                JobSpecifications.withFilters(
                        currentUser,
                        status,
                        jobType,
                        from,
                        to
                ),
                pageable
        );
        return pageResult.map(JobResponse::from);
    }

    private Instant computeNextRunAt(CreateJobRequest request) {
        try {
            if (request.getJobType() == JobType.ONE_TIME) {

                if (request.getExecutionMode() == ExecutionMode.IMMEDIATE) {
                    return clockService.now();
                }
                // Else SCHEDULED
                return request.getRunAt();
            }
            // RECURRING
            return CronUtils.computeNextRun(
                    request.getCronExpression(),
                    clockService.now()
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }
}
