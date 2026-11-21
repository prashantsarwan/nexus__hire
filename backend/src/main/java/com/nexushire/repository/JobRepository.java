package com.nexushire.repository;

import com.nexushire.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);
    Page<Job> findByStatus(Job.JobStatus status, Pageable pageable);
    Page<Job> findByPostedBy(Long postedBy, Pageable pageable);
    long countByStatus(Job.JobStatus status);
    long countByPostedBy(Long postedBy);
    long countByPostedByAndStatus(Long postedBy, Job.JobStatus status);
    List<Job> findTop20ByStatusOrderByCreatedAtDesc(Job.JobStatus status);
}
