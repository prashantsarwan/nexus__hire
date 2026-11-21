package com.nexushire.dto.admin;

import java.util.Map;

public record RecruiterAnalyticsResponse(
        long totalJobs,
        long activeJobs,
        long totalApplications,
        long hiredCount,
        long interviewCount,
        long shortlistedCount,
        long offeredCount,
        Map<String, Long> applicationsByStatus
) {
}
