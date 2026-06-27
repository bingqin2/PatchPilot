package io.patchpilot.backend.demo.domain;

public record DemoHandoffFinalizationCheckVo(
        String name,
        DemoReadinessStatus status,
        String summary,
        String nextAction
) {
}
