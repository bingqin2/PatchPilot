package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.NoOpTriggerIntentClassifier;
import io.patchpilot.backend.safety.NoOpTriggerRateLimitService;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.TriggerIntentClassifier;
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

import java.util.UUID;

@Service
public class DefaultManualFixTaskService implements ManualFixTaskService {

    private final FixTaskService fixTaskService;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final FixTaskDispatcher fixTaskDispatcher;
    private final CommandSafetyGate commandSafetyGate;
    private final RejectedTriggerAuditService rejectedTriggerAuditService;
    private final TriggerRateLimitService triggerRateLimitService;
    private final TriggerIntentClassifier triggerIntentClassifier;

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
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier()
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
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier()
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
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier()
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
                new NoOpTriggerRateLimitService(),
                triggerIntentClassifier
        );
    }

    @Autowired
    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier
    ) {
        this.fixTaskService = fixTaskService;
        this.fixTaskTimelineService = fixTaskTimelineService;
        this.fixTaskDispatcher = fixTaskDispatcher;
        this.rejectedTriggerAuditService = rejectedTriggerAuditService;
        this.commandSafetyGate = commandSafetyGate;
        this.triggerRateLimitService = triggerRateLimitService;
        this.triggerIntentClassifier = triggerIntentClassifier;
    }

    @Override
    public FixTaskVo createManualTask(CreateManualFixTaskCommand command) {
        SafetyGateDecision safetyDecision = commandSafetyGate.evaluate(new SafetyGateRequest(
                command.repositoryOwner(),
                command.repositoryName(),
                command.triggerUser(),
                command.triggerComment()
        ));
        if (!safetyDecision.allowed()) {
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
        TriggerIntentDecision triggerIntentDecision = triggerIntentClassifier.classify(
                new TriggerIntentClassificationRequest(
                        classificationId(),
                        "manual",
                        command.repositoryOwner(),
                        command.repositoryName(),
                        command.issueNumber(),
                        command.triggerUser(),
                        command.triggerComment()
                )
        );
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

    private static String classificationId() {
        return UUID.randomUUID().toString();
    }
}
