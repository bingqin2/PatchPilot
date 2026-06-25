package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PullRequestTool {

    private final GitHubPullRequestClient gitHubPullRequestClient;

    public PullRequestResult createPullRequest(FixTaskVo task, String branchName) {
        return gitHubPullRequestClient.createPullRequest(new CreatePullRequestCommand(
                task.repositoryOwner(),
                task.repositoryName(),
                task.repositoryOwner() + ":" + branchName,
                "main",
                "PatchPilot fix for #" + task.issueNumber(),
                body(task, branchName)
        ));
    }

    private String body(FixTaskVo task, String branchName) {
        StringBuilder body = new StringBuilder();
        body.append("Fixes #").append(task.issueNumber()).append("\n\n");
        body.append("## PatchPilot task\n\n");
        body.append("- Task: `").append(task.id()).append("`\n");
        body.append("- Triggered by: ").append(task.triggerUser()).append("\n");
        body.append("- Branch: ").append(branchName).append("\n");
        appendAdapterEvidence(body, task);
        body.append("\n## Verification and review\n\n");
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
}
