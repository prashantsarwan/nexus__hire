package com.nexushire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_seeker_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String headline;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private Integer totalExperienceYears;

    private String currentLocation;

    private String preferredLocations;

    private Long expectedSalaryMin;

    private Long expectedSalaryMax;

    private Integer noticePeriodDays;

    private String resumeUrl;

    private String videoProfileUrl;

    @Column(columnDefinition = "LONGTEXT")
    private String resumeParsedText;
}
