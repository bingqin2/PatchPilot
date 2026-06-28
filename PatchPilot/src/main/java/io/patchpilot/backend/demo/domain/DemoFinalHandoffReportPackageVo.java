package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalHandoffReportPackageVo(
        DemoReadinessStatus status,
        boolean downloadReady,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestSessionId,
        String latestDeliveryReceiptId,
        String taskCertificateArchiveId,
        boolean taskCertificateReady,
        List<String> readinessChecks,
        List<String> requiredAttachments,
        List<String> preSendChecks,
        List<String> evidenceNotes,
        List<String> sourceReports,
        String markdownReport,
        Instant generatedAt
) {
}
