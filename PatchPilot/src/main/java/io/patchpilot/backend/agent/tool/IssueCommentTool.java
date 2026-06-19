package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.client.domain.UpdateIssueCommentCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IssueCommentTool {

    private final GitHubIssueCommentClient gitHubIssueCommentClient;

    public IssueCommentResult commentAccepted(FixTaskVo task) {
        return gitHubIssueCommentClient.createIssueComment(command(task, body(
                "PatchPilot task accepted.",
                task,
                FixTaskStatus.PENDING,
                null,
                null
        )));
    }

    public Optional<IssueCommentResult> updateRunning(FixTaskVo task) {
        return update(task, "PatchPilot is working on this task.", FixTaskStatus.RUNNING, null, null);
    }

    public Optional<IssueCommentResult> updateRunningTests(FixTaskVo task) {
        return update(task, "PatchPilot is running verification.", FixTaskStatus.RUNNING_TESTS, null, null);
    }

    public Optional<IssueCommentResult> updateCompleted(FixTaskVo task) {
        return update(task, "PatchPilot completed the task.", FixTaskStatus.COMPLETED, task.pullRequestUrl(), null);
    }

    public Optional<IssueCommentResult> updateFailed(FixTaskVo task) {
        return update(task, "PatchPilot failed the task.", FixTaskStatus.FAILED, null, task.failureReason());
    }

    public Optional<IssueCommentResult> updateActiveTaskExists(FixTaskVo task) {
        return update(task, "PatchPilot is already working on this issue.", task.status(), task.pullRequestUrl(),
                task.failureReason());
    }

    public IssueCommentResult commentCompleted(FixTaskVo task, String pullRequestUrl) {
        return updateCompleted(task).orElseGet(() -> gitHubIssueCommentClient.createIssueComment(command(task, body(
                "PatchPilot completed the task.",
                task,
                FixTaskStatus.COMPLETED,
                pullRequestUrl,
                null
        ))));
    }

    public IssueCommentResult commentFailed(FixTaskVo task, String failureReason) {
        return updateFailed(task).orElseGet(() -> gitHubIssueCommentClient.createIssueComment(command(task, body(
                "PatchPilot failed the task.",
                task,
                FixTaskStatus.FAILED,
                null,
                failureReason
        ))));
    }

    private static CreateIssueCommentCommand command(FixTaskVo task, String body) {
        return new CreateIssueCommentCommand(
                task.repositoryOwner(),
                task.repositoryName(),
                task.issueNumber(),
                body
        );
    }

    private Optional<IssueCommentResult> update(
            FixTaskVo task,
            String headline,
            FixTaskStatus status,
            String pullRequestUrl,
            String failureReason
    ) {
        if (task.statusCommentId() == null) {
            return Optional.empty();
        }
        return Optional.of(gitHubIssueCommentClient.updateIssueComment(new UpdateIssueCommentCommand(
                task.repositoryOwner(),
                task.repositoryName(),
                task.statusCommentId(),
                body(headline, task, status, pullRequestUrl, failureReason)
        )));
    }

    private static String body(
            String headline,
            FixTaskVo task,
            FixTaskStatus status,
            String pullRequestUrl,
            String failureReason
    ) {
        StringBuilder body = new StringBuilder();
        body.append(headline).append("\n\n");
        body.append("Status: ").append(status).append("\n");
        body.append("Task: ").append(task.id()).append("\n");
        body.append("Repository: ").append(task.repositoryOwner()).append("/").append(task.repositoryName()).append("\n");
        body.append("Issue: #").append(task.issueNumber()).append("\n");
        body.append("Triggered by: ").append(task.triggerUser()).append("\n");
        if (StringUtils.hasText(pullRequestUrl)) {
            body.append("PR: ").append(pullRequestUrl).append("\n");
        }
        if (StringUtils.hasText(failureReason)) {
            body.append("Reason: ").append(failureReason).append("\n");
        }
        return body.toString();
    }
}
