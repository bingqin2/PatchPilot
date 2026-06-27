package io.patchpilot.backend.demo.domain;

public record DemoHandoffReadinessCheckVo(
        String name,
        DemoReadinessStatus status,
        String summary,
        String nextAction
) {

    public DemoHandoffReadinessCheckVo(String name, DemoReadinessStatus status, String summary) {
        this(name, status, summary, "No action recorded.");
    }
}
