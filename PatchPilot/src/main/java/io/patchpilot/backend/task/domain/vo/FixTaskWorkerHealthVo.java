package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskWorkerHealthVo(
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
}
