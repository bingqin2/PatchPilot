package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.client.domain.UpdateIssueCommentCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.PatchReviewFailureClassifier;
import io.patchpilot.backend.task.service.TaskFailureFeedback;
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
        return update(task, failedHeadline(task.failureReason()), FixTaskStatus.FAILED, null, task.failureReason());
    }

    public Optional<IssueCommentResult> updatePendingReview(FixTaskVo task) {
        return update(
                task,
                "PatchPilot paused this task for human review.",
                FixTaskStatus.PENDING_REVIEW,
                null,
                task.failureReason()
        );
    }

    public Optional<IssueCommentResult> updateActiveTaskExists(FixTaskVo task) {
        return update(task, "PatchPilot is already working on this issue.", task.status(), task.pullRequestUrl(),
                task.failureReason());
    }

    public IssueCommentResult commentRejected(
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason,
            String category
    ) {
        return gitHubIssueCommentClient.createIssueComment(new CreateIssueCommentCommand(
                repositoryOwner,
                repositoryName,
                issueNumber,
                rejectionBody(repositoryOwner, repositoryName, issueNumber, triggerUser, reason, category)
        ));
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
                failedHeadline(failureReason),
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
        if (PatchReviewFailureClassifier.isPatchReviewRejection(failureReason)) {
            body.append("Review gate: ").append(PatchReviewFailureClassifier.REVIEW_GATE).append("\n");
            body.append(PatchReviewFailureClassifier.STATUS_COMMENT_RECOVERY).append("\n");
        }
        if (StringUtils.hasText(failureReason)) {
            TaskFailureFeedback feedback = TaskFailureFeedback.from(failureReason);
            body.append("Failure category: ").append(feedback.category()).append("\n");
            body.append("Next action: ").append(feedback.nextAction()).append("\n");
            body.append("Reason: ").append(feedback.safeReason()).append("\n");
        }
        return body.toString();
    }

    private static String failedHeadline(String failureReason) {
        if (PatchReviewFailureClassifier.isPatchReviewRejection(failureReason)) {
            return "PatchPilot blocked the generated patch during review.";
        }
        return "PatchPilot failed the task.";
    }

    private static String rejectionBody(
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String reason,
            String category
    ) {
        StringBuilder body = new StringBuilder();
        body.append("PatchPilot did not start a task for this request.\n\n");
        body.append("Status: REJECTED\n");
        body.append("Repository: ").append(repositoryOwner).append("/").append(repositoryName).append("\n");
        body.append("Issue: #").append(issueNumber).append("\n");
        body.append("Triggered by: ").append(triggerUser).append("\n");
        if (StringUtils.hasText(category)) {
            body.append("Category: ").append(category).append("\n");
        }
        if (StringUtils.hasText(reason)) {
            body.append("Reason: ").append(reason).append("\n");
        }
        body.append("Next action: ").append(rejectionNextAction(category)).append("\n");
        body.append("\n");
        body.append("No repository changes, commands, tests, commits, or pull requests were attempted. ");
        body.append("Update the request so it is specific, safe, authorized, and within rate limits, then comment `/agent fix ...` again.");
        return body.toString();
    }

    private static String rejectionNextAction(String category) {
        if (!StringUtils.hasText(category)) {
            return "Update the request so it is specific, safe, authorized, and within rate limits.";
        }
        return switch (category) {
            case "EMPTY_COMMAND", "UNSUPPORTED_COMMAND" ->
                    "Use `/agent fix <specific maintenance request>` on a GitHub issue.";
            case "NOT_ACTIONABLE", "MODEL_NEEDS_CLARIFICATION", "MODEL_REJECTED" ->
                    "Describe the concrete bug, failing test, file path, or expected code change.";
            case "DANGEROUS_INSTRUCTION" ->
                    "Remove destructive or secret-related instructions and ask for a specific, safe code change.";
            case "TRIGGER_USER_NOT_ALLOWED" ->
                    "Ask the PatchPilot operator to add this GitHub user to the trigger allowlist, or have an allowed maintainer retry.";
            case "REPOSITORY_NOT_ALLOWED" ->
                    "Ask the PatchPilot operator to add this repository to the allowlist, or retry in an allowed repository.";
            case "RATE_LIMITED" ->
                    "Wait for the configured rate-limit window to reset before retrying.";
            case "ABUSE_QUARANTINED" ->
                    "Ask the PatchPilot operator to inspect or release the active trigger quarantine before retrying.";
            case "MODEL_CLASSIFICATION_FAILED" ->
                    "Retry after the model provider is healthy, or make the request more explicit so deterministic checks can accept it.";
            default ->
                    "Update the request so it is specific, safe, authorized, and within rate limits.";
        };
    }
}
