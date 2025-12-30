package com.job_service.models.responses;

import com.job_service.models.dao.JobEntity;
import com.job_service.models.enums.JobStatus;
import com.job_service.models.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class JobResponse {

    private String jobId;
    private JobType jobType;
    private JobStatus status;
    private Instant nextRunAt;
    private Instant createdAt;

    public static JobResponse from(JobEntity job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .nextRunAt(job.getNextRunAt())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
