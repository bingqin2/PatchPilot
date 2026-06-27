package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoSelfHostedLaunchReadinessVo(
        DemoReadinessStatus status,
        boolean readyToLaunch,
        String summary,
        List<DemoSelfHostedLaunchCheckVo> checks,
        List<String> nextActions,
        Instant generatedAt,
        String markdownReport
) {

    public DemoSelfHostedLaunchReadinessVo {
        checks = List.copyOf(checks);
        nextActions = List.copyOf(nextActions);
    }
}
