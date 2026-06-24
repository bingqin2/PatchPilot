package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.domain.TriggerIntentIssueComment;
import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.TriggerIntentClassifier;
import io.patchpilot.backend.safety.service.TriggerQuarantineService;
import io.patchpilot.backend.safety.service.TriggerRateLimitService;
import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.TriggerEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultTriggerEvaluationService implements TriggerEvaluationService {

    private static final String STATUS_WOULD_CREATE_TASK = "WOULD_CREATE_TASK";
    private static final String STATUS_BLOCKED = "BLOCKED";

    private final FixTaskService fixTaskService;
    private final CommandSafetyGate commandSafetyGate;
    private final TriggerQuarantineService triggerQuarantineService;
    private final TriggerRateLimitService triggerRateLimitService;
    private final TriggerIntentClassifier triggerIntentClassifier;
    private final IssueContextService issueContextService;

    @Override
    public TriggerEvaluationResultVo evaluate(EvaluateTriggerCommand command) {
        SafetyGateDecision safetyDecision = commandSafetyGate.evaluate(new SafetyGateRequest(
                command.repositoryOwner(),
                command.repositoryName(),
                command.triggerUser(),
                command.triggerComment()
        ));
        if (!safetyDecision.allowed() && !canClassifyWithIssueContext(safetyDecision)) {
            return blocked(
                    decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                    null,
                    null,
                    null,
                    null,
                    false,
                    safetyDecision.reason(),
                    safetyDecision.category()
            );
        }

        boolean hasActiveTask = fixTaskService.findActiveTaskForIssue(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber()
        ).isPresent();
        TriggerEvaluationDecisionVo activeTaskDecision = hasActiveTask
                ? decision(false, "An active task already exists for this issue", RejectedTriggerCategory.UNKNOWN)
                : decision(true, "No active task exists for this issue", RejectedTriggerCategory.UNKNOWN);
        if (hasActiveTask) {
            return blocked(
                    decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                    activeTaskDecision,
                    null,
                    null,
                    null,
                    false,
                    activeTaskDecision.reason(),
                    activeTaskDecision.category()
            );
        }

        TriggerQuarantineDecision quarantineDecision = triggerQuarantineService.check(new TriggerQuarantineRequest(
                "manual",
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser()
        ));
        if (!quarantineDecision.allowed()) {
            return blocked(
                    decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                    activeTaskDecision,
                    decision(quarantineDecision.allowed(), quarantineDecision.reason(), quarantineDecision.category()),
                    null,
                    null,
                    false,
                    quarantineDecision.reason(),
                    quarantineDecision.category()
            );
        }

        TriggerRateLimitDecision rateLimitDecision = triggerRateLimitService.check(new TriggerRateLimitRequest(
                "manual",
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser()
        ));
        if (!rateLimitDecision.allowed()) {
            return blocked(
                    decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                    activeTaskDecision,
                    decision(quarantineDecision.allowed(), quarantineDecision.reason(), quarantineDecision.category()),
                    decision(rateLimitDecision.allowed(), rateLimitDecision.reason(), rateLimitDecision.category()),
                    null,
                    false,
                    rateLimitDecision.reason(),
                    rateLimitDecision.category()
            );
        }

        TriggerClassificationResult triggerClassificationResult = classifyTriggerIntent(command);
        TriggerIntentDecision triggerIntentDecision = triggerClassificationResult.decision();
        TriggerEvaluationDecisionVo triggerIntentEvaluation = decision(
                triggerIntentDecision.shouldExecute(),
                triggerIntentDecision.shouldExecute() ? triggerIntentDecision.reason() : triggerIntentDecision.rejectionReason(),
                triggerIntentDecision.rejectionCategory()
        );
        if (!triggerIntentDecision.shouldExecute()) {
            return blocked(
                    decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                    activeTaskDecision,
                    decision(quarantineDecision.allowed(), quarantineDecision.reason(), quarantineDecision.category()),
                    decision(rateLimitDecision.allowed(), rateLimitDecision.reason(), rateLimitDecision.category()),
                    triggerIntentEvaluation,
                    triggerClassificationResult.issueContextLoaded(),
                    triggerIntentEvaluation.reason(),
                    triggerIntentEvaluation.category()
            );
        }

        return new TriggerEvaluationResultVo(
                STATUS_WOULD_CREATE_TASK,
                true,
                null,
                null,
                decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                activeTaskDecision,
                decision(quarantineDecision.allowed(), quarantineDecision.reason(), quarantineDecision.category()),
                decision(rateLimitDecision.allowed(), rateLimitDecision.reason(), rateLimitDecision.category()),
                triggerIntentEvaluation,
                triggerClassificationResult.issueContextLoaded(),
                "Create task is allowed for this trigger."
        );
    }

    private TriggerClassificationResult classifyTriggerIntent(EvaluateTriggerCommand command) {
        try {
            boolean issueContextLoaded = triggerIntentClassifier.supportsIssueContextClassification();
            GitHubIssueContext issueContext = issueContextLoaded
                    ? issueContextService.loadIssueContext(
                            command.repositoryOwner(),
                            command.repositoryName(),
                            command.issueNumber()
                    )
                    : new GitHubIssueContext("", "", "", List.of());
            return new TriggerClassificationResult(
                    triggerIntentClassifier.classify(new TriggerIntentClassificationRequest(
                            UUID.randomUUID().toString(),
                            "manual",
                            command.repositoryOwner(),
                            command.repositoryName(),
                            command.issueNumber(),
                            command.triggerUser(),
                            command.triggerComment(),
                            issueContext.title(),
                            issueContext.body(),
                            issueComments(issueContext)
                    )),
                    issueContextLoaded
            );
        } catch (RuntimeException exception) {
            return new TriggerClassificationResult(
                    TriggerIntentDecision.rejected(
                            "Model trigger classification failed: unable to load issue context: " + failureReason(exception)
                    ),
                    false
            );
        }
    }

    private static TriggerEvaluationResultVo blocked(
            TriggerEvaluationDecisionVo safetyDecision,
            TriggerEvaluationDecisionVo activeTaskDecision,
            TriggerEvaluationDecisionVo quarantineDecision,
            TriggerEvaluationDecisionVo rateLimitDecision,
            TriggerEvaluationDecisionVo triggerIntentDecision,
            boolean issueContextLoaded,
            String blockedReason,
            String blockedCategory
    ) {
        return new TriggerEvaluationResultVo(
                STATUS_BLOCKED,
                false,
                blockedReason,
                blockedCategory,
                safetyDecision,
                activeTaskDecision,
                quarantineDecision,
                rateLimitDecision,
                triggerIntentDecision,
                issueContextLoaded,
                "Revise the /agent fix request before creating a task."
        );
    }

    private boolean canClassifyWithIssueContext(SafetyGateDecision safetyDecision) {
        return RejectedTriggerCategory.NOT_ACTIONABLE.equals(safetyDecision.category())
                && triggerIntentClassifier.supportsIssueContextClassification();
    }

    private static TriggerEvaluationDecisionVo decision(boolean allowed, String reason, String category) {
        return new TriggerEvaluationDecisionVo(allowed, reason, category);
    }

    private static List<TriggerIntentIssueComment> issueComments(GitHubIssueContext issueContext) {
        return issueContext.comments().stream()
                .map(comment -> new TriggerIntentIssueComment(comment.author(), comment.body()))
                .toList();
    }

    private static String failureReason(RuntimeException exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }

    private record TriggerClassificationResult(TriggerIntentDecision decision, boolean issueContextLoaded) {
    }
}
