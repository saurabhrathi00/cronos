package com.job_service.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class CreateJobResponse {

    private String jobId;

    private String status;

    private Instant nextRunAt;
}