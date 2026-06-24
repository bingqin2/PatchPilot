package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoScriptVo(
        DemoReadinessStatus status,
        String summary,
        List<DemoScriptStepVo> steps,
        List<String> healthContract,
        List<String> nextActions,
        Instant generatedAt
) {

    public DemoScriptVo {
        steps = List.copyOf(steps);
        healthContract = List.copyOf(healthContract);
        nextActions = List.copyOf(nextActions);
    }
}
