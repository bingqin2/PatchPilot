package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLaunchEvidencePackageVo(
        DemoReadinessStatus status,
        boolean readyToShare,
        String summary,
        String sessionId,
        DemoReadinessStatus launchReadinessStatus,
        DemoReadinessStatus evidenceBundleStatus,
        DemoReadinessStatus handoffFinalizationStatus,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestWebhookDeliveryId,
        String evaluationRunId,
        List<String> evaluationCoverage,
        List<DemoSelfHostedLaunchCheckVo> preLaunchChecks,
        List<String> liveRunProof,
        List<String> postDemoProof,
        List<String> nextActions,
        List<String> healthContract,
        String markdownReport,
        Instant generatedAt
) {
    public DemoLaunchEvidencePackageVo {
        evaluationCoverage = List.copyOf(evaluationCoverage);
        preLaunchChecks = List.copyOf(preLaunchChecks);
        liveRunProof = List.copyOf(liveRunProof);
        postDemoProof = List.copyOf(postDemoProof);
        nextActions = List.copyOf(nextActions);
        healthContract = List.copyOf(healthContract);
    }
}
