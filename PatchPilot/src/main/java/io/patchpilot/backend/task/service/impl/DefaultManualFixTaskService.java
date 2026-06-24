package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.NoOpTriggerIntentClassifier;
import io.patchpilot.backend.safety.NoOpTriggerQuarantineService;
import io.patchpilot.backend.safety.NoOpTriggerRateLimitService;
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
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.ManualFixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                defaultIssueContextService()
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
                defaultIssueContextService()
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
                defaultIssueContextService()
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
                defaultIssueContextService()
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
            IssueContextService issueContextService
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
                defaultIssueContextService()
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
        TriggerIntentDecision triggerIntentDecision = classifyTriggerIntent(command);
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
        fixTaskTimelineService.recordEvent(
                task.id(),
                FixTaskTimelineEventType.TASK_CREATED,
                "Task accepted from dashboard manual creation"
        );
        fixTaskDispatcher.dispatch(task.id());
        return task;
    }

    private TriggerIntentDecision classifyTriggerIntent(CreateManualFixTaskCommand command) {
        try {
            GitHubIssueContext issueContext = loadIssueContextForClassification(command);
            return triggerIntentClassifier.classify(new TriggerIntentClassificationRequest(
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
            ));
        } catch (RuntimeException exception) {
            return TriggerIntentDecision.rejected(
                    "Model trigger classification failed: unable to load issue context: " + failureReason(exception)
            );
        }
    }

    private GitHubIssueContext loadIssueContextForClassification(CreateManualFixTaskCommand command) {
        if (!triggerIntentClassifier.supportsIssueContextClassification()) {
            return new GitHubIssueContext("", "", "", List.of());
        }
        return issueContextService.loadIssueContext(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber()
        );
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

    private static IssueContextService defaultIssueContextService() {
        return new IssueContextService(new GitHubIssueContextClient(new GitHubProperties()));
    }
}
