package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoSessionSnapshotVo(
        String sessionId,
        DemoReadinessStatus status,
        String summary,
        Instant generatedAt,
        DemoEvidenceBundleVo evidenceBundle,
        DemoScriptVo script,
        String runbook,
        List<String> operatorChecklist,
        List<String> healthContract,
        String shareSummary,
        List<String> nextActions
) {

    public DemoSessionSnapshotVo {
        operatorChecklist = List.copyOf(operatorChecklist);
        healthContract = List.copyOf(healthContract);
        nextActions = List.copyOf(nextActions);
    }
}
