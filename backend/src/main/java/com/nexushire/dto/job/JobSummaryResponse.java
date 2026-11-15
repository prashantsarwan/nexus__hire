package com.nexushire.dto.job;

import com.nexushire.entity.Job;

import java.time.LocalDateTime;

public record JobSummaryResponse(
        Long id,
        String title,
        String companyName,
        String companyLogoUrl,
        String location,
        boolean remote,
        Job.EmploymentType employmentType,
        Integer minExperience,
        Integer maxExperience,
        Long salaryMin,
        Long salaryMax,
        String salaryCurrency,
        Job.JobStatus status,
        LocalDateTime createdAt
) {
}
