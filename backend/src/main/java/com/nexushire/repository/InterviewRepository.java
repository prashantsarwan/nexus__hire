package com.nexushire.repository;

import com.nexushire.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplicationIdOrderByScheduledAtDesc(Long applicationId);
    List<Interview> findByApplicationJobPostedBy(Long recruiterId);
}
