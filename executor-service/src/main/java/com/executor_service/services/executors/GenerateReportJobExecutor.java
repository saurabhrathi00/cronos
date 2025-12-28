package com.executor_service.services.executors;

import com.executor_service.models.dao.JobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class GenerateReportJobExecutor implements JobExecutor {

    @Override
    public boolean supports(JobEntity job) {
        return "generate_report".equalsIgnoreCase(job.getPayload().get("task").toString());
    }

    @Override
    public void execute(JobEntity job) {
        // report generation logic
        Map<String, Object> payload = job.getPayload();

        String template = payload.get("template").toString();
        // Simulate generating report
        log.info(
                "Executing GENERATE_REPORT job. jobId={}, template={}",
                job.getJobId(),
                template
        );
    }
}

