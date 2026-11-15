package com.nexushire.dto.alert;

import com.nexushire.entity.JobAlert;

public record JobAlertRequest(
        String keyword,
        String location,
        Long minSalary,
        JobAlert.Frequency frequency
) {
}
