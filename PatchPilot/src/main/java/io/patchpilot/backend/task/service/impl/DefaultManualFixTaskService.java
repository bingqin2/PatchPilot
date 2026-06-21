package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
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
                new CommandSafetyGate()
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
                commandSafetyGate
        );
    }

    @Autowired
    public DefaultManualFixTaskService(
            FixTaskService fixTaskService,
            FixTaskTimelineService fixTaskTimelineService,
            FixTaskDispatcher fixTaskDispatcher,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate
    ) {
        this.fixTaskService = fixTaskService;
        this.fixTaskTimelineService = fixTaskTimelineService;
        this.fixTaskDispatcher = fixTaskDispatcher;
        this.rejectedTriggerAuditService = rejectedTriggerAuditService;
        this.commandSafetyGate = commandSafetyGate;
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
                    safetyDecision.reason()
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
}
