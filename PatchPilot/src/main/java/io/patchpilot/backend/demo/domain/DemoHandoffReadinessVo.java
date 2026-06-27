package io.patchpilot.backend.demo.domain;

import java.util.List;

public record DemoHandoffReadinessVo(
        DemoReadinessStatus status,
        String summary,
        List<DemoHandoffReadinessCheckVo> checks
) {

    public DemoHandoffReadinessVo {
        checks = checks == null ? List.of() : List.copyOf(checks);
    }
}
