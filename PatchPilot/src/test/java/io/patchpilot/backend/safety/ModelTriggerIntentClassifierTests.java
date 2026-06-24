package io.patchpilot.backend.safety;

import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderException;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.domain.TriggerIntentDecisionStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ModelTriggerIntentClassifierTests {

    @Test
    void should_skip_model_call_when_classification_is_disabled() {
        RecordingModelProviderClient modelClient = new RecordingModelProviderClient("""
                {"decision":"REJECTED","reason":"not relevant"}
                """);
        ModelTriggerIntentClassifier classifier = new ModelTriggerIntentClassifier(modelClient, properties(false));

        TriggerIntentDecision decision = classifier.classify(request());

        assertThat(decision.status()).isEqualTo(TriggerIntentDecisionStatus.SHOULD_EXECUTE);
        assertThat(decision.reason()).isEqualTo("Model trigger classification is disabled");
        assertThat(modelClient.request()).isNull();
    }

    @Test
    void should_allow_trigger_when_model_returns_should_execute() {
        RecordingModelProviderClient modelClient = new RecordingModelProviderClient("""
                {
                  "decision": "SHOULD_EXECUTE",
                  "reason": "Concrete file update request."
                }
                """);
        ModelTriggerIntentClassifier classifier = new ModelTriggerIntentClassifier(modelClient, properties(true));

        TriggerIntentDecision decision = classifier.classify(request());

        assertThat(decision.status()).isEqualTo(TriggerIntentDecisionStatus.SHOULD_EXECUTE);
        assertThat(decision.shouldExecute()).isTrue();
        assertThat(decision.reason()).isEqualTo("Concrete file update request.");
        assertThat(modelClient.request().taskId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(modelClient.request().systemPrompt()).contains("Return only JSON");
        assertThat(modelClient.request().userPrompt()).contains("Repository: octocat/hello-world");
        assertThat(modelClient.request().userPrompt()).contains("Issue number: 42");
        assertThat(modelClient.request().userPrompt()).contains("Trigger user: alice");
        assertThat(modelClient.request().userPrompt()).contains("Trigger comment: /agent fix touch docs/demo.md");
        assertThat(modelClient.request().userPrompt()).contains("Issue title: Calculator total is wrong");
        assertThat(modelClient.request().userPrompt()).contains("Issue body: The add endpoint returns 5 for 2 + 2.");
        assertThat(modelClient.request().userPrompt())
                .contains("alice: The failing test is CalculatorControllerTests#addsNumbers.");
    }

    @Test
    void should_reject_trigger_when_model_requests_clarification() {
        RecordingModelProviderClient modelClient = new RecordingModelProviderClient("""
                {
                  "decision": "NEEDS_CLARIFICATION",
                  "reason": "The requested change does not describe the expected behavior."
                }
                """);
        ModelTriggerIntentClassifier classifier = new ModelTriggerIntentClassifier(modelClient, properties(true));

        TriggerIntentDecision decision = classifier.classify(request());

        assertThat(decision.status()).isEqualTo(TriggerIntentDecisionStatus.NEEDS_CLARIFICATION);
        assertThat(decision.shouldExecute()).isFalse();
        assertThat(decision.reason()).isEqualTo("The requested change does not describe the expected behavior.");
    }

    @Test
    void should_reject_trigger_when_model_response_is_invalid() {
        ModelTriggerIntentClassifier classifier = new ModelTriggerIntentClassifier(
                new RecordingModelProviderClient("not-json"),
                properties(true)
        );

        TriggerIntentDecision decision = classifier.classify(request());

        assertThat(decision.status()).isEqualTo(TriggerIntentDecisionStatus.REJECTED);
        assertThat(decision.shouldExecute()).isFalse();
        assertThat(decision.reason()).isEqualTo("Model trigger classification failed: invalid JSON response");
    }

    @Test
    void should_reject_trigger_when_model_call_fails() {
        ModelTriggerIntentClassifier classifier = new ModelTriggerIntentClassifier(
                new FailingModelProviderClient(),
                properties(true)
        );

        TriggerIntentDecision decision = classifier.classify(request());

        assertThat(decision.status()).isEqualTo(TriggerIntentDecisionStatus.REJECTED);
        assertThat(decision.shouldExecute()).isFalse();
        assertThat(decision.reason()).isEqualTo("Model trigger classification failed: provider unavailable");
    }

    private static TriggerIntentClassificationRequest request() {
        return new TriggerIntentClassificationRequest(
                "11111111-1111-1111-1111-111111111111",
                "issue_comment",
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix touch docs/demo.md",
                "Calculator total is wrong",
                "The add endpoint returns 5 for 2 + 2.",
                List.of(new io.patchpilot.backend.safety.domain.TriggerIntentIssueComment(
                        "alice",
                        "The failing test is CalculatorControllerTests#addsNumbers."
                ))
        );
    }

    private static SafetyProperties properties(boolean enabled) {
        SafetyProperties properties = new SafetyProperties();
        properties.setModelTriggerClassificationEnabled(enabled);
        return properties;
    }

    private static final class RecordingModelProviderClient implements ModelProviderClient {

        private final String responseContent;
        private ModelProviderRequest request;

        private RecordingModelProviderClient(String responseContent) {
            this.responseContent = responseContent;
        }

        @Override
        public ModelProviderResponse complete(ModelProviderRequest request) {
            this.request = request;
            return new ModelProviderResponse(responseContent, 10, 5, 15);
        }

        private ModelProviderRequest request() {
            return request;
        }
    }

    private static final class FailingModelProviderClient implements ModelProviderClient {

        @Override
        public ModelProviderResponse complete(ModelProviderRequest request) {
            throw new ModelProviderException("provider unavailable");
        }
    }
}
