package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoReplayPackageVo(
        String status,
        boolean replayReady,
        String summary,
        String nextAction,
        String artifactChainStatus,
        String launchPackageArchiveId,
        String outcomeCloseoutArchiveId,
        String evidenceBundleArchiveId,
        String handoffFinalizationArchiveId,
        String completionCertificateArchiveId,
        String repository,
        long issueNumber,
        String issueUrl,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
        List<Section> sections,
        List<EvidenceLink> evidenceLinks,
        List<String> replaySteps,
        List<String> downloadActions,
        String sideEffectContract,
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveDemoReplayPackageVo {
        sections = sections == null ? List.of() : List.copyOf(sections);
        evidenceLinks = evidenceLinks == null ? List.of() : List.copyOf(evidenceLinks);
        replaySteps = replaySteps == null ? List.of() : List.copyOf(replaySteps);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }

    public record Section(
            String name,
            String status,
            String summary,
            String action
    ) {
    }

    public record EvidenceLink(
            String label,
            String url,
            String description
    ) {
    }
}
