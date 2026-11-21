package com.nexushire.dto.seeker;

import com.nexushire.entity.SeekerSkill;

public record SeekerSkillItem(
        Long skillId,
        String name,
        SeekerSkill.Proficiency proficiency,
        boolean verified
) {
}
