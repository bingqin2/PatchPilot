package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoHandoffShareCenterVo(
        DemoReadinessStatus status,
        boolean shareReady,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestSessionId,
        String latestCreatedAt,
        List<String> downloadActions,
        List<String> evidenceNotes,
        String markdownReport,
        Instant generatedAt
) {
}
