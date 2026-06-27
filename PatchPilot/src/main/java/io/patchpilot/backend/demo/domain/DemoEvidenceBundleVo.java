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
        Instant generatedAt,
        List<String> nextActions
) {
}
