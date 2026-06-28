package io.patchpilot.backend.task.domain.vo;

public record FixTaskEvidencePackageFinalizationCheckVo(
        String name,
        String status,
        String summary,
        String nextAction
) {
}
