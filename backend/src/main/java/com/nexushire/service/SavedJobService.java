package com.nexushire.service;

import com.nexushire.dto.job.JobSummaryResponse;
import com.nexushire.entity.Job;
import com.nexushire.entity.SavedJob;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.JobRepository;
import com.nexushire.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final JobService jobService;

    @Transactional(readOnly = true)
    public List<JobSummaryResponse> list(User seeker) {
        return savedJobRepository.findBySeekerIdOrderBySavedAtDesc(seeker.getId())
                .stream()
                .map(sj -> jobService.toSummary(sj.getJob()))
                .toList();
    }

    @Transactional
    public void save(User seeker, Long jobId) {
        if (savedJobRepository.existsBySeekerIdAndJobId(seeker.getId(), jobId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Job already saved");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        savedJobRepository.save(SavedJob.builder().seeker(seeker).job(job).build());
    }

    @Transactional
    public void remove(User seeker, Long jobId) {
        savedJobRepository.deleteBySeekerIdAndJobId(seeker.getId(), jobId);
    }
}
