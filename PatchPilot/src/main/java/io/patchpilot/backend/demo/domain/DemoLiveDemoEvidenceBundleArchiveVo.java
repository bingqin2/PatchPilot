package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoEvidenceBundleArchiveVo(
        String id,
        String status,
        boolean readyForHandoff,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String launchPackageArchiveId,
        Instant launchPackageArchivedAt,
        String outcomeCloseoutArchiveId,
        Instant outcomeCloseoutArchivedAt,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
        String webhookDeliveryId,
        String summary,
        List<String> evidenceNotes,
        List<String> nextActions,
        String sideEffectContract,
        Instant bundleGeneratedAt,
        Instant archivedAt,
        String report
) {
    public DemoLiveDemoEvidenceBundleArchiveVo {
        evidenceNotes = List.copyOf(evidenceNotes);
        nextActions = List.copyOf(nextActions);
    }
}
