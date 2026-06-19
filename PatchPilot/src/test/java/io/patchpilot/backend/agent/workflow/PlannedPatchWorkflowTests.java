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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlannedPatchWorkflowTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_replace_file_when_target_is_authorized_by_fix_plan() throws Exception {
        PlannedPatchWorkflow workflow = workflow();

        PatchWorkflowResult result = workflow.apply(
                task("/agent fix replace src/main/App.java class App {}"),
                repositoryDir,
                plan("src/main/App.java")
        );

        assertThat(result.patchApplied()).isTrue();
        assertThat(result.summary()).isEqualTo("Replaced src/main/App.java from planned instruction");
        assertThat(Files.readString(repositoryDir.resolve("src/main/App.java"))).isEqualTo("class App {}");
    }

    @Test
    void should_skip_when_comment_has_no_replace_instruction() {
        PlannedPatchWorkflow workflow = workflow();

        PatchWorkflowResult result = workflow.apply(task("/agent fix"), repositoryDir, plan("src/main/App.java"));

        assertThat(result.patchApplied()).isFalse();
        assertThat(result.summary()).isEqualTo("No planned replace instruction found");
    }

    @Test
    void should_reject_replacement_for_file_not_in_fix_plan() {
        PlannedPatchWorkflow workflow = workflow();

        assertThatThrownBy(() -> workflow.apply(
                task("/agent fix replace src/main/App.java class App {}"),
                repositoryDir,
                plan("src/main/Other.java")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Replacement target is not listed in fix plan: src/main/App.java");
    }

    @Test
    void should_reject_unsafe_paths_through_workspace_guard() {
        PlannedPatchWorkflow workflow = workflow();

        assertThatThrownBy(() -> workflow.apply(
                task("/agent fix replace ../outside.md bad"),
                repositoryDir,
                plan("../outside.md")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path escapes workspace: ../outside.md");
    }

    private PlannedPatchWorkflow workflow() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        return new PlannedPatchWorkflow(new FileWriteTool(new WorkspacePathResolver(properties)));
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
}
