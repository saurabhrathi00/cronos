package com.job_service.repository.specification;

import com.job_service.models.dao.JobEntity;
import com.job_service.models.enums.JobStatus;
import com.job_service.models.enums.JobType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class JobSpecifications {

    private JobSpecifications() {}

    public static Specification<JobEntity> withFilters(
            String createdBy,
            JobStatus status,
            JobType jobType,
            Instant from,
            Instant to
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // mandatory filter
            predicates.add(cb.equal(root.get("createdBy"), createdBy));

            // optional filters
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (jobType != null) {
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }

            if (from != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                );
            }

            if (to != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("createdAt"), to)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

