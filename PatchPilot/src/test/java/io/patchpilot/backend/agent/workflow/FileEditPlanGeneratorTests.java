package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.workflow.domain.FileEditContext;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlan;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlanGenerationException;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileEditPlanGeneratorTests {

    @Test
    void should_generate_file_edit_plan_from_model_json() {
        RecordingModelProviderClient modelClient = new RecordingModelProviderClient("""
                {
                  "edits": [
                    {
                      "path": "src/main/java/example/Calculator.java",
                      "content": "class Calculator { int add(int a, int b) { return a + b; } }",
                      "rationale": "Return the sum instead of a constant."
                    }
                  ]
                }
                """);
        FileEditPlanGenerator generator = new FileEditPlanGenerator(modelClient);
        FixTaskVo task = task("/agent fix failing add test");

        FileEditPlan plan = generator.generateEdits(
                task,
                fixPlan("src/main/java/example/Calculator.java"),
                List.of(new FileEditContext(
                        "src/main/java/example/Calculator.java",
                        "class Calculator { int add(int a, int b) { return 0; } }"
                ))
        );

        assertThat(plan.edits()).hasSize(1);
        assertThat(plan.edits().get(0).path()).isEqualTo("src/main/java/example/Calculator.java");
        assertThat(plan.edits().get(0).content()).contains("return a + b");
        assertThat(plan.edits().get(0).rationale()).isEqualTo("Return the sum instead of a constant.");
        assertThat(modelClient.request().taskId()).isEqualTo(task.id());
        assertThat(modelClient.request().systemPrompt()).contains("Return only JSON");
        assertThat(modelClient.request().userPrompt()).contains("Fix plan summary: Fix Calculator#add");
        assertThat(modelClient.request().userPrompt()).contains("Target files: src/main/java/example/Calculator.java");
        assertThat(modelClient.request().userPrompt()).contains("File: src/main/java/example/Calculator.java");
        assertThat(modelClient.request().userPrompt()).contains("return 0");
    }

    @Test
    void should_fail_when_model_output_is_not_json() {
        FileEditPlanGenerator generator = new FileEditPlanGenerator(new RecordingModelProviderClient("edit Calculator.java"));

        assertThatThrownBy(() -> generator.generateEdits(
                task("/agent fix failing add test"),
                fixPlan("src/main/java/example/Calculator.java"),
                List.of(new FileEditContext("src/main/java/example/Calculator.java", "class Calculator {}"))
        ))
                .isInstanceOf(FileEditPlanGenerationException.class)
                .hasMessage("Model file edit response was not valid JSON");
    }

    @Test
    void should_fail_when_required_edit_fields_are_missing() {
        FileEditPlanGenerator generator = new FileEditPlanGenerator(new RecordingModelProviderClient("""
                {
                  "edits": [
                    {
                      "path": "src/main/java/example/Calculator.java"
                    }
                  ]
                }
                """));

        assertThatThrownBy(() -> generator.generateEdits(
                task("/agent fix failing add test"),
                fixPlan("src/main/java/example/Calculator.java"),
                List.of(new FileEditContext("src/main/java/example/Calculator.java", "class Calculator {}"))
        ))
                .isInstanceOf(FileEditPlanGenerationException.class)
                .hasMessage("Model file edit response missing required field: content");
    }

    private static FixPlan fixPlan(String targetFile) {
        return new FixPlan(
                "Fix Calculator#add",
                List.of(targetFile),
                List.of("Update the add implementation"),
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
