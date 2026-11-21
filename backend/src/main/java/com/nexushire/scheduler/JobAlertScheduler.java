package com.nexushire.scheduler;

import com.nexushire.entity.Job;
import com.nexushire.entity.JobAlert;
import com.nexushire.repository.JobAlertRepository;
import com.nexushire.repository.JobRepository;
import com.nexushire.repository.spec.JobSpecification;
import com.nexushire.service.EmailService;
import com.nexushire.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JobAlertScheduler {

    private final JobAlertRepository jobAlertRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void run() {
        List<JobAlert> alerts = jobAlertRepository.findByActiveTrue();
        for (JobAlert alert : alerts) {
            List<Job> matches = jobRepository.findAll(
                    JobSpecification.search(alert.getKeyword(), alert.getLocation(), null,
                            alert.getMinSalary(), null, null, Job.JobStatus.ACTIVE))
                    .stream().limit(5).toList();
            if (matches.isEmpty()) {
                continue;
            }
            StringBuilder body = new StringBuilder("Hi, based on your job alert, here are some new matches:\n");
            for (Job job : matches) {
                body.append("- ").append(job.getTitle()).append(" at ")
                        .append(job.getCompany().getName()).append("\n");
            }
            emailService.send(alert.getSeeker().getEmail(), "New jobs matching your alert", body.toString());
            notificationService.create(alert.getSeeker(), "JOB_ALERT",
                    matches.size() + " new jobs match your alert");
        }
    }
}
