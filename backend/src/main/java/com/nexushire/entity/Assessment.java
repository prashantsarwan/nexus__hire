package com.nexushire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    private String title;

    private Integer durationMinutes;

    private Integer passingScore;

    @Column(columnDefinition = "LONGTEXT")
    private String questionsJson;
}
