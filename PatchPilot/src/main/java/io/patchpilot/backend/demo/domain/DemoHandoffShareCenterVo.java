package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoHandoffShareCenterVo(
        DemoReadinessStatus status,
        boolean shareReady,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestSessionId,
        String latestCreatedAt,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        boolean deliveryReceiptRecorded,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        DemoReadinessStatus taskCertificateStatus,
        boolean taskCertificateReady,
        String taskCertificateSummary,
        String taskCertificateNextAction,
        String taskCertificateArchiveId,
        String taskCertificateTaskId,
        String taskCertificatePullRequestUrl,
        List<String> downloadActions,
        List<String> evidenceNotes,
        String markdownReport,
        Instant generatedAt
) {
    public DemoHandoffShareCenterVo(
            DemoReadinessStatus status,
            boolean shareReady,
            String summary,
            String nextAction,
            String latestArchiveId,
            String latestSessionId,
            String latestCreatedAt,
            String latestDeliveryReceiptId,
            String latestDeliveryTarget,
            String latestDeliveryChannel,
            String latestDeliveredAt,
            boolean deliveryReceiptRecorded,
            String deliveryReceiptFreshness,
            boolean deliveryReceiptFresh,
            String deliveryReceiptFreshnessSummary,
            List<String> downloadActions,
            List<String> evidenceNotes,
            String markdownReport,
            Instant generatedAt
    ) {
        this(
                status,
                shareReady,
                summary,
                nextAction,
                latestArchiveId,
                latestSessionId,
                latestCreatedAt,
                latestDeliveryReceiptId,
                latestDeliveryTarget,
                latestDeliveryChannel,
                latestDeliveredAt,
                deliveryReceiptRecorded,
                deliveryReceiptFreshness,
                deliveryReceiptFresh,
                deliveryReceiptFreshnessSummary,
                DemoReadinessStatus.READY,
                true,
                "Latest task evidence acceptance certificate archive is certified and ready.",
                "Use the archived task evidence acceptance certificate as task-level review proof.",
                "task-evidence-certificate-archive-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                downloadActions,
                evidenceNotes,
                markdownReport,
                generatedAt
        );
    }
}
