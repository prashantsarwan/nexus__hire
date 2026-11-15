package com.nexushire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private Long postedBy;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private Integer minExperience;

    private Integer maxExperience;

    private Long salaryMin;

    private Long salaryMax;

    @Builder.Default
    private String salaryCurrency = "INR";

    private String location;

    @Builder.Default
    private boolean remote = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobStatus status = JobStatus.ACTIVE;

    private LocalDateTime applicationDeadline;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum EmploymentType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP,
        FREELANCE
    }

    public enum JobStatus {
        DRAFT,
        ACTIVE,
        CLOSED,
        FLAGGED
    }
}
