package com.nexushire.repository;

import com.nexushire.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobIdAndSeekerId(Long jobId, Long seekerId);
    Optional<Application> findByJobIdAndSeekerId(Long jobId, Long seekerId);
    Page<Application> findBySeekerId(Long seekerId, Pageable pageable);
    Page<Application> findByJobId(Long jobId, Pageable pageable);
    Page<Application> findByJobIdAndStatus(Long jobId, Application.ApplicationStatus status, Pageable pageable);
    long countByJobId(Long jobId);
    long countByJobIdAndStatus(Long jobId, Application.ApplicationStatus status);
    long countBySeekerId(Long seekerId);
    long countByStatus(Application.ApplicationStatus status);
}
