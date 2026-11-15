package com.nexushire.repository;

import com.nexushire.entity.SeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Long>, JpaSpecificationExecutor<SeekerProfile> {
    Optional<SeekerProfile> findByUserId(Long userId);
}
