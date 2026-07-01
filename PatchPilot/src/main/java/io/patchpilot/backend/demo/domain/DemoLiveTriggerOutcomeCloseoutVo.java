package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveTriggerOutcomeCloseoutVo(
        String status,
        boolean successful,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String launchPackageArchiveId,
        String launchPackageStatus,
        Instant launchPackageArchivedAt,
        String taskId,
        String taskStatus,
        String failureReason,
        Instant taskCreatedAt,
        Instant taskUpdatedAt,
        String pullRequestUrl,
        String webhookDeliveryId,
        String webhookDeliveryStatus,
        String summary,
        List<String> evidenceNotes,
        List<String> nextActions,
        String sideEffectContract,
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveTriggerOutcomeCloseoutVo {
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
    }
}
