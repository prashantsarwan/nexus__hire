package com.nexushire.dto.job;

import com.nexushire.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record JobRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Job.EmploymentType employmentType,
        Integer minExperience,
        Integer maxExperience,
        Long salaryMin,
        Long salaryMax,
        String salaryCurrency,
        @NotBlank String location,
        boolean remote,
        Job.JobStatus status,
        LocalDateTime applicationDeadline,
        List<SkillAssign> skills
) {
    public record SkillAssign(Long skillId, boolean mandatory) {
    }
}
