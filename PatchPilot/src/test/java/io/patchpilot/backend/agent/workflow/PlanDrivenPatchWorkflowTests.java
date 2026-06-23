package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.tool.FileReadTool;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlan;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.agent.workflow.domain.PatchReview;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewDecision;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.safety.GeneratedDiffSafetyPolicy;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskPatchReviewService;
import io.patchpilot.backend.workspace.WorkspacePathResolver;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    void should_pass_issue_context_to_fix_plan_generator() {
        RecordingFixPlanGenerator fixPlanGenerator = new RecordingFixPlanGenerator(plan("src/main/App.java"));
        RecordingPlannedPatchWorkflow plannedPatchWorkflow = new RecordingPlannedPatchWorkflow(
                new PatchWorkflowResult(true, "Replaced src/main/App.java from planned instruction")
        );
        PlanDrivenPatchWorkflow workflow = new PlanDrivenPatchWorkflow(fixPlanGenerator, plannedPatchWorkflow);
        GitHubIssueContext issueContext = new GitHubIssueContext(
                "Calculator add returns wrong value",
                "The issue body explains the failing expectation.",
                "https://github.com/octocat/hello-world/issues/42",
                List.of()
        );

        workflow.apply(task("/agent fix failing add test"), Path.of("/tmp/repo"), issueContext);

        assertThat(fixPlanGenerator.issueContext()).isEqualTo(issueContext);
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
        WorkspacePathResolver pathResolver = new WorkspacePathResolver(properties);
        return new PlannedPatchWorkflow(
                new FileWriteTool(pathResolver),
                new FileReadTool(pathResolver),
                new RecordingFileEditPlanGenerator(FileEditPlan.empty()),
                new RecordingPatchReviewGenerator(),
                new NoOpFixTaskPatchReviewService(),
                new GeneratedDiffSafetyPolicy()
        );
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
        private GitHubIssueContext issueContext;
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

        @Override
        public FixPlan generatePlan(FixTaskVo task, GitHubIssueContext issueContext) {
            this.task = task;
            this.issueContext = issueContext;
            this.callOrder = CallOrder.next();
            return fixPlan;
        }

        private FixTaskVo task() {
            return task;
        }

        private FixPlan fixPlan() {
            return fixPlan;
        }

        private GitHubIssueContext issueContext() {
            return issueContext;
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
            super(null, null, null, null, new NoOpFixTaskPatchReviewService(), new GeneratedDiffSafetyPolicy());
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

    private static final class RecordingFileEditPlanGenerator extends FileEditPlanGenerator {

        private RecordingFileEditPlanGenerator(FileEditPlan fileEditPlan) {
            super(request -> {
                throw new AssertionError("Model client should not be called by this test double");
            });
        }
    }

    private static final class RecordingPatchReviewGenerator extends PatchReviewGenerator {

        private RecordingPatchReviewGenerator() {
            super(request -> {
                throw new AssertionError("Model client should not be called by this test double");
            });
        }

        @Override
        public PatchReview review(
                FixTaskVo task,
                FixPlan fixPlan,
                List<io.patchpilot.backend.agent.workflow.domain.FileEditContext> fileContexts,
                List<io.patchpilot.backend.agent.workflow.domain.ProposedFileEdit> edits
        ) {
            return new PatchReview(PatchReviewDecision.APPROVE, "ok", "HIGH", "Run verification.");
        }
    }

    private static final class NoOpFixTaskPatchReviewService implements FixTaskPatchReviewService {

        @Override
        public FixTaskPatchReviewVo recordPatchReview(
                String taskId,
                String decision,
                String reason,
                String confidence,
                String requiredFollowUp,
                List<String> editedFiles,
                Instant createdAt
        ) {
            return new FixTaskPatchReviewVo(
                    "patch-review-test",
                    taskId,
                    decision,
                    reason,
                    confidence,
                    requiredFollowUp,
                    editedFiles,
                    createdAt
            );
        }

        @Override
        public Optional<FixTaskPatchReviewVo> findLatestPatchReview(String taskId) {
            return Optional.empty();
        }
    }
}
