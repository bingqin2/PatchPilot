package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunCommand;
import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunVo;
import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.enums.TriggerEvaluationSource;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.TriggerEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubTriggerDryRunService {

    private static final String SIDE_EFFECT_CONTRACT = """
            Read-only live trigger dry run: this endpoint does not create tasks, does not enqueue work, \
            does not record rate-limit usage, does not run git push, does not create branches, \
            does not open Pull Requests, does not write GitHub comments, and does not expose tokens.\
            """;

    private final TriggerEvaluationService triggerEvaluationService;

    public GitHubTriggerDryRunVo dryRun(GitHubTriggerDryRunCommand command) {
        TriggerEvaluationResultVo evaluation = triggerEvaluationService.evaluate(new EvaluateTriggerCommand(
                TriggerEvaluationSource.ISSUE_COMMENT,
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser(),
                command.triggerComment()
        ));
        return new GitHubTriggerDryRunVo(
                evaluation.status(),
                evaluation.wouldCreateTask(),
                command.repositoryOwner() + "/" + command.repositoryName(),
                command.issueNumber(),
                issueUrl(command),
                command.triggerUser(),
                command.triggerComment(),
                summary(evaluation),
                nextAction(evaluation),
                SIDE_EFFECT_CONTRACT,
                evaluation
        );
    }

    private static String summary(TriggerEvaluationResultVo evaluation) {
        return evaluation.wouldCreateTask()
                ? "Live GitHub trigger dry run would create a PatchPilot task."
                : "Live GitHub trigger dry run is blocked before task creation.";
    }

    private static String nextAction(TriggerEvaluationResultVo evaluation) {
        if (evaluation.wouldCreateTask()) {
            return "Post this /agent fix comment on the GitHub issue when publish preflight is ready.";
        }
        if (evaluation.blockedReason() != null && evaluation.blockedReason().contains("active task")) {
            return "Wait for the active task to finish or cancel it before posting another /agent fix.";
        }
        return "Revise the /agent fix comment before posting it on GitHub.";
    }

    private static String issueUrl(GitHubTriggerDryRunCommand command) {
        return "https://github.com/"
                + command.repositoryOwner()
                + "/"
                + command.repositoryName()
                + "/issues/"
                + command.issueNumber();
    }
}
