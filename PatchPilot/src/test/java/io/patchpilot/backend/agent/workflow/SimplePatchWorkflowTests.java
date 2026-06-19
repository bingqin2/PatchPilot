package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.workspace.WorkspacePathResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimplePatchWorkflowTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_apply_touch_instruction_as_deterministic_file_patch() throws Exception {
        SimplePatchWorkflow workflow = new SimplePatchWorkflow(new FileWriteTool(new WorkspacePathResolver()));

        PatchWorkflowResult result = workflow.apply(task("/agent fix touch docs/demo.md"), repositoryDir);

        assertThat(result.patchApplied()).isTrue();
        assertThat(result.summary()).isEqualTo("Created docs/demo.md from touch instruction");
        assertThat(Files.readString(repositoryDir.resolve("docs/demo.md"))).isEqualTo("""
                # PatchPilot generated file

                Task: task-123
                Issue: octocat/hello-world#42
                Triggered by: alice
                """);
    }

    @Test
    void should_skip_patch_when_comment_has_no_touch_instruction() {
        SimplePatchWorkflow workflow = new SimplePatchWorkflow(new FileWriteTool(new WorkspacePathResolver()));

        PatchWorkflowResult result = workflow.apply(task("/agent fix"), repositoryDir);

        assertThat(result.patchApplied()).isFalse();
        assertThat(result.summary()).isEqualTo("No deterministic patch instruction found");
    }

    @Test
    void should_reject_unsafe_touch_paths() {
        SimplePatchWorkflow workflow = new SimplePatchWorkflow(new FileWriteTool(new WorkspacePathResolver()));

        assertThatThrownBy(() -> workflow.apply(task("/agent fix touch ../outside.md"), repositoryDir))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path escapes workspace: ../outside.md");
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
