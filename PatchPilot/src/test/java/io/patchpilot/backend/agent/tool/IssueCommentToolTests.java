package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentToolTests {

    @Test
    void should_comment_completed_task_with_pull_request_url() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        IssueCommentResult result = tool.commentCompleted(task(), "https://github.com/octocat/hello-world/pull/7");

        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(client.command().owner()).isEqualTo("octocat");
        assertThat(client.command().repository()).isEqualTo("hello-world");
        assertThat(client.command().issueNumber()).isEqualTo(42);
        assertThat(client.command().body()).contains("PatchPilot completed the task.");
        assertThat(client.command().body()).contains("PR: https://github.com/octocat/hello-world/pull/7");
        assertThat(client.command().body()).contains("Task: task-123");
    }

    @Test
    void should_comment_failed_task_with_failure_reason() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        IssueCommentResult result = tool.commentFailed(task(), "maven tests failed");

        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(client.command().owner()).isEqualTo("octocat");
        assertThat(client.command().repository()).isEqualTo("hello-world");
        assertThat(client.command().issueNumber()).isEqualTo(42);
        assertThat(client.command().body()).contains("PatchPilot failed the task.");
        assertThat(client.command().body()).contains("Reason: maven tests failed");
        assertThat(client.command().body()).contains("Task: task-123");
    }

    private static FixTaskVo task() {
        return new FixTaskVo(
                "task-123",
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                "delivery-123",
                98765,
                FixTaskStatus.RUNNING,
                null,
                Instant.parse("2026-06-18T00:00:00Z")
        );
    }

    private static final class RecordingGitHubIssueCommentClient extends GitHubIssueCommentClient {

        private CreateIssueCommentCommand command;

        private RecordingGitHubIssueCommentClient() {
            super(new GitHubProperties());
        }

        @Override
        public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
            this.command = command;
            return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        private CreateIssueCommentCommand command() {
            return command;
        }
    }
}
