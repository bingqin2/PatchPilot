package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoEvidenceBundleVo(
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
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveDemoEvidenceBundleVo {
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
    }
}
