package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import lombok.RequiredArgsConstructor;
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
        return """
                Fixes #%d

                Triggered by: %s
                Branch: %s
                """.formatted(task.issueNumber(), task.triggerUser(), branchName);
    }
}
