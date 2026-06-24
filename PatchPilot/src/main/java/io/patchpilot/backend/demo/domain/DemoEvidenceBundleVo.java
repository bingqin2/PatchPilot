package io.patchpilot.backend.demo.domain;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
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
        WebhookDeliveryDiagnosticVo latestWebhookDelivery,
        RejectedTriggerAuditSummaryVo rejectedTriggerSummary,
        long activeQuarantineCount,
        Instant generatedAt,
        List<String> nextActions
) {
}
