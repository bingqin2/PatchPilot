package io.patchpilot.backend.demo.domain;

public record DemoHandoffShareChecklistItemVo(
        String name,
        DemoReadinessStatus status,
        String summary,
        String nextAction
) {
}
