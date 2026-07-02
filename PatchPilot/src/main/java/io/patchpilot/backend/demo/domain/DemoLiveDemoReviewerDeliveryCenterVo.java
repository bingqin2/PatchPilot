package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoReviewerDeliveryCenterVo(
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
        List<ReadinessCard> readinessCards,
        List<String> blockers,
        List<EvidenceLink> evidenceLinks,
        List<String> downloadActions,
        String sideEffectContract,
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveDemoReviewerDeliveryCenterVo {
        readinessCards = readinessCards == null ? List.of() : List.copyOf(readinessCards);
        blockers = blockers == null ? List.of() : List.copyOf(blockers);
        evidenceLinks = evidenceLinks == null ? List.of() : List.copyOf(evidenceLinks);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }

    public record ReadinessCard(
            String name,
            String status,
            boolean ready,
            String summary,
            String nextAction
    ) {
    }

    public record EvidenceLink(
            String label,
            String url,
            String description
    ) {
    }
}
