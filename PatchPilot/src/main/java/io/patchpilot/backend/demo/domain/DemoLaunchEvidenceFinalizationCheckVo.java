package io.patchpilot.backend.demo.domain;

public record DemoLaunchEvidenceFinalizationCheckVo(
        String name,
        DemoReadinessStatus status,
        String summary,
        String nextAction
) {
}
