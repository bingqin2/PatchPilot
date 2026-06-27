package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoHandoffShareChecklistVo(
        DemoReadinessStatus status,
        String summary,
        String nextAction,
        List<DemoHandoffShareChecklistItemVo> checks,
        String markdownReport,
        Instant generatedAt
) {
}
