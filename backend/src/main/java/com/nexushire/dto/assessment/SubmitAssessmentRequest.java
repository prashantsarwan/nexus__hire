package com.nexushire.dto.assessment;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitAssessmentRequest(
        @NotNull Long assessmentId,
        @NotNull List<Integer> answers
) {
}
