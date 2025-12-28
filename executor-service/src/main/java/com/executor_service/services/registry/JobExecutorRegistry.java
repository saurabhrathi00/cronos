package com.executor_service.services.registry;

import com.executor_service.exceptions.JobValidationException;
import com.executor_service.models.dao.JobEntity;
import com.executor_service.services.executors.JobExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JobExecutorRegistry {

    private final List<JobExecutor> executors;

    public JobExecutor getExecutor(JobEntity job) {
        return executors.stream()
                .filter(executor -> executor.supports(job))
                .findFirst()
                .orElseThrow(() ->
                        new JobValidationException(
                                "No executor found for jobId=" +
                                        job.getJobId()
                        )
                );
    }
}

