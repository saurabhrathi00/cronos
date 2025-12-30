package com.executor_service.services.executors;

import com.executor_service.models.dao.JobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SendEmailJobExecutor implements JobExecutor {

    @Override
    public boolean supports(JobEntity job) {
        return "send_email".equalsIgnoreCase(job.getPayload().get("task").toString());
    }

    @Override
    public void execute(JobEntity job) {
        Map<String, Object> payload = job.getPayload();

//        String userId = payload.get("userId").toString();
        String template = payload.get("template").toString();
        // Simulate sending email
        log.info(
                "Executing SEND_EMAIL job. jobId={}, template={}",
                job.getJobId(),
                template
        );
        // Here you would integrate with an email service to send the actual email
        // for now we can put sleep to simulate delay
        try {
            Thread.sleep(2000); // Simulate email sending delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Email sending interrupted for jobId={}", job.getJobId());
        }
    }
}

