package com.job_service.validators;

import com.job_service.exceptions.BadRequestException;
import com.job_service.models.requests.CreateJobRequest;

public interface JobValidator {
    void validate(CreateJobRequest request) throws BadRequestException;
}
