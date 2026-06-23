package io.patchpilot.backend.demo.domain;

import java.util.List;

public record DemoSmokeChecklistVo(
        DemoSmokeChecklistStatus status,
        String summary,
        List<DemoSmokeChecklistStepVo> steps,
        List<String> nextActions
) {

    public DemoSmokeChecklistVo {
        steps = List.copyOf(steps);
        nextActions = List.copyOf(nextActions);
    }
}
