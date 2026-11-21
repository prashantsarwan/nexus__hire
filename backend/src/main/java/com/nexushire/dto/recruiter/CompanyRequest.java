package com.nexushire.dto.recruiter;

import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(
        @NotBlank String name,
        String logoUrl,
        String website,
        String industry,
        String size,
        String description,
        String cultureText
) {
}
