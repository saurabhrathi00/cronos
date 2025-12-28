package com.executor_service.services;

import com.executor_service.models.JobEvent;

public interface JobExecutionService {
    void execute(JobEvent event);
    void markFailed(JobEvent event, Exception cause);
}
