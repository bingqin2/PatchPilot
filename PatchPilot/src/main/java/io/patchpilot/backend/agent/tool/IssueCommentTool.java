package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.stereotype.Component;

@Component
public class IssueCommentTool {

    private final GitHubIssueCommentClient gitHubIssueCommentClient;

    public IssueCommentTool(GitHubIssueCommentClient gitHubIssueCommentClient) {
        this.gitHubIssueCommentClient = gitHubIssueCommentClient;
    }

    public IssueCommentResult commentCompleted(FixTaskVo task, String pullRequestUrl) {
        return gitHubIssueCommentClient.createIssueComment(command(task, completedBody(task, pullRequestUrl)));
    }

    public IssueCommentResult commentFailed(FixTaskVo task, String failureReason) {
        return gitHubIssueCommentClient.createIssueComment(command(task, failedBody(task, failureReason)));
    }

    private static CreateIssueCommentCommand command(FixTaskVo task, String body) {
        return new CreateIssueCommentCommand(
                task.repositoryOwner(),
                task.repositoryName(),
                task.issueNumber(),
                body
        );
    }

    private static String completedBody(FixTaskVo task, String pullRequestUrl) {
        return """
                PatchPilot completed the task.

                PR: %s
                Task: %s
                """.formatted(pullRequestUrl, task.id());
    }

    private static String failedBody(FixTaskVo task, String failureReason) {
        return """
                PatchPilot failed the task.

                Reason: %s
                Task: %s
                """.formatted(failureReason, task.id());
    }
}
