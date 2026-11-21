package com.nexushire.dto.job;

import com.nexushire.entity.Job;

import java.time.LocalDateTime;
import java.util.List;

public record JobResponse(
        Long id,
        Long companyId,
        String companyName,
        String companyLogoUrl,
        String companyDescription,
        Long postedBy,
        String title,
        String description,
        Job.EmploymentType employmentType,
        Integer minExperience,
        Integer maxExperience,
        Long salaryMin,
        Long salaryMax,
        String salaryCurrency,
        String location,
        boolean remote,
        Job.JobStatus status,
        LocalDateTime applicationDeadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<JobSkillItem> skills,
        long applicantCount
) {
}
