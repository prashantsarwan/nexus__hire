package com.nexushire.controller;

import com.nexushire.dto.admin.DashboardStatsResponse;
import com.nexushire.dto.common.MessageResponse;
import com.nexushire.dto.common.PageResponse;
import com.nexushire.dto.job.JobResponse;
import com.nexushire.dto.job.JobSummaryResponse;
import com.nexushire.dto.user.UserResponse;
import com.nexushire.entity.Application;
import com.nexushire.entity.Job;
import com.nexushire.entity.Role;
import com.nexushire.entity.User;
import com.nexushire.repository.ApplicationRepository;
import com.nexushire.repository.JobRepository;
import com.nexushire.repository.UserRepository;
import com.nexushire.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final JobService jobService;

    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>> users(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return ResponseEntity.ok(PageResponse.of(page, UserResponse::from));
    }

    @PatchMapping("/users/{id}/active")
    public ResponseEntity<UserResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(active);
        userRepository.save(user);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping("/jobs")
    public ResponseEntity<PageResponse<JobSummaryResponse>> jobs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Job> page = jobRepository.findAll(pageable);
        return ResponseEntity.ok(PageResponse.of(page, jobService::toSummary));
    }

    @PatchMapping("/jobs/{id}/moderate")
    public ResponseEntity<JobResponse> moderate(@PathVariable Long id, @RequestParam Job.JobStatus status) {
        return ResponseEntity.ok(jobService.moderate(id, status));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> stats() {
        Map<String, Long> usersByRole = new LinkedHashMap<>();
        for (Role role : Role.values()) {
            long count = userRepository.findAll().stream().filter(u -> u.getRole() == role).count();
            usersByRole.put(role.name(), count);
        }
        Map<String, Long> jobsByStatus = new LinkedHashMap<>();
        for (Job.JobStatus s : Job.JobStatus.values()) {
            jobsByStatus.put(s.name(), jobRepository.countByStatus(s));
        }
        Map<String, Long> appsByStatus = new LinkedHashMap<>();
        for (Application.ApplicationStatus s : Application.ApplicationStatus.values()) {
            appsByStatus.put(s.name(), applicationRepository.countByStatus(s));
        }
        return ResponseEntity.ok(new DashboardStatsResponse(
                userRepository.count(),
                jobRepository.count(),
                applicationRepository.count(),
                usersByRole,
                jobsByStatus,
                appsByStatus));
    }
}
