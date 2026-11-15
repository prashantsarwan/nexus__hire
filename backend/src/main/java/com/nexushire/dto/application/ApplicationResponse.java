package com.nexushire.dto.application;

import com.nexushire.entity.Application;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String companyName,
        String companyLogoUrl,
        Long seekerId,
        String seekerName,
        String seekerEmail,
        String seekerHeadline,
        String seekerResumeUrl,
        Application.ApplicationStatus status,
        Double matchScore,
        String coverNote,
        LocalDateTime appliedAt,
        LocalDateTime updatedAt
) {
}
