package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;
import java.util.List;

public record ExternalExposureHandoffPackageVo(
        String status,
        boolean handoffReady,
        String summary,
        String nextAction,
        String readinessStatus,
        boolean readinessSafeToExpose,
        int readinessReadyCount,
        int readinessNeedsAttentionCount,
        int readinessBlockedCount,
        int readinessTotalCount,
        String latestArchiveId,
        String latestArchiveStatus,
        Boolean latestArchiveSafeToExpose,
        Instant latestArchiveCreatedAt,
        String archiveFreshness,
        List<String> nextActions,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        Instant generatedAt,
        String markdownReport
) {
    public ExternalExposureHandoffPackageVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }
}
