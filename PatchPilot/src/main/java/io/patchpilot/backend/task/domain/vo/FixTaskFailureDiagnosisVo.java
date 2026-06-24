package io.patchpilot.backend.task.domain.vo;

public record FixTaskFailureDiagnosisVo(
        String category,
        String nextAction,
        String safeReason
) {
}
