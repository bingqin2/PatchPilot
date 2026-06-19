package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.service.impl.AsyncFixTaskDispatcher;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncFixTaskDispatcherTests {

    @Test
    void should_enqueue_task_without_executing_it_directly() {
        RecordingFixTaskQueue queue = new RecordingFixTaskQueue();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(queue);

        dispatcher.dispatch("task-123");

        assertThat(queue.taskIds()).containsExactly("task-123");
    }

    private static final class RecordingFixTaskQueue implements FixTaskQueue {

        private final List<String> taskIds = new CopyOnWriteArrayList<>();

        @Override
        public void enqueue(String taskId) {
            taskIds.add(taskId);
        }

        private List<String> taskIds() {
            return taskIds;
        }
    }
}
