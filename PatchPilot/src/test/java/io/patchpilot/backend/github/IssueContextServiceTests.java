package io.patchpilot.backend.github;

import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextComment;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueContextServiceTests {

    @Test
    void should_load_issue_context_for_task_with_bounded_comment_limit() {
        RecordingIssueContextClient client = new RecordingIssueContextClient(new GitHubIssueContext(
                "Fix the calculator add bug",
                "The issue body includes the failing example.",
                "https://github.com/octocat/hello-world/issues/42",
                List.of(new GitHubIssueContextComment(
                        1001,
                        "alice",
                        "The failing test is CalculatorTest#addsNumbers.",
                        "2026-06-20T01:00:00Z",
                        "https://github.com/octocat/hello-world/issues/42#issuecomment-1001"
                ))
        ));
        IssueContextService service = new IssueContextService(client);

        GitHubIssueContext context = service.loadIssueContext(task());

        assertThat(context.title()).isEqualTo("Fix the calculator add bug");
        assertThat(context.comments()).hasSize(1);
        assertThat(client.command().owner()).isEqualTo("octocat");
        assertThat(client.command().repository()).isEqualTo("hello-world");
        assertThat(client.command().issueNumber()).isEqualTo(42);
        assertThat(client.command().commentLimit()).isEqualTo(5);
    }

    private static FixTaskVo task() {
        return new FixTaskVo(
                "task-123",
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix failing add test",
                "delivery-123",
                98765,
                FixTaskStatus.RUNNING,
                null,
                Instant.parse("2026-06-18T00:00:00Z")
        );
    }

    private static final class RecordingIssueContextClient extends GitHubIssueContextClient {

        private final GitHubIssueContext context;
        private GetIssueContextCommand command;

        private RecordingIssueContextClient(GitHubIssueContext context) {
            super(new io.patchpilot.backend.github.config.GitHubProperties());
            this.context = context;
        }

        @Override
        public GitHubIssueContext getIssueContext(GetIssueContextCommand command) {
            this.command = command;
            return context;
        }

        private GetIssueContextCommand command() {
            return command;
        }
    }
}
