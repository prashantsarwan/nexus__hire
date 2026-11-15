package com.nexushire.dto.assessment;

import java.time.LocalDateTime;

public record AssessmentAttemptResponse(
        Long id,
        Long assessmentId,
        String assessmentTitle,
        String skillName,
        Integer score,
        boolean passed,
        LocalDateTime attemptedAt
) {
}
