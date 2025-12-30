package com.job_service.repository;

import com.job_service.models.dao.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, String>, JpaSpecificationExecutor<JobEntity> {

    Optional<JobEntity> findByJobId(String jobId);

    Optional<JobEntity> findByJobIdAndCreatedBy(
            String jobId,
            String createdBy
    );
}
