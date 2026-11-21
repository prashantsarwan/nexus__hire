package com.nexushire.service;

import com.nexushire.entity.Job;
import com.nexushire.entity.JobSkill;
import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.SeekerSkill;
import com.nexushire.repository.JobSkillRepository;
import com.nexushire.repository.SeekerSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchScoreService {

    private final JobSkillRepository jobSkillRepository;
    private final SeekerSkillRepository seekerSkillRepository;

    @Transactional(readOnly = true)
    public double compute(Job job, SeekerProfile profile) {
        if (job == null || profile == null) {
            return 0.0;
        }
        double score = 0.0;

        List<JobSkill> jobSkills = jobSkillRepository.findByJobId(job.getId());
        List<SeekerSkill> seekerSkills = seekerSkillRepository.findBySeekerProfileId(profile.getId());

        if (jobSkills.isEmpty()) {
            score += 20.0;
        } else {
            Set<Long> seekerSkillIds = seekerSkills.stream()
                    .map(ss -> ss.getSkill().getId())
                    .collect(Collectors.toSet());
            long matched = jobSkills.stream()
                    .filter(js -> seekerSkillIds.contains(js.getSkill().getId()))
                    .count();
            score += 40.0 * matched / jobSkills.size();
        }

        int jobMin = job.getMinExperience() != null ? job.getMinExperience() : 0;
        int exp = profile.getTotalExperienceYears() != null ? profile.getTotalExperienceYears() : 0;
        if (jobMin <= 0 || exp >= jobMin) {
            score += 20.0;
        } else {
            score += 20.0 * exp / jobMin;
        }

        if (job.isRemote()) {
            score += 15.0;
        } else if (job.getLocation() != null && profile.getCurrentLocation() != null) {
            if (job.getLocation().equalsIgnoreCase(profile.getCurrentLocation())) {
                score += 15.0;
            } else if (profile.getPreferredLocations() != null
                    && profile.getPreferredLocations().toLowerCase().contains(job.getLocation().toLowerCase())) {
                score += 10.0;
            }
        }

        long jobMaxSalary = job.getSalaryMax() != null ? job.getSalaryMax() : Long.MAX_VALUE;
        long expectedMin = profile.getExpectedSalaryMin() != null ? profile.getExpectedSalaryMin() : 0L;
        if (expectedMin <= jobMaxSalary) {
            score += 15.0;
        }

        long verifiedCount = seekerSkills.stream().filter(SeekerSkill::isVerified).count();
        score += Math.min(10.0, verifiedCount * 2.0);

        return Math.round(score * 10.0) / 10.0;
    }
}
