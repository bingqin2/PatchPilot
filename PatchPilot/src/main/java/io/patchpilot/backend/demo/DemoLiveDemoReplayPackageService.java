package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoArtifactChainReportVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReplayPackageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoLiveDemoReplayPackageService {

    private static final String READY = "READY";
    private static final String BLOCKED = "BLOCKED";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String READ_ONLY_CONTRACT =
            "GET /api/demo/live-demo-handoff-package/replay-package is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, mutate Git, "
                    + "send messages, or write to GitHub.";

    private final DemoLiveDemoArtifactChainReportService artifactChainReportService;
    private final Clock clock;

    @Autowired
    public DemoLiveDemoReplayPackageService(DemoLiveDemoArtifactChainReportService artifactChainReportService) {
        this(artifactChainReportService, Clock.systemUTC());
    }

    DemoLiveDemoReplayPackageService(
            DemoLiveDemoArtifactChainReportService artifactChainReportService,
            Clock clock
    ) {
        this.artifactChainReportService = artifactChainReportService;
        this.clock = clock;
    }

    public DemoLiveDemoReplayPackageVo getPackage() {
        DemoLiveDemoArtifactChainReportVo chain = artifactChainReportService.getReport();
        String status = status(chain);
        boolean replayReady = READY.equals(status);
        Instant generatedAt = Instant.now(clock);
        DemoLiveDemoReplayPackageVo replayPackage = new DemoLiveDemoReplayPackageVo(
                status,
                replayReady,
                summary(status),
                nextAction(status),
                chain.status(),
                chain.launchPackageArchiveId(),
                chain.outcomeCloseoutArchiveId(),
                chain.evidenceBundleArchiveId(),
                chain.handoffFinalizationArchiveId(),
                chain.completionCertificateArchiveId(),
                chain.repository(),
                chain.issueNumber(),
                chain.issueUrl(),
                chain.taskId(),
                chain.taskStatus(),
                chain.pullRequestUrl(),
                sections(chain, status),
                evidenceLinks(chain),
                replaySteps(chain, status),
                downloadActions(chain),
                READ_ONLY_CONTRACT,
                generatedAt,
                ""
        );
        return withMarkdown(replayPackage);
    }

    private static String status(DemoLiveDemoArtifactChainReportVo chain) {
        if (READY.equals(chain.status()) && chain.complete()) {
            return READY;
        }
        if (BLOCKED.equals(chain.status())) {
            return BLOCKED;
        }
        return NEEDS_ATTENTION;
    }

    private static String summary(String status) {
        return switch (status) {
            case READY -> "PatchPilot live demo replay package is ready for reviewer walkthrough.";
            case BLOCKED -> "PatchPilot live demo replay package is blocked by an incomplete artifact chain.";
            default -> "PatchPilot live demo replay package needs artifact chain attention before review.";
        };
    }

    private static String nextAction(String status) {
        return switch (status) {
            case READY -> "Share the replay package with reviewers.";
            case BLOCKED -> "Create the missing live demo archives before sharing the replay package.";
            default -> "Regenerate the inconsistent live demo artifacts before sharing the replay package.";
        };
    }

    private static List<DemoLiveDemoReplayPackageVo.Section> sections(
            DemoLiveDemoArtifactChainReportVo chain,
            String status
    ) {
        return List.of(
                section(
                        "Open the live demo issue",
                        presentStatus(chain.issueUrl(), status),
                        chain.issueUrl() == null
                                ? "GitHub issue evidence is missing."
                                : "GitHub issue evidence is present.",
                        "Open the GitHub issue and confirm the original /agent trigger comment."
                ),
                section(
                        "Inspect the generated Pull Request",
                        presentStatus(chain.pullRequestUrl(), status),
                        chain.pullRequestUrl() == null
                                ? "Generated Pull Request evidence is missing."
                                : "Pull Request evidence is present.",
                        "Open the generated Pull Request and inspect the changed files."
                ),
                section(
                        "Review the artifact chain",
                        READY.equals(chain.status()) && chain.complete() ? READY : chain.status(),
                        chain.summary(),
                        "Download the artifact chain report and verify all archive ids are consistent."
                ),
                section(
                        "Download final evidence",
                        presentStatus(chain.completionCertificateArchiveId(), status),
                        chain.completionCertificateArchiveId() == null
                                ? "Completion certificate archive is missing."
                                : "Completion certificate archive is present.",
                        "Download the completion certificate archive as the final demo certificate."
                ),
                section(
                        "Confirm read-only replay boundary",
                        READY,
                        "Replay package generation is read-only.",
                        "Use the replay package without rerunning the agent or mutating GitHub."
                )
        );
    }

    private static DemoLiveDemoReplayPackageVo.Section section(
            String name,
            String status,
            String summary,
            String action
    ) {
        return new DemoLiveDemoReplayPackageVo.Section(name, status, summary, action);
    }

    private static String presentStatus(String value, String packageStatus) {
        if (value != null && !value.isBlank()) {
            return READY;
        }
        return READY.equals(packageStatus) ? NEEDS_ATTENTION : packageStatus;
    }

    private static List<DemoLiveDemoReplayPackageVo.EvidenceLink> evidenceLinks(
            DemoLiveDemoArtifactChainReportVo chain
    ) {
        List<DemoLiveDemoReplayPackageVo.EvidenceLink> links = new ArrayList<>();
        addLink(links, "GitHub issue", chain.issueUrl(), "Original live demo trigger context.");
        addLink(links, "Generated Pull Request", chain.pullRequestUrl(), "Review generated code changes.");
        links.add(new DemoLiveDemoReplayPackageVo.EvidenceLink(
                "Artifact chain report",
                "/api/demo/live-demo-handoff-package/artifact-chain-report/download",
                "Download the final archive consistency report."
        ));
        if (chain.completionCertificateArchiveId() != null) {
            links.add(new DemoLiveDemoReplayPackageVo.EvidenceLink(
                    "Completion certificate archive",
                    "/api/demo/live-demo-handoff-package/completion-certificate/archives/"
                            + chain.completionCertificateArchiveId()
                            + "/report/download",
                    "Download the certified final live demo evidence."
            ));
        }
        return links;
    }

    private static void addLink(
            List<DemoLiveDemoReplayPackageVo.EvidenceLink> links,
            String label,
            String url,
            String description
    ) {
        if (url != null && !url.isBlank()) {
            links.add(new DemoLiveDemoReplayPackageVo.EvidenceLink(label, url, description));
        }
    }

    private static List<String> replaySteps(DemoLiveDemoArtifactChainReportVo chain, String status) {
        if (!READY.equals(status)) {
            return List.of(chain.nextAction());
        }
        return List.of(
                "Open the GitHub issue and confirm the original /agent trigger comment.",
                "Open the generated Pull Request and inspect the changed files.",
                "Download the artifact chain report and verify all archive ids are consistent.",
                "Download the completion certificate archive as the final demo certificate.",
                "Use the read-only replay package as reviewer-facing evidence without rerunning the agent."
        );
    }

    private static List<String> downloadActions(DemoLiveDemoArtifactChainReportVo chain) {
        List<String> actions = new ArrayList<>();
        actions.add("Download live demo replay package.");
        actions.addAll(chain.downloadActions());
        if (chain.completionCertificateArchiveId() != null) {
            actions.add("Download live demo completion certificate archive "
                    + chain.completionCertificateArchiveId()
                    + ".");
        }
        return actions.stream().distinct().toList();
    }

    private static DemoLiveDemoReplayPackageVo withMarkdown(DemoLiveDemoReplayPackageVo replayPackage) {
        return new DemoLiveDemoReplayPackageVo(
                replayPackage.status(),
                replayPackage.replayReady(),
                replayPackage.summary(),
                replayPackage.nextAction(),
                replayPackage.artifactChainStatus(),
                replayPackage.launchPackageArchiveId(),
                replayPackage.outcomeCloseoutArchiveId(),
                replayPackage.evidenceBundleArchiveId(),
                replayPackage.handoffFinalizationArchiveId(),
                replayPackage.completionCertificateArchiveId(),
                replayPackage.repository(),
                replayPackage.issueNumber(),
                replayPackage.issueUrl(),
                replayPackage.taskId(),
                replayPackage.taskStatus(),
                replayPackage.pullRequestUrl(),
                replayPackage.sections(),
                replayPackage.evidenceLinks(),
                replayPackage.replaySteps(),
                replayPackage.downloadActions(),
                replayPackage.sideEffectContract(),
                replayPackage.generatedAt(),
                markdownReport(replayPackage)
        );
    }

    private static String markdownReport(DemoLiveDemoReplayPackageVo replayPackage) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# PatchPilot Live Demo Replay Package\n\n");
        markdown.append("- Status: `").append(replayPackage.status()).append("`\n");
        markdown.append("- Replay ready: `").append(replayPackage.replayReady()).append("`\n");
        markdown.append("- Summary: ").append(replayPackage.summary()).append("\n");
        markdown.append("- Next action: ").append(replayPackage.nextAction()).append("\n");
        markdown.append("- Artifact chain status: `").append(replayPackage.artifactChainStatus()).append("`\n");
        markdown.append("- Repository: ").append(valueOrMissing(replayPackage.repository())).append("\n");
        markdown.append("- Issue: #").append(replayPackage.issueNumber()).append("\n");
        markdown.append("- Issue URL: ").append(valueOrMissing(replayPackage.issueUrl())).append("\n");
        markdown.append("- Task: `").append(valueOrMissing(replayPackage.taskId())).append("`\n");
        markdown.append("- Task status: `").append(valueOrMissing(replayPackage.taskStatus())).append("`\n");
        markdown.append("- Pull Request: ").append(valueOrMissing(replayPackage.pullRequestUrl())).append("\n");
        markdown.append("- Completion certificate archive: `")
                .append(valueOrMissing(replayPackage.completionCertificateArchiveId()))
                .append("`\n");
        markdown.append("- Generated at: `").append(replayPackage.generatedAt()).append("`\n\n");
        appendSections(markdown, replayPackage.sections());
        appendLinks(markdown, replayPackage.evidenceLinks());
        appendList(markdown, "Replay Steps", replayPackage.replaySteps());
        appendList(markdown, "Download Actions", replayPackage.downloadActions());
        markdown.append("## Side Effect Contract\n\n").append(replayPackage.sideEffectContract()).append("\n");
        return markdown.toString();
    }

    private static void appendSections(StringBuilder markdown, List<DemoLiveDemoReplayPackageVo.Section> sections) {
        markdown.append("## Reviewer Walkthrough\n\n");
        for (DemoLiveDemoReplayPackageVo.Section section : sections) {
            markdown.append("- `").append(section.status()).append("` ")
                    .append(section.name()).append(": ")
                    .append(section.summary()).append(" Action: ")
                    .append(section.action())
                    .append("\n");
        }
        markdown.append("\n");
    }

    private static void appendLinks(StringBuilder markdown, List<DemoLiveDemoReplayPackageVo.EvidenceLink> links) {
        markdown.append("## Evidence Links\n\n");
        for (DemoLiveDemoReplayPackageVo.EvidenceLink link : links) {
            markdown.append("- ").append(link.label()).append(": ")
                    .append(link.url()).append(" - ")
                    .append(link.description())
                    .append("\n");
        }
        markdown.append("\n");
    }

    private static void appendList(StringBuilder markdown, String heading, List<String> items) {
        markdown.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            markdown.append("- ").append(item).append("\n");
        }
        markdown.append("\n");
    }

    private static String valueOrMissing(String value) {
        return value == null || value.isBlank() ? "missing" : value;
    }
}
