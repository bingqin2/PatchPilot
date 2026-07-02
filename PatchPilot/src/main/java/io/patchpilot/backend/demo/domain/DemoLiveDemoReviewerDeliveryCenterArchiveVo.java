package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoReviewerDeliveryCenterArchiveVo(
        String id,
        String status,
        boolean deliverable,
        String summary,
        String nextAction,
        String repository,
        long issueNumber,
        String issueUrl,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
        List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> readinessCards,
        List<String> blockers,
        List<DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink> evidenceLinks,
        List<String> downloadActions,
        String sideEffectContract,
        Instant centerGeneratedAt,
        Instant archivedAt,
        String report
) {
    public DemoLiveDemoReviewerDeliveryCenterArchiveVo {
        readinessCards = readinessCards == null ? List.of() : List.copyOf(readinessCards);
        blockers = blockers == null ? List.of() : List.copyOf(blockers);
        evidenceLinks = evidenceLinks == null ? List.of() : List.copyOf(evidenceLinks);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }
}
