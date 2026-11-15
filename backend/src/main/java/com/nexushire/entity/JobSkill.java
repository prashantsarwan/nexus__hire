package com.nexushire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "skill_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Builder.Default
    private boolean mandatory = false;
}
