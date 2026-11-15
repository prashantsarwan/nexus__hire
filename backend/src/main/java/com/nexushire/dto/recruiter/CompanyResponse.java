package com.nexushire.dto.recruiter;

public record CompanyResponse(
        Long id,
        String name,
        String logoUrl,
        String website,
        String industry,
        String size,
        String description,
        String cultureText
) {
}
