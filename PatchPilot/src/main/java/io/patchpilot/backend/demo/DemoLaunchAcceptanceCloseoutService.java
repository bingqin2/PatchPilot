package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutCheckVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoLaunchAcceptanceCloseoutService {

    private final SelfHostedLaunchReadinessService launchReadinessService;
    private final DemoLaunchEvidencePackageService launchEvidencePackageService;
    private final DemoLaunchEvidenceShareCenterService shareCenterService;
    private final DemoLaunchEvidenceFinalizationService finalizationService;
    private final Clock clock;

    @Autowired
    public DemoLaunchAcceptanceCloseoutService(
            SelfHostedLaunchReadinessService launchReadinessService,
            DemoLaunchEvidencePackageService launchEvidencePackageService,
            DemoLaunchEvidenceShareCenterService shareCenterService,
            DemoLaunchEvidenceFinalizationService finalizationService
    ) {
        this(
                launchReadinessService,
                launchEvidencePackageService,
                shareCenterService,
                finalizationService,
                Clock.systemUTC()
        );
    }

    DemoLaunchAcceptanceCloseoutService(
            SelfHostedLaunchReadinessService launchReadinessService,
            DemoLaunchEvidencePackageService launchEvidencePackageService,
            DemoLaunchEvidenceShareCenterService shareCenterService,
            DemoLaunchEvidenceFinalizationService finalizationService,
            Clock clock
    ) {
        this.launchReadinessService = launchReadinessService;
        this.launchEvidencePackageService = launchEvidencePackageService;
        this.shareCenterService = shareCenterService;
        this.finalizationService = finalizationService;
        this.clock = clock;
    }

    public DemoLaunchAcceptanceCloseoutVo getCloseout() {
        DemoSelfHostedLaunchReadinessVo launchReadiness = launchReadinessService.getReadinessPackage();
        DemoLaunchEvidencePackageVo evidencePackage = launchEvidencePackageService.getPackage();
        DemoLaunchEvidenceShareCenterVo shareCenter = shareCenterService.getShareCenter();
        DemoLaunchEvidenceFinalizationVo finalization = finalizationService.getFinalizationGate();
        Instant generatedAt = Instant.now(clock);
        DemoReadinessStatus status = status(launchReadiness, evidencePackage, shareCenter, finalization);
        boolean accepted = status == DemoReadinessStatus.READY;
        List<DemoLaunchAcceptanceCloseoutCheckVo> checks = checks(
                launchReadiness,
                evidencePackage,
                shareCenter,
                finalization
        );
        List<String> evidenceNotes = evidenceNotes(launchReadiness, evidencePackage, shareCenter, finalization);
        List<String> downloadActions = downloadActions(evidencePackage);
        String summary = summary(status);
        String nextAction = nextAction(status, evidencePackage, shareCenter, finalization);
        String markdownReport = markdownReport(
                status,
                accepted,
                summary,
                nextAction,
                evidencePackage,
                shareCenter,
                finalization,
                checks,
                evidenceNotes,
                downloadActions,
                generatedAt
        );
        return new DemoLaunchAcceptanceCloseoutVo(
                status,
                accepted,
                summary,
                nextAction,
                evidencePackage.sessionId(),
                evidencePackage.latestTaskId(),
                evidencePackage.latestPullRequestUrl(),
                evidencePackage.latestWebhookDeliveryId(),
                evidencePackage.evaluationRunId(),
                shareCenter.latestArchiveId(),
                evidencePackage.finalHandoffReportPackageArchiveStatus(),
                evidencePackage.finalHandoffReportPackageArchiveReady(),
                evidencePackage.finalHandoffReportPackageArchiveId(),
                evidencePackage.finalHandoffReportPackageArchiveSummary(),
                finalization.latestDeliveryReceiptId(),
                finalization.latestDeliveryTarget(),
                finalization.latestDeliveryChannel(),
                finalization.latestDeliveredAt(),
                finalization.deliveryReceiptFreshness(),
                generatedAt,
                checks,
                evidenceNotes,
                downloadActions,
                markdownReport
        );
    }

    private static DemoReadinessStatus status(
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            DemoLaunchEvidencePackageVo evidencePackage,
            DemoLaunchEvidenceShareCenterVo shareCenter,
            DemoLaunchEvidenceFinalizationVo finalization
    ) {
        if (launchReadiness.status() == DemoReadinessStatus.BLOCKED
                || evidencePackage.status() == DemoReadinessStatus.BLOCKED
                || evidencePackage.finalHandoffReportPackageArchiveStatus() == DemoReadinessStatus.BLOCKED
                || shareCenterStatus(shareCenter) == DemoReadinessStatus.BLOCKED
                || finalization.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (launchReadiness.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || evidencePackage.status() == DemoReadinessStatus.NEEDS_ATTENTION
                || evidencePackage.finalHandoffReportPackageArchiveStatus() == DemoReadinessStatus.NEEDS_ATTENTION
                || !evidencePackage.finalHandoffReportPackageArchiveReady()
                || !shareCenter.shareReady()
                || !finalization.finalized()) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static List<DemoLaunchAcceptanceCloseoutCheckVo> checks(
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            DemoLaunchEvidencePackageVo evidencePackage,
            DemoLaunchEvidenceShareCenterVo shareCenter,
            DemoLaunchEvidenceFinalizationVo finalization
    ) {
        return List.of(
                new DemoLaunchAcceptanceCloseoutCheckVo(
                        "Self-hosted launch readiness",
                        launchReadiness.status(),
                        launchReadiness.summary(),
                        firstAction(launchReadiness.nextActions())
                ),
                new DemoLaunchAcceptanceCloseoutCheckVo(
                        "Launch evidence package",
                        evidencePackage.status(),
                        evidencePackage.summary(),
                        firstAction(evidencePackage.nextActions())
                ),
                new DemoLaunchAcceptanceCloseoutCheckVo(
                        "Final handoff package archive",
                        evidencePackage.finalHandoffReportPackageArchiveStatus(),
                        evidencePackage.finalHandoffReportPackageArchiveSummary(),
                        evidencePackage.finalHandoffReportPackageArchiveReady()
                                ? "No action needed."
                                : "Archive a final handoff report package before treating launch closeout as accepted."
                ),
                new DemoLaunchAcceptanceCloseoutCheckVo(
                        "Launch evidence share center",
                        shareCenterCheckStatus(shareCenter),
                        shareCenter.summary(),
                        shareCenter.deliveryReceiptFresh() ? "No action needed." : receiptNextAction(shareCenter)
                ),
                new DemoLaunchAcceptanceCloseoutCheckVo(
                        "Launch evidence finalization",
                        finalization.status(),
                        finalization.summary(),
                        finalization.finalized() ? "No action needed." : finalization.nextAction()
                )
        );
    }

    private static DemoReadinessStatus shareCenterStatus(DemoLaunchEvidenceShareCenterVo shareCenter) {
        if ("NO_ARCHIVE".equals(shareCenter.status())) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.valueOf(shareCenter.status());
    }

    private static DemoReadinessStatus shareCenterCheckStatus(DemoLaunchEvidenceShareCenterVo shareCenter) {
        DemoReadinessStatus status = shareCenterStatus(shareCenter);
        if (status == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return shareCenter.shareReady() && shareCenter.deliveryReceiptFresh()
                ? status
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String receiptNextAction(DemoLaunchEvidenceShareCenterVo shareCenter) {
        if (!shareCenter.shareReady()) {
            return shareCenter.nextAction();
        }
        if ("STALE".equals(shareCenter.deliveryReceiptFreshness())) {
            return "Record a fresh delivery receipt for launch evidence archive "
                    + valueOrNone(shareCenter.latestArchiveId()) + ".";
        }
        return "Record a launch evidence delivery receipt after sharing the package.";
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "PatchPilot launch acceptance closeout is complete.";
            case NEEDS_ATTENTION -> "PatchPilot launch acceptance closeout needs attention.";
            case BLOCKED -> "PatchPilot launch acceptance closeout is blocked.";
        };
    }

    private static String nextAction(
            DemoReadinessStatus status,
            DemoLaunchEvidencePackageVo evidencePackage,
            DemoLaunchEvidenceShareCenterVo shareCenter,
            DemoLaunchEvidenceFinalizationVo finalization
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use this closeout report as the final self-hosted launch acceptance record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Resolve blocked launch closeout checks before treating the demo as accepted.";
        }
        if (!finalization.finalized()) {
            return finalization.nextAction();
        }
        if (!evidencePackage.finalHandoffReportPackageArchiveReady()) {
            return "Archive a final handoff report package before treating launch closeout as accepted.";
        }
        if (!shareCenter.shareReady()) {
            return shareCenter.nextAction();
        }
        return firstAction(evidencePackage.nextActions());
    }

    private static List<String> evidenceNotes(
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            DemoLaunchEvidencePackageVo evidencePackage,
            DemoLaunchEvidenceShareCenterVo shareCenter,
            DemoLaunchEvidenceFinalizationVo finalization
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Launch readiness status is " + launchReadiness.status() + ".");
        notes.add("Launch evidence package status is " + evidencePackage.status() + ".");
        if (evidencePackage.finalHandoffReportPackageArchiveReady()) {
            notes.add("Final handoff report package archive "
                    + valueOrNone(evidencePackage.finalHandoffReportPackageArchiveId())
                    + " is " + evidencePackage.finalHandoffReportPackageArchiveStatus()
                    + " and download-ready.");
        } else {
            notes.add("Final handoff report package archive is not ready: "
                    + evidencePackage.finalHandoffReportPackageArchiveSummary());
        }
        if (shareCenter.shareReady()) {
            notes.add("Launch evidence archive " + valueOrNone(shareCenter.latestArchiveId()) + " is share-ready.");
        } else {
            notes.add("Launch evidence archive is not share-ready: " + shareCenter.summary());
        }
        if (finalization.latestDeliveryReceiptId() == null) {
            notes.add("No accepted delivery receipt is available for " + valueOrNone(evidencePackage.sessionId()) + ".");
        } else {
            notes.add("Delivery receipt " + finalization.latestDeliveryReceiptId()
                    + " is " + finalization.deliveryReceiptFreshness().toLowerCase()
                    + " for " + valueOrNone(evidencePackage.sessionId()) + ".");
        }
        if (evidencePackage.latestPullRequestUrl() != null) {
            notes.add("Launch Pull Request " + evidencePackage.latestPullRequestUrl() + " is available for review.");
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(DemoLaunchEvidencePackageVo evidencePackage) {
        List<String> actions = new ArrayList<>();
        actions.add("Download self-hosted launch readiness report.");
        actions.add("Download launch evidence package report.");
        actions.add("Download launch evidence share center report.");
        actions.add("Download launch evidence finalization report.");
        if (evidencePackage.finalHandoffReportPackageArchiveId() != null) {
            actions.add("Download final handoff report package archive "
                    + evidencePackage.finalHandoffReportPackageArchiveId() + ".");
        }
        actions.add("Download launch acceptance closeout report.");
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean accepted,
            String summary,
            String nextAction,
            DemoLaunchEvidencePackageVo evidencePackage,
            DemoLaunchEvidenceShareCenterVo shareCenter,
            DemoLaunchEvidenceFinalizationVo finalization,
            List<DemoLaunchAcceptanceCloseoutCheckVo> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Launch Acceptance Closeout\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Accepted: `").append(accepted).append("`\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Next action: ").append(nextAction).append("\n");
        report.append("- Session: `").append(valueOrNone(evidencePackage.sessionId())).append("`\n");
        report.append("- Task: `").append(valueOrNone(evidencePackage.latestTaskId())).append("`\n");
        report.append("- Pull Request: `").append(valueOrNone(evidencePackage.latestPullRequestUrl())).append("`\n");
        report.append("- Webhook delivery: `").append(valueOrNone(evidencePackage.latestWebhookDeliveryId())).append("`\n");
        report.append("- Evaluation run: `").append(valueOrNone(evidencePackage.evaluationRunId())).append("`\n");
        report.append("- Launch evidence archive: `").append(valueOrNone(shareCenter.latestArchiveId())).append("`\n");
        report.append("- Final handoff archive: `")
                .append(valueOrNone(evidencePackage.finalHandoffReportPackageArchiveId()))
                .append("`\n");
        report.append("- Final handoff archive status: `")
                .append(evidencePackage.finalHandoffReportPackageArchiveStatus())
                .append("`\n");
        report.append("- Final handoff archive ready: `")
                .append(evidencePackage.finalHandoffReportPackageArchiveReady())
                .append("`\n");
        report.append("- Final handoff archive summary: ")
                .append(evidencePackage.finalHandoffReportPackageArchiveSummary())
                .append("\n");
        report.append("- Delivery receipt: `").append(valueOrNone(finalization.latestDeliveryReceiptId())).append("`\n");
        report.append("- Delivery target: `").append(valueOrNone(finalization.latestDeliveryTarget())).append("`\n");
        report.append("- Delivery channel: `").append(valueOrNone(finalization.latestDeliveryChannel())).append("`\n");
        report.append("- Delivered at: `").append(valueOrNone(finalization.latestDeliveredAt())).append("`\n");
        report.append("- Delivery receipt freshness: `").append(finalization.deliveryReceiptFreshness()).append("`\n");
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        report.append("## Checks\n\n");
        for (DemoLaunchAcceptanceCloseoutCheckVo check : checks) {
            report.append("- `").append(check.status()).append("` ")
                    .append(check.name()).append(": ")
                    .append(check.summary()).append(" Next action: ")
                    .append(check.nextAction()).append('\n');
        }
        report.append("\n## Evidence Notes\n\n");
        for (String note : evidenceNotes) {
            report.append("- ").append(note).append('\n');
        }
        report.append("\n## Download Actions\n\n");
        for (String downloadAction : downloadActions) {
            report.append("- ").append(downloadAction).append('\n');
        }
        report.append("\n## Side-Effect Contract\n\n");
        report.append("GET /api/demo/launch-acceptance-closeout is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.\n");
        return report.toString();
    }

    private static String firstAction(List<String> nextActions) {
        return nextActions.isEmpty() ? "No action needed." : nextActions.get(0);
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
