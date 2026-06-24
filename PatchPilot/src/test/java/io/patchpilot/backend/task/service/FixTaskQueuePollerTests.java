package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.service.impl.FixTaskQueuePoller;
import io.patchpilot.backend.task.service.impl.FixTaskWorker;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskWorkerHealthService;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskQueue;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskQueuePollerTests {

    @Test
    void should_exit_when_no_queue_item_is_available() {
        RecordingQueue queue = new RecordingQueue(Optional.empty());
        RecordingWorker worker = new RecordingWorker(false);
        InMemoryFixTaskWorkerHealthService workerHealthService = workerHealthService();
        FixTaskQueuePoller poller = new FixTaskQueuePoller(queue, worker, workerHealthService);

        poller.pollOnce();

        assertThat(queue.recovered()).isTrue();
        assertThat(queue.claimed()).isTrue();
        assertThat(worker.taskId()).isNull();
        assertThat(queue.completedQueueItemId()).isNull();
        assertThat(queue.failedQueueItemId()).isNull();
        assertThat(workerHealthService.getHealth().pollCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().idlePollCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().lastClaimedQueueItemId()).isNull();
    }

    @Test
    void should_execute_claimed_item_and_mark_completed() {
        RecordingQueue queue = new RecordingQueue(Optional.of(queueItem()));
        RecordingWorker worker = new RecordingWorker(false);
        InMemoryFixTaskWorkerHealthService workerHealthService = workerHealthService();
        FixTaskQueuePoller poller = new FixTaskQueuePoller(queue, worker, workerHealthService);

        poller.pollOnce();

        assertThat(queue.recovered()).isTrue();
        assertThat(queue.recoveredBeforeClaim()).isTrue();
        assertThat(worker.taskId()).isEqualTo("task-123");
        assertThat(queue.completedQueueItemId()).isEqualTo("queue-123");
        assertThat(queue.failedQueueItemId()).isNull();
        assertThat(workerHealthService.getHealth().pollCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().claimedCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().completedCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().lastClaimedQueueItemId()).isEqualTo("queue-123");
        assertThat(workerHealthService.getHealth().lastClaimedTaskId()).isEqualTo("task-123");
    }

    @Test
    void should_mark_failed_when_worker_throws() {
        RecordingQueue queue = new RecordingQueue(Optional.of(queueItem()));
        RecordingWorker worker = new RecordingWorker(true);
        InMemoryFixTaskWorkerHealthService workerHealthService = workerHealthService();
        FixTaskQueuePoller poller = new FixTaskQueuePoller(queue, worker, workerHealthService);

        poller.pollOnce();

        assertThat(worker.taskId()).isEqualTo("task-123");
        assertThat(queue.completedQueueItemId()).isNull();
        assertThat(queue.failedQueueItemId()).isEqualTo("queue-123");
        assertThat(queue.failureReason()).isEqualTo("worker failed");
        assertThat(workerHealthService.getHealth().claimedCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().failedCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().lastError()).isEqualTo("worker failed");
    }

    @Test
    void should_record_worker_error_when_queue_polling_fails() {
        RecordingQueue queue = new RecordingQueue(Optional.empty());
        queue.failOnClaim();
        RecordingWorker worker = new RecordingWorker(false);
        InMemoryFixTaskWorkerHealthService workerHealthService = workerHealthService();
        FixTaskQueuePoller poller = new FixTaskQueuePoller(queue, worker, workerHealthService);

        org.assertj.core.api.Assertions.assertThatThrownBy(poller::pollOnce)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("claim failed");

        assertThat(worker.taskId()).isNull();
        assertThat(workerHealthService.getHealth().state()).isEqualTo("ERROR");
        assertThat(workerHealthService.getHealth().pollCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().failedCount()).isEqualTo(1);
        assertThat(workerHealthService.getHealth().lastError()).isEqualTo("claim failed");
    }

    private static InMemoryFixTaskWorkerHealthService workerHealthService() {
        return new InMemoryFixTaskWorkerHealthService(Clock.fixed(Instant.parse("2026-06-24T06:00:00Z"), java.time.ZoneOffset.UTC));
    }

    private static FixTaskQueueItemVo queueItem() {
        Instant now = Instant.parse("2026-06-19T10:00:00Z");
        return new FixTaskQueueItemVo(
                "queue-123",
                "task-123",
                FixTaskQueueItemStatus.RUNNING,
                1,
                null,
                now,
                now,
                now,
                now
        );
    }

    private static final class RecordingQueue extends MyBatisFixTaskQueue {

        private final Optional<FixTaskQueueItemVo> nextItem;
        private boolean recovered;
        private boolean claimed;
        private boolean recoveredBeforeClaim;
        private String completedQueueItemId;
        private String failedQueueItemId;
        private String failureReason;
        private boolean failOnClaim;

        private RecordingQueue(Optional<FixTaskQueueItemVo> nextItem) {
            super(null, null);
            this.nextItem = nextItem;
        }

        @Override
        public int recoverTimedOutRunningItems() {
            recovered = true;
            return 0;
        }

        @Override
        public Optional<FixTaskQueueItemVo> claimNext() {
            recoveredBeforeClaim = recovered;
            claimed = true;
            if (failOnClaim) {
                throw new IllegalStateException("claim failed");
            }
            return nextItem;
        }

        @Override
        public void markCompleted(String queueItemId) {
            completedQueueItemId = queueItemId;
        }

        @Override
        public void markFailed(String queueItemId, String failureReason) {
            failedQueueItemId = queueItemId;
            this.failureReason = failureReason;
        }

        private boolean claimed() {
            return claimed;
        }

        private boolean recovered() {
            return recovered;
        }

        private boolean recoveredBeforeClaim() {
            return recoveredBeforeClaim;
        }

        private String completedQueueItemId() {
            return completedQueueItemId;
        }

        private String failedQueueItemId() {
            return failedQueueItemId;
        }

        private String failureReason() {
            return failureReason;
        }

        private void failOnClaim() {
            this.failOnClaim = true;
        }
    }

    private static final class RecordingWorker extends FixTaskWorker {

        private final boolean fail;
        private final AtomicReference<String> taskId = new AtomicReference<>();

        private RecordingWorker(boolean fail) {
            super(null, null, null, null);
            this.fail = fail;
        }

        @Override
        public void execute(String taskId) {
            this.taskId.set(taskId);
            if (fail) {
                throw new IllegalStateException("worker failed");
            }
        }

        private String taskId() {
            return taskId.get();
        }
    }
}
