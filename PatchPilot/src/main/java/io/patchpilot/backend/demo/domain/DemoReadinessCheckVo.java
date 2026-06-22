package io.patchpilot.backend.demo.domain;

public record DemoReadinessCheckVo(
        String name,
        DemoReadinessStatus status,
        String message,
        String action
) {
}
