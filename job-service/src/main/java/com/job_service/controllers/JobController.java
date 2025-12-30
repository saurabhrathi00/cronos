package com.job_service.controllers;

import com.job_service.models.enums.JobStatus;
import com.job_service.models.enums.JobType;
import com.job_service.models.requests.CreateJobRequest;
import com.job_service.models.requests.RescheduleJobRequest;
import com.job_service.models.responses.CreateJobResponse;
import com.job_service.models.responses.JobResponse;
import com.job_service.services.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAuthority('jobs.create')")
    public ResponseEntity<CreateJobResponse> createJob(
            @RequestBody CreateJobRequest request) {

        CreateJobResponse response = jobService.createJob(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('jobs.read')")
    public ResponseEntity<List<JobResponse>> getMyJobs(
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") int page
    ) {
        int safePage = Math.max(page - 1, 0);
        Page<JobResponse> jobs = jobService.getJobs(status,jobType,from,to,safePage,size);

        return ResponseEntity
                .ok(jobs.getContent());
    }


    @PostMapping("/{jobId}/cancel")
    @PreAuthorize("hasAuthority('jobs.update')")
    public ResponseEntity<Void> cancelJob(@PathVariable String jobId) {
        jobService.cancelJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobId}/reschedule")
    @PreAuthorize("hasAuthority('jobs.update')")
    public ResponseEntity<Void> rescheduleJob(
            @PathVariable String jobId,
            @RequestBody @Valid RescheduleJobRequest request
    ) {
        jobService.rescheduleJob(jobId, request);
        return ResponseEntity.noContent().build();
    }
}
