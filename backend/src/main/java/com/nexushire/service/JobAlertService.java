package com.nexushire.service;

import com.nexushire.dto.alert.JobAlertRequest;
import com.nexushire.dto.alert.JobAlertResponse;
import com.nexushire.entity.JobAlert;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.JobAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAlertService {

    private final JobAlertRepository jobAlertRepository;

    @Transactional(readOnly = true)
    public List<JobAlertResponse> list(User seeker) {
        return jobAlertRepository.findBySeekerIdOrderByCreatedAtDesc(seeker.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public JobAlertResponse create(JobAlertRequest request, User seeker) {
        JobAlert alert = JobAlert.builder()
                .seeker(seeker)
                .keyword(request.keyword())
                .location(request.location())
                .minSalary(request.minSalary())
                .frequency(request.frequency() != null ? request.frequency() : JobAlert.Frequency.DAILY)
                .active(true)
                .build();
        return toResponse(jobAlertRepository.save(alert));
    }

    @Transactional
    public void delete(Long id, User seeker) {
        JobAlert alert = jobAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
        if (!alert.getSeeker().getId().equals(seeker.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your alert");
        }
        jobAlertRepository.delete(alert);
    }

    private JobAlertResponse toResponse(JobAlert a) {
        return new JobAlertResponse(a.getId(), a.getKeyword(), a.getLocation(),
                a.getMinSalary(), a.getFrequency(), a.isActive(), a.getCreatedAt());
    }
}
