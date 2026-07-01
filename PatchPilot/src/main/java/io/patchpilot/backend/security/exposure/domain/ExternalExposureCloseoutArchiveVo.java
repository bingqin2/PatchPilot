package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;
import java.util.List;

public record ExternalExposureCloseoutArchiveVo(
        String id,
        String status,
        boolean closeoutReady,
        String summary,
        String nextAction,
        String latestSessionId,
        String latestSessionStatus,
        String publicUrl,
        String webhookUrl,
        String purpose,
        String operator,
        Instant startedAt,
        String closedBy,
        Instant closedAt,
        String closeNotes,
        String linkedReadinessArchiveId,
        String handoffStatus,
        String archiveFreshness,
        int readyCount,
        int needsAttentionCount,
        int blockedCount,
        int totalCount,
        List<String> nextActions,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        Instant generatedAt,
        Instant archivedAt,
        String report
) {
    public ExternalExposureCloseoutArchiveVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }
}
