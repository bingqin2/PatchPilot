package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.tool.FileReadTool;
import io.patchpilot.backend.agent.workflow.domain.FileEditContext;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlan;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.agent.workflow.domain.PatchReview;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewDecision;
import io.patchpilot.backend.agent.workflow.domain.ProposedFileEdit;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlannedPatchWorkflowTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_replace_file_when_target_is_authorized_by_fix_plan() throws Exception {
        RecordingFileEditPlanGenerator editPlanGenerator = new RecordingFileEditPlanGenerator(FileEditPlan.empty());
        RecordingPatchReviewGenerator reviewGenerator = new RecordingPatchReviewGenerator(approvedReview());
        RecordingFixTaskPatchReviewService patchReviewService = new RecordingFixTaskPatchReviewService();
        PlannedPatchWorkflow workflow = workflow(editPlanGenerator, reviewGenerator, patchReviewService);

        PatchWorkflowResult result = workflow.apply(
                task("/agent fix replace src/main/App.java class App {}"),
                repositoryDir,
                plan("src/main/App.java")
        );

        assertThat(result.patchApplied()).isTrue();
        assertThat(result.summary()).isEqualTo("Replaced src/main/App.java from planned instruction");
        assertThat(Files.readString(repositoryDir.resolve("src/main/App.java"))).isEqualTo("class App {}");
        assertThat(editPlanGenerator.called()).isFalse();
        assertThat(reviewGenerator.called()).isFalse();
        assertThat(patchReviewService.patchReviews()).isEmpty();
    }

    @Test
    void should_apply_model_generated_file_edit_when_comment_has_no_replace_instruction() throws Exception {
        Files.createDirectories(repositoryDir.resolve("src/main"));
        Files.writeString(repositoryDir.resolve("src/main/App.java"), "class App { int add(int a, int b) { return 0; } }\n");
        RecordingFileEditPlanGenerator editPlanGenerator = new RecordingFileEditPlanGenerator(new FileEditPlan(List.of(
                new ProposedFileEdit(
                        "src/main/App.java",
                        "class App { int add(int a, int b) { return a + b; } }\n",
                        "Use the operands from the issue."
                )
        )));
        RecordingPatchReviewGenerator reviewGenerator = new RecordingPatchReviewGenerator(approvedReview());
        RecordingFixTaskPatchReviewService patchReviewService = new RecordingFixTaskPatchReviewService();
        PlannedPatchWorkflow workflow = workflow(editPlanGenerator, reviewGenerator, patchReviewService);

        PatchWorkflowResult result = workflow.apply(task("/agent fix"), repositoryDir, plan("src/main/App.java"));

        assertThat(result.patchApplied()).isTrue();
        assertThat(result.summary()).isEqualTo("Applied 1 model-generated file edit after review APPROVE: src/main/App.java");
        assertThat(Files.readString(repositoryDir.resolve("src/main/App.java"))).contains("return a + b");
        assertThat(editPlanGenerator.called()).isTrue();
        assertThat(reviewGenerator.called()).isTrue();
        assertThat(reviewGenerator.edits()).containsExactly(new ProposedFileEdit(
                "src/main/App.java",
                "class App { int add(int a, int b) { return a + b; } }\n",
                "Use the operands from the issue."
        ));
        assertThat(editPlanGenerator.fileContexts()).containsExactly(new FileEditContext(
                "src/main/App.java",
                "class App { int add(int a, int b) { return 0; } }\n"
        ));
        assertThat(patchReviewService.patchReviews())
                .singleElement()
                .satisfies(review -> {
                    assertThat(review.taskId()).isEqualTo("task-123");
                    assertThat(review.decision()).isEqualTo("APPROVE");
                    assertThat(review.reason()).isEqualTo("The edit matches the fix plan.");
                    assertThat(review.confidence()).isEqualTo("HIGH");
                    assertThat(review.requiredFollowUp()).isEqualTo("Run verification.");
                    assertThat(review.editedFiles()).containsExactly("src/main/App.java");
                });
    }

    @Test
    void should_reject_model_generated_file_edit_when_review_declines_it() throws Exception {
        Files.createDirectories(repositoryDir.resolve("src/main"));
        Files.writeString(repositoryDir.resolve("src/main/App.java"), "class App { int add(int a, int b) { return 0; } }\n");
        RecordingFileEditPlanGenerator editPlanGenerator = new RecordingFileEditPlanGenerator(new FileEditPlan(List.of(
                new ProposedFileEdit(
                        "src/main/App.java",
                        "class App { int add(int a, int b) { return 999; } }\n",
                        "Incorrectly return a constant."
                )
        )));
        RecordingPatchReviewGenerator reviewGenerator = new RecordingPatchReviewGenerator(new PatchReview(
                PatchReviewDecision.REJECT,
                "The edit does not solve the issue because it returns a constant.",
                "HIGH",
                "Generate a focused edit that returns a + b."
        ));
        RecordingFixTaskPatchReviewService patchReviewService = new RecordingFixTaskPatchReviewService();
        PlannedPatchWorkflow workflow = workflow(editPlanGenerator, reviewGenerator, patchReviewService);

        assertThatThrownBy(() -> workflow.apply(task("/agent fix"), repositoryDir, plan("src/main/App.java")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Model patch review rejected generated edits: The edit does not solve the issue because it returns a constant.");

        assertThat(Files.readString(repositoryDir.resolve("src/main/App.java"))).contains("return 0");
        assertThat(reviewGenerator.called()).isTrue();
        assertThat(patchReviewService.patchReviews())
                .singleElement()
                .satisfies(review -> {
                    assertThat(review.decision()).isEqualTo("REJECT");
                    assertThat(review.reason()).contains("returns a constant");
                    assertThat(review.requiredFollowUp()).isEqualTo("Generate a focused edit that returns a + b.");
                    assertThat(review.editedFiles()).containsExactly("src/main/App.java");
                });
    }

    @Test
    void should_reject_replacement_for_file_not_in_fix_plan() {
        PlannedPatchWorkflow workflow = workflow(new RecordingFileEditPlanGenerator(FileEditPlan.empty()), approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(
                task("/agent fix replace src/main/App.java class App {}"),
                repositoryDir,
                plan("src/main/Other.java")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Replacement target is not listed in fix plan: src/main/App.java");
    }

    @Test
    void should_reject_sensitive_replacement_target_before_writing() {
        PlannedPatchWorkflow workflow = workflow(new RecordingFileEditPlanGenerator(FileEditPlan.empty()), approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(
                task("/agent fix replace .npmrc registry=https://npm.pkg.github.com/"),
                repositoryDir,
                plan(".npmrc")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Replacement target is sensitive and cannot be modified: .npmrc");

        assertThat(repositoryDir.resolve(".npmrc")).doesNotExist();
    }

    @Test
    void should_reject_unsafe_paths_through_workspace_guard() {
        PlannedPatchWorkflow workflow = workflow(new RecordingFileEditPlanGenerator(FileEditPlan.empty()), approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(
                task("/agent fix replace ../outside.md bad"),
                repositoryDir,
                plan("../outside.md")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path escapes workspace: ../outside.md");
    }

    @Test
    void should_reject_model_edit_for_file_not_in_fix_plan() throws Exception {
        Files.createDirectories(repositoryDir.resolve("src/main"));
        Files.writeString(repositoryDir.resolve("src/main/Other.java"), "class Other {}\n");
        RecordingFileEditPlanGenerator editPlanGenerator = new RecordingFileEditPlanGenerator(new FileEditPlan(List.of(
                new ProposedFileEdit("src/main/App.java", "class App {}", "Change an unapproved file.")
        )));
        PlannedPatchWorkflow workflow = workflow(editPlanGenerator, approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(task("/agent fix"), repositoryDir, plan("src/main/Other.java")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Model edit target is not listed in fix plan: src/main/App.java");
    }

    @Test
    void should_reject_sensitive_fix_plan_target_before_model_editing() {
        PlannedPatchWorkflow workflow = workflow(new RecordingFileEditPlanGenerator(FileEditPlan.empty()), approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(task("/agent fix"), repositoryDir, plan(".env")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fix plan target is sensitive and cannot be modified: .env");
    }

    @Test
    void should_reject_sensitive_package_manager_config_before_model_editing() {
        PlannedPatchWorkflow workflow = workflow(new RecordingFileEditPlanGenerator(FileEditPlan.empty()), approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(task("/agent fix"), repositoryDir, plan(".npmrc")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fix plan target is sensitive and cannot be modified: .npmrc");
    }

    @Test
    void should_reject_git_metadata_before_model_editing() {
        PlannedPatchWorkflow workflow = workflow(new RecordingFileEditPlanGenerator(FileEditPlan.empty()), approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(task("/agent fix"), repositoryDir, plan(".git/config")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fix plan target is sensitive and cannot be modified: .git/config");
    }

    @Test
    void should_reject_model_edit_with_blank_content() throws Exception {
        Files.createDirectories(repositoryDir.resolve("src/main"));
        Files.writeString(repositoryDir.resolve("src/main/App.java"), "class App {}\n");
        RecordingFileEditPlanGenerator editPlanGenerator = new RecordingFileEditPlanGenerator(new FileEditPlan(List.of(
                new ProposedFileEdit("src/main/App.java", "  ", "Empty rewrite would be destructive.")
        )));
        PlannedPatchWorkflow workflow = workflow(editPlanGenerator, approvedReviewGenerator());

        assertThatThrownBy(() -> workflow.apply(task("/agent fix"), repositoryDir, plan("src/main/App.java")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Model edit content must not be blank: src/main/App.java");
    }

    private PlannedPatchWorkflow workflow(
            RecordingFileEditPlanGenerator editPlanGenerator,
            RecordingPatchReviewGenerator reviewGenerator
    ) {
        return workflow(editPlanGenerator, reviewGenerator, new RecordingFixTaskPatchReviewService());
    }

    private PlannedPatchWorkflow workflow(
            RecordingFileEditPlanGenerator editPlanGenerator,
            RecordingPatchReviewGenerator reviewGenerator,
            RecordingFixTaskPatchReviewService patchReviewService
    ) {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        WorkspacePathResolver pathResolver = new WorkspacePathResolver(properties);
        return new PlannedPatchWorkflow(
                new FileWriteTool(pathResolver),
                new FileReadTool(pathResolver),
                editPlanGenerator,
                reviewGenerator,
                patchReviewService,
                new GeneratedDiffSafetyPolicy()
        );
    }

    private static FixPlan plan(String targetFile) {
        return new FixPlan(
                "Replace a file",
                List.of(targetFile),
                List.of("Replace " + targetFile),
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

    private static PatchReview approvedReview() {
        return new PatchReview(
                PatchReviewDecision.APPROVE,
                "The edit matches the fix plan.",
                "HIGH",
                "Run verification."
        );
    }

    private static RecordingPatchReviewGenerator approvedReviewGenerator() {
        return new RecordingPatchReviewGenerator(approvedReview());
    }

    private static final class RecordingFileEditPlanGenerator extends FileEditPlanGenerator {

        private final FileEditPlan fileEditPlan;
        private boolean called;
        private List<FileEditContext> fileContexts;

        private RecordingFileEditPlanGenerator(FileEditPlan fileEditPlan) {
            super(request -> {
                throw new AssertionError("Model client should not be called by this test double");
            });
            this.fileEditPlan = fileEditPlan;
        }

        @Override
        public FileEditPlan generateEdits(FixTaskVo task, FixPlan fixPlan, List<FileEditContext> fileContexts) {
            this.called = true;
            this.fileContexts = fileContexts;
            return fileEditPlan;
        }

        private boolean called() {
            return called;
        }

        private List<FileEditContext> fileContexts() {
            return fileContexts;
        }
    }

    private static final class RecordingPatchReviewGenerator extends PatchReviewGenerator {

        private final PatchReview review;
        private boolean called;
        private List<ProposedFileEdit> edits;

        private RecordingPatchReviewGenerator(PatchReview review) {
            super(request -> {
                throw new AssertionError("Model client should not be called by this test double");
            });
            this.review = review;
        }

        @Override
        public PatchReview review(
                FixTaskVo task,
                FixPlan fixPlan,
                List<FileEditContext> fileContexts,
                List<ProposedFileEdit> edits
        ) {
            this.called = true;
            this.edits = edits;
            return review;
        }

        private boolean called() {
            return called;
        }

        private List<ProposedFileEdit> edits() {
            return edits;
        }
    }

    private static final class RecordingFixTaskPatchReviewService implements FixTaskPatchReviewService {

        private final List<FixTaskPatchReviewVo> patchReviews = new ArrayList<>();

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
            FixTaskPatchReviewVo patchReview = new FixTaskPatchReviewVo(
                    "patch-review-" + (patchReviews.size() + 1),
                    taskId,
                    decision,
                    reason,
                    confidence,
                    requiredFollowUp,
                    editedFiles,
                    createdAt
            );
            patchReviews.add(patchReview);
            return patchReview;
        }

        @Override
        public Optional<FixTaskPatchReviewVo> findLatestPatchReview(String taskId) {
            return patchReviews.stream()
                    .filter(review -> review.taskId().equals(taskId))
                    .reduce((left, right) -> right);
        }

        private List<FixTaskPatchReviewVo> patchReviews() {
            return patchReviews;
        }
    }
}
