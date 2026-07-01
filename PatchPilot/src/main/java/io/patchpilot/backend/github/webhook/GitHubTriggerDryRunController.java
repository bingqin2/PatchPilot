package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunVo;
import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.enums.TriggerEvaluationSource;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.TriggerEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github/trigger-dry-run")
@RequiredArgsConstructor
public class GitHubTriggerDryRunController {

    private static final String SIDE_EFFECT_CONTRACT = """
            Read-only live trigger dry run: this endpoint does not create tasks, does not enqueue work, \
            does not record rate-limit usage, does not run git push, does not create branches, \
            does not open Pull Requests, does not write GitHub comments, and does not expose tokens.\
            """;

    private final TriggerEvaluationService triggerEvaluationService;

    @PostMapping
    public ResponseEntity<ApiResponse<GitHubTriggerDryRunVo>> dryRun(
            @RequestBody GitHubTriggerDryRunRequestDto request
    ) {
        try {
            TriggerRequestParts parts = triggerRequestParts(request);
            TriggerEvaluationResultVo evaluation = triggerEvaluationService.evaluate(new EvaluateTriggerCommand(
                    TriggerEvaluationSource.ISSUE_COMMENT,
                    parts.repositoryOwner(),
                    parts.repositoryName(),
                    parts.issueNumber(),
                    parts.triggerUser(),
                    parts.triggerComment()
            ));
            return ResponseEntity.ok(ApiResponse.ok(toVo(parts, evaluation)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    private static GitHubTriggerDryRunVo toVo(
            TriggerRequestParts parts,
            TriggerEvaluationResultVo evaluation
    ) {
        return new GitHubTriggerDryRunVo(
                evaluation.status(),
                evaluation.wouldCreateTask(),
                parts.repositoryOwner() + "/" + parts.repositoryName(),
                parts.issueNumber(),
                issueUrl(parts),
                parts.triggerUser(),
                parts.triggerComment(),
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

    private static String issueUrl(TriggerRequestParts parts) {
        return "https://github.com/"
                + parts.repositoryOwner()
                + "/"
                + parts.repositoryName()
                + "/issues/"
                + parts.issueNumber();
    }

    private static TriggerRequestParts triggerRequestParts(GitHubTriggerDryRunRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        String repositoryOwner = requiredText(request.repositoryOwner(), "repositoryOwner must not be blank");
        String repositoryName = requiredText(request.repositoryName(), "repositoryName must not be blank");
        if (request.issueNumber() == null || request.issueNumber() < 1) {
            throw new IllegalArgumentException("issueNumber must be positive");
        }
        String triggerUser = requiredText(request.triggerUser(), "triggerUser must not be blank");
        String triggerComment = requiredText(request.triggerComment(), "triggerComment must not be blank");
        if (!triggerComment.equals("/agent fix") && !triggerComment.startsWith("/agent fix ")) {
            throw new IllegalArgumentException("triggerComment must start with /agent fix");
        }
        return new TriggerRequestParts(
                repositoryOwner,
                repositoryName,
                request.issueNumber(),
                triggerUser,
                triggerComment
        );
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private record TriggerRequestParts(
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment
    ) {
    }
}
