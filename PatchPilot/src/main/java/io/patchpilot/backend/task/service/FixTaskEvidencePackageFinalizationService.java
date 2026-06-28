package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageFinalizationCheckVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageFinalizationVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareCenterVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixTaskEvidencePackageFinalizationService {

    private final FixTaskEvidencePackageArchiveService archiveService;
    private final FixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;

    @Autowired
    public FixTaskEvidencePackageFinalizationService(
            FixTaskEvidencePackageArchiveService archiveService,
            FixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository
    ) {
        this(archiveService, receiptRepository, Clock.systemUTC());
    }

    FixTaskEvidencePackageFinalizationService(
            FixTaskEvidencePackageArchiveService archiveService,
            FixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository,
            Clock clock
    ) {
        this.archiveService = archiveService;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
    }

    public FixTaskEvidencePackageFinalizationVo getFinalizationGate() {
        FixTaskEvidencePackageShareCenterVo center = archiveService.shareCenter(20);
        FixTaskEvidencePackageShareDeliveryReceiptVo latestReceipt =
                receiptRepository.listRecentReceipts(1).stream().findFirst().orElse(null);
        ReceiptFreshness freshness = receiptFreshness(center, latestReceipt);
        String status = finalizationStatus(center, freshness);
        boolean finalized = "READY".equals(status);
        Instant generatedAt = Instant.now(clock);
        String summary = summary(center, status);
        String nextAction = nextAction(center, status, freshness);
        List<FixTaskEvidencePackageFinalizationCheckVo> checks = checks(center, status, freshness);
        List<String> evidenceNotes = evidenceNotes(center, latestReceipt, freshness, finalized);
        String markdownReport = formatMarkdown(
                center,
                latestReceipt,
                freshness,
                status,
                finalized,
                summary,
                nextAction,
                checks,
                evidenceNotes,
                generatedAt
        );
        return new FixTaskEvidencePackageFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                center.shareableArchiveId(),
                center.shareableTaskId(),
                center.shareablePullRequestUrl(),
                latestReceipt == null ? null : latestReceipt.id(),
                latestReceipt == null ? null : latestReceipt.deliveryTarget(),
                latestReceipt == null ? null : latestReceipt.deliveryChannel(),
                latestReceipt == null ? null : latestReceipt.deliveredAt().toString(),
                freshness.status(),
                freshness.fresh(),
                freshness.summary(),
                checks,
                evidenceNotes,
                markdownReport,
                generatedAt
        );
    }

    public String report() {
        return getFinalizationGate().markdownReport();
    }

    private static String finalizationStatus(FixTaskEvidencePackageShareCenterVo center, ReceiptFreshness freshness) {
        if ("BLOCKED".equals(center.status())) {
            return "BLOCKED";
        }
        if (!center.shareReady()) {
            return "NEEDS_ATTENTION";
        }
        return freshness.fresh() ? "READY" : "NEEDS_ATTENTION";
    }

    private static String summary(FixTaskEvidencePackageShareCenterVo center, String status) {
        if ("READY".equals(status)) {
            return "Task evidence is finalized with a fresh delivery receipt for the current shareable archive.";
        }
        if ("BLOCKED".equals(status)) {
            return "Task evidence finalization is blocked before delivery can be accepted.";
        }
        if (center.shareReady()) {
            return "Task evidence is share-ready but final delivery evidence is not current.";
        }
        return "Task evidence finalization needs a share-ready archive before delivery can be accepted.";
    }

    private static String nextAction(
            FixTaskEvidencePackageShareCenterVo center,
            String status,
            ReceiptFreshness freshness
    ) {
        if ("READY".equals(status)) {
            return "Use the finalization report as the accepted task evidence delivery record.";
        }
        if ("BLOCKED".equals(status) || !center.shareReady()) {
            return center.nextAction();
        }
        if ("STALE".equals(freshness.status())) {
            return "Record a new delivery receipt for task evidence archive "
                    + valueOrNone(center.shareableArchiveId()) + ", then download the finalization report.";
        }
        return "Share the current task evidence package, record a delivery receipt, then download the finalization report.";
    }

    private static List<FixTaskEvidencePackageFinalizationCheckVo> checks(
            FixTaskEvidencePackageShareCenterVo center,
            String finalizationStatus,
            ReceiptFreshness freshness
    ) {
        String receiptStatus = freshness.fresh() ? "READY" : "NEEDS_ATTENTION";
        String acceptanceStatus = "BLOCKED".equals(finalizationStatus) ? "BLOCKED" : receiptStatus;
        return List.of(
                new FixTaskEvidencePackageFinalizationCheckVo(
                        "Task evidence share readiness",
                        center.status(),
                        center.summary(),
                        center.shareReady() ? "No action needed." : center.nextAction()
                ),
                new FixTaskEvidencePackageFinalizationCheckVo(
                        "Delivery receipt freshness",
                        receiptStatus,
                        freshness.summary(),
                        freshness.fresh() ? "No action needed." : receiptNextAction(center, freshness)
                ),
                new FixTaskEvidencePackageFinalizationCheckVo(
                        "Task evidence acceptance",
                        acceptanceStatus,
                        "READY".equals(acceptanceStatus)
                                ? "Finalization report is ready as the task evidence acceptance record."
                                : "Finalization report is not yet acceptable as delivered task evidence.",
                        "READY".equals(acceptanceStatus)
                                ? "Download the finalization report."
                                : nextAction(center, finalizationStatus, freshness)
                )
        );
    }

    private static String receiptNextAction(FixTaskEvidencePackageShareCenterVo center, ReceiptFreshness freshness) {
        if ("STALE".equals(freshness.status())) {
            return "Record a new delivery receipt for task evidence archive "
                    + valueOrNone(center.shareableArchiveId()) + ".";
        }
        return "Record a task evidence delivery receipt after sharing the package.";
    }

    private static List<String> evidenceNotes(
            FixTaskEvidencePackageShareCenterVo center,
            FixTaskEvidencePackageShareDeliveryReceiptVo latestReceipt,
            ReceiptFreshness freshness,
            boolean finalized
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Task evidence share center status is " + center.status() + ".");
        if (finalized) {
            notes.add("Latest delivery receipt " + latestReceipt.id() + " is fresh for "
                    + valueOrNone(center.shareableArchiveId()) + "/" + valueOrNone(center.shareableTaskId()) + ".");
            notes.add("Finalization report can be downloaded as the accepted task evidence delivery record.");
        } else {
            notes.add("No fresh delivery receipt is available for "
                    + valueOrNone(center.shareableArchiveId()) + "/" + valueOrNone(center.shareableTaskId()) + ".");
            notes.add(freshness.summary());
        }
        notes.add("Share center next action: " + center.nextAction());
        return List.copyOf(notes);
    }

    private static ReceiptFreshness receiptFreshness(
            FixTaskEvidencePackageShareCenterVo center,
            FixTaskEvidencePackageShareDeliveryReceiptVo latestReceipt
    ) {
        if (latestReceipt == null) {
            return new ReceiptFreshness(
                    "MISSING",
                    false,
                    "No delivery receipt has been recorded for the current task evidence package."
            );
        }
        if (matchesCurrentShareableArchive(center, latestReceipt)) {
            return new ReceiptFreshness(
                    "FRESH",
                    true,
                    "Latest delivery receipt matches the current task evidence archive and task."
            );
        }
        return new ReceiptFreshness(
                "STALE",
                false,
                "Latest delivery receipt " + latestReceipt.id()
                        + " belongs to " + latestReceipt.taskEvidenceArchiveId() + "/" + latestReceipt.taskId()
                        + ", not current " + valueOrNone(center.shareableArchiveId())
                        + "/" + valueOrNone(center.shareableTaskId()) + "."
        );
    }

    private static boolean matchesCurrentShareableArchive(
            FixTaskEvidencePackageShareCenterVo center,
            FixTaskEvidencePackageShareDeliveryReceiptVo latestReceipt
    ) {
        return latestReceipt.taskEvidenceArchiveId().equals(center.shareableArchiveId())
                && latestReceipt.taskId().equals(center.shareableTaskId());
    }

    private static String formatMarkdown(
            FixTaskEvidencePackageShareCenterVo center,
            FixTaskEvidencePackageShareDeliveryReceiptVo latestReceipt,
            ReceiptFreshness freshness,
            String status,
            boolean finalized,
            String summary,
            String nextAction,
            List<FixTaskEvidencePackageFinalizationCheckVo> checks,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Task Evidence Finalization Gate\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Finalized: `").append(finalized).append("`\n");
        builder.append("- Latest archive: `").append(valueOrNone(center.shareableArchiveId())).append("`\n");
        builder.append("- Latest task: `").append(valueOrNone(center.shareableTaskId())).append("`\n");
        builder.append("- Latest delivery receipt: `")
                .append(latestReceipt == null ? "none" : latestReceipt.id())
                .append("`\n");
        builder.append("- Delivery target: `")
                .append(latestReceipt == null ? "none" : latestReceipt.deliveryTarget())
                .append("`\n");
        builder.append("- Delivery channel: `")
                .append(latestReceipt == null ? "none" : latestReceipt.deliveryChannel())
                .append("`\n");
        builder.append("- Delivery receipt freshness: `").append(freshness.status()).append("`\n");
        builder.append("- Delivery receipt fresh: `").append(freshness.fresh()).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        builder.append("## Checks\n\n");
        checks.forEach(check -> builder.append("- ")
                .append(check.name())
                .append(": `")
                .append(check.status())
                .append("` - ")
                .append(check.summary())
                .append(" Next action: ")
                .append(check.nextAction())
                .append('\n'));
        builder.append("\n## Evidence Notes\n\n");
        evidenceNotes.forEach(note -> builder.append("- ").append(note).append('\n'));
        builder.append("\n## Embedded Share Center\n\n");
        builder.append(center.markdownReport()).append('\n');
        builder.append("\n## Side-Effect Contract\n\n");
        builder.append("GET /api/tasks/evidence-packages/finalization is read-only: it does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.\n");
        return builder.toString();
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }

    private record ReceiptFreshness(String status, boolean fresh, String summary) {
    }
}
