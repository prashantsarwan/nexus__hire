package com.nexushire.dto.interview;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InterviewRequest(
        @NotNull Long applicationId,
        @NotNull LocalDateTime scheduledAt,
        Integer durationMinutes,
        String mode,
        String meetingLink,
        String notes
) {
}
