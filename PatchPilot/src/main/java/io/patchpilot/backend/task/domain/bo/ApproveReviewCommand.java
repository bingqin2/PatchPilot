package io.patchpilot.backend.task.domain.bo;

public record ApproveReviewCommand(
        String operator,
        String reason
) {
}
