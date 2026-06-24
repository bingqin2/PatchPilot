package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskWorkerHealthService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskWorkerHealthServiceTests {

    private final InMemoryFixTaskWorkerHealthService service = new InMemoryFixTaskWorkerHealthService(
            Clock.fixed(Instant.parse("2026-06-24T06:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void should_start_without_worker_activity() {
        assertThat(service.getHealth().state()).isEqualTo("NOT_STARTED");
        assertThat(service.getHealth().pollCount()).isZero();
        assertThat(service.getHealth().lastPollAt()).isNull();
        assertThat(service.getHealth().message()).isEqualTo("Worker poller has not reported a heartbeat yet.");
    }

    @Test
    void should_record_idle_poll() {
        service.recordPollStarted();
        service.recordIdlePoll();

        assertThat(service.getHealth().state()).isEqualTo("IDLE");
        assertThat(service.getHealth().pollCount()).isEqualTo(1);
        assertThat(service.getHealth().idlePollCount()).isEqualTo(1);
        assertThat(service.getHealth().lastPollAt()).isEqualTo(Instant.parse("2026-06-24T06:00:00Z"));
        assertThat(service.getHealth().message()).isEqualTo("Worker poller is active but no queue item was available.");
    }

    @Test
    void should_record_claim_completion_and_failure() {
        service.recordPollStarted();
        service.recordClaimed(queueItem());
        service.recordCompleted(queueItem());
        service.recordFailed(queueItem(), "worker failed");

        assertThat(service.getHealth().state()).isEqualTo("ERROR");
        assertThat(service.getHealth().claimedCount()).isEqualTo(1);
        assertThat(service.getHealth().completedCount()).isEqualTo(1);
        assertThat(service.getHealth().failedCount()).isEqualTo(1);
        assertThat(service.getHealth().lastClaimedQueueItemId()).isEqualTo("queue-123");
        assertThat(service.getHealth().lastClaimedTaskId()).isEqualTo("task-123");
        assertThat(service.getHealth().lastError()).isEqualTo("worker failed");
        assertThat(service.getHealth().message()).isEqualTo("Worker poller recorded a task failure: worker failed");
    }

    private static FixTaskQueueItemVo queueItem() {
        Instant now = Instant.parse("2026-06-24T06:00:00Z");
        return new FixTaskQueueItemVo(
                "queue-123",
                "task-123",
                RUNNING,
                1,
                null,
                now,
                now,
                now,
                now
        );
    }
}
