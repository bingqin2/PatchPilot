package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PullRequestToolTests {

    @Test
    void should_create_pull_request_from_task_context() {
        RecordingGitHubPullRequestClient client = new RecordingGitHubPullRequestClient();
        PullRequestTool tool = new PullRequestTool(client);

        PullRequestResult result = tool.createPullRequest(task(), "patchpilot/task-123");

        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(client.command().owner()).isEqualTo("octocat");
        assertThat(client.command().repository()).isEqualTo("hello-world");
        assertThat(client.command().head()).isEqualTo("octocat:patchpilot/task-123");
        assertThat(client.command().base()).isEqualTo("main");
        assertThat(client.command().title()).isEqualTo("PatchPilot fix for #42");
        assertThat(client.command().body()).contains("Fixes #42");
        assertThat(client.command().body()).contains("Task: `task-123`");
        assertThat(client.command().body()).contains("Triggered by: alice");
        assertThat(client.command().body()).contains("Branch: patchpilot/task-123");
        assertThat(client.command().body()).contains("Language: `java`");
        assertThat(client.command().body()).contains("Build system: `maven`");
        assertThat(client.command().body()).contains("Verification: `./mvnw test`");
        assertThat(client.command().body()).contains("Detection reason: pom.xml detected with mvnw wrapper");
        assertThat(client.command().body())
                .contains("PatchPilot opened this PR only after adapter-selected verification passed.");
        assertThat(client.command().body())
                .contains("PatchPilot does not auto-merge Pull Requests.");
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
