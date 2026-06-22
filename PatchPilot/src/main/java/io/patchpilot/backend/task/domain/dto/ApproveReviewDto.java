package io.patchpilot.backend.task.domain.dto;

public record ApproveReviewDto(
        String operator,
        String reason
) {
}
