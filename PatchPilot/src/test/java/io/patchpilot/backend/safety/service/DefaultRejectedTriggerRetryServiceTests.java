package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.ManualFixTaskService;
import io.patchpilot.backend.safety.service.impl.DefaultRejectedTriggerRetryService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultRejectedTriggerRetryServiceTests {

    private final RecordingRejectedTriggerAuditService auditService = new RecordingRejectedTriggerAuditService();
    private final RecordingManualFixTaskService manualFixTaskService = new RecordingManualFixTaskService();
    private final RecordingTimelineService timelineService = new RecordingTimelineService();
    private final RejectedTriggerRetryService retryService = new DefaultRejectedTriggerRetryService(
            auditService,
            manualFixTaskService,
            timelineService
    );

    @Test
    void should_retry_rejected_trigger_through_manual_task_flow() {
        RejectedTriggerAuditVo audit = auditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                "issue_comment",
                "delivery-123",
                "bingqin2",
                "PatchPilot",
                7L,
                "alice",
                "/agent fix touch docs/retry.md",
                "Unsafe request rejected: instruction is not actionable"
        ));

        FixTaskVo task = retryService.retryRejectedTrigger(audit.id());

        assertThat(task.id()).isEqualTo("task-1");
        assertThat(manualFixTaskService.commands()).containsExactly(new CreateManualFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "alice",
                "/agent fix touch docs/retry.md"
        ));
        assertThat(timelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.REQUEUED);
        assertThat(timelineService.messages()).containsExactly(
                "Task retried from rejected trigger " + audit.id()
                        + " after previous rejection: Unsafe request rejected: instruction is not actionable"
        );
        assertThat(auditService.findRejectedTrigger(audit.id()))
                .hasValueSatisfying(updatedAudit -> {
                    assertThat(updatedAudit.retriedTaskId()).isEqualTo("task-1");
                    assertThat(updatedAudit.retriedAt()).isNotNull();
                });
    }

    @Test
    void should_reject_retry_when_audit_does_not_exist() {
        assertThatThrownBy(() -> retryService.retryRejectedTrigger("missing-audit"))
                .isInstanceOf(RejectedTriggerRetryService.RejectedTriggerNotFoundException.class)
                .hasMessage("Rejected trigger not found");
    }

    @Test
    void should_reject_retry_when_audit_is_missing_task_inputs() {
        RejectedTriggerAuditVo audit = auditService.add(new RejectedTriggerAuditVo(
                "audit-incomplete",
                "issue_comment",
                "delivery-123",
                "bingqin2",
                "PatchPilot",
                null,
                "alice",
                "/agent fix touch docs/retry.md",
                "Unsafe request rejected: instruction is not actionable",
                Instant.parse("2026-06-21T01:00:00Z")
        ));

        assertThatThrownBy(() -> retryService.retryRejectedTrigger(audit.id()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rejected trigger is missing required task inputs");

        assertThat(manualFixTaskService.commands()).isEmpty();
        assertThat(timelineService.eventTypes()).isEmpty();
        assertThat(auditService.findRejectedTrigger(audit.id()))
                .hasValueSatisfying(updatedAudit -> {
                    assertThat(updatedAudit.retriedTaskId()).isNull();
                    assertThat(updatedAudit.retriedAt()).isNull();
                });
    }

    private static final class RecordingRejectedTriggerAuditService implements RejectedTriggerAuditService {

        private final List<RejectedTriggerAuditVo> audits = new CopyOnWriteArrayList<>();

        @Override
        public RejectedTriggerAuditVo recordRejectedTrigger(RecordRejectedTriggerCommand command) {
            return add(new RejectedTriggerAuditVo(
                    "audit-" + (audits.size() + 1),
                    command.source(),
                    command.deliveryId(),
                    command.repositoryOwner(),
                    command.repositoryName(),
                    command.issueNumber(),
                    command.triggerUser(),
                    command.triggerComment(),
                    command.reason(),
                    command.commentId(),
                    command.commentUrl(),
                    Instant.parse("2026-06-21T01:00:00Z").plusSeconds(audits.size())
            ));
        }

        @Override
        public List<RejectedTriggerAuditVo> listRejectedTriggers(int limit) {
            return audits.stream().limit(limit).toList();
        }

        @Override
        public Optional<RejectedTriggerAuditVo> findRejectedTrigger(String id) {
            return audits.stream()
                    .filter(audit -> audit.id().equals(id))
                    .findFirst();
        }

        @Override
        public RejectedTriggerAuditVo markRetried(String id, String taskId, Instant retriedAt) {
            for (int index = 0; index < audits.size(); index++) {
                RejectedTriggerAuditVo audit = audits.get(index);
                if (audit.id().equals(id)) {
                    RejectedTriggerAuditVo updated = new RejectedTriggerAuditVo(
                            audit.id(),
                            audit.source(),
                            audit.deliveryId(),
                            audit.repositoryOwner(),
                            audit.repositoryName(),
                            audit.issueNumber(),
                            audit.triggerUser(),
                            audit.triggerComment(),
                            audit.reason(),
                            audit.commentId(),
                            audit.commentUrl(),
                            taskId,
                            retriedAt,
                            audit.createdAt()
                    );
                    audits.set(index, updated);
                    return updated;
                }
            }
            throw new IllegalArgumentException("Rejected trigger not found");
        }

        private RejectedTriggerAuditVo add(RejectedTriggerAuditVo audit) {
            audits.add(audit);
            return audit;
        }
    }

    private static final class RecordingManualFixTaskService implements ManualFixTaskService {

        private final List<CreateManualFixTaskCommand> commands = new ArrayList<>();

        @Override
        public FixTaskVo createManualTask(CreateManualFixTaskCommand command) {
            commands.add(command);
            return new FixTaskVo(
                    "task-" + commands.size(),
                    command.repositoryOwner(),
                    command.repositoryName(),
                    command.issueNumber(),
                    0,
                    command.triggerUser(),
                    command.triggerComment(),
                    "manual-retry-" + commands.size(),
                    0,
                    FixTaskStatus.PENDING,
                    null,
                    Instant.parse("2026-06-21T02:00:00Z"),
                    null,
                    null,
                    Instant.parse("2026-06-21T02:00:00Z"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        private List<CreateManualFixTaskCommand> commands() {
            return commands;
        }
    }

    private static final class RecordingTimelineService implements FixTaskTimelineService {

        private final List<FixTaskTimelineEventType> eventTypes = new CopyOnWriteArrayList<>();
        private final List<String> messages = new CopyOnWriteArrayList<>();

        @Override
        public FixTaskTimelineEventVo recordEvent(String taskId, FixTaskTimelineEventType eventType, String message) {
            eventTypes.add(eventType);
            messages.add(message);
            return new FixTaskTimelineEventVo(
                    "event-" + eventTypes.size(),
                    taskId,
                    eventType,
                    message,
                    Instant.parse("2026-06-21T03:00:00Z")
            );
        }

        @Override
        public List<FixTaskTimelineEventVo> listEvents(String taskId) {
            return List.of();
        }

        private List<FixTaskTimelineEventType> eventTypes() {
            return eventTypes;
        }

        private List<String> messages() {
            return messages;
        }
    }
}
