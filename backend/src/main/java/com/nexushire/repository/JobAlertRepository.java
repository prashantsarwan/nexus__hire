package com.nexushire.repository;

import com.nexushire.entity.JobAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
    List<JobAlert> findBySeekerIdOrderByCreatedAtDesc(Long seekerId);
    List<JobAlert> findByActiveTrue();
    List<JobAlert> findByActiveTrueAndFrequency(JobAlert.Frequency frequency);
}
