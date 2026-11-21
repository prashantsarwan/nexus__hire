package com.nexushire.controller;

import com.nexushire.dto.alert.JobAlertRequest;
import com.nexushire.dto.alert.JobAlertResponse;
import com.nexushire.dto.application.ApplicationResponse;
import com.nexushire.dto.application.ApplyRequest;
import com.nexushire.dto.assessment.AssessmentAttemptResponse;
import com.nexushire.dto.assessment.AssessmentResponse;
import com.nexushire.dto.assessment.SubmitAssessmentRequest;
import com.nexushire.dto.common.MessageResponse;
import com.nexushire.dto.common.PageResponse;
import com.nexushire.dto.job.JobSummaryResponse;
import com.nexushire.dto.notification.NotificationResponse;
import com.nexushire.dto.seeker.SeekerProfileRequest;
import com.nexushire.dto.seeker.SeekerProfileResponse;
import com.nexushire.entity.SeekerSkill;
import com.nexushire.entity.User;
import com.nexushire.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seeker")
@RequiredArgsConstructor
public class SeekerController {

    private final UserService userService;
    private final SeekerProfileService seekerProfileService;
    private final ApplicationService applicationService;
    private final SavedJobService savedJobService;
    private final JobAlertService jobAlertService;
    private final NotificationService notificationService;
    private final AssessmentService assessmentService;

    @GetMapping("/profile")
    public ResponseEntity<SeekerProfileResponse> getProfile() {
        return ResponseEntity.ok(seekerProfileService.getMyProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<SeekerProfileResponse> updateProfile(@Valid @RequestBody SeekerProfileRequest request) {
        return ResponseEntity.ok(seekerProfileService.updateMyProfile(request));
    }

    @PostMapping("/profile/resume")
    public ResponseEntity<SeekerProfileResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(seekerProfileService.uploadResume(file));
    }

    @PostMapping("/profile/skills")
    public ResponseEntity<SeekerProfileResponse> addSkill(@RequestBody Map<String, Object> body) {
        Long skillId = Long.valueOf(body.get("skillId").toString());
        SeekerSkill.Proficiency proficiency = body.get("proficiency") != null
                ? SeekerSkill.Proficiency.valueOf(body.get("proficiency").toString())
                : SeekerSkill.Proficiency.INTERMEDIATE;
        return ResponseEntity.ok(seekerProfileService.addSkill(skillId, proficiency));
    }

    @DeleteMapping("/profile/skills/{skillId}")
    public ResponseEntity<SeekerProfileResponse> removeSkill(@PathVariable Long skillId) {
        return ResponseEntity.ok(seekerProfileService.removeSkill(skillId));
    }

    @GetMapping("/applications")
    public ResponseEntity<PageResponse<ApplicationResponse>> myApplications(
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User seeker = userService.getCurrentUser();
        return ResponseEntity.ok(applicationService.myApplications(seeker, pageable));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplyRequest request) {
        User seeker = userService.getCurrentUser();
        return ResponseEntity.ok(applicationService.apply(seeker, request));
    }

    @GetMapping("/saved-jobs")
    public ResponseEntity<List<JobSummaryResponse>> savedJobs() {
        return ResponseEntity.ok(savedJobService.list(userService.getCurrentUser()));
    }

    @PostMapping("/saved-jobs/{jobId}")
    public ResponseEntity<MessageResponse> saveJob(@PathVariable Long jobId) {
        savedJobService.save(userService.getCurrentUser(), jobId);
        return ResponseEntity.ok(new MessageResponse("Saved"));
    }

    @DeleteMapping("/saved-jobs/{jobId}")
    public ResponseEntity<MessageResponse> unsaveJob(@PathVariable Long jobId) {
        savedJobService.remove(userService.getCurrentUser(), jobId);
        return ResponseEntity.ok(new MessageResponse("Removed"));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<JobAlertResponse>> alerts() {
        return ResponseEntity.ok(jobAlertService.list(userService.getCurrentUser()));
    }

    @PostMapping("/alerts")
    public ResponseEntity<JobAlertResponse> createAlert(@RequestBody JobAlertRequest request) {
        return ResponseEntity.ok(jobAlertService.create(request, userService.getCurrentUser()));
    }

    @DeleteMapping("/alerts/{id}")
    public ResponseEntity<MessageResponse> deleteAlert(@PathVariable Long id) {
        jobAlertService.delete(id, userService.getCurrentUser());
        return ResponseEntity.ok(new MessageResponse("Deleted"));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> notifications() {
        return ResponseEntity.ok(notificationService.listForUser(userService.getCurrentUser().getId()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<MessageResponse> markRead(@PathVariable Long id) {
        notificationService.markRead(id, userService.getCurrentUser().getId());
        return ResponseEntity.ok(new MessageResponse("Read"));
    }

    @GetMapping("/assessments")
    public ResponseEntity<List<AssessmentResponse>> assessments() {
        return ResponseEntity.ok(assessmentService.listAll());
    }

    @PostMapping("/assessments/submit")
    public ResponseEntity<AssessmentAttemptResponse> submitAssessment(@Valid @RequestBody SubmitAssessmentRequest request) {
        return ResponseEntity.ok(assessmentService.submit(request, userService.getCurrentUser()));
    }

    @GetMapping("/assessments/attempts")
    public ResponseEntity<List<AssessmentAttemptResponse>> attempts() {
        return ResponseEntity.ok(assessmentService.myAttempts(userService.getCurrentUser()));
    }
}
