package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.workspace.WorkspacePathResolver;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanDrivenPatchWorkflowTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_generate_fix_plan_before_applying_planned_patch() {
        RecordingFixPlanGenerator fixPlanGenerator = new RecordingFixPlanGenerator(plan("src/main/App.java"));
        RecordingPlannedPatchWorkflow plannedPatchWorkflow = new RecordingPlannedPatchWorkflow(
                new PatchWorkflowResult(true, "Replaced src/main/App.java from planned instruction")
        );
        PlanDrivenPatchWorkflow workflow = new PlanDrivenPatchWorkflow(fixPlanGenerator, plannedPatchWorkflow);
        FixTaskVo task = task("/agent fix replace src/main/App.java class App {}");
        Path repositoryDir = Path.of("/tmp/repo");

        PatchWorkflowResult result = workflow.apply(task, repositoryDir);

        assertThat(result.patchApplied()).isTrue();
        assertThat(result.summary()).isEqualTo("Replaced src/main/App.java from planned instruction");
        assertThat(fixPlanGenerator.task()).isEqualTo(task);
        assertThat(plannedPatchWorkflow.task()).isEqualTo(task);
        assertThat(plannedPatchWorkflow.repositoryDir()).isEqualTo(repositoryDir);
        assertThat(plannedPatchWorkflow.fixPlan()).isEqualTo(fixPlanGenerator.fixPlan());
        assertThat(fixPlanGenerator.callOrder()).isLessThan(plannedPatchWorkflow.callOrder());
    }

    @Test
    void should_write_file_when_generated_plan_authorizes_replacement_target() throws Exception {
        RecordingFixPlanGenerator fixPlanGenerator = new RecordingFixPlanGenerator(plan("src/main/App.java"));
        PlanDrivenPatchWorkflow workflow = new PlanDrivenPatchWorkflow(fixPlanGenerator, plannedPatchWorkflow());

        PatchWorkflowResult result = workflow.apply(
                task("/agent fix replace src/main/App.java class App {}"),
                repositoryDir
        );

        assertThat(result.patchApplied()).isTrue();
        assertThat(result.summary()).isEqualTo("Replaced src/main/App.java from planned instruction");
        assertThat(Files.readString(repositoryDir.resolve("src/main/App.java"))).isEqualTo("class App {}");
        assertThat(fixPlanGenerator.task().triggerComment()).isEqualTo("/agent fix replace src/main/App.java class App {}");
    }

    private PlannedPatchWorkflow plannedPatchWorkflow() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        return new PlannedPatchWorkflow(new FileWriteTool(new WorkspacePathResolver(properties)));
    }

    private static FixPlan plan(String targetFile) {
        return new FixPlan(
                "Replace a file",
                List.of(targetFile),
                List.of("Apply replacement"),
                "LOW"
        );
    }

    private static FixTaskVo task(String triggerComment) {
        return new FixTaskVo(
                "task-123",
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                triggerComment,
                "delivery-123",
                98765,
                FixTaskStatus.RUNNING,
                null,
                Instant.parse("2026-06-18T00:00:00Z")
        );
    }

    private static final class RecordingFixPlanGenerator extends FixPlanGenerator {

        private final FixPlan fixPlan;
        private FixTaskVo task;
        private int callOrder;

        private RecordingFixPlanGenerator(FixPlan fixPlan) {
            super(request -> {
                throw new AssertionError("Model client should not be called by this test double");
            });
            this.fixPlan = fixPlan;
        }

        @Override
        public FixPlan generatePlan(FixTaskVo task) {
            this.task = task;
            this.callOrder = CallOrder.next();
            return fixPlan;
        }

        private FixTaskVo task() {
            return task;
        }

        private FixPlan fixPlan() {
            return fixPlan;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class RecordingPlannedPatchWorkflow extends PlannedPatchWorkflow {

        private final PatchWorkflowResult result;
        private FixTaskVo task;
        private Path repositoryDir;
        private FixPlan fixPlan;
        private int callOrder;

        private RecordingPlannedPatchWorkflow(PatchWorkflowResult result) {
            super(null);
            this.result = result;
        }

        @Override
        public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir, FixPlan fixPlan) {
            this.task = task;
            this.repositoryDir = repositoryDir;
            this.fixPlan = fixPlan;
            this.callOrder = CallOrder.next();
            return result;
        }

        private FixTaskVo task() {
            return task;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private FixPlan fixPlan() {
            return fixPlan;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class CallOrder {

        private static int next;

        private static int next() {
            next += 1;
            return next;
        }
    }
}
