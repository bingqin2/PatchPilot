package io.patchpilot.backend.demo.domain;

public record DemoSelfHostedLaunchCheckVo(
        String name,
        DemoReadinessStatus status,
        String message,
        String action
) {
}
