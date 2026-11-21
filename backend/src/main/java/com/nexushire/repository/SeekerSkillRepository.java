package com.nexushire.repository;

import com.nexushire.entity.SeekerSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeekerSkillRepository extends JpaRepository<SeekerSkill, Long> {
    List<SeekerSkill> findBySeekerProfileId(Long seekerProfileId);
    Optional<SeekerSkill> findBySeekerProfileIdAndSkillId(Long seekerProfileId, Long skillId);
    boolean existsBySeekerProfileIdAndSkillId(Long seekerProfileId, Long skillId);
    void deleteBySeekerProfileIdAndSkillId(Long seekerProfileId, Long skillId);
}
