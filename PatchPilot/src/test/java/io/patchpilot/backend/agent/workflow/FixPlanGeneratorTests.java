package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.FixPlanGenerationException;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixPlanGeneratorTests {

    @Test
    void should_generate_structured_fix_plan_from_model_json() {
        RecordingModelProviderClient modelClient = new RecordingModelProviderClient("""
                {
                  "summary": "Fix Calculator#add so it returns the sum.",
                  "targetFiles": ["src/main/java/example/Calculator.java"],
                  "steps": [
                    "Inspect Calculator#add",
                    "Update the return expression",
                    "Run Maven tests"
                  ],
                  "risk": "LOW"
                }
                """);
        FixPlanGenerator generator = new FixPlanGenerator(modelClient);
        FixTaskVo task = task("/agent fix failing add test");

        FixPlan plan = generator.generatePlan(task);

        assertThat(plan.summary()).isEqualTo("Fix Calculator#add so it returns the sum.");
        assertThat(plan.targetFiles()).containsExactly("src/main/java/example/Calculator.java");
        assertThat(plan.steps()).containsExactly(
                "Inspect Calculator#add",
                "Update the return expression",
                "Run Maven tests"
        );
        assertThat(plan.risk()).isEqualTo("LOW");
        assertThat(modelClient.request().taskId()).isEqualTo(task.id());
        assertThat(modelClient.request().systemPrompt()).contains("Return only JSON");
        assertThat(modelClient.request().userPrompt()).contains("Repository: octocat/hello-world");
        assertThat(modelClient.request().userPrompt()).contains("Issue number: 42");
        assertThat(modelClient.request().userPrompt()).contains("Trigger comment: /agent fix failing add test");
    }

    @Test
    void should_fail_when_model_output_is_not_json() {
        FixPlanGenerator generator = new FixPlanGenerator(new RecordingModelProviderClient("Use Calculator.java"));

        assertThatThrownBy(() -> generator.generatePlan(task("/agent fix failing add test")))
                .isInstanceOf(FixPlanGenerationException.class)
                .hasMessage("Model fix plan response was not valid JSON");
    }

    @Test
    void should_fail_when_required_fields_are_missing() {
        FixPlanGenerator generator = new FixPlanGenerator(new RecordingModelProviderClient("""
                {
                  "summary": "Fix Calculator#add"
                }
                """));

        assertThatThrownBy(() -> generator.generatePlan(task("/agent fix failing add test")))
                .isInstanceOf(FixPlanGenerationException.class)
                .hasMessage("Model fix plan response missing required field: targetFiles");
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
