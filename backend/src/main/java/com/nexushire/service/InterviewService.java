package com.nexushire.service;

import com.nexushire.dto.interview.InterviewRequest;
import com.nexushire.dto.interview.InterviewResponse;
import com.nexushire.entity.Application;
import com.nexushire.entity.Interview;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.ApplicationRepository;
import com.nexushire.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public InterviewResponse schedule(InterviewRequest request, User recruiter) {
        Application application = applicationRepository.findById(request.applicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (!application.getJob().getPostedBy().equals(recruiter.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot schedule this interview");
        }
        Interview interview = Interview.builder()
                .application(application)
                .scheduledAt(request.scheduledAt())
                .durationMinutes(request.durationMinutes() != null ? request.durationMinutes() : 60)
                .mode(request.mode())
                .meetingLink(request.meetingLink())
                .notes(request.notes())
                .status(Interview.InterviewStatus.SCHEDULED)
                .build();
        interview = interviewRepository.save(interview);
        application.setStatus(Application.ApplicationStatus.INTERVIEW);
        applicationRepository.save(application);
        notificationService.create(application.getSeeker(), "INTERVIEW_SCHEDULED",
                "Interview scheduled for " + application.getJob().getTitle());
        emailService.send(application.getSeeker().getEmail(), "Interview Scheduled",
                "Your interview is scheduled on " + request.scheduledAt());
        return toResponse(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> listForRecruiter(User recruiter) {
        return interviewRepository.findByApplicationJobPostedBy(recruiter.getId())
                .stream().map(this::toResponse).toList();
    }

    public InterviewResponse toResponse(Interview i) {
        return new InterviewResponse(
                i.getId(),
                i.getApplication().getId(),
                i.getApplication().getSeeker().getFullName(),
                i.getApplication().getJob().getTitle(),
                i.getScheduledAt(),
                i.getDurationMinutes(),
                i.getMode(),
                i.getMeetingLink(),
                i.getNotes(),
                i.getStatus());
    }
}
