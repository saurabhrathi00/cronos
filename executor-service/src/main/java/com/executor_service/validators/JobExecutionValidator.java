package com.executor_service.validators;

import com.executor_service.models.JobEvent;
import com.executor_service.models.dao.JobEntity;

public interface JobExecutionValidator {

    void validate(JobEntity job, JobEvent event);
}
