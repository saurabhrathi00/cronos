package com.job_service.models.requests;

import com.job_service.models.enums.ExecutionMode;
import com.job_service.models.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {

    private JobType jobType;
    private ExecutionMode executionMode;
    private Instant runAt;
    private String cronExpression;
    private Map<String, Object> payload;
}

