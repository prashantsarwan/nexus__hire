package com.nexushire.dto.recruiter;

import java.util.List;

public record CandidateSearchCriteria(
        List<Long> skillIds,
        Integer minExperience,
        String location,
        Long minSalary,
        String keyword
) {
}
