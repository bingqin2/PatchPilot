package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceCompletionEvidenceBundleVo(
        DemoReadinessStatus status,
        boolean readyToShare,
        String summary,
        String nextAction,
        String latestCompletionArchiveId,
        String latestSharePackageArchiveId,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestTaskId,
        int completionArchiveCount,
        Instant latestArchivedAt,
        Instant generatedAt,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport
) {
}
