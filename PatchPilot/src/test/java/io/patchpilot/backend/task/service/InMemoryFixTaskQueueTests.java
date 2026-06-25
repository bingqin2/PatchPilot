package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.service.impl.FixTaskWorker;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskQueueTests {

    @Test
    void should_execute_enqueued_task_with_worker() throws Exception {
        RecordingFixTaskWorker worker = new RecordingFixTaskWorker();
        FixTaskQueue queue = new InMemoryFixTaskQueue(worker);

        queue.enqueue("task-123");

        assertThat(worker.await()).isTrue();
        assertThat(worker.taskId()).isEqualTo("task-123");
    }

    private static final class RecordingFixTaskWorker extends FixTaskWorker {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicReference<String> taskId = new AtomicReference<>();

        private RecordingFixTaskWorker() {
            super(null, null, null, null, null, null);
        }

        @Override
        public void execute(String taskId) {
            this.taskId.set(taskId);
            latch.countDown();
        }

        private boolean await() throws InterruptedException {
            return latch.await(3, TimeUnit.SECONDS);
        }

        private String taskId() {
            return taskId.get();
        }
    }
}
