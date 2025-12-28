package com.executor_service.services.executors;

import com.executor_service.models.dao.JobEntity;

public interface JobExecutor {

    boolean supports(JobEntity job);

    void execute(JobEntity job);
}

