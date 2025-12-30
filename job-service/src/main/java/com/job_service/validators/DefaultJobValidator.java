package com.job_service.validators;

import com.job_service.exceptions.BadRequestException;
import com.job_service.models.enums.ExecutionMode;
import com.job_service.models.enums.JobStatus;
import com.job_service.models.enums.JobType;
import com.job_service.models.requests.CreateJobRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Component
public class DefaultJobValidator implements JobValidator {

    private static final int MAX_PAGE_SIZE = 100;
    @Override
    public void validate(CreateJobRequest request) throws BadRequestException {

        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }

        JobType jobType = request.getJobType();
        ExecutionMode executionMode = request.getExecutionMode();

        if (jobType == null) {
            throw new BadRequestException("jobType must be provided");
        }

        switch (jobType) {

            case ONE_TIME:
                validateOneTimeJob(request, executionMode);
                break;

            case RECURRING:
                validateRecurringJob(request);
                break;

            default:
                throw new BadRequestException("Unsupported jobType: " + jobType);
        }
    }

    @Override
    public void validateGetJobs(
            JobStatus status,
            JobType jobType,
            Instant from,
            Instant to,
            int page,
            int size
    ) {
        validatePagination(page, size);
        validateTimeRange(from, to);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("page must be >= 0");
        }
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException(
                    "size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }

    private void validateTimeRange(Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException(
                    "'from' must be before or equal to 'to'");
        }
    }

    private void validateOneTimeJob(CreateJobRequest request,
                                    ExecutionMode executionMode) {

        if (executionMode == null) {
            throw new BadRequestException(
                    "executionMode must be provided for ONE_TIME jobs");
        }

        if (executionMode == ExecutionMode.IMMEDIATE) {

            if (request.getRunAt() != null) {
                throw new BadRequestException(
                        "runAt must be null for IMMEDIATE jobs");
            }

            if (StringUtils.hasText(request.getCronExpression())) {
                throw new BadRequestException(
                        "cronExpression must not be provided for ONE_TIME jobs");
            }
        }

        if (executionMode == ExecutionMode.SCHEDULED) {

            if (request.getRunAt() == null) {
                throw new BadRequestException(
                        "runAt must be provided for SCHEDULED jobs");
            }

            if (StringUtils.hasText(request.getCronExpression())) {
                throw new BadRequestException(
                        "cronExpression must not be provided for ONE_TIME jobs");
            }
        }
    }

    private void validateRecurringJob(CreateJobRequest request) {

        if (!StringUtils.hasText(request.getCronExpression())) {
            throw new BadRequestException(
                    "cronExpression must be provided for RECURRING jobs");
        }

        if (request.getRunAt() != null) {
            throw new BadRequestException(
                    "runAt must be null for RECURRING jobs");
        }

        if (request.getExecutionMode() != null) {
            throw new BadRequestException(
                    "executionMode must not be provided for RECURRING jobs");
        }
    }
}

