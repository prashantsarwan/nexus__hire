package com.nexushire.dto.application;

import com.nexushire.entity.Application;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(
        @NotNull Application.ApplicationStatus status,
        String reason
) {
}
