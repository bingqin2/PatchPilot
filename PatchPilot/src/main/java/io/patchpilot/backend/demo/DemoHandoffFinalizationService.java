package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoHandoffFinalizationService {

    private final DemoHandoffShareCenterService shareCenterService;
    private final Clock clock;

    @Autowired
    public DemoHandoffFinalizationService(DemoHandoffShareCenterService shareCenterService) {
        this(shareCenterService, Clock.systemUTC());
    }

    DemoHandoffFinalizationService(DemoHandoffShareCenterService shareCenterService, Clock clock) {
        this.shareCenterService = shareCenterService;
        this.clock = clock;
    }

    public DemoHandoffFinalizationVo getFinalizationGate() {
        DemoHandoffShareCenterVo center = shareCenterService.getShareCenter();
        Instant generatedAt = Instant.now(clock);
        DemoReadinessStatus status = finalizationStatus(center);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(center, status);
        String nextAction = nextAction(center, status);
        List<DemoHandoffFinalizationCheckVo> checks = checks(center, status);
        List<String> evidenceNotes = evidenceNotes(center, finalized);
        String markdownReport = formatMarkdown(center, status, finalized, summary, nextAction, checks, evidenceNotes, generatedAt);
        return new DemoHandoffFinalizationVo(
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

    private static DemoReadinessStatus finalizationStatus(DemoHandoffShareCenterVo center) {
        if (center.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!center.shareReady()) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return center.deliveryReceiptFresh() ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(DemoHandoffShareCenterVo center, DemoReadinessStatus status) {
        if (status == DemoReadinessStatus.READY) {
            return "Demo handoff is finalized with a fresh delivery receipt for the current archive.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Demo handoff finalization is blocked before delivery can be accepted.";
        }
        if (center.shareReady()) {
            return "Demo handoff package is send-ready but final delivery evidence is not current.";
        }
        return "Demo handoff finalization needs a share-ready package before delivery can be accepted.";
    }

    private static String nextAction(DemoHandoffShareCenterVo center, DemoReadinessStatus status) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the finalization report as the post-demo delivery acceptance record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return center.nextAction();
        }
        if (!center.shareReady()) {
            return center.nextAction();
        }
        if ("STALE".equals(center.deliveryReceiptFreshness())) {
            return "Record a new delivery receipt for archive " + valueOrNone(center.latestArchiveId())
                    + ", then download the finalization report.";
        }
        return "Send the current handoff package, record a delivery receipt, then download the finalization report.";
    }

    private static List<DemoHandoffFinalizationCheckVo> checks(
            DemoHandoffShareCenterVo center,
            DemoReadinessStatus finalizationStatus
    ) {
        DemoReadinessStatus receiptStatus = center.deliveryReceiptFresh()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        DemoReadinessStatus taskCertificateStatus = center.taskCertificateReady()
                ? DemoReadinessStatus.READY
                : center.taskCertificateStatus();
        DemoReadinessStatus acceptanceStatus = finalizationStatus == DemoReadinessStatus.BLOCKED
                ? DemoReadinessStatus.BLOCKED
                : worst(receiptStatus, taskCertificateStatus);
        return List.of(
                new DemoHandoffFinalizationCheckVo(
                        "Handoff package share readiness",
                        center.status(),
                        center.summary(),
                        center.shareReady() ? "No action needed." : center.nextAction()
                ),
                new DemoHandoffFinalizationCheckVo(
                        "Delivery receipt freshness",
                        receiptStatus,
                        center.deliveryReceiptFreshnessSummary(),
                        center.deliveryReceiptFresh() ? "No action needed." : receiptNextAction(center)
                ),
                new DemoHandoffFinalizationCheckVo(
                        "Task evidence certificate",
                        taskCertificateStatus,
                        taskCertificateStatus == DemoReadinessStatus.READY
                                ? "Task evidence acceptance certificate is attached to the final handoff package."
                                : center.taskCertificateSummary(),
                        taskCertificateStatus == DemoReadinessStatus.READY
                                ? "No action needed."
                                : center.taskCertificateNextAction()
                ),
                new DemoHandoffFinalizationCheckVo(
                        "Final acceptance evidence",
                        acceptanceStatus,
                        acceptanceStatus == DemoReadinessStatus.READY
                                ? "Finalization report is ready as the acceptance record."
                                : "Finalization report is not yet acceptable as delivery evidence.",
                        acceptanceStatus == DemoReadinessStatus.READY
                                ? "Download the finalization report."
                                : nextAction(center, finalizationStatus)
                )
        );
    }

    private static String receiptNextAction(DemoHandoffShareCenterVo center) {
        if ("STALE".equals(center.deliveryReceiptFreshness())) {
            return "Record a new delivery receipt for archive " + valueOrNone(center.latestArchiveId()) + ".";
        }
        return "Record a handoff share delivery receipt after sending the package.";
    }

    private static List<String> evidenceNotes(DemoHandoffShareCenterVo center, boolean finalized) {
        List<String> notes = new ArrayList<>();
        notes.add("Share center status is " + center.status().name() + ".");
        if (center.taskCertificateReady()) {
            notes.add("Task evidence certificate " + valueOrNone(center.taskCertificateArchiveId())
                    + " is ready for task " + valueOrNone(center.taskCertificateTaskId()) + ".");
        } else {
            notes.add("Task evidence certificate is not ready: " + center.taskCertificateSummary());
        }
        if (finalized) {
            notes.add("Latest delivery receipt " + center.latestDeliveryReceiptId() + " is fresh for "
                    + valueOrNone(center.latestArchiveId()) + "/" + valueOrNone(center.latestSessionId()) + ".");
            notes.add("Finalization report can be downloaded as the acceptance record.");
        } else {
            notes.add("No fresh delivery receipt is available for "
                    + valueOrNone(center.latestArchiveId()) + "/" + valueOrNone(center.latestSessionId()) + ".");
            notes.add(center.deliveryReceiptFreshnessSummary());
        }
        notes.add("Share center next action: " + center.nextAction());
        return List.copyOf(notes);
    }

    private static DemoReadinessStatus worst(DemoReadinessStatus first, DemoReadinessStatus second) {
        if (first == DemoReadinessStatus.BLOCKED || second == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (first == DemoReadinessStatus.NEEDS_ATTENTION || second == DemoReadinessStatus.NEEDS_ATTENTION) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static String formatMarkdown(
            DemoHandoffShareCenterVo center,
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            List<DemoHandoffFinalizationCheckVo> checks,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Handoff Finalization Gate\n\n");
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
        for (DemoHandoffFinalizationCheckVo check : checks) {
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
        builder.append("GET /api/demo/handoff-finalization is read-only: it does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.\n");
        return builder.toString();
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
