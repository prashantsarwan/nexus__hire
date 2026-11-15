package com.nexushire.dto.assessment;

public record AssessmentResponse(
        Long id,
        Long skillId,
        String skillName,
        String title,
        Integer durationMinutes,
        Integer passingScore
) {
}
