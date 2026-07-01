package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveTriggerLaunchPackageArchiveVo(
        String id,
        String status,
        boolean readyToPost,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String summary,
        String operatorHandoffArchiveId,
        boolean operatorHandoffArchiveReady,
        Instant operatorHandoffArchivedAt,
        String liveLaunchGateStatus,
        boolean liveLaunchGateReady,
        List<String> evidenceNotes,
        List<String> nextActions,
        String sideEffectContract,
        Instant packageGeneratedAt,
        Instant archivedAt,
        String report
) {
    public DemoLiveTriggerLaunchPackageArchiveVo {
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
    }
}
