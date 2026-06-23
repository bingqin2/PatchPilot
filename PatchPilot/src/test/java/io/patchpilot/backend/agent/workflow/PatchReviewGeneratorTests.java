package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.workflow.domain.FileEditContext;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchReview;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewDecision;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewGenerationException;
import io.patchpilot.backend.agent.workflow.domain.ProposedFileEdit;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatchReviewGeneratorTests {

    @Test
    void should_generate_patch_review_from_model_json() {
        RecordingModelProviderClient modelClient = new RecordingModelProviderClient("""
                {
                  "decision": "APPROVE",
                  "reason": "The edit changes Calculator#add to return the sum requested by the issue.",
                  "confidence": "HIGH",
                  "requiredFollowUp": "Run the adapter verification command."
                }
                """);
        PatchReviewGenerator generator = new PatchReviewGenerator(modelClient);

        FixTaskVo task = task("/agent fix failing add test");
        PatchReview review = generator.review(
                task,
                fixPlan("src/main/java/example/Calculator.java"),
                List.of(new FileEditContext(
                        "src/main/java/example/Calculator.java",
                        "class Calculator { int add(int a, int b) { return 0; } }"
                )),
                List.of(new ProposedFileEdit(
                        "src/main/java/example/Calculator.java",
                        "class Calculator { int add(int a, int b) { return a + b; } }",
                        "Return the sum."
                ))
        );

        assertThat(review.decision()).isEqualTo(PatchReviewDecision.APPROVE);
        assertThat(review.reason()).contains("return the sum");
        assertThat(review.confidence()).isEqualTo("HIGH");
        assertThat(review.requiredFollowUp()).isEqualTo("Run the adapter verification command.");
        assertThat(modelClient.request().taskId()).isEqualTo(task.id());
        assertThat(modelClient.request().systemPrompt()).contains("Return only JSON");
        assertThat(modelClient.request().userPrompt()).contains("Trigger comment: /agent fix failing add test");
        assertThat(modelClient.request().userPrompt()).contains("Before: class Calculator");
        assertThat(modelClient.request().userPrompt()).contains("After: class Calculator");
    }

    @Test
    void should_fail_when_review_json_is_invalid() {
        PatchReviewGenerator generator = new PatchReviewGenerator(new RecordingModelProviderClient("looks fine"));

        assertThatThrownBy(() -> generator.review(
                task("/agent fix failing add test"),
                fixPlan("src/main/java/example/Calculator.java"),
                List.of(new FileEditContext("src/main/java/example/Calculator.java", "class Calculator {}")),
                List.of(new ProposedFileEdit("src/main/java/example/Calculator.java", "class Calculator {}", "No-op"))
        ))
                .isInstanceOf(PatchReviewGenerationException.class)
                .hasMessage("Model patch review response was not valid JSON");
    }

    @Test
    void should_fail_when_review_decision_is_unknown() {
        PatchReviewGenerator generator = new PatchReviewGenerator(new RecordingModelProviderClient("""
                {
                  "decision": "MAYBE",
                  "reason": "Unclear",
                  "confidence": "LOW",
                  "requiredFollowUp": "Manual review"
                }
                """));

        assertThatThrownBy(() -> generator.review(
                task("/agent fix failing add test"),
                fixPlan("src/main/java/example/Calculator.java"),
                List.of(new FileEditContext("src/main/java/example/Calculator.java", "class Calculator {}")),
                List.of(new ProposedFileEdit("src/main/java/example/Calculator.java", "class Calculator {}", "No-op"))
        ))
                .isInstanceOf(PatchReviewGenerationException.class)
                .hasMessage("Model patch review response has unsupported decision: MAYBE");
    }

    private static FixPlan fixPlan(String targetFile) {
        return new FixPlan(
                "Fix Calculator#add",
                List.of(targetFile),
                List.of("Update add to return the sum"),
                "LOW"
        );
    }

    private static FixTaskVo task(String triggerComment) {
        InMemoryFixTaskService service = new InMemoryFixTaskService();
        return service.createFixTask(new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                triggerComment,
                "delivery-123",
                98765
        ));
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
            return new ModelProviderResponse(responseContent, 10, 20, 30);
        }

        private ModelProviderRequest request() {
            return request;
        }
    }
}
