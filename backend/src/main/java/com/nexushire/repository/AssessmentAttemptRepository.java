package com.nexushire.repository;

import com.nexushire.entity.AssessmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {
    List<AssessmentAttempt> findBySeekerIdOrderByAttemptedAtDesc(Long seekerId);
    List<AssessmentAttempt> findByAssessmentIdAndSeekerId(Long assessmentId, Long seekerId);
}
