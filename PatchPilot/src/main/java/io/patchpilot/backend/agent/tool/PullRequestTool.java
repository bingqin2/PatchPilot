package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.dashboard.DashboardLinkService;
import io.patchpilot.backend.dashboard.config.DashboardProperties;
import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PullRequestTool {

    private final GitHubPullRequestClient gitHubPullRequestClient;
    private final DashboardLinkService dashboardLinkService;

    public PullRequestTool(GitHubPullRequestClient gitHubPullRequestClient) {
        this(gitHubPullRequestClient, new DashboardLinkService());
    }

    public PullRequestTool(GitHubPullRequestClient gitHubPullRequestClient, DashboardProperties dashboardProperties) {
        this(gitHubPullRequestClient, new DashboardLinkService(dashboardProperties));
    }

    @Autowired
    public PullRequestTool(
            GitHubPullRequestClient gitHubPullRequestClient,
            DashboardLinkService dashboardLinkService
    ) {
        this.gitHubPullRequestClient = gitHubPullRequestClient;
        this.dashboardLinkService = dashboardLinkService;
    }

    public PullRequestResult createPullRequest(FixTaskVo task, String branchName) {
        return createPullRequest(task, branchName, null);
    }

    public PullRequestResult createPullRequest(FixTaskVo task, String branchName, FixTaskTestRunVo latestTestRun) {
        return createPullRequest(task, branchName, latestTestRun, null);
    }

    public PullRequestResult createPullRequest(
            FixTaskVo task,
            String branchName,
            FixTaskTestRunVo latestTestRun,
            FixTaskPatchReviewVo latestPatchReview
    ) {
        return gitHubPullRequestClient.createPullRequest(new CreatePullRequestCommand(
                task.repositoryOwner(),
                task.repositoryName(),
                task.repositoryOwner() + ":" + branchName,
                "main",
                "PatchPilot fix for #" + task.issueNumber(),
                body(task, branchName, latestTestRun, latestPatchReview)
        ));
    }

    private String body(
            FixTaskVo task,
            String branchName,
            FixTaskTestRunVo latestTestRun,
            FixTaskPatchReviewVo latestPatchReview
    ) {
        StringBuilder body = new StringBuilder();
        body.append("Fixes #").append(task.issueNumber()).append("\n\n");
        body.append("## PatchPilot task\n\n");
        body.append("- Task: `").append(task.id()).append("`\n");
        dashboardLinkService.taskUrl(task.id())
                .ifPresent(taskUrl -> body.append("- Dashboard: ").append(taskUrl).append("\n"));
        body.append("- Triggered by: ").append(task.triggerUser()).append("\n");
        body.append("- Branch: ").append(branchName).append("\n");
        appendAdapterEvidence(body, task);
        body.append("\n## Verification and review\n\n");
        appendVerificationResult(body, latestTestRun);
        GitHubFeedbackEvidenceFormatter.appendPatchReviewEvidence(body, latestPatchReview, true);
        GitHubFeedbackEvidenceFormatter.appendRiskReviewApprovalEvidence(body, task, true);
        body.append("- PatchPilot opened this PR only after adapter-selected verification passed.\n");
        body.append("- Verification commands are selected by the detected repository adapter, not by arbitrary issue text.\n");
        body.append("- PatchPilot does not auto-merge Pull Requests.\n");
        return body.toString();
    }

    private static void appendAdapterEvidence(StringBuilder body, FixTaskVo task) {
        if (StringUtils.hasText(task.language())) {
            body.append("- Language: `").append(task.language()).append("`\n");
        }
        if (StringUtils.hasText(task.buildSystem())) {
            body.append("- Build system: `").append(task.buildSystem()).append("`\n");
        }
        if (StringUtils.hasText(task.verificationCommand())) {
            body.append("- Verification: `").append(task.verificationCommand()).append("`\n");
        }
        if (StringUtils.hasText(task.adapterDetectionReason())) {
            body.append("- Detection reason: ").append(task.adapterDetectionReason()).append("\n");
        }
    }

    private static void appendVerificationResult(StringBuilder body, FixTaskTestRunVo latestTestRun) {
        if (latestTestRun == null) {
            return;
        }
        body.append("- Verification result: `")
                .append(latestTestRun.command())
                .append("` exited `")
                .append(latestTestRun.exitCode())
                .append("` in `")
                .append(latestTestRun.durationMs())
                .append(" ms`.\n");
    }

}
