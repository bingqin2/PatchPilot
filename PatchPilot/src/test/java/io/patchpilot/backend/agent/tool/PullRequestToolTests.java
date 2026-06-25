package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.dashboard.config.DashboardProperties;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PullRequestToolTests {

    @Test
    void should_create_pull_request_from_task_context() {
        RecordingGitHubPullRequestClient client = new RecordingGitHubPullRequestClient();
        DashboardProperties dashboardProperties = new DashboardProperties();
        dashboardProperties.setBaseUrl("https://dashboard.example.test");
        PullRequestTool tool = new PullRequestTool(client, dashboardProperties);

        PullRequestResult result = tool.createPullRequest(task(), "patchpilot/task-123", testRun(0, 2_345));

        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(client.command().owner()).isEqualTo("octocat");
        assertThat(client.command().repository()).isEqualTo("hello-world");
        assertThat(client.command().head()).isEqualTo("octocat:patchpilot/task-123");
        assertThat(client.command().base()).isEqualTo("main");
        assertThat(client.command().title()).isEqualTo("PatchPilot fix for #42");
        assertThat(client.command().body()).contains("Fixes #42");
        assertThat(client.command().body()).contains("Task: `task-123`");
        assertThat(client.command().body()).contains("Dashboard: https://dashboard.example.test/tasks/task-123");
        assertThat(client.command().body()).contains("Triggered by: alice");
        assertThat(client.command().body()).contains("Branch: patchpilot/task-123");
        assertThat(client.command().body()).contains("Language: `java`");
        assertThat(client.command().body()).contains("Build system: `maven`");
        assertThat(client.command().body()).contains("Verification: `./mvnw test`");
        assertThat(client.command().body()).contains("Detection reason: pom.xml detected with mvnw wrapper");
        assertThat(client.command().body()).contains("Verification result: `./mvnw test` exited `0` in `2345 ms`.");
        assertThat(client.command().body())
                .contains("PatchPilot opened this PR only after adapter-selected verification passed.");
        assertThat(client.command().body())
                .contains("PatchPilot does not auto-merge Pull Requests.");
    }

    @Test
    void should_omit_dashboard_link_when_dashboard_base_url_is_missing() {
        RecordingGitHubPullRequestClient client = new RecordingGitHubPullRequestClient();
        PullRequestTool tool = new PullRequestTool(client, new DashboardProperties());

        tool.createPullRequest(task(), "patchpilot/task-123", testRun(0, 2_345));

        assertThat(client.command().body()).doesNotContain("Dashboard:");
    }

    @Test
    void should_include_risk_review_approval_evidence_when_task_resumed_after_review() {
        RecordingGitHubPullRequestClient client = new RecordingGitHubPullRequestClient();
        PullRequestTool tool = new PullRequestTool(client);

        tool.createPullRequest(approvedReviewTask(), "patchpilot/task-123", testRun(0, 2_345));

        assertThat(client.command().body()).contains("Risk review approval:");
        assertThat(client.command().body()).contains("Review approved by: `release-captain`");
        assertThat(client.command().body()).contains("Review approved at: `2026-06-18T01:02:03Z`");
        assertThat(client.command().body())
                .contains("Review approval reason: Reviewed generated diff and accepted docs-only change");
        assertThat(client.command().body())
                .contains("PatchPilot resumed this task only after an allowed operator approved the generated diff risk review.");
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
        ).withAdapterMetadata(
                "java",
                "maven",
                "./mvnw test",
                "pom.xml detected with mvnw wrapper"
        );
    }

    private static FixTaskVo approvedReviewTask() {
        return task().withRiskReviewApproval(
                Instant.parse("2026-06-18T01:02:03Z"),
                "release-captain",
                "Reviewed generated diff and accepted docs-only change"
        );
    }

    private static FixTaskTestRunVo testRun(int exitCode, long durationMs) {
        return new FixTaskTestRunVo(
                "test-run-123",
                "task-123",
                "./mvnw test",
                exitCode,
                "tests passed",
                Instant.parse("2026-06-18T00:01:00Z"),
                Instant.parse("2026-06-18T00:01:02Z"),
                durationMs
        );
    }

    private static final class RecordingGitHubPullRequestClient extends GitHubPullRequestClient {

        private CreatePullRequestCommand command;

        private RecordingGitHubPullRequestClient() {
            super(new GitHubProperties());
        }

        @Override
        public PullRequestResult createPullRequest(CreatePullRequestCommand command) {
            this.command = command;
            return new PullRequestResult("https://github.com/octocat/hello-world/pull/7");
        }

        private CreatePullRequestCommand command() {
            return command;
        }
    }
}
