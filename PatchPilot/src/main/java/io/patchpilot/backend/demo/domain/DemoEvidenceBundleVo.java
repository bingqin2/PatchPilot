package io.patchpilot.backend.demo.domain;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.time.Instant;
import java.util.List;

public record DemoEvidenceBundleVo(
        DemoReadinessStatus status,
        String summary,
        DemoEvidenceBundleSummaryVo summaryCounts,
        DemoReadinessVo readiness,
        DemoSmokeChecklistVo smokeChecklist,
        ConfigurationSummaryVo configuration,
        DemoAdapterFixtureEvidenceVo adapterFixtures,
        FixTaskQueueSummaryVo queueSummary,
        FixTaskVo recentTask,
        String recentPullRequestUrl,
        GitHubWebhookSetupReadinessVo webhookSetupReadiness,
        WebhookDeliveryDiagnosticVo latestWebhookDelivery,
        List<WebhookDeliveryDiagnosticVo> recentWebhookDeliveries,
        RejectedTriggerAuditSummaryVo rejectedTriggerSummary,
        long activeQuarantineCount,
        DemoReadinessStatus handoffShareChecklistStatus,
        String handoffShareChecklistSummary,
        String handoffShareChecklistNextAction,
        DemoReadinessStatus handoffShareCenterStatus,
        String handoffShareCenterSummary,
        String handoffShareCenterNextAction,
        List<String> handoffShareCenterDownloadActions,
        boolean handoffShareDeliveryReceiptRecorded,
        String handoffShareLatestDeliveryReceiptId,
        String handoffShareLatestDeliveryTarget,
        String handoffShareLatestDeliveryChannel,
        String handoffShareLatestDeliveredAt,
        Instant generatedAt,
        List<String> nextActions
) {
    public DemoEvidenceBundleVo(
            DemoReadinessStatus status,
            String summary,
            DemoEvidenceBundleSummaryVo summaryCounts,
            DemoReadinessVo readiness,
            DemoSmokeChecklistVo smokeChecklist,
            ConfigurationSummaryVo configuration,
            DemoAdapterFixtureEvidenceVo adapterFixtures,
            FixTaskQueueSummaryVo queueSummary,
            FixTaskVo recentTask,
            String recentPullRequestUrl,
            GitHubWebhookSetupReadinessVo webhookSetupReadiness,
            WebhookDeliveryDiagnosticVo latestWebhookDelivery,
            List<WebhookDeliveryDiagnosticVo> recentWebhookDeliveries,
            RejectedTriggerAuditSummaryVo rejectedTriggerSummary,
            long activeQuarantineCount,
            DemoReadinessStatus handoffShareChecklistStatus,
            String handoffShareChecklistSummary,
            String handoffShareChecklistNextAction,
            DemoReadinessStatus handoffShareCenterStatus,
            String handoffShareCenterSummary,
            String handoffShareCenterNextAction,
            List<String> handoffShareCenterDownloadActions,
            Instant generatedAt,
            List<String> nextActions
    ) {
        this(
                status,
                summary,
                summaryCounts,
                readiness,
                smokeChecklist,
                configuration,
                adapterFixtures,
                queueSummary,
                recentTask,
                recentPullRequestUrl,
                webhookSetupReadiness,
                latestWebhookDelivery,
                recentWebhookDeliveries,
                rejectedTriggerSummary,
                activeQuarantineCount,
                handoffShareChecklistStatus,
                handoffShareChecklistSummary,
                handoffShareChecklistNextAction,
                handoffShareCenterStatus,
                handoffShareCenterSummary,
                handoffShareCenterNextAction,
                handoffShareCenterDownloadActions,
                false,
                null,
                null,
                null,
                null,
                generatedAt,
                nextActions
        );
    }
}
