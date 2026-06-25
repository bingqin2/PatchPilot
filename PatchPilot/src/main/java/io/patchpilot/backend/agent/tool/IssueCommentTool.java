package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.client.domain.UpdateIssueCommentCommand;
import io.patchpilot.backend.language.LanguageAdapterCatalogService;
import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.PatchReviewFailureClassifier;
import io.patchpilot.backend.task.service.TaskFailureFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class IssueCommentTool {

    private static final String UNSUPPORTED_REPOSITORY_CATEGORY = "UNSUPPORTED_REPOSITORY";
    private static final String UNSUPPORTED_REPOSITORY_ACTION = "Add one supported project marker and "
            + "deterministic test command, then trigger `/agent fix ...` again.";

    private final GitHubIssueCommentClient gitHubIssueCommentClient;
    private final LanguageAdapterCatalogService languageAdapterCatalogService;

    public IssueCommentTool(GitHubIssueCommentClient gitHubIssueCommentClient) {
        this(gitHubIssueCommentClient, new LanguageAdapterCatalogService());
    }

    @Autowired
    public IssueCommentTool(
            GitHubIssueCommentClient gitHubIssueCommentClient,
            LanguageAdapterCatalogService languageAdapterCatalogService
    ) {
        this.gitHubIssueCommentClient = gitHubIssueCommentClient;
        this.languageAdapterCatalogService = languageAdapterCatalogService;
    }

    public IssueCommentResult commentAccepted(FixTaskVo task) {
        return gitHubIssueCommentClient.createIssueComment(command(task, body(
                "PatchPilot task accepted.",
                task,
                FixTaskStatus.PENDING,
                null,
                null,
                null
        )));
    }

    public Optional<IssueCommentResult> updateRunning(FixTaskVo task) {
        return update(task, "PatchPilot is working on this task.", FixTaskStatus.RUNNING, null, null, null);
    }

    public Optional<IssueCommentResult> updateRunningTests(FixTaskVo task) {
        return update(task, "PatchPilot is running verification.", FixTaskStatus.RUNNING_TESTS, null, null, null);
    }

    public Optional<IssueCommentResult> updateCompleted(FixTaskVo task) {
        return updateCompleted(task, null);
    }

    public Optional<IssueCommentResult> updateCompleted(FixTaskVo task, FixTaskTestRunVo latestTestRun) {
        return update(
                task,
                "PatchPilot completed the task.",
                FixTaskStatus.COMPLETED,
                task.pullRequestUrl(),
                null,
                latestTestRun
        );
    }

    public Optional<IssueCommentResult> updateFailed(FixTaskVo task) {
        return updateFailed(task, null);
    }

    public Optional<IssueCommentResult> updateFailed(FixTaskVo task, FixTaskTestRunVo latestTestRun) {
        return update(
                task,
                failedHeadline(task.failureReason()),
                FixTaskStatus.FAILED,
                null,
                task.failureReason(),
                latestTestRun
        );
    }

    public Optional<IssueCommentResult> updatePendingReview(FixTaskVo task) {
        return updatePendingReview(task, null);
    }

    public Optional<IssueCommentResult> updatePendingReview(FixTaskVo task, FixTaskTestRunVo latestTestRun) {
        return update(
                task,
                "PatchPilot paused this task for human review.",
                FixTaskStatus.PENDING_REVIEW,
                null,
                task.failureReason(),
                latestTestRun
        );
    }

    public Optional<IssueCommentResult> updateActiveTaskExists(FixTaskVo task) {
        return update(task, "PatchPilot is already working on this issue.", task.status(), task.pullRequestUrl(),
                task.failureReason(), null);
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
                null,
                null
        ))));
    }

    public IssueCommentResult commentFailed(FixTaskVo task, String failureReason) {
        return updateFailed(task).orElseGet(() -> gitHubIssueCommentClient.createIssueComment(command(task, body(
                failedHeadline(failureReason),
                task,
                FixTaskStatus.FAILED,
                null,
                failureReason,
                null
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
            String failureReason,
            FixTaskTestRunVo latestTestRun
    ) {
        if (task.statusCommentId() == null) {
            return Optional.empty();
        }
        return Optional.of(gitHubIssueCommentClient.updateIssueComment(new UpdateIssueCommentCommand(
                task.repositoryOwner(),
                task.repositoryName(),
                task.statusCommentId(),
                body(headline, task, status, pullRequestUrl, failureReason, latestTestRun)
        )));
    }

    private String body(
            String headline,
            FixTaskVo task,
            FixTaskStatus status,
            String pullRequestUrl,
            String failureReason,
            FixTaskTestRunVo latestTestRun
    ) {
        StringBuilder body = new StringBuilder();
        TaskFailureFeedback feedback = TaskFailureFeedback.from(failureReason);
        body.append(unsupportedRepository(feedback) ? "PatchPilot stopped before modifying this repository." : headline)
                .append("\n\n");
        body.append("Status: ").append(status).append("\n");
        body.append("Task: ").append(task.id()).append("\n");
        body.append("Repository: ").append(task.repositoryOwner()).append("/").append(task.repositoryName()).append("\n");
        body.append("Issue: #").append(task.issueNumber()).append("\n");
        body.append("Triggered by: ").append(task.triggerUser()).append("\n");
        if (StringUtils.hasText(pullRequestUrl)) {
            body.append("PR: ").append(pullRequestUrl).append("\n");
        }
        if (status == FixTaskStatus.COMPLETED) {
            appendCompletionEvidence(body, task, latestTestRun);
        }
        if (status == FixTaskStatus.FAILED || status == FixTaskStatus.PENDING_REVIEW) {
            appendNonSuccessEvidence(body, task, status, latestTestRun);
        }
        if (PatchReviewFailureClassifier.isPatchReviewRejection(failureReason)) {
            body.append("Review gate: ").append(PatchReviewFailureClassifier.REVIEW_GATE).append("\n");
            body.append(PatchReviewFailureClassifier.STATUS_COMMENT_RECOVERY).append("\n");
        }
        if (StringUtils.hasText(failureReason)) {
            body.append("Failure category: ").append(feedback.category()).append("\n");
            body.append("Next action: ")
                    .append(unsupportedRepository(feedback) ? UNSUPPORTED_REPOSITORY_ACTION : feedback.nextAction())
                    .append("\n");
            body.append("Reason: ").append(feedback.safeReason()).append("\n");
            if (unsupportedRepository(feedback)) {
                appendUnsupportedRepositoryGuidance(body, languageAdapterCatalogService);
            }
        }
        return body.toString();
    }

    private static void appendCompletionEvidence(StringBuilder body, FixTaskVo task, FixTaskTestRunVo latestTestRun) {
        appendAdapterEvidence(body, task);
        appendVerificationResult(body, latestTestRun);
        body.append("PatchPilot opened the Pull Request only after adapter-selected verification passed.\n");
        body.append("Verification commands come from repository adapters, not arbitrary issue text.\n");
        body.append("PatchPilot does not auto-merge Pull Requests.\n");
    }

    private static void appendNonSuccessEvidence(
            StringBuilder body,
            FixTaskVo task,
            FixTaskStatus status,
            FixTaskTestRunVo latestTestRun
    ) {
        if (!hasAdapterEvidence(task)) {
            return;
        }
        appendAdapterEvidence(body, task);
        if (latestTestRun != null) {
            appendVerificationResult(body, latestTestRun);
        } else if (status == FixTaskStatus.PENDING_REVIEW) {
            body.append("Verification result: not run because the task paused before verification.\n");
        }
        body.append("PatchPilot selected this verification command from the repository adapter allowlist.\n");
        body.append("PatchPilot does not run arbitrary shell commands from issue comments.\n");
    }

    private static void appendAdapterEvidence(StringBuilder body, FixTaskVo task) {
        if (StringUtils.hasText(task.language())) {
            body.append("Language: `").append(task.language()).append("`\n");
        }
        if (StringUtils.hasText(task.buildSystem())) {
            body.append("Build system: `").append(task.buildSystem()).append("`\n");
        }
        if (StringUtils.hasText(task.verificationCommand())) {
            body.append("Verification: `").append(task.verificationCommand()).append("`\n");
        }
        if (StringUtils.hasText(task.adapterDetectionReason())) {
            body.append("Detection reason: ").append(task.adapterDetectionReason()).append("\n");
        }
    }

    private static boolean hasAdapterEvidence(FixTaskVo task) {
        return StringUtils.hasText(task.language())
                || StringUtils.hasText(task.buildSystem())
                || StringUtils.hasText(task.verificationCommand())
                || StringUtils.hasText(task.adapterDetectionReason());
    }

    private static void appendVerificationResult(StringBuilder body, FixTaskTestRunVo latestTestRun) {
        if (latestTestRun == null) {
            return;
        }
        body.append("Verification result: `")
                .append(latestTestRun.command())
                .append("` exited `")
                .append(latestTestRun.exitCode())
                .append("` in `")
                .append(latestTestRun.durationMs())
                .append(" ms`.\n");
    }

    private static boolean unsupportedRepository(TaskFailureFeedback feedback) {
        return UNSUPPORTED_REPOSITORY_CATEGORY.equals(feedback.category());
    }

    private static void appendUnsupportedRepositoryGuidance(
            StringBuilder body,
            LanguageAdapterCatalogService languageAdapterCatalogService
    ) {
        body.append("\n");
        body.append("No model patch generation, tests, commits, pushes, or Pull Request creation were attempted.\n");
        body.append("Supported repository shapes:\n");
        for (SupportedLanguageAdapterVo adapter : languageAdapterCatalogService.listSupportedAdapters()) {
            body.append("- ")
                    .append(adapter.language())
                    .append("/")
                    .append(adapter.buildSystem())
                    .append(": `")
                    .append(String.join(" ", adapter.verificationCommand()))
                    .append("`\n");
        }
        body.append("\n");
        body.append("PatchPilot only runs allowlisted verification commands for detected repository shapes. ");
        body.append("It will not run arbitrary commands for unsupported repositories.\n");
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
