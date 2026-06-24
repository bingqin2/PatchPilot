package io.patchpilot.backend.task.domain.vo;

public record FixTaskFailureCauseSummaryVo(
        String cause,
        long count,
        String nextAction
) {
}
