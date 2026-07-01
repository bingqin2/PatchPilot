package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;
import java.util.List;

public record ExternalExposureReadinessVo(
        String status,
        boolean safeToExpose,
        int readyCount,
        int needsAttentionCount,
        int blockedCount,
        int totalCount,
        String summary,
        List<String> nextActions,
        String sideEffectContract,
        List<ExternalExposureReadinessCheckVo> checks,
        Instant generatedAt,
        String markdownReport
) {
    public ExternalExposureReadinessVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
        checks = checks == null ? List.of() : List.copyOf(checks);
    }
}
