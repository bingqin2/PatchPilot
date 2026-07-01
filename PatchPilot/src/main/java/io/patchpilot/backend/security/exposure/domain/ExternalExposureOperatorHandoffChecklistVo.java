package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;
import java.util.List;

public record ExternalExposureOperatorHandoffChecklistVo(
        String status,
        boolean readyForNextLiveStep,
        String summary,
        String nextAction,
        String repository,
        String latestCloseoutArchiveId,
        String latestSessionId,
        String latestSessionStatus,
        String publicUrl,
        String webhookUrl,
        String handoffStatus,
        String archiveFreshness,
        String livePublishStatus,
        boolean livePublishReady,
        int activeSessionCount,
        int readyCount,
        int needsAttentionCount,
        int blockedCount,
        int totalCount,
        List<String> nextActions,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        List<ExternalExposureOperatorHandoffChecklistCheckVo> checks,
        Instant generatedAt,
        String markdownReport
) {
    public ExternalExposureOperatorHandoffChecklistVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
        checks = checks == null ? List.of() : List.copyOf(checks);
    }
}
