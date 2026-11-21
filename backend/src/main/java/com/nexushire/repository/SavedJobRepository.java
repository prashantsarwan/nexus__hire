package com.nexushire.repository;

import com.nexushire.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findBySeekerIdOrderBySavedAtDesc(Long seekerId);
    Optional<SavedJob> findBySeekerIdAndJobId(Long seekerId, Long jobId);
    boolean existsBySeekerIdAndJobId(Long seekerId, Long jobId);
    void deleteBySeekerIdAndJobId(Long seekerId, Long jobId);
}
