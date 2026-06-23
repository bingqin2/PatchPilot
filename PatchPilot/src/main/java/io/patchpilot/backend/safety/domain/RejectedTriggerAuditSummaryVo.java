package io.patchpilot.backend.safety.domain;

import java.util.List;

public record RejectedTriggerAuditSummaryVo(
        long totalCount,
        List<RejectedTriggerCountVo> categoryCounts,
        List<RejectedTriggerCountVo> sourceCounts,
        List<RejectedTriggerCountVo> triggerUserCounts,
        List<RejectedTriggerCountVo> repositoryCounts
) {
}
