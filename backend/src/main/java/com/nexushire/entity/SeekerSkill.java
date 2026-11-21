package com.nexushire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seeker_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"seeker_profile_id", "skill_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_profile_id", nullable = false)
    private SeekerProfile seekerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Proficiency proficiency = Proficiency.INTERMEDIATE;

    @Builder.Default
    private boolean verified = false;

    public enum Proficiency {
        BEGINNER,
        INTERMEDIATE,
        EXPERT
    }
}
