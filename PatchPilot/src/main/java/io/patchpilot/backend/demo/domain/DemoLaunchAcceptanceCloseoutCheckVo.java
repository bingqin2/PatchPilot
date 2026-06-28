package io.patchpilot.backend.demo.domain;

public record DemoLaunchAcceptanceCloseoutCheckVo(
        String name,
        DemoReadinessStatus status,
        String summary,
        String nextAction
) {
}
