package com.nexushire.dto.interview;

import com.nexushire.entity.Interview;

import java.time.LocalDateTime;

public record InterviewResponse(
        Long id,
        Long applicationId,
        String seekerName,
        String jobTitle,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String mode,
        String meetingLink,
        String notes,
        Interview.InterviewStatus status
) {
}
