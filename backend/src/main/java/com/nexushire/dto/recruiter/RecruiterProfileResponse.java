package com.nexushire.dto.recruiter;

public record RecruiterProfileResponse(
        Long id,
        Long userId,
        String fullName,
        String email,
        String designation,
        boolean companyAdmin,
        CompanyResponse company
) {
}
