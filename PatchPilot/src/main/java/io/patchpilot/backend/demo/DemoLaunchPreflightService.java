package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchPreflightVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.enums.TriggerEvaluationSource;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.TriggerEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class DemoLaunchPreflightService {

    private final Supplier<DemoReadinessVo> readinessSupplier;
    private final Function<EvaluateTriggerCommand, TriggerEvaluationResultVo> triggerEvaluator;

    @Autowired
    public DemoLaunchPreflightService(
            DemoReadinessService demoReadinessService,
            TriggerEvaluationService triggerEvaluationService
    ) {
        this(demoReadinessService::getReadiness, triggerEvaluationService::evaluate);
    }

    DemoLaunchPreflightService(
            Supplier<DemoReadinessVo> readinessSupplier,
            Function<EvaluateTriggerCommand, TriggerEvaluationResultVo> triggerEvaluator
    ) {
        this.readinessSupplier = readinessSupplier;
        this.triggerEvaluator = triggerEvaluator;
    }

    public DemoLaunchPreflightVo preflight(DemoLaunchPreflightRequestDto request) {
        TriggerRequestParts parts = triggerRequestParts(request);
        DemoReadinessVo readiness = readinessSupplier.get();
        TriggerEvaluationResultVo triggerEvaluation = triggerEvaluator.apply(new EvaluateTriggerCommand(
                TriggerEvaluationSource.ISSUE_COMMENT,
                parts.repositoryOwner(),
                parts.repositoryName(),
                parts.issueNumber(),
                parts.triggerUser(),
                parts.triggerComment()
        ));

        DemoReadinessStatus status = status(readiness, triggerEvaluation);
        return new DemoLaunchPreflightVo(
                status,
                status == DemoReadinessStatus.READY,
                summary(status, readiness, triggerEvaluation),
                readiness,
                triggerEvaluation,
                nextActions(status, readiness, triggerEvaluation)
        );
    }

    private static DemoReadinessStatus status(DemoReadinessVo readiness, TriggerEvaluationResultVo triggerEvaluation) {
        if (!triggerEvaluation.wouldCreateTask()) {
            return DemoReadinessStatus.BLOCKED;
        }
        return readiness.status();
    }

    private static String summary(
            DemoReadinessStatus status,
            DemoReadinessVo readiness,
            TriggerEvaluationResultVo triggerEvaluation
    ) {
        if (!triggerEvaluation.wouldCreateTask()) {
            return "Demo launch preflight is blocked because the tested /agent fix comment would not create a task.";
        }
        return switch (readiness.status()) {
            case READY -> "Demo launch preflight is ready to post the tested /agent fix comment.";
            case NEEDS_ATTENTION -> "Demo launch preflight needs attention before posting /agent fix.";
            case BLOCKED -> "Demo launch preflight is blocked by current demo readiness.";
        };
    }

    private static List<String> nextActions(
            DemoReadinessStatus status,
            DemoReadinessVo readiness,
            TriggerEvaluationResultVo triggerEvaluation
    ) {
        if (!triggerEvaluation.wouldCreateTask()) {
            List<String> actions = new ArrayList<>();
            if (hasText(triggerEvaluation.blockedReason())) {
                actions.add("Revise the tested /agent fix comment: " + triggerEvaluation.blockedReason() + ".");
            }
            if (hasText(triggerEvaluation.nextAction())) {
                actions.add(triggerEvaluation.nextAction());
            }
            return actions.isEmpty()
                    ? List.of("Revise the tested /agent fix comment before posting it on GitHub.")
                    : actions;
        }
        if (status == DemoReadinessStatus.READY) {
            return List.of("Post the tested /agent fix comment on the controlled GitHub issue.");
        }
        List<String> actions = new ArrayList<>(readiness.nextActions());
        actions.add("Resolve readiness warnings, then rerun this launch preflight.");
        return actions;
    }

    private static TriggerRequestParts triggerRequestParts(DemoLaunchPreflightRequestDto request) {
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
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
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
