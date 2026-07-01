package io.patchpilot.backend.demo.domain;

import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunVo;

import java.time.Instant;
import java.util.List;

public record DemoLiveLaunchGateVo(
        String status,
        boolean readyToPost,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String summary,
        List<String> nextActions,
        String sideEffectContract,
        DemoSelfHostedLaunchReadinessVo launchReadiness,
        GitHubWebhookSetupReadinessVo webhookSetup,
        GitHubLivePublishPreflightVo livePublishPreflight,
        GitHubTriggerDryRunVo triggerDryRun,
        List<DemoLiveLaunchGateCheckVo> checks,
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveLaunchGateVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
        checks = checks == null ? List.of() : List.copyOf(checks);
    }
}
