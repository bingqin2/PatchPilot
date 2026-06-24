package io.patchpilot.backend.safety.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RejectedTriggerRetryPolicyTests {

    @Test
    void should_allow_retry_for_actionability_categories_that_can_be_reclassified() {
        assertThat(RejectedTriggerRetryPolicy.isRetryable(RejectedTriggerCategory.NOT_ACTIONABLE, null)).isTrue();
        assertThat(RejectedTriggerRetryPolicy.isRetryable(RejectedTriggerCategory.MODEL_REJECTED, null)).isTrue();
        assertThat(RejectedTriggerRetryPolicy.isRetryable(RejectedTriggerCategory.MODEL_NEEDS_CLARIFICATION, null)).isTrue();
        assertThat(RejectedTriggerRetryPolicy.isRetryable(RejectedTriggerCategory.MODEL_CLASSIFICATION_FAILED, null)).isTrue();
        assertThat(RejectedTriggerRetryPolicy.retryBlockedReason(RejectedTriggerCategory.NOT_ACTIONABLE, null)).isNull();
    }

    @Test
    void should_block_retry_for_rejections_that_require_operator_or_author_changes() {
        assertThat(RejectedTriggerRetryPolicy.isRetryable(RejectedTriggerCategory.DANGEROUS_INSTRUCTION, null)).isFalse();
        assertThat(RejectedTriggerRetryPolicy.retryBlockedReason(RejectedTriggerCategory.DANGEROUS_INSTRUCTION, null))
                .isEqualTo("Remove destructive or secret-related instructions and ask for a specific, safe code change.");
        assertThat(RejectedTriggerRetryPolicy.retryBlockedReason(RejectedTriggerCategory.TRIGGER_USER_NOT_ALLOWED, null))
                .isEqualTo("Add this GitHub user to the trigger allowlist, or have an allowed maintainer post a new `/agent fix` comment.");
        assertThat(RejectedTriggerRetryPolicy.retryBlockedReason(RejectedTriggerCategory.RATE_LIMITED, null))
                .isEqualTo("Wait for the configured rate-limit window to reset before retrying.");
    }

    @Test
    void should_block_retry_when_rejected_trigger_already_created_a_retry_task() {
        assertThat(RejectedTriggerRetryPolicy.isRetryable(RejectedTriggerCategory.NOT_ACTIONABLE, "task-123")).isFalse();
        assertThat(RejectedTriggerRetryPolicy.retryBlockedReason(RejectedTriggerCategory.NOT_ACTIONABLE, "task-123"))
                .isEqualTo("Rejected trigger has already been retried; open the linked retried task instead.");
    }
}
