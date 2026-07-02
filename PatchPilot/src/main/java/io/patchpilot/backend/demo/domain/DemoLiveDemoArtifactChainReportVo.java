package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoArtifactChainReportVo(
        String status,
        boolean complete,
        String summary,
        String nextAction,
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
        List<Step> steps,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        Instant generatedAt,
        String markdownReport
) {
    public DemoLiveDemoArtifactChainReportVo {
        steps = steps == null ? List.of() : List.copyOf(steps);
        checks = checks == null ? List.of() : List.copyOf(checks);
        evidenceNotes = evidenceNotes == null ? List.of() : List.copyOf(evidenceNotes);
        downloadActions = downloadActions == null ? List.of() : List.copyOf(downloadActions);
    }

    public record Step(
            String name,
            String status,
            String artifactId,
            String summary,
            String nextAction
    ) {
    }

    public record Check(
            String name,
            String status,
            String summary,
            String nextAction
    ) {
    }
}
