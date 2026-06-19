package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import io.patchpilot.backend.task.service.impl.DefaultFixTaskControlService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultFixTaskControlServiceTests {

    private final InMemoryFixTaskService fixTaskService = new InMemoryFixTaskService();
    private final RecordingFixTaskQueue fixTaskQueue = new RecordingFixTaskQueue();
    private final RecordingTimelineService fixTaskTimelineService = new RecordingTimelineService();
    private final RecordingTaskProcessRegistry taskProcessRegistry = new RecordingTaskProcessRegistry();
    private final FixTaskControlService controlService = new DefaultFixTaskControlService(
            fixTaskService,
            fixTaskQueue,
            fixTaskTimelineService,
            taskProcessRegistry
    );

    @Test
    void should_cancel_pending_task_and_pending_queue_items() {
        FixTaskVo task = createTask("delivery-control-cancel");

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(cancelledTask.failureReason()).isEqualTo("Task cancelled by user request");
        assertThat(fixTaskQueue.cancelledTaskIds()).containsExactly(task.id());
        assertThat(taskProcessRegistry.cancelledTaskIds()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
        assertThat(fixTaskTimelineService.messages()).containsExactly("Task cancelled by user request");
    }

    @Test
    void should_cancel_running_task_without_cancelling_queue_item() {
        FixTaskVo task = createTask("delivery-control-cancel-running");
        fixTaskService.markRunning(task.id());

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(fixTaskQueue.cancelledTaskIds()).isEmpty();
        assertThat(taskProcessRegistry.cancelledTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
    }

    @Test
    void should_cancel_running_tests_task_without_cancelling_queue_item() {
        FixTaskVo task = createTask("delivery-control-cancel-running-tests");
        fixTaskService.markRunningTests(task.id());

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(fixTaskQueue.cancelledTaskIds()).isEmpty();
        assertThat(taskProcessRegistry.cancelledTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
    }

    @Test
    void should_reject_cancelling_terminal_task() {
        FixTaskVo task = createTask("delivery-control-cancel-terminal");
        fixTaskService.markCompleted(task.id(), "https://github.com/octocat/hello-world/pull/7");

        assertThatThrownBy(() -> controlService.cancelTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only active tasks can be cancelled");
    }

    @Test
    void should_retry_failed_task_and_enqueue_it() {
        FixTaskVo task = createTask("delivery-control-retry");
        fixTaskService.markFailed(task.id(), "executor failed");

        FixTaskVo retriedTask = controlService.retryTask(task.id());

        assertThat(retriedTask.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(retriedTask.failureReason()).isNull();
        assertThat(fixTaskQueue.enqueuedTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.REQUEUED);
        assertThat(fixTaskTimelineService.messages()).containsExactly("Task requeued by user request");
    }

    @Test
    void should_reject_retrying_active_task() {
        FixTaskVo task = createTask("delivery-control-retry-active");

        assertThatThrownBy(() -> controlService.retryTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only failed or cancelled tasks can be retried");

        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
    }

    private FixTaskVo createTask(String deliveryId) {
        return fixTaskService.createFixTask(new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        ));
    }

    private static final class RecordingFixTaskQueue implements FixTaskQueue {

        private final List<String> enqueuedTaskIds = new CopyOnWriteArrayList<>();
        private final List<String> cancelledTaskIds = new CopyOnWriteArrayList<>();

        @Override
        public void enqueue(String taskId) {
            enqueuedTaskIds.add(taskId);
        }

        @Override
        public int cancelPendingForTask(String taskId) {
            cancelledTaskIds.add(taskId);
            return 1;
        }

        private List<String> enqueuedTaskIds() {
            return enqueuedTaskIds;
        }

        private List<String> cancelledTaskIds() {
            return cancelledTaskIds;
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
                    Instant.parse("2026-06-20T00:00:00Z").plusSeconds(eventTypes.size())
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

    private static final class RecordingTaskProcessRegistry extends TaskProcessRegistry {

        private final List<String> cancelledTaskIds = new CopyOnWriteArrayList<>();

        @Override
        public boolean cancel(String taskId) {
            cancelledTaskIds.add(taskId);
            return true;
        }

        private List<String> cancelledTaskIds() {
            return cancelledTaskIds;
        }
    }
}
