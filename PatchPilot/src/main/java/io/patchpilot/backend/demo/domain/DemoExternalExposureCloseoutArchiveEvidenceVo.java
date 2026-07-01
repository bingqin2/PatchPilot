package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoExternalExposureCloseoutArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean closeoutReady,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestSessionId,
        String latestSessionStatus,
        String publicUrl,
        String webhookUrl,
        String linkedReadinessArchiveId,
        String handoffStatus,
        String archiveFreshness,
        Instant latestArchivedAt,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract
) {
    public DemoExternalExposureCloseoutArchiveEvidenceVo {
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }
}
