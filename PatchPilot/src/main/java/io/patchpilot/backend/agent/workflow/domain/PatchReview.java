package io.patchpilot.backend.agent.workflow.domain;

public record PatchReview(
        PatchReviewDecision decision,
        String reason,
        String confidence,
        String requiredFollowUp
) {
}
