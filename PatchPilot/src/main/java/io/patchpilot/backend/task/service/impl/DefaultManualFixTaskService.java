package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.NoOpTriggerIntentClassifier;
import io.patchpilot.backend.safety.NoOpTriggerQuarantineService;
import io.patchpilot.backend.safety.NoOpTriggerRateLimitService;
import io.patchpilot.backend.safety.TriggerDecisionEvidenceFormatter;
import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.domain.TriggerIntentIssueComment;
import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.TriggerIntentClassifier;
import io.patchpilot.backend.safety.service.TriggerQuarantineService;
import io.patchpilot.backend.safety.service.TriggerRateLimitService;
import io.patchpilot.backend.safety.service.impl.InMemoryRejectedTriggerAuditService;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskPreExecutionDecisionService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.ManualFixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultManualFixTaskService implements ManualFixTaskService {

    private final FixTaskService fixTaskService;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final FixTaskDispatcher fixTaskDispatcher;
    private final CommandSafetyGate commandSafetyGate;
    private final RejectedTriggerAuditService rejectedTriggerAuditService;
    private final TriggerQuarantineService triggerQuarantineService;
    private final TriggerRateLimitService triggerRateLimitService;
    private final TriggerIntentClassifier triggerIntentClassifier;
    private final IssueContextService issueContextService;
    private final FixTaskPreExecutionDecisionService preExecutionDecisionService;

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                new InMemoryRejectedTriggerAuditService(),
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            FixTaskPreExecutionDecisionService preExecutionDecisionService
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                new InMemoryRejectedTriggerAuditService(),
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                defaultIssueContextService(),
                preExecutionDecisionService
        );
    }

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            CommandSafetyGate commandSafetyGate
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                new InMemoryRejectedTriggerAuditService(),
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                rejectedTriggerAuditService,
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerIntentClassifier triggerIntentClassifier
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                rejectedTriggerAuditService,
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                triggerIntentClassifier,
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    @Autowired
    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier,
            IssueContextService issueContextService,
            FixTaskPreExecutionDecisionService preExecutionDecisionService
    ) {
        this.fixTaskService = fixTaskService;
        this.fixTaskTimelineService = fixTaskTimelineService;
        this.fixTaskDispatcher = fixTaskDispatcher;
        this.rejectedTriggerAuditService = rejectedTriggerAuditService;
        this.commandSafetyGate = commandSafetyGate;
        this.triggerQuarantineService = triggerQuarantineService;
        this.triggerRateLimitService = triggerRateLimitService;
        this.triggerIntentClassifier = triggerIntentClassifier;
        this.issueContextService = issueContextService;
        this.preExecutionDecisionService = preExecutionDecisionService;
    }

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier,
            IssueContextService issueContextService
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                rejectedTriggerAuditService,
                commandSafetyGate,
                triggerQuarantineService,
                triggerRateLimitService,
                triggerIntentClassifier,
                issueContextService,
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier
    ) {
        this(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                rejectedTriggerAuditService,
                commandSafetyGate,
                triggerQuarantineService,
                triggerRateLimitService,
                triggerIntentClassifier,
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    @Override
    public FixTaskVo createManualTask(CreateManualFixTaskCommand command) {
        SafetyGateDecision safetyDecision = commandSafetyGate.evaluate(new SafetyGateRequest(
                command.repositoryOwner(),
                command.repositoryName(),
                command.triggerUser(),
                command.triggerComment()
        ));
        if (!safetyDecision.allowed() && !canClassifyWithIssueContext(safetyDecision)) {
            rejectedTriggerAuditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                    "manual",
                    "manual-rejected-" + UUID.randomUUID(),
                    command.repositoryOwner(),
                    command.repositoryName(),
                    command.issueNumber(),
                    command.triggerUser(),
                    command.triggerComment(),
                    safetyDecision.reason(),
                    safetyDecision.category()
            ));
            throw new IllegalArgumentException(safetyDecision.reason());
        }
        fixTaskService.findActiveTaskForIssue(
                        command.repositoryOwner(),
                        command.repositoryName(),
                        command.issueNumber()
                )
                .ifPresent(activeTask -> {
                    throw new IllegalStateException("An active task already exists for this issue");
                });
        TriggerEvaluationDecisionVo activeTaskDecision = new TriggerEvaluationDecisionVo(
                true,
                "No active task exists for this issue",
                RejectedTriggerCategory.UNKNOWN
        );

        TriggerQuarantineDecision quarantineDecision = triggerQuarantineService.check(new TriggerQuarantineRequest(
                "manual",
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser()
        ));
        if (!quarantineDecision.allowed()) {
            rejectedTriggerAuditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                    "manual",
                    "manual-rejected-" + UUID.randomUUID(),
                    command.repositoryOwner(),
                    command.repositoryName(),
                    command.issueNumber(),
                    command.triggerUser(),
                    command.triggerComment(),
                    quarantineDecision.reason(),
                    quarantineDecision.category()
            ));
            throw new IllegalArgumentException(quarantineDecision.reason());
        }

        TriggerRateLimitDecision rateLimitDecision = triggerRateLimitService.checkAndRecord(new TriggerRateLimitRequest(
                "manual",
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser()
        ));
        if (!rateLimitDecision.allowed()) {
            rejectedTriggerAuditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                    "manual",
                    "manual-rejected-" + UUID.randomUUID(),
                    command.repositoryOwner(),
                    command.repositoryName(),
                    command.issueNumber(),
                    command.triggerUser(),
                    command.triggerComment(),
                    rateLimitDecision.reason(),
                    rateLimitDecision.category()
            ));
            throw new IllegalArgumentException(rateLimitDecision.reason());
        }
        TriggerClassificationResult triggerClassificationResult = classifyTriggerIntent(command);
        TriggerIntentDecision triggerIntentDecision = triggerClassificationResult.decision();
        if (!triggerIntentDecision.shouldExecute()) {
            rejectedTriggerAuditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                    "manual",
                    "manual-rejected-" + UUID.randomUUID(),
                    command.repositoryOwner(),
                    command.repositoryName(),
                    command.issueNumber(),
                    command.triggerUser(),
                    command.triggerComment(),
                    triggerIntentDecision.rejectionReason(),
                    triggerIntentDecision.rejectionCategory()
            ));
            throw new IllegalArgumentException(triggerIntentDecision.rejectionReason());
        }
        TriggerEvaluationDecisionVo triggerIntentEvaluation = new TriggerEvaluationDecisionVo(
                true,
                triggerIntentDecision.reason(),
                triggerIntentDecision.rejectionCategory()
        );

        FixTaskVo task = fixTaskService.createFixTask(new CreateFixTaskCommand(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                0,
                command.triggerUser(),
                command.triggerComment(),
                "manual-" + UUID.randomUUID(),
                0
        ));
        preExecutionDecisionService.recordDecision(new RecordFixTaskPreExecutionDecisionCommand(
                task.id(),
                "MANUAL",
                "ALLOWED",
                decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                activeTaskDecision,
                decision(quarantineDecision.allowed(), quarantineDecision.reason(), quarantineDecision.category()),
                decision(rateLimitDecision.allowed(), rateLimitDecision.reason(), rateLimitDecision.category()),
                triggerIntentEvaluation,
                triggerClassificationResult.issueContextLoaded(),
                Instant.now()
        ));
        fixTaskTimelineService.recordEvent(
                task.id(),
                FixTaskTimelineEventType.TRIGGER_ACCEPTED,
                TriggerDecisionEvidenceFormatter.accepted(
                        safetyDecision,
                        triggerIntentDecision,
                        triggerClassificationResult.issueContextLoaded()
                )
        );
        fixTaskTimelineService.recordEvent(
                task.id(),
                FixTaskTimelineEventType.TASK_CREATED,
                "Task accepted from dashboard manual creation"
        );
        fixTaskDispatcher.dispatch(task.id());
        return task;
    }

    private TriggerClassificationResult classifyTriggerIntent(CreateManualFixTaskCommand command) {
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
                            classificationId(),
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

    private boolean canClassifyWithIssueContext(SafetyGateDecision safetyDecision) {
        return RejectedTriggerCategory.NOT_ACTIONABLE.equals(safetyDecision.category())
                && triggerIntentClassifier.supportsIssueContextClassification();
    }

    private static String classificationId() {
        return UUID.randomUUID().toString();
    }

    private static List<TriggerIntentIssueComment> issueComments(GitHubIssueContext issueContext) {
        return issueContext.comments().stream()
                .map(comment -> new TriggerIntentIssueComment(comment.author(), comment.body()))
                .toList();
    }

    private static String failureReason(RuntimeException exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }

    private static TriggerEvaluationDecisionVo decision(boolean allowed, String reason, String category) {
        return new TriggerEvaluationDecisionVo(allowed, reason, category);
    }

    private static IssueContextService defaultIssueContextService() {
        return new IssueContextService(new GitHubIssueContextClient(new GitHubProperties()));
    }

    private record TriggerClassificationResult(TriggerIntentDecision decision, boolean issueContextLoaded) {
    }
}
