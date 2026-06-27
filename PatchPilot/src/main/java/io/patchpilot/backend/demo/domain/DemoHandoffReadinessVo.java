package io.patchpilot.backend.demo.domain;

import java.util.List;

public record DemoHandoffReadinessVo(
        DemoReadinessStatus status,
        String summary,
        String nextAction,
        List<DemoHandoffReadinessCheckVo> checks
) {

    public DemoHandoffReadinessVo(DemoReadinessStatus status, String summary, List<DemoHandoffReadinessCheckVo> checks) {
        this(status, summary, "No next action recorded.", checks);
    }

    public DemoHandoffReadinessVo {
        checks = checks == null ? List.of() : List.copyOf(checks);
    }
}
