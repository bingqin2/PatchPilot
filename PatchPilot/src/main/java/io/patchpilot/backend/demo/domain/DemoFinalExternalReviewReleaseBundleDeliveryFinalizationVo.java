package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo(
        DemoReadinessStatus status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestDeliveryReceiptId,
        String latestCertificateArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestPackageArchiveId,
        String latestPackageDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String releaseBundleDeliveryReceiptFreshness,
        boolean releaseBundleDeliveryReceiptFresh,
        String releaseBundleDeliveryReceiptFreshnessSummary,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {

    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo {
        checks = List.copyOf(checks);
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }

    public record Check(
            String name,
            DemoReadinessStatus status,
            String summary,
            String nextAction
    ) {
    }
}
