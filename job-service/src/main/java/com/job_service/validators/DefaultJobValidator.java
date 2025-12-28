package com.job_service.validators;

import com.job_service.exceptions.BadRequestException;
import com.job_service.models.enums.ExecutionMode;
import com.job_service.models.enums.JobType;
import com.job_service.models.requests.CreateJobRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultJobValidator implements JobValidator {

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

