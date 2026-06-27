package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoLaunchEvidenceFinalizationService {

    private final DemoLaunchEvidenceShareCenterService shareCenterService;
    private final Clock clock;

    @Autowired
    public DemoLaunchEvidenceFinalizationService(DemoLaunchEvidenceShareCenterService shareCenterService) {
        this(shareCenterService, Clock.systemUTC());
    }

    DemoLaunchEvidenceFinalizationService(DemoLaunchEvidenceShareCenterService shareCenterService, Clock clock) {
        this.shareCenterService = shareCenterService;
        this.clock = clock;
    }

    public DemoLaunchEvidenceFinalizationVo getFinalizationGate() {
        DemoLaunchEvidenceShareCenterVo center = shareCenterService.getShareCenter();
        Instant generatedAt = Instant.now(clock);
        DemoReadinessStatus status = finalizationStatus(center);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(center, status);
        String nextAction = nextAction(center, status);
        List<DemoLaunchEvidenceFinalizationCheckVo> checks = checks(center, status);
        List<String> evidenceNotes = evidenceNotes(center, finalized);
        String markdownReport = formatMarkdown(center, status, finalized, summary, nextAction, checks, evidenceNotes, generatedAt);
        return new DemoLaunchEvidenceFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                center.latestArchiveId(),
                center.latestSessionId(),
                center.latestDeliveryReceiptId(),
                center.latestDeliveryTarget(),
                center.latestDeliveryChannel(),
                center.latestDeliveredAt(),
                center.deliveryReceiptFreshness(),
                center.deliveryReceiptFresh(),
                center.deliveryReceiptFreshnessSummary(),
                checks,
                evidenceNotes,
                markdownReport,
                generatedAt
        );
    }

    private static DemoReadinessStatus finalizationStatus(DemoLaunchEvidenceShareCenterVo center) {
        if ("NO_ARCHIVE".equals(center.status())) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        DemoReadinessStatus centerStatus = DemoReadinessStatus.valueOf(center.status());
        if (centerStatus == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!center.shareReady()) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return center.deliveryReceiptFresh() ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(DemoLaunchEvidenceShareCenterVo center, DemoReadinessStatus status) {
        if (status == DemoReadinessStatus.READY) {
            return "Demo launch evidence is finalized with a fresh delivery receipt for the current archive.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Demo launch evidence finalization is blocked before delivery can be accepted.";
        }
        if (center.shareReady()) {
            return "Demo launch evidence package is share-ready but final delivery evidence is not current.";
        }
        return "Demo launch evidence finalization needs a share-ready archive before delivery can be accepted.";
    }

    private static String nextAction(DemoLaunchEvidenceShareCenterVo center, DemoReadinessStatus status) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the finalization report as the launch evidence delivery acceptance record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return center.nextAction();
        }
        if (!center.shareReady()) {
            return center.nextAction();
        }
        if ("STALE".equals(center.deliveryReceiptFreshness())) {
            return "Record a new delivery receipt for launch evidence archive "
                    + valueOrNone(center.latestArchiveId()) + ", then download the finalization report.";
        }
        return "Share the current launch evidence package, record a delivery receipt, then download the finalization report.";
    }

    private static List<DemoLaunchEvidenceFinalizationCheckVo> checks(
            DemoLaunchEvidenceShareCenterVo center,
            DemoReadinessStatus finalizationStatus
    ) {
        DemoReadinessStatus shareStatus = shareCenterStatus(center);
        DemoReadinessStatus receiptStatus = center.deliveryReceiptFresh()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        DemoReadinessStatus acceptanceStatus = finalizationStatus == DemoReadinessStatus.BLOCKED
                ? DemoReadinessStatus.BLOCKED
                : receiptStatus;
        return List.of(
                new DemoLaunchEvidenceFinalizationCheckVo(
                        "Launch evidence share readiness",
                        shareStatus,
                        center.summary(),
                        center.shareReady() ? "No action needed." : center.nextAction()
                ),
                new DemoLaunchEvidenceFinalizationCheckVo(
                        "Delivery receipt freshness",
                        receiptStatus,
                        center.deliveryReceiptFreshnessSummary(),
                        center.deliveryReceiptFresh() ? "No action needed." : receiptNextAction(center)
                ),
                new DemoLaunchEvidenceFinalizationCheckVo(
                        "Launch acceptance evidence",
                        acceptanceStatus,
                        acceptanceStatus == DemoReadinessStatus.READY
                                ? "Finalization report is ready as the launch acceptance record."
                                : "Finalization report is not yet acceptable as launch delivery evidence.",
                        acceptanceStatus == DemoReadinessStatus.READY
                                ? "Download the finalization report."
                                : nextAction(center, finalizationStatus)
                )
        );
    }

    private static DemoReadinessStatus shareCenterStatus(DemoLaunchEvidenceShareCenterVo center) {
        if ("NO_ARCHIVE".equals(center.status())) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.valueOf(center.status());
    }

    private static String receiptNextAction(DemoLaunchEvidenceShareCenterVo center) {
        if ("STALE".equals(center.deliveryReceiptFreshness())) {
            return "Record a new delivery receipt for launch evidence archive "
                    + valueOrNone(center.latestArchiveId()) + ".";
        }
        return "Record a launch evidence delivery receipt after sharing the package.";
    }

    private static List<String> evidenceNotes(DemoLaunchEvidenceShareCenterVo center, boolean finalized) {
        List<String> notes = new ArrayList<>();
        notes.add("Launch evidence share center status is " + center.status() + ".");
        if (finalized) {
            notes.add("Latest delivery receipt " + center.latestDeliveryReceiptId() + " is fresh for "
                    + valueOrNone(center.latestArchiveId()) + "/" + valueOrNone(center.latestSessionId()) + ".");
            notes.add("Finalization report can be downloaded as the launch delivery acceptance record.");
        } else {
            notes.add("No fresh delivery receipt is available for "
                    + valueOrNone(center.latestArchiveId()) + "/" + valueOrNone(center.latestSessionId()) + ".");
            notes.add(center.deliveryReceiptFreshnessSummary());
        }
        notes.add("Share center next action: " + center.nextAction());
        return List.copyOf(notes);
    }

    private static String formatMarkdown(
            DemoLaunchEvidenceShareCenterVo center,
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            List<DemoLaunchEvidenceFinalizationCheckVo> checks,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Launch Evidence Finalization Gate\n\n");
        builder.append("- Status: `").append(status.name()).append("`\n");
        builder.append("- Finalized: `").append(finalized).append("`\n");
        builder.append("- Latest archive: `").append(valueOrNone(center.latestArchiveId())).append("`\n");
        builder.append("- Latest session: `").append(valueOrNone(center.latestSessionId())).append("`\n");
        builder.append("- Latest delivery receipt: `").append(valueOrNone(center.latestDeliveryReceiptId())).append("`\n");
        builder.append("- Delivery target: `").append(valueOrNone(center.latestDeliveryTarget())).append("`\n");
        builder.append("- Delivery channel: `").append(valueOrNone(center.latestDeliveryChannel())).append("`\n");
        builder.append("- Delivered at: `").append(valueOrNone(center.latestDeliveredAt())).append("`\n");
        builder.append("- Delivery receipt freshness: `").append(center.deliveryReceiptFreshness()).append("`\n");
        builder.append("- Delivery receipt fresh: `").append(center.deliveryReceiptFresh()).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        builder.append("## Checks\n\n");
        for (DemoLaunchEvidenceFinalizationCheckVo check : checks) {
            builder.append("- ").append(check.name())
                    .append(": `").append(check.status().name()).append("` - ")
                    .append(check.summary())
                    .append(" Next action: ")
                    .append(check.nextAction())
                    .append('\n');
        }
        builder.append("\n## Evidence Notes\n\n");
        for (String note : evidenceNotes) {
            builder.append("- ").append(note).append('\n');
        }
        builder.append("\n## Embedded Share Center\n\n");
        builder.append(center.markdownReport()).append('\n');
        builder.append("\n## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/launch-evidence-finalization is read-only: it does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.\n");
        return builder.toString();
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
