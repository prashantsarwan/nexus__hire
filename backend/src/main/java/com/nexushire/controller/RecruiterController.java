package com.nexushire.controller;

import com.nexushire.dto.admin.RecruiterAnalyticsResponse;
import com.nexushire.dto.application.ApplicationResponse;
import com.nexushire.dto.application.UpdateApplicationStatusRequest;
import com.nexushire.dto.common.MessageResponse;
import com.nexushire.dto.common.PageResponse;
import com.nexushire.dto.interview.InterviewRequest;
import com.nexushire.dto.interview.InterviewResponse;
import com.nexushire.dto.job.JobRequest;
import com.nexushire.dto.job.JobResponse;
import com.nexushire.dto.job.JobSummaryResponse;
import com.nexushire.dto.recruiter.CandidateSearchCriteria;
import com.nexushire.dto.recruiter.CompanyRequest;
import com.nexushire.dto.recruiter.CompanyResponse;
import com.nexushire.dto.recruiter.RecruiterProfileResponse;
import com.nexushire.dto.seeker.SeekerProfileResponse;
import com.nexushire.entity.Application;
import com.nexushire.entity.Job;
import com.nexushire.entity.User;
import com.nexushire.repository.ApplicationRepository;
import com.nexushire.repository.JobRepository;
import com.nexushire.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final UserService userService;
    private final RecruiterProfileService recruiterProfileService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final SeekerProfileService seekerProfileService;
    private final InterviewService interviewService;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @GetMapping("/profile")
    public ResponseEntity<RecruiterProfileResponse> profile() {
        return ResponseEntity.ok(recruiterProfileService.getMyProfile());
    }

    @PostMapping("/company")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.createCompany(request, userService.getCurrentUser()));
    }

    @PutMapping("/company")
    public ResponseEntity<CompanyResponse> updateCompany(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.updateCompany(request, userService.getCurrentUser()));
    }

    @GetMapping("/jobs")
    public ResponseEntity<PageResponse<JobSummaryResponse>> myJobs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(jobService.listMine(userService.getCurrentUser(), pageable));
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.create(request, userService.getCurrentUser()));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.update(id, request, userService.getCurrentUser()));
    }

    @PatchMapping("/jobs/{id}/status")
    public ResponseEntity<JobResponse> changeJobStatus(@PathVariable Long id, @RequestParam Job.JobStatus status) {
        return ResponseEntity.ok(jobService.changeStatus(id, status, userService.getCurrentUser()));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<MessageResponse> deleteJob(@PathVariable Long id) {
        jobService.delete(id, userService.getCurrentUser());
        return ResponseEntity.ok(new MessageResponse("Deleted"));
    }

    @GetMapping("/jobs/{id}/applicants")
    public ResponseEntity<PageResponse<ApplicationResponse>> applicants(
            @PathVariable Long id,
            @RequestParam(required = false) Application.ApplicationStatus status,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.forJob(id, status, userService.getCurrentUser(), pageable));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        return ResponseEntity.ok(applicationService.updateStatus(id, request, userService.getCurrentUser()));
    }

    @GetMapping("/candidates")
    public ResponseEntity<PageResponse<SeekerProfileResponse>> searchCandidates(
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(seekerProfileService.searchCandidates(
                new CandidateSearchCriteria(skillIds, minExperience, location, minSalary, keyword), pageable));
    }

    @PostMapping("/interviews")
    public ResponseEntity<InterviewResponse> scheduleInterview(@Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(interviewService.schedule(request, userService.getCurrentUser()));
    }

    @GetMapping("/interviews")
    public ResponseEntity<List<InterviewResponse>> myInterviews() {
        return ResponseEntity.ok(interviewService.listForRecruiter(userService.getCurrentUser()));
    }

    @GetMapping("/analytics")
    public ResponseEntity<RecruiterAnalyticsResponse> analytics() {
        User recruiter = userService.getCurrentUser();
        long totalJobs = jobRepository.countByPostedBy(recruiter.getId());
        long activeJobs = jobRepository.countByPostedByAndStatus(recruiter.getId(), Job.JobStatus.ACTIVE);
        List<Job> jobs = jobRepository.findByPostedBy(recruiter.getId(), Pageable.unpaged()).getContent();
        long totalApplications = 0;
        long hired = 0;
        long interview = 0;
        long shortlisted = 0;
        long offered = 0;
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Job job : jobs) {
            totalApplications += applicationRepository.countByJobId(job.getId());
            for (Application.ApplicationStatus s : Application.ApplicationStatus.values()) {
                long count = applicationRepository.countByJobIdAndStatus(job.getId(), s);
                if (s == Application.ApplicationStatus.HIRED) hired += count;
                if (s == Application.ApplicationStatus.INTERVIEW) interview += count;
                if (s == Application.ApplicationStatus.SHORTLISTED) shortlisted += count;
                if (s == Application.ApplicationStatus.OFFERED) offered += count;
                byStatus.merge(s.name(), count, Long::sum);
            }
        }
        return ResponseEntity.ok(new RecruiterAnalyticsResponse(
                totalJobs, activeJobs, totalApplications, hired, interview, shortlisted, offered, byStatus));
    }
}
