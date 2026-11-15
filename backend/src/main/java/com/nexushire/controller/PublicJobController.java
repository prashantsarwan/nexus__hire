package com.nexushire.controller;

import com.nexushire.dto.common.PageResponse;
import com.nexushire.dto.job.JobResponse;
import com.nexushire.dto.job.JobSummaryResponse;
import com.nexushire.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class PublicJobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<PageResponse<JobSummaryResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) List<Long> skillIds,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(jobService.search(keyword, location, remote, minSalary,
                minExperience, skillIds, pageable));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<JobSummaryResponse>> latest() {
        return ResponseEntity.ok(jobService.latest());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }
}
