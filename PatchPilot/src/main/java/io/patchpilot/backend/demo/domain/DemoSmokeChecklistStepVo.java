package io.patchpilot.backend.demo.domain;

public record DemoSmokeChecklistStepVo(
        int order,
        String name,
        DemoSmokeChecklistStatus status,
        String message,
        String evidence,
        String action
) {
}
