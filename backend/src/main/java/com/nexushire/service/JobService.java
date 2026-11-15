package com.nexushire.service;

import com.nexushire.dto.common.PageResponse;
import com.nexushire.dto.job.JobRequest;
import com.nexushire.dto.job.JobResponse;
import com.nexushire.dto.job.JobSkillItem;
import com.nexushire.dto.job.JobSummaryResponse;
import com.nexushire.entity.Company;
import com.nexushire.entity.Job;
import com.nexushire.entity.JobSkill;
import com.nexushire.entity.RecruiterProfile;
import com.nexushire.entity.Skill;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.ApplicationRepository;
import com.nexushire.repository.CompanyRepository;
import com.nexushire.repository.JobRepository;
import com.nexushire.repository.JobSkillRepository;
import com.nexushire.repository.SkillRepository;
import com.nexushire.repository.spec.JobSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruiterProfileService recruiterProfileService;

    @Transactional(readOnly = true)
    public PageResponse<JobSummaryResponse> search(String keyword,
                                                   String location,
                                                   Boolean remote,
                                                   Long minSalary,
                                                   Integer minExperience,
                                                   List<Long> skillIds,
                                                   Pageable pageable) {
        Page<Job> page = jobRepository.findAll(
                JobSpecification.search(keyword, location, remote, minSalary, minExperience,
                        skillIds, Job.JobStatus.ACTIVE),
                pageable);
        return PageResponse.of(page, this::toSummary);
    }

    @Transactional(readOnly = true)
    public List<JobSummaryResponse> latest() {
        return jobRepository.findTop20ByStatusOrderByCreatedAtDesc(Job.JobStatus.ACTIVE)
                .stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobSummaryResponse> listMine(User recruiter, Pageable pageable) {
        return PageResponse.of(jobRepository.findByPostedBy(recruiter.getId(), pageable), this::toSummary);
    }

    @Transactional
    public JobResponse create(JobRequest request, User recruiter) {
        RecruiterProfile profile = recruiterProfileService.loadProfile(recruiter.getId());
        Company company = profile.getCompany();
        if (company == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Create your company profile first");
        }
        Job job = Job.builder()
                .company(company)
                .postedBy(recruiter.getId())
                .title(request.title())
                .description(request.description())
                .employmentType(request.employmentType())
                .minExperience(request.minExperience())
                .maxExperience(request.maxExperience())
                .salaryMin(request.salaryMin())
                .salaryMax(request.salaryMax())
                .salaryCurrency(request.salaryCurrency() != null ? request.salaryCurrency() : "INR")
                .location(request.location())
                .remote(request.remote())
                .status(request.status() != null ? request.status() : Job.JobStatus.ACTIVE)
                .applicationDeadline(request.applicationDeadline())
                .build();
        job = jobRepository.save(job);
        saveSkills(job, request.skills());
        return toResponse(job);
    }

    @Transactional
    public JobResponse update(Long id, JobRequest request, User recruiter) {
        Job job = ownedJob(id, recruiter);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setEmploymentType(request.employmentType());
        job.setMinExperience(request.minExperience());
        job.setMaxExperience(request.maxExperience());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setSalaryCurrency(request.salaryCurrency() != null ? request.salaryCurrency() : job.getSalaryCurrency());
        job.setLocation(request.location());
        job.setRemote(request.remote());
        job.setApplicationDeadline(request.applicationDeadline());
        if (request.status() != null) {
            job.setStatus(request.status());
        }
        jobRepository.save(job);
        jobSkillRepository.deleteByJobId(job.getId());
        saveSkills(job, request.skills());
        return toResponse(job);
    }

    @Transactional
    public JobResponse changeStatus(Long id, Job.JobStatus status, User recruiter) {
        Job job = ownedJob(id, recruiter);
        job.setStatus(status);
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public void delete(Long id, User recruiter) {
        Job job = ownedJob(id, recruiter);
        jobSkillRepository.deleteByJobId(job.getId());
        jobRepository.delete(job);
    }

    @Transactional
    public JobResponse moderate(Long id, Job.JobStatus status) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setStatus(status);
        return toResponse(jobRepository.save(job));
    }

    private void saveSkills(Job job, List<JobRequest.SkillAssign> skillAssigns) {
        if (skillAssigns == null) {
            return;
        }
        for (JobRequest.SkillAssign assign : skillAssigns) {
            Skill skill = skillRepository.findById(assign.skillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
            jobSkillRepository.save(JobSkill.builder()
                    .job(job)
                    .skill(skill)
                    .mandatory(assign.mandatory())
                    .build());
        }
    }

    private Job ownedJob(Long id, User recruiter) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.getPostedBy().equals(recruiter.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not own this job");
        }
        return job;
    }

    public JobSummaryResponse toSummary(Job job) {
        return new JobSummaryResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany().getName(),
                job.getCompany().getLogoUrl(),
                job.getLocation(),
                job.isRemote(),
                job.getEmploymentType(),
                job.getMinExperience(),
                job.getMaxExperience(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSalaryCurrency(),
                job.getStatus(),
                job.getCreatedAt());
    }

    public JobResponse toResponse(Job job) {
        List<JobSkillItem> skillItems = jobSkillRepository.findByJobId(job.getId())
                .stream()
                .map(js -> new JobSkillItem(js.getSkill().getId(), js.getSkill().getName(), js.isMandatory()))
                .toList();
        long applicantCount = applicationRepository.countByJobId(job.getId());
        Company c = job.getCompany();
        return new JobResponse(
                job.getId(),
                c.getId(),
                c.getName(),
                c.getLogoUrl(),
                c.getDescription(),
                job.getPostedBy(),
                job.getTitle(),
                job.getDescription(),
                job.getEmploymentType(),
                job.getMinExperience(),
                job.getMaxExperience(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSalaryCurrency(),
                job.getLocation(),
                job.isRemote(),
                job.getStatus(),
                job.getApplicationDeadline(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                skillItems,
                applicantCount);
    }
}
