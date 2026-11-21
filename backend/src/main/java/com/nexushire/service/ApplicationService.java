package com.nexushire.service;

import com.nexushire.dto.application.ApplicationResponse;
import com.nexushire.dto.application.ApplyRequest;
import com.nexushire.dto.application.UpdateApplicationStatusRequest;
import com.nexushire.dto.common.PageResponse;
import com.nexushire.entity.Application;
import com.nexushire.entity.Job;
import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.ApplicationRepository;
import com.nexushire.repository.JobRepository;
import com.nexushire.repository.SeekerProfileRepository;
import com.nexushire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final MatchScoreService matchScoreService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public ApplicationResponse apply(User seeker, ApplyRequest request) {
        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (job.getStatus() != Job.JobStatus.ACTIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Job is not accepting applications");
        }
        if (applicationRepository.existsByJobIdAndSeekerId(job.getId(), seeker.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Already applied to this job");
        }
        SeekerProfile profile = seekerProfileRepository.findByUserId(seeker.getId()).orElse(null);
        double score = profile != null ? matchScoreService.compute(job, profile) : 0.0;
        Application application = Application.builder()
                .job(job)
                .seeker(seeker)
                .status(Application.ApplicationStatus.APPLIED)
                .matchScore(score)
                .coverNote(request.coverNote())
                .build();
        application = applicationRepository.save(application);

        notificationService.create(seeker, "APPLICATION_SUBMITTED",
                "You applied to " + job.getTitle() + " at " + job.getCompany().getName());
        userRepository.findById(job.getPostedBy()).ifPresent(recruiter ->
                notificationService.create(recruiter, "NEW_APPLICANT",
                        seeker.getFullName() + " applied to " + job.getTitle()));
        emailService.send(seeker.getEmail(), "Application Received",
                "Your application for " + job.getTitle() + " has been received.");
        return toResponse(application);
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> myApplications(User seeker, Pageable pageable) {
        Page<Application> page = applicationRepository.findBySeekerId(seeker.getId(), pageable);
        return PageResponse.of(page, this::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> forJob(Long jobId, Application.ApplicationStatus status,
                                                    User recruiter, Pageable pageable) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.getPostedBy().equals(recruiter.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not own this job");
        }
        Page<Application> page = status == null
                ? applicationRepository.findByJobId(jobId, pageable)
                : applicationRepository.findByJobIdAndStatus(jobId, status, pageable);
        return PageResponse.of(page, this::toResponse);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, UpdateApplicationStatusRequest request, User recruiter) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (!application.getJob().getPostedBy().equals(recruiter.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot modify this application");
        }
        application.setStatus(request.status());
        applicationRepository.save(application);
        notificationService.create(application.getSeeker(), "APPLICATION_STATUS",
                "Your application for " + application.getJob().getTitle()
                        + " is now " + request.status().name());
        emailService.send(application.getSeeker().getEmail(), "Application Update",
                "Your application status changed to " + request.status().name());
        return toResponse(application);
    }

    public ApplicationResponse toResponse(Application a) {
        Job job = a.getJob();
        User seeker = a.getSeeker();
        SeekerProfile profile = seekerProfileRepository.findByUserId(seeker.getId()).orElse(null);
        return new ApplicationResponse(
                a.getId(),
                job.getId(),
                job.getTitle(),
                job.getCompany().getName(),
                job.getCompany().getLogoUrl(),
                seeker.getId(),
                seeker.getFullName(),
                seeker.getEmail(),
                profile != null ? profile.getHeadline() : null,
                profile != null ? profile.getResumeUrl() : null,
                a.getStatus(),
                a.getMatchScore(),
                a.getCoverNote(),
                a.getAppliedAt(),
                a.getUpdatedAt());
    }
}
