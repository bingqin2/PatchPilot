package io.patchpilot.backend.agent.workflow.domain;

import java.util.List;

public record FixPlan(
        String summary,
        List<String> targetFiles,
        List<String> steps,
        String risk
) {
}
