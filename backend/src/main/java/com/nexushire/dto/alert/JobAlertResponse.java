package com.nexushire.dto.alert;

import com.nexushire.entity.JobAlert;

import java.time.LocalDateTime;

public record JobAlertResponse(
        Long id,
        String keyword,
        String location,
        Long minSalary,
        JobAlert.Frequency frequency,
        boolean active,
        LocalDateTime createdAt
) {
}
