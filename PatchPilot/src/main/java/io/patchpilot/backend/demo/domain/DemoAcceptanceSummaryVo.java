package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoAcceptanceSummaryVo(
        DemoReadinessStatus status,
        boolean accepted,
        String summary,
        String nextAction,
        DemoReadinessStatus launchCertificateStatus,
        boolean launchCertificateArchived,
        boolean launchCertificateCertified,
        String launchCertificateArchiveId,
        String launchCloseoutArchiveId,
        String launchEvidenceArchiveId,
        String launchDeliveryReceiptId,
        DemoReadinessStatus taskCertificateStatus,
        boolean taskCertificateArchived,
        boolean taskCertificateCertified,
        String taskCertificateArchiveId,
        String taskCloseoutArchiveId,
        String taskEvidenceArchiveId,
        String taskDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant generatedAt,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport
) {
    public record Check(
            String name,
            DemoReadinessStatus status,
            String summary,
            String nextAction
    ) {
    }
}
