package io.patchpilot.backend.demo.domain;

public record DemoHandoffReadinessCheckVo(
        String name,
        DemoReadinessStatus status,
        String summary
) {
}
