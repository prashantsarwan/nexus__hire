package com.nexushire.dto.seeker;

import java.util.List;

public record SeekerProfileResponse(
        Long id,
        Long userId,
        String fullName,
        String email,
        String headline,
        String summary,
        Integer totalExperienceYears,
        String currentLocation,
        String preferredLocations,
        Long expectedSalaryMin,
        Long expectedSalaryMax,
        Integer noticePeriodDays,
        String resumeUrl,
        String videoProfileUrl,
        List<SeekerSkillItem> skills
) {
}
