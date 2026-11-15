package com.nexushire.dto.application;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplyRequest(
        @NotNull Long jobId,
        @Size(max = 2000) String coverNote
) {
}
