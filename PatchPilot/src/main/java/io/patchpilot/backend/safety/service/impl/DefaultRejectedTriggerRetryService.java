package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.RejectedTriggerRetryService;
import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.ManualFixTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DefaultRejectedTriggerRetryService implements RejectedTriggerRetryService {

    private final RejectedTriggerAuditService auditService;
    private final ManualFixTaskService manualFixTaskService;
    private final FixTaskTimelineService timelineService;

    @Override
    public FixTaskVo retryRejectedTrigger(String rejectedTriggerId) {
        RejectedTriggerAuditVo audit = auditService.findRejectedTrigger(rejectedTriggerId)
                .orElseThrow(() -> new RejectedTriggerNotFoundException("Rejected trigger not found"));
        validateRetryInputs(audit);

        FixTaskVo task = manualFixTaskService.createManualTask(new CreateManualFixTaskCommand(
                audit.repositoryOwner(),
                audit.repositoryName(),
                audit.issueNumber(),
                audit.triggerUser(),
                audit.triggerComment()
        ));
        timelineService.recordEvent(
                task.id(),
                FixTaskTimelineEventType.REQUEUED,
                "Task retried from rejected trigger " + audit.id()
                        + " after previous rejection: " + audit.reason()
        );
        auditService.markRetried(audit.id(), task.id(), Instant.now());
        return task;
    }

    private static void validateRetryInputs(RejectedTriggerAuditVo audit) {
        if (isBlank(audit.repositoryOwner())
                || isBlank(audit.repositoryName())
                || audit.issueNumber() == null
                || isBlank(audit.triggerUser())
                || isBlank(audit.triggerComment())) {
            throw new IllegalArgumentException("Rejected trigger is missing required task inputs");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
