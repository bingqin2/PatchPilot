package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.config.TaskQueueProperties;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskWorkerHealthVo;
import io.patchpilot.backend.task.service.FixTaskWorkerHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class InMemoryFixTaskWorkerHealthService implements FixTaskWorkerHealthService {

    private final Clock clock;
    private final long workerHeartbeatStaleMs;
    private final AtomicReference<State> state = new AtomicReference<>(State.notStarted());

    @Autowired
    public InMemoryFixTaskWorkerHealthService(TaskQueueProperties taskQueueProperties) {
        this(Clock.systemUTC(), taskQueueProperties.getWorkerHeartbeatStaleMs());
    }

    public InMemoryFixTaskWorkerHealthService(Clock clock) {
        this(clock, 10000);
    }

    public InMemoryFixTaskWorkerHealthService(Clock clock, long workerHeartbeatStaleMs) {
        this.clock = clock;
        this.workerHeartbeatStaleMs = workerHeartbeatStaleMs;
    }

    @Override
    public FixTaskWorkerHealthVo getHealth() {
        State snapshot = state.get();
        RuntimeReadiness readiness = runtimeReadiness(snapshot, now());
        return new FixTaskWorkerHealthVo(
                snapshot.state,
                snapshot.message,
                snapshot.startedAt,
                snapshot.lastPollAt,
                snapshot.pollCount,
                snapshot.claimedCount,
                snapshot.completedCount,
                snapshot.failedCount,
                snapshot.idlePollCount,
                snapshot.lastClaimedQueueItemId,
                snapshot.lastClaimedTaskId,
                snapshot.lastError,
                readiness.lastPollAgeMs,
                readiness.status,
                readiness.operatorAction
        );
    }

    @Override
    public void recordPollStarted() {
        state.updateAndGet(current -> current.pollStarted(now()));
    }

    @Override
    public void recordIdlePoll() {
        state.updateAndGet(current -> current.idle());
    }

    @Override
    public void recordClaimed(FixTaskQueueItemVo queueItem) {
        state.updateAndGet(current -> current.claimed(queueItem));
    }

    @Override
    public void recordCompleted(FixTaskQueueItemVo queueItem) {
        state.updateAndGet(current -> current.completed());
    }

    @Override
    public void recordFailed(FixTaskQueueItemVo queueItem, String failureReason) {
        state.updateAndGet(current -> current.failed(failureReason));
    }

    @Override
    public void recordPollingFailed(String failureReason) {
        state.updateAndGet(current -> current.failed(failureReason));
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private RuntimeReadiness runtimeReadiness(State snapshot, Instant timestamp) {
        if ("NOT_STARTED".equals(snapshot.state)) {
            return new RuntimeReadiness(
                    -1,
                    "NEEDS_ATTENTION",
                    "Wait for the queue worker poller to start or check the active Spring profile."
            );
        }
        long lastPollAgeMs = snapshot.lastPollAt == null ? -1 : Math.max(0, timestamp.toEpochMilli() - snapshot.lastPollAt.toEpochMilli());
        if ("ERROR".equals(snapshot.state)) {
            return new RuntimeReadiness(
                    lastPollAgeMs,
                    "NEEDS_ATTENTION",
                    "Inspect worker logs and the latest failed queue item before starting a demo."
            );
        }
        if (lastPollAgeMs > workerHeartbeatStaleMs) {
            return new RuntimeReadiness(
                    lastPollAgeMs,
                    "NEEDS_ATTENTION",
                    "Check whether the queue worker scheduler is still running."
            );
        }
        return new RuntimeReadiness(
                lastPollAgeMs,
                "READY",
                "No action needed."
        );
    }

    private record RuntimeReadiness(long lastPollAgeMs, String status, String operatorAction) {
    }

    private record State(
            String state,
            String message,
            Instant startedAt,
            Instant lastPollAt,
            long pollCount,
            long claimedCount,
            long completedCount,
            long failedCount,
            long idlePollCount,
            String lastClaimedQueueItemId,
            String lastClaimedTaskId,
            String lastError
    ) {

        private static State notStarted() {
            return new State(
                    "NOT_STARTED",
                    "Worker poller has not reported a heartbeat yet.",
                    null,
                    null,
                    0,
                    0,
                    0,
                    0,
                    0,
                    null,
                    null,
                    null
            );
        }

        private State pollStarted(Instant timestamp) {
            return new State(
                    "POLLING",
                    "Worker poller is checking for available queue items.",
                    startedAt == null ? timestamp : startedAt,
                    timestamp,
                    pollCount + 1,
                    claimedCount,
                    completedCount,
                    failedCount,
                    idlePollCount,
                    lastClaimedQueueItemId,
                    lastClaimedTaskId,
                    lastError
            );
        }

        private State idle() {
            return new State(
                    "IDLE",
                    "Worker poller is active but no queue item was available.",
                    startedAt,
                    lastPollAt,
                    pollCount,
                    claimedCount,
                    completedCount,
                    failedCount,
                    idlePollCount + 1,
                    lastClaimedQueueItemId,
                    lastClaimedTaskId,
                    lastError
            );
        }

        private State claimed(FixTaskQueueItemVo queueItem) {
            return new State(
                    "ACTIVE",
                    "Worker poller is executing a queue item.",
                    startedAt,
                    lastPollAt,
                    pollCount,
                    claimedCount + 1,
                    completedCount,
                    failedCount,
                    idlePollCount,
                    queueItem.id(),
                    queueItem.taskId(),
                    null
            );
        }

        private State completed() {
            return new State(
                    "IDLE",
                    "Worker poller completed the latest queue item.",
                    startedAt,
                    lastPollAt,
                    pollCount,
                    claimedCount,
                    completedCount + 1,
                    failedCount,
                    idlePollCount,
                    lastClaimedQueueItemId,
                    lastClaimedTaskId,
                    null
            );
        }

        private State failed(String failureReason) {
            String normalizedFailureReason = StringUtils.hasText(failureReason) ? failureReason : "Unknown worker failure";
            return new State(
                    "ERROR",
                    "Worker poller recorded a task failure: " + normalizedFailureReason,
                    startedAt,
                    lastPollAt,
                    pollCount,
                    claimedCount,
                    completedCount,
                    failedCount + 1,
                    idlePollCount,
                    lastClaimedQueueItemId,
                    lastClaimedTaskId,
                    normalizedFailureReason
            );
        }
    }
}
