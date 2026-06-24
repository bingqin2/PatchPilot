package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TriggerDecisionEvidenceFormatterTests {

    @Test
    void should_format_accepted_trigger_without_model_context() {
        String evidence = TriggerDecisionEvidenceFormatter.accepted(
                SafetyGateDecision.accepted(),
                TriggerIntentDecision.shouldExecute("Model trigger classification is disabled"),
                false
        );

        assertThat(evidence)
                .isEqualTo("Trigger accepted: safety gate accepted; issue context not loaded; model trigger classification disabled");
    }

    @Test
    void should_format_issue_context_model_acceptance_without_exposing_internal_rejection_reason() {
        String evidence = TriggerDecisionEvidenceFormatter.accepted(
                SafetyGateDecision.rejected(
                        "Unsafe request rejected: instruction is not actionable",
                        RejectedTriggerCategory.NOT_ACTIONABLE
                ),
                TriggerIntentDecision.shouldExecute("Issue context describes a concrete failing test."),
                true
        );

        assertThat(evidence)
                .isEqualTo("Trigger accepted: safety gate requested issue-context classification because command is too short to describe a concrete code change; issue context loaded; model accepted trigger: Issue context describes a concrete failing test");
    }
}
