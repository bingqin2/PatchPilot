package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoEndToEndAcceptanceMatrixVo(
        String status,
        boolean readyForFinalDemo,
        int readinessPercent,
        int readyCount,
        int needsAttentionCount,
        int blockedCount,
        int totalCount,
        String summary,
        List<String> nextActions,
        String sideEffectContract,
        List<DemoEndToEndAcceptanceMatrixItemVo> items,
        Instant generatedAt,
        String markdownReport
) {
    public DemoEndToEndAcceptanceMatrixVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
        items = items == null ? List.of() : List.copyOf(items);
    }
}
