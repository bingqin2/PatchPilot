package io.patchpilot.backend.demo.domain;

import java.util.List;

public record DemoReadinessVo(
        DemoReadinessStatus status,
        String summary,
        List<DemoReadinessCheckVo> checks,
        List<String> nextActions
) {

    public DemoReadinessVo {
        checks = List.copyOf(checks);
        nextActions = List.copyOf(nextActions);
    }
}
