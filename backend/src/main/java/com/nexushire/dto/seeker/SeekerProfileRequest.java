package com.nexushire.dto.seeker;

import com.nexushire.entity.SeekerSkill;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SeekerProfileRequest(
        @Size(max = 200) String headline,
        String summary,
        Integer totalExperienceYears,
        String currentLocation,
        String preferredLocations,
        Long expectedSalaryMin,
        Long expectedSalaryMax,
        Integer noticePeriodDays,
        List<SkillItem> skills
) {
    public record SkillItem(Long skillId, SeekerSkill.Proficiency proficiency) {
    }
}
