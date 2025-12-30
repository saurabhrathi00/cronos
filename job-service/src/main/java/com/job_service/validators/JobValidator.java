package com.job_service.validators;

import com.job_service.exceptions.BadRequestException;
import com.job_service.models.enums.JobStatus;
import com.job_service.models.enums.JobType;
import com.job_service.models.requests.CreateJobRequest;

import java.time.Instant;

public interface JobValidator {
    void validate(CreateJobRequest request) throws BadRequestException;
    void validateGetJobs(
            JobStatus status,
            JobType jobType,
            Instant from,
            Instant to,
            int page,
            int size
    ) throws BadRequestException;
}
