package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoArtifactChainReportVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReplayPackageVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoReviewerDeliveryCenterService {

    private static final String READY = "READY";
    private static final String BLOCKED = "BLOCKED";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String READ_ONLY_CONTRACT =
            "GET /api/demo/live-demo-handoff-package/reviewer-delivery-center is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, mutate Git, "
                    + "send messages, or write to GitHub.";

    private final Supplier<DemoLiveDemoHandoffPackageVo> handoffPackageSupplier;
    private final Supplier<DemoLiveDemoArtifactChainReportVo> artifactChainReportSupplier;
    private final Supplier<DemoLiveDemoCompletionCertificateVo> completionCertificateSupplier;
    private final Supplier<DemoLiveDemoReplayPackageVo> replayPackageSupplier;
    private final Clock clock;

    @Autowired
    public DemoLiveDemoReviewerDeliveryCenterService(
            DemoLiveDemoHandoffPackageService handoffPackageService,
            DemoLiveDemoArtifactChainReportService artifactChainReportService,
            DemoLiveDemoCompletionCertificateService completionCertificateService,
            DemoLiveDemoReplayPackageService replayPackageService
    ) {
        this(
                handoffPackageService::createPackage,
                artifactChainReportService::getReport,
                completionCertificateService::getCertificate,
                replayPackageService::getPackage,
                Clock.systemUTC()
        );
    }

    DemoLiveDemoReviewerDeliveryCenterService(
            Supplier<DemoLiveDemoHandoffPackageVo> handoffPackageSupplier,
            Supplier<DemoLiveDemoArtifactChainReportVo> artifactChainReportSupplier,
            Supplier<DemoLiveDemoCompletionCertificateVo> completionCertificateSupplier,
            Supplier<DemoLiveDemoReplayPackageVo> replayPackageSupplier,
            Clock clock
    ) {
        this.handoffPackageSupplier = handoffPackageSupplier;
        this.artifactChainReportSupplier = artifactChainReportSupplier;
        this.completionCertificateSupplier = completionCertificateSupplier;
        this.replayPackageSupplier = replayPackageSupplier;
        this.clock = clock;
    }

    public DemoLiveDemoReviewerDeliveryCenterVo getCenter() {
        DemoLiveDemoHandoffPackageVo handoffPackage = handoffPackageSupplier.get();
        DemoLiveDemoArtifactChainReportVo artifactChain = artifactChainReportSupplier.get();
        DemoLiveDemoCompletionCertificateVo certificate = completionCertificateSupplier.get();
        DemoLiveDemoReplayPackageVo replayPackage = replayPackageSupplier.get();
        List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> cards = readinessCards(
                handoffPackage,
                artifactChain,
                certificate,
                replayPackage
        );
        List<String> blockers = blockers(cards);
        String status = status(cards);
        boolean deliverable = READY.equals(status);
        Instant generatedAt = Instant.now(clock);
        DemoLiveDemoReviewerDeliveryCenterVo center = new DemoLiveDemoReviewerDeliveryCenterVo(
                status,
                deliverable,
                summary(status),
                nextAction(status, blockers),
                firstNonBlank(
                        replayPackage.repository(),
                        artifactChain.repository(),
                        certificate.repository(),
                        handoffPackage.repository()
                ),
                firstPositive(
                        replayPackage.issueNumber(),
                        artifactChain.issueNumber(),
                        certificate.issueNumber(),
                        handoffPackage.issueNumber()
                ),
                firstNonBlank(
                        replayPackage.issueUrl(),
                        artifactChain.issueUrl(),
                        certificate.issueUrl(),
                        handoffPackage.issueUrl()
                ),
                firstNonBlank(
                        replayPackage.taskId(),
                        artifactChain.taskId(),
                        certificate.taskId(),
                        handoffPackage.taskId()
                ),
                firstNonBlank(
                        replayPackage.taskStatus(),
                        artifactChain.taskStatus(),
                        certificate.taskStatus(),
                        handoffPackage.taskStatus()
                ),
                firstNonBlank(
                        replayPackage.pullRequestUrl(),
                        artifactChain.pullRequestUrl(),
                        certificate.pullRequestUrl(),
                        handoffPackage.pullRequestUrl()
                ),
                cards,
                blockers,
                evidenceLinks(handoffPackage, artifactChain, certificate, replayPackage),
                downloadActions(artifactChain, certificate, replayPackage),
                READ_ONLY_CONTRACT,
                generatedAt,
                ""
        );
        return withMarkdown(center);
    }

    private static List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> readinessCards(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoArtifactChainReportVo artifactChain,
            DemoLiveDemoCompletionCertificateVo certificate,
            DemoLiveDemoReplayPackageVo replayPackage
    ) {
        return List.of(
                card(
                        "Reviewer handoff package",
                        handoffPackage.status(),
                        handoffPackage.readyForReview(),
                        handoffPackage.summary(),
                        firstOrDefault(
                                handoffPackage.deliveryInstructions(),
                                "Refresh the reviewer handoff package."
                        )
                ),
                card(
                        "Artifact chain",
                        artifactChain.status(),
                        artifactChain.complete(),
                        artifactChain.summary(),
                        artifactChain.nextAction()
                ),
                card(
                        "Completion certificate",
                        certificate.status(),
                        certificate.certified(),
                        certificate.summary(),
                        certificate.nextAction()
                ),
                card(
                        "Replay package",
                        replayPackage.status(),
                        replayPackage.replayReady(),
                        replayPackage.summary(),
                        replayPackage.nextAction()
                )
        );
    }

    private static DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard card(
            String name,
            String status,
            boolean ready,
            String summary,
            String nextAction
    ) {
        return new DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard(name, status, ready, summary, nextAction);
    }

    private static List<String> blockers(List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> cards) {
        return cards.stream()
                .filter(card -> !card.ready() || !READY.equals(card.status()))
                .map(card -> card.name() + " is " + card.status() + ": " + card.summary())
                .toList();
    }

    private static String status(List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> cards) {
        if (cards.stream().allMatch(card -> card.ready() && READY.equals(card.status()))) {
            return READY;
        }
        if (cards.stream().anyMatch(card -> BLOCKED.equals(card.status()))) {
            return BLOCKED;
        }
        return NEEDS_ATTENTION;
    }

    private static String summary(String status) {
        return switch (status) {
            case READY -> "PatchPilot live demo reviewer delivery center is ready.";
            case BLOCKED -> "PatchPilot live demo reviewer delivery center is blocked.";
            default -> "PatchPilot live demo reviewer delivery center needs attention.";
        };
    }

    private static String nextAction(String status, List<String> blockers) {
        if (!READY.equals(status) && !blockers.isEmpty()) {
            return blockers.get(0);
        }
        return "Send the replay package and final evidence links to reviewers.";
    }

    private static List<DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink> evidenceLinks(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoArtifactChainReportVo artifactChain,
            DemoLiveDemoCompletionCertificateVo certificate,
            DemoLiveDemoReplayPackageVo replayPackage
    ) {
        List<DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink> links = new ArrayList<>();
        addLink(links, "GitHub issue", firstNonBlank(
                replayPackage.issueUrl(),
                artifactChain.issueUrl(),
                certificate.issueUrl(),
                handoffPackage.issueUrl()
        ), "Original live demo issue context.");
        addLink(links, "Generated Pull Request", firstNonBlank(
                replayPackage.pullRequestUrl(),
                artifactChain.pullRequestUrl(),
                certificate.pullRequestUrl(),
                handoffPackage.pullRequestUrl()
        ), "Generated code review target.");
        links.add(new DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink(
                "Reviewer handoff package",
                "/api/demo/live-demo-handoff-package/report/download",
                "Download the reviewer handoff package."
        ));
        links.add(new DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink(
                "Artifact chain report",
                "/api/demo/live-demo-handoff-package/artifact-chain-report/download",
                "Download the final artifact consistency report."
        ));
        links.add(new DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink(
                "Completion certificate",
                "/api/demo/live-demo-handoff-package/completion-certificate/report/download",
                "Download the final completion certificate."
        ));
        links.add(new DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink(
                "Replay package",
                "/api/demo/live-demo-handoff-package/replay-package/download",
                "Download the reviewer replay package."
        ));
        return links;
    }

    private static void addLink(
            List<DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink> links,
            String label,
            String url,
            String description
    ) {
        if (url != null && !url.isBlank()) {
            links.add(new DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink(label, url, description));
        }
    }

    private static List<String> downloadActions(
            DemoLiveDemoArtifactChainReportVo artifactChain,
            DemoLiveDemoCompletionCertificateVo certificate,
            DemoLiveDemoReplayPackageVo replayPackage
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download live demo reviewer delivery center.");
        actions.add("Download live demo handoff package.");
        actions.addAll(artifactChain.downloadActions());
        actions.addAll(certificate.downloadActions());
        actions.addAll(replayPackage.downloadActions());
        return actions.stream().distinct().toList();
    }

    private static DemoLiveDemoReviewerDeliveryCenterVo withMarkdown(
            DemoLiveDemoReviewerDeliveryCenterVo center
    ) {
        return new DemoLiveDemoReviewerDeliveryCenterVo(
                center.status(),
                center.deliverable(),
                center.summary(),
                center.nextAction(),
                center.repository(),
                center.issueNumber(),
                center.issueUrl(),
                center.taskId(),
                center.taskStatus(),
                center.pullRequestUrl(),
                center.readinessCards(),
                center.blockers(),
                center.evidenceLinks(),
                center.downloadActions(),
                center.sideEffectContract(),
                center.generatedAt(),
                markdownReport(center)
        );
    }

    private static String markdownReport(DemoLiveDemoReviewerDeliveryCenterVo center) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# PatchPilot Live Demo Reviewer Delivery Center\n\n");
        markdown.append("- Status: `").append(center.status()).append("`\n");
        markdown.append("- Deliverable: `").append(center.deliverable()).append("`\n");
        markdown.append("- Summary: ").append(center.summary()).append("\n");
        markdown.append("- Next action: ").append(center.nextAction()).append("\n");
        markdown.append("- Repository: ").append(valueOrMissing(center.repository())).append("\n");
        markdown.append("- Issue: #").append(center.issueNumber()).append("\n");
        markdown.append("- Issue URL: ").append(valueOrMissing(center.issueUrl())).append("\n");
        markdown.append("- Task: `").append(valueOrMissing(center.taskId())).append("`\n");
        markdown.append("- Task status: `").append(valueOrMissing(center.taskStatus())).append("`\n");
        markdown.append("- Pull Request: ").append(valueOrMissing(center.pullRequestUrl())).append("\n");
        markdown.append("- Generated at: `").append(center.generatedAt()).append("`\n\n");
        appendCards(markdown, center.readinessCards());
        appendList(markdown, "Blockers", center.blockers());
        appendLinks(markdown, center.evidenceLinks());
        appendList(markdown, "Download Actions", center.downloadActions());
        markdown.append("## Side Effect Contract\n\n").append(center.sideEffectContract()).append("\n");
        return markdown.toString();
    }

    private static void appendCards(
            StringBuilder markdown,
            List<DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard> cards
    ) {
        markdown.append("## Readiness Cards\n\n");
        for (DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard card : cards) {
            markdown.append("- `").append(card.status()).append("` ")
                    .append(card.name())
                    .append(": ")
                    .append(card.summary())
                    .append(" Next: ")
                    .append(card.nextAction())
                    .append("\n");
        }
        markdown.append("\n");
    }

    private static void appendLinks(
            StringBuilder markdown,
            List<DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink> links
    ) {
        markdown.append("## Evidence Links\n\n");
        for (DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink link : links) {
            markdown.append("- ").append(link.label()).append(": ")
                    .append(link.url()).append(" - ")
                    .append(link.description())
                    .append("\n");
        }
        markdown.append("\n");
    }

    private static void appendList(StringBuilder markdown, String heading, List<String> items) {
        markdown.append("## ").append(heading).append("\n\n");
        if (items.isEmpty()) {
            markdown.append("- None.\n\n");
            return;
        }
        for (String item : items) {
            markdown.append("- ").append(item).append("\n");
        }
        markdown.append("\n");
    }

    private static String firstOrDefault(List<String> values, String fallback) {
        return values == null || values.isEmpty() ? fallback : values.get(0);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static long firstPositive(long... values) {
        for (long value : values) {
            if (value > 0) {
                return value;
            }
        }
        return 0;
    }

    private static String valueOrMissing(String value) {
        return value == null || value.isBlank() ? "missing" : value;
    }
}
