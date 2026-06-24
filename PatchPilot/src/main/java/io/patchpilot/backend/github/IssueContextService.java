package io.patchpilot.backend.github;

import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueContextService {

    private static final int DEFAULT_COMMENT_LIMIT = 5;

    private final GitHubIssueContextClient gitHubIssueContextClient;

    public GitHubIssueContext loadIssueContext(FixTaskVo task) {
        return loadIssueContext(task.repositoryOwner(), task.repositoryName(), task.issueNumber());
    }

    public GitHubIssueContext loadIssueContext(String repositoryOwner, String repositoryName, long issueNumber) {
        return gitHubIssueContextClient.getIssueContext(new GetIssueContextCommand(
                repositoryOwner,
                repositoryName,
                issueNumber,
                DEFAULT_COMMENT_LIMIT
        ));
    }
}
