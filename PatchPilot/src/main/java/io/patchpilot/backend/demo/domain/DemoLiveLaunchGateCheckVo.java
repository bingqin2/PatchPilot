package io.patchpilot.backend.demo.domain;

public record DemoLiveLaunchGateCheckVo(
        String name,
        String status,
        String message,
        String action
) {
}
