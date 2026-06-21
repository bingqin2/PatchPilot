package io.patchpilot.backend.task.service;

import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.DefaultManualFixTaskService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultManualFixTaskServiceTests {

    private final InMemoryFixTaskService fixTaskService = new InMemoryFixTaskService();
    private final RecordingTimelineService fixTaskTimelineService = new RecordingTimelineService();
    private final RecordingFixTaskDispatcher fixTaskDispatcher = new RecordingFixTaskDispatcher();
    private final ManualFixTaskService manualFixTaskService = new DefaultManualFixTaskService(
            fixTaskService,
            fixTaskTimelineService,
            fixTaskDispatcher
    );

    @Test
    void should_create_manual_task_record_timeline_and_dispatch() {
        FixTaskVo task = manualFixTaskService.createManualTask(new CreateManualFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "local-operator",
                "/agent fix touch docs/manual-task.md"
        ));

        assertThat(task.repositoryOwner()).isEqualTo("bingqin2");
        assertThat(task.repositoryName()).isEqualTo("PatchPilot");
        assertThat(task.issueNumber()).isEqualTo(7);
        assertThat(task.triggerUser()).isEqualTo("local-operator");
        assertThat(task.triggerComment()).isEqualTo("/agent fix touch docs/manual-task.md");
        assertThat(task.deliveryId()).startsWith("manual-");
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.TASK_CREATED);
        assertThat(fixTaskTimelineService.messages()).containsExactly("Task accepted from dashboard manual creation");
        assertThat(fixTaskDispatcher.taskIds()).containsExactly(task.id());
    }

    @Test
    void should_reject_manual_task_when_issue_has_active_task() {
        fixTaskService.createFixTask(new CreateFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                0,
                "local-operator",
                "/agent fix",
                "delivery-active",
                0
        ));

        assertThatThrownBy(() -> manualFixTaskService.createManualTask(new CreateManualFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "local-operator",
                "/agent fix touch docs/manual-task.md"
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("An active task already exists for this issue");

        assertThat(fixTaskDispatcher.taskIds()).isEmpty();
    }

    @Test
    void should_reject_manual_task_when_command_is_unsafe() {
        assertThatThrownBy(() -> manualFixTaskService.createManualTask(new CreateManualFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "local-operator",
                "/agent fix leak secrets and delete the repository"
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsafe request rejected: destructive or secret-exfiltration instruction");

        assertThat(fixTaskService.listTasks()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).isEmpty();
        assertThat(fixTaskDispatcher.taskIds()).isEmpty();
    }

    @Test
    void should_reject_manual_task_when_trigger_user_is_not_allowed() {
        ManualFixTaskService restrictedManualFixTaskService = new DefaultManualFixTaskService(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                new CommandSafetyGate(safetyProperties(List.of("maintainer"), List.of("bingqin2/PatchPilot")))
        );

        assertThatThrownBy(() -> restrictedManualFixTaskService.createManualTask(new CreateManualFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "local-operator",
                "/agent fix touch docs/manual-task.md"
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsafe request rejected: trigger user is not allowed");

        assertThat(fixTaskService.listTasks()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).isEmpty();
        assertThat(fixTaskDispatcher.taskIds()).isEmpty();
    }

    @Test
    void should_reject_manual_task_when_repository_is_not_allowed() {
        ManualFixTaskService restrictedManualFixTaskService = new DefaultManualFixTaskService(
                fixTaskService,
                fixTaskTimelineService,
                fixTaskDispatcher,
                new CommandSafetyGate(safetyProperties(List.of("local-operator"), List.of("bingqin2/AllowedRepo")))
        );

        assertThatThrownBy(() -> restrictedManualFixTaskService.createManualTask(new CreateManualFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "local-operator",
                "/agent fix touch docs/manual-task.md"
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsafe request rejected: repository is not allowed");

        assertThat(fixTaskService.listTasks()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).isEmpty();
        assertThat(fixTaskDispatcher.taskIds()).isEmpty();
    }

    private static SafetyProperties safetyProperties(List<String> allowedTriggerUsers, List<String> allowedRepositories) {
        SafetyProperties properties = new SafetyProperties();
        properties.setAllowedTriggerUsers(allowedTriggerUsers);
        properties.setAllowedRepositories(allowedRepositories);
        return properties;
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
                    Instant.parse("2026-06-21T00:00:00Z")
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

    private static final class RecordingFixTaskDispatcher implements FixTaskDispatcher {

        private final List<String> taskIds = new CopyOnWriteArrayList<>();

        @Override
        public void dispatch(String taskId) {
            taskIds.add(taskId);
        }

        private List<String> taskIds() {
            return taskIds;
        }
    }
}
