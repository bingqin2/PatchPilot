package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoEvaluationRunReadinessEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class DemoLaunchEvidencePackageService {

    private final Supplier<DemoSelfHostedLaunchReadinessVo> launchReadinessSupplier;
    private final Supplier<DemoSessionSnapshotVo> sessionSnapshotSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLaunchEvidencePackageService(
            SelfHostedLaunchReadinessService selfHostedLaunchReadinessService,
            DemoSessionSnapshotService demoSessionSnapshotService
    ) {
        this(
                selfHostedLaunchReadinessService::getReadinessPackage,
                demoSessionSnapshotService::getSessionSnapshot,
                Instant::now
        );
    }

    DemoLaunchEvidencePackageService(
            Supplier<DemoSelfHostedLaunchReadinessVo> launchReadinessSupplier,
            Supplier<DemoSessionSnapshotVo> sessionSnapshotSupplier
    ) {
        this(launchReadinessSupplier, sessionSnapshotSupplier, Instant::now);
    }

    DemoLaunchEvidencePackageService(
            Supplier<DemoSelfHostedLaunchReadinessVo> launchReadinessSupplier,
            Supplier<DemoSessionSnapshotVo> sessionSnapshotSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.launchReadinessSupplier = launchReadinessSupplier;
        this.sessionSnapshotSupplier = sessionSnapshotSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLaunchEvidencePackageVo getPackage() {
        DemoSelfHostedLaunchReadinessVo launchReadiness = launchReadinessSupplier.get();
        DemoSessionSnapshotVo sessionSnapshot = sessionSnapshotSupplier.get();
        DemoEvidenceBundleVo evidenceBundle = sessionSnapshot.evidenceBundle();
        DemoReadinessStatus status = aggregateStatus(launchReadiness, sessionSnapshot, evidenceBundle);
        List<String> liveRunProof = liveRunProof(evidenceBundle);
        List<String> postDemoProof = postDemoProof(evidenceBundle);
        List<String> nextActions = nextActions(status, launchReadiness, sessionSnapshot, evidenceBundle);
        List<String> healthContract = healthContract();
        Instant generatedAt = nowSupplier.get();

        String markdownReport = markdownReport(
                status,
                sessionSnapshot,
                launchReadiness,
                evidenceBundle,
                liveRunProof,
                postDemoProof,
                nextActions,
                healthContract,
                generatedAt
        );

        return new DemoLaunchEvidencePackageVo(
                status,
                status == DemoReadinessStatus.READY,
                summary(status),
                sessionSnapshot.sessionId(),
                launchReadiness.status(),
                evidenceBundle.status(),
                evidenceBundle.handoffFinalizationStatus(),
                evidenceBundle.finalHandoffReportPackageArchiveEvidence().status(),
                evidenceBundle.finalHandoffReportPackageArchiveEvidence().archived()
                        && evidenceBundle.finalHandoffReportPackageArchiveEvidence().downloadReady(),
                evidenceBundle.finalHandoffReportPackageArchiveEvidence().latestArchiveId(),
                evidenceBundle.finalHandoffReportPackageArchiveEvidence().summary(),
                evidenceBundle.recentTask() == null ? null : evidenceBundle.recentTask().id(),
                evidenceBundle.recentPullRequestUrl(),
                evidenceBundle.latestWebhookDelivery() == null ? null : evidenceBundle.latestWebhookDelivery().deliveryId(),
                evidenceBundle.evaluationRunReadiness().latestRunId(),
                evaluationCoverage(evidenceBundle.evaluationRunReadiness()),
                launchReadiness.checks(),
                liveRunProof,
                postDemoProof,
                nextActions,
                healthContract,
                markdownReport,
                generatedAt
        );
    }

    private static DemoReadinessStatus aggregateStatus(
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            DemoSessionSnapshotVo sessionSnapshot,
            DemoEvidenceBundleVo evidenceBundle
    ) {
        if (launchReadiness.status() == DemoReadinessStatus.BLOCKED
                || sessionSnapshot.status() == DemoReadinessStatus.BLOCKED
                || evidenceBundle.status() == DemoReadinessStatus.BLOCKED
                || evidenceBundle.handoffFinalizationStatus() == DemoReadinessStatus.BLOCKED
                || evidenceBundle.finalHandoffReportPackageArchiveEvidence().status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (launchReadiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || sessionSnapshot.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || evidenceBundle.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || evidenceBundle.handoffFinalizationStatus() == DemoReadinessStatus.NEEDS_ATTENTION
                || evidenceBundle.finalHandoffReportPackageArchiveEvidence().status() == DemoReadinessStatus.NEEDS_ATTENTION) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static List<String> liveRunProof(DemoEvidenceBundleVo evidenceBundle) {
        List<String> proof = new ArrayList<>();
        FixTaskVo task = evidenceBundle.recentTask();
        if (task != null) {
            proof.add("Recent task " + task.id() + " reached " + task.status() + ".");
        }
        if (hasText(evidenceBundle.recentPullRequestUrl())) {
            proof.add("Recent Pull Request " + evidenceBundle.recentPullRequestUrl() + " is available.");
        }
        WebhookDeliveryDiagnosticVo delivery = evidenceBundle.latestWebhookDelivery();
        if (delivery != null) {
            String taskId = hasText(delivery.taskId()) ? delivery.taskId() : valueOrNone(delivery.outcomeId());
            proof.add("Latest webhook delivery " + delivery.deliveryId() + " created task " + taskId + ".");
        }
        if (proof.isEmpty()) {
            proof.add("No completed live-run task, Pull Request, or webhook delivery evidence is available yet.");
        }
        return proof;
    }

    private static List<String> postDemoProof(DemoEvidenceBundleVo evidenceBundle) {
        List<String> proof = new ArrayList<>();
        proof.add("Handoff finalization is " + evidenceBundle.handoffFinalizationStatus() + ".");
        if (hasText(evidenceBundle.handoffFinalizationLatestDeliveryReceiptId())) {
            proof.add("Latest delivery receipt " + evidenceBundle.handoffFinalizationLatestDeliveryReceiptId() + " is fresh.");
        } else {
            proof.add(evidenceBundle.handoffFinalizationSummary());
        }
        DemoFinalHandoffReportPackageArchiveEvidenceVo finalArchive =
                evidenceBundle.finalHandoffReportPackageArchiveEvidence();
        if (finalArchive.archived() && finalArchive.downloadReady() && hasText(finalArchive.latestArchiveId())) {
            proof.add("Final handoff report package archive " + finalArchive.latestArchiveId()
                    + " is download-ready.");
        } else {
            proof.add(finalArchive.summary());
        }
        return proof;
    }

    private static List<String> nextActions(
            DemoReadinessStatus status,
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            DemoSessionSnapshotVo sessionSnapshot,
            DemoEvidenceBundleVo evidenceBundle
    ) {
        Set<String> actions = new LinkedHashSet<>();
        actions.addAll(launchReadiness.nextActions());
        actions.addAll(sessionSnapshot.nextActions());
        actions.addAll(evidenceBundle.nextActions());
        if (status == DemoReadinessStatus.BLOCKED) {
            actions.add("Resolve blocked launch evidence checks before sharing or posting a live /agent fix trigger.");
        } else if (status == DemoReadinessStatus.NEEDS_ATTENTION) {
            actions.add("Resolve launch evidence warnings, then regenerate this package.");
        }
        return actions.stream().filter(DemoLaunchEvidencePackageService::hasText).toList();
    }

    private static List<String> evaluationCoverage(DemoEvaluationRunReadinessEvidenceVo readiness) {
        List<String> coverage = new ArrayList<>();
        coverage.addAll(readiness.coveredLanguages());
        coverage.addAll(readiness.coveredBuildSystems());
        return coverage.stream().distinct().toList();
    }

    private static List<String> healthContract() {
        return List.of(
                "GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.",
                "GET /api/demo/launch-evidence-package/report/download only formats the same current package as Markdown."
        );
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "PatchPilot launch evidence package is ready to share.";
            case NEEDS_ATTENTION -> "PatchPilot launch evidence package needs attention before sharing.";
            case BLOCKED -> "PatchPilot launch evidence package is blocked from sharing.";
        };
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            DemoSessionSnapshotVo sessionSnapshot,
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            DemoEvidenceBundleVo evidenceBundle,
            List<String> liveRunProof,
            List<String> postDemoProof,
            List<String> nextActions,
            List<String> healthContract,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder()
                .append("# PatchPilot Demo Launch Evidence Package\n\n")
                .append("- Status: `").append(status).append("`\n")
                .append("- Ready to share: `").append(status == DemoReadinessStatus.READY).append("`\n")
                .append("- Session: `").append(sessionSnapshot.sessionId()).append("`\n")
                .append("- Generated at: `").append(generatedAt).append("`\n\n")
                .append("## Pre-Launch Checks\n\n");
        launchReadiness.checks().forEach(check -> report
                .append("- `").append(check.status()).append("` ")
                .append(check.name()).append(": ")
                .append(check.message()).append(" Action: ")
                .append(check.action()).append("\n"));
        report.append("\n## Live Run Proof\n\n");
        liveRunProof.forEach(proof -> report.append("- ").append(proof).append("\n"));
        report.append("\n## Post-Demo Handoff Proof\n\n");
        postDemoProof.forEach(proof -> report.append("- ").append(proof).append("\n"));
        appendFinalHandoffArchiveProof(report, evidenceBundle.finalHandoffReportPackageArchiveEvidence());
        appendEvaluationProof(report, evidenceBundle.evaluationRunReadiness());
        report.append("\n## Next Actions\n\n");
        nextActions.forEach(action -> report.append("- ").append(action).append("\n"));
        report.append("\n## Side Effect Contract\n\n");
        healthContract.forEach(contract -> report.append("- ").append(contract).append("\n"));
        return report.toString();
    }

    private static void appendEvaluationProof(
            StringBuilder report,
            DemoEvaluationRunReadinessEvidenceVo evaluationReadiness
    ) {
        report.append("\n## Evaluation Proof\n\n")
                .append("- Full evaluation run readiness: `").append(evaluationReadiness.status()).append("`\n")
                .append("- Latest evaluation run: `").append(valueOrNone(evaluationReadiness.latestRunId())).append("`\n")
                .append("- Previous evaluation run: `").append(valueOrNone(evaluationReadiness.previousRunId())).append("`\n")
                .append("- Evaluation coverage: ")
                .append(String.join(", ", evaluationCoverage(evaluationReadiness)))
                .append("\n")
                .append("- Safety categories: ")
                .append(String.join(", ", evaluationReadiness.safetyRejectionCategories()))
                .append("\n")
                .append("- Side effect contract: ")
                .append(evaluationReadiness.sideEffectContract())
                .append("\n");
    }

    private static void appendFinalHandoffArchiveProof(
            StringBuilder report,
            DemoFinalHandoffReportPackageArchiveEvidenceVo finalArchive
    ) {
        report.append("\n## Final Handoff Report Package Archive Proof\n\n")
                .append("- Final handoff report package archive: `")
                .append(valueOrNone(finalArchive.latestArchiveId()))
                .append("`\n")
                .append("- Final handoff report package archive status: `")
                .append(finalArchive.status())
                .append("`\n")
                .append("- Final handoff report package archive download-ready: `")
                .append(finalArchive.archived() && finalArchive.downloadReady())
                .append("`\n")
                .append("- Final handoff report package archive summary: ")
                .append(finalArchive.summary())
                .append("\n");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value : "none";
    }
}
