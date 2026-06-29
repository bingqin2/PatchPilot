package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewReleaseBundleArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean releaseReady,
        String summary,
        String nextAction,
        String latestCertificateArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestPackageArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        Instant latestCertificateArchivedAt,
        List<String> requiredAttachments,
        List<ReleaseCheck> releaseChecks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String report,
        Instant generatedAt,
        Instant archivedAt
) {
    public DemoFinalExternalReviewReleaseBundleArchiveVo {
        requiredAttachments = List.copyOf(requiredAttachments);
        releaseChecks = List.copyOf(releaseChecks);
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }

    public record ReleaseCheck(
            String name,
            DemoReadinessStatus status,
            String summary,
            String nextAction
    ) {
    }
}
