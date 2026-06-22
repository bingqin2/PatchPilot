package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.client.domain.UpdateIssueCommentCommand;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentToolTests {

    @Test
    void should_create_accepted_status_comment() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        IssueCommentResult result = tool.commentAccepted(task(FixTaskStatus.PENDING, null, null, null));

        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(client.createCommand().owner()).isEqualTo("octocat");
        assertThat(client.createCommand().repository()).isEqualTo("hello-world");
        assertThat(client.createCommand().issueNumber()).isEqualTo(42);
        assertThat(client.createCommand().body()).contains("PatchPilot task accepted.");
        assertThat(client.createCommand().body()).contains("Status: PENDING");
        assertThat(client.createCommand().body()).contains("Task: task-123");
        assertThat(client.createCommand().body()).contains("Repository: octocat/hello-world");
        assertThat(client.createCommand().body()).contains("Issue: #42");
        assertThat(client.createCommand().body()).contains("Triggered by: alice");
    }

    @Test
    void should_update_running_status_comment() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updateRunning(task(FixTaskStatus.RUNNING, 123L, null, null));

        assertThat(result).isPresent();
        assertThat(client.updateCommand().commentId()).isEqualTo(123);
        assertThat(client.updateCommand().body()).contains("PatchPilot is working on this task.");
        assertThat(client.updateCommand().body()).contains("Status: RUNNING");
        assertThat(client.updateCommand().body()).contains("Task: task-123");
    }

    @Test
    void should_update_running_tests_status_comment() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updateRunningTests(task(FixTaskStatus.RUNNING_TESTS, 123L, null, null));

        assertThat(result).isPresent();
        assertThat(client.updateCommand().commentId()).isEqualTo(123);
        assertThat(client.updateCommand().body()).contains("PatchPilot is running verification.");
        assertThat(client.updateCommand().body()).contains("Status: RUNNING_TESTS");
    }

    @Test
    void should_update_completed_status_comment_with_pull_request_url() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updateCompleted(task(
                FixTaskStatus.COMPLETED,
                123L,
                "https://github.com/octocat/hello-world/pull/7",
                null
        ));

        assertThat(result).isPresent();
        assertThat(client.updateCommand().commentId()).isEqualTo(123);
        assertThat(client.updateCommand().body()).contains("PatchPilot completed the task.");
        assertThat(client.updateCommand().body()).contains("Status: COMPLETED");
        assertThat(client.updateCommand().body()).contains("PR: https://github.com/octocat/hello-world/pull/7");
    }

    @Test
    void should_update_failed_status_comment_with_failure_reason() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updateFailed(task(
                FixTaskStatus.FAILED,
                123L,
                null,
                "maven tests failed"
        ));

        assertThat(result).isPresent();
        assertThat(client.updateCommand().commentId()).isEqualTo(123);
        assertThat(client.updateCommand().body()).contains("PatchPilot failed the task.");
        assertThat(client.updateCommand().body()).contains("Status: FAILED");
        assertThat(client.updateCommand().body()).contains("Reason: maven tests failed");
    }

    @Test
    void should_update_pending_review_status_comment_with_rejection_reason() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updatePendingReview(task(
                FixTaskStatus.PENDING_REVIEW,
                123L,
                null,
                "Generated diff rejected: sensitive path .env"
        ));

        assertThat(result).isPresent();
        assertThat(client.updateCommand().commentId()).isEqualTo(123);
        assertThat(client.updateCommand().body()).contains("PatchPilot paused this task for human review.");
        assertThat(client.updateCommand().body()).contains("Status: PENDING_REVIEW");
        assertThat(client.updateCommand().body()).contains("Reason: Generated diff rejected: sensitive path .env");
    }

    @Test
    void should_update_status_comment_when_active_task_already_exists() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updateActiveTaskExists(task(
                FixTaskStatus.RUNNING,
                123L,
                null,
                null
        ));

        assertThat(result).isPresent();
        assertThat(client.updateCommand().commentId()).isEqualTo(123);
        assertThat(client.updateCommand().body()).contains("PatchPilot is already working on this issue.");
        assertThat(client.updateCommand().body()).contains("Status: RUNNING");
        assertThat(client.updateCommand().body()).contains("Task: task-123");
    }

    @Test
    void should_skip_update_when_status_comment_id_is_missing() {
        RecordingGitHubIssueCommentClient client = new RecordingGitHubIssueCommentClient();
        IssueCommentTool tool = new IssueCommentTool(client);

        Optional<IssueCommentResult> result = tool.updateRunning(task(FixTaskStatus.RUNNING, null, null, null));

        assertThat(result).isEmpty();
        assertThat(client.updateCommand()).isNull();
    }

    private static FixTaskVo task(
            FixTaskStatus status,
            Long statusCommentId,
            String pullRequestUrl,
            String failureReason
    ) {
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
                status,
                failureReason,
                Instant.parse("2026-06-18T00:00:00Z"),
                pullRequestUrl,
                status == FixTaskStatus.COMPLETED ? Instant.parse("2026-06-18T00:05:00Z") : null,
                Instant.parse("2026-06-18T00:05:00Z"),
                statusCommentId,
                statusCommentId == null ? null : "https://github.com/octocat/hello-world/issues/42#issuecomment-123"
        );
    }

    private static final class RecordingGitHubIssueCommentClient extends GitHubIssueCommentClient {

        private CreateIssueCommentCommand createCommand;
        private UpdateIssueCommentCommand updateCommand;

        private RecordingGitHubIssueCommentClient() {
            super(new GitHubProperties());
        }

        @Override
        public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
            this.createCommand = command;
            return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        @Override
        public IssueCommentResult updateIssueComment(UpdateIssueCommentCommand command) {
            this.updateCommand = command;
            return new IssueCommentResult(command.commentId(), "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        private CreateIssueCommentCommand createCommand() {
            return createCommand;
        }

        private UpdateIssueCommentCommand updateCommand() {
            return updateCommand;
        }
    }
}
