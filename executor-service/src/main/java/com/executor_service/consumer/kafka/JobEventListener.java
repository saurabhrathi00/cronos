package com.executor_service.consumer.kafka;

import com.executor_service.exceptions.JobTransientException;
import com.executor_service.exceptions.JobValidationException;
import com.executor_service.models.JobEvent;
import com.executor_service.services.JobExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobEventListener {

    private final JobExecutionService jobExecutionService;

    @KafkaListener(
            topics = "${configs.kafka.topic.job-events}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onJobEvent(JobEvent event, Acknowledgment acknowledgment) {

        log.info("Received JobEvent jobId={} attempt={}", event.getJobId(), event.getAttempt());
        try {
            jobExecutionService.execute(event);
            acknowledgment.acknowledge();
            log.info("Job executed successfully jobId={} attempt={}", event.getJobId(), event.getAttempt());

        } catch (JobValidationException ex) {
            log.error("Permanent job failure jobId={} attempt={}", event.getJobId(), event.getAttempt(), ex);
            jobExecutionService.markFailed(event, ex);
            acknowledgment.acknowledge(); // NO RETRY
        } catch (JobTransientException ex) {
            log.warn("Transient job failure, retrying jobId={} attempt={}", event.getJobId(), event.getAttempt(), ex);
            throw ex; // RETRY

        } catch (Exception ex) {
            log.error("Unexpected error, treating as transient jobId={} attempt={}", event.getJobId(), event.getAttempt(), ex);
            throw new JobTransientException(ex.getMessage(),ex); // DEFENSIVE RETRY
        }
    }

}

