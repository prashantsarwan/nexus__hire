package com.nexushire.dto.admin;

import java.util.Map;

public record DashboardStatsResponse(
        long totalUsers,
        long totalJobs,
        long totalApplications,
        Map<String, Long> usersByRole,
        Map<String, Long> jobsByStatus,
        Map<String, Long> applicationsByStatus
) {
}
