package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalHandoffReportPackageArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean downloadReady,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestHandoffArchiveId,
        String latestSessionId,
        String latestDeliveryReceiptId,
        String taskCertificateArchiveId,
        boolean taskCertificateReady,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
}
