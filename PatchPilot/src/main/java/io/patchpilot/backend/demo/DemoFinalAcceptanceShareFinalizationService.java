package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceShareDeliveryReceiptRepository;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceSharePackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DemoFinalAcceptanceShareFinalizationService {

    private final DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository;
    private final DemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;

    @Autowired
    public DemoFinalAcceptanceShareFinalizationService(
            DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository,
            DemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository
    ) {
        this(archiveRepository, receiptRepository, Clock.systemUTC());
    }

    DemoFinalAcceptanceShareFinalizationService(
            DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository,
            DemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository,
            Clock clock
    ) {
        this.archiveRepository = archiveRepository;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
    }

    public DemoFinalAcceptanceShareFinalizationVo getFinalizationGate() {
        Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive = latestArchive();
        Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt = latestReceipt();
        Instant generatedAt = Instant.now(clock);
        DemoReadinessStatus status = finalizationStatus(archive, receipt);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(archive, status);
        String nextAction = nextAction(archive, receipt, status);
        String freshness = deliveryReceiptFreshness(archive, receipt);
        boolean fresh = "FRESH".equals(freshness);
        String freshnessSummary = deliveryReceiptFreshnessSummary(archive, receipt, freshness);
        List<DemoFinalAcceptanceShareFinalizationVo.Check> checks =
                checks(archive, receipt, status, freshness, freshnessSummary);
        List<String> evidenceNotes = evidenceNotes(archive, receipt, finalized, freshnessSummary);
        String markdown = formatMarkdown(
                archive,
                receipt,
                status,
                finalized,
                summary,
                nextAction,
                freshness,
                fresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                generatedAt
        );
        return new DemoFinalAcceptanceShareFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                archive.map(DemoFinalAcceptanceSharePackageArchiveVo::id).orElse(null),
                archive.map(DemoFinalAcceptanceSharePackageArchiveVo::latestTaskId).orElse(null),
                receipt.map(DemoFinalAcceptanceShareDeliveryReceiptVo::id).orElse(null),
                receipt.map(DemoFinalAcceptanceShareDeliveryReceiptVo::deliveryTarget).orElse(null),
                receipt.map(DemoFinalAcceptanceShareDeliveryReceiptVo::deliveryChannel).orElse(null),
                receipt.map(item -> item.deliveredAt().toString()).orElse(null),
                freshness,
                fresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                markdown,
                generatedAt
        );
    }

    private Optional<DemoFinalAcceptanceSharePackageArchiveVo> latestArchive() {
        return archiveRepository.listRecentArchives(1).stream().findFirst();
    }

    private Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> latestReceipt() {
        return receiptRepository.listRecentReceipts(1).stream().findFirst();
    }

    private static DemoReadinessStatus finalizationStatus(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt
    ) {
        if (archive.isEmpty()) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if (!archive.get().sendReady()) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "FRESH".equals(deliveryReceiptFreshness(archive, receipt))
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            DemoReadinessStatus status
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Final demo acceptance share package is finalized with a fresh delivery receipt.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final demo acceptance share finalization is blocked because the latest package archive is not send-ready.";
        }
        if (archive.isPresent() && archive.get().sendReady()) {
            return "Final demo acceptance share package is send-ready but final delivery evidence is not current.";
        }
        return "Final demo acceptance share finalization needs an archived send-ready package before delivery can be accepted.";
    }

    private static String nextAction(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt,
            DemoReadinessStatus status
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the finalization report as the external-review acceptance delivery record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return archive.map(DemoFinalAcceptanceSharePackageArchiveVo::nextAction)
                    .orElse("Archive a send-ready final acceptance share package.");
        }
        if (archive.isEmpty()) {
            return "Archive the final acceptance share package, then record a delivery receipt.";
        }
        if ("STALE".equals(deliveryReceiptFreshness(archive, receipt))) {
            return "Record a new delivery receipt for final acceptance share package archive "
                    + archive.get().id() + ", then download the finalization report.";
        }
        return "Send the current final acceptance share package, record a delivery receipt, then download the finalization report.";
    }

    private static String deliveryReceiptFreshness(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt
    ) {
        if (archive.isEmpty() || receipt.isEmpty()) {
            return "MISSING";
        }
        DemoFinalAcceptanceSharePackageArchiveVo latestArchive = archive.get();
        DemoFinalAcceptanceShareDeliveryReceiptVo latestReceipt = receipt.get();
        boolean sameArchive = latestArchive.id().equals(latestReceipt.finalAcceptanceSharePackageArchiveId());
        boolean sameTask = valueOrNone(latestArchive.latestTaskId()).equals(valueOrNone(latestReceipt.latestTaskId()));
        return sameArchive && sameTask ? "FRESH" : "STALE";
    }

    private static String deliveryReceiptFreshnessSummary(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt,
            String freshness
    ) {
        if ("FRESH".equals(freshness)) {
            return "Latest delivery receipt matches the current final acceptance share package archive.";
        }
        if ("STALE".equals(freshness) && receipt.isPresent()) {
            DemoFinalAcceptanceShareDeliveryReceiptVo latestReceipt = receipt.get();
            return "Latest delivery receipt " + latestReceipt.id()
                    + " belongs to " + latestReceipt.finalAcceptanceSharePackageArchiveId()
                    + "/" + valueOrNone(latestReceipt.latestTaskId())
                    + ", not "
                    + archive.map(DemoFinalAcceptanceSharePackageArchiveVo::id).orElse("none")
                    + "/" + archive.map(DemoFinalAcceptanceSharePackageArchiveVo::latestTaskId).orElse("none")
                    + ".";
        }
        return "No final acceptance share delivery receipt has been recorded for the current archive.";
    }

    private static List<DemoFinalAcceptanceShareFinalizationVo.Check> checks(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt,
            DemoReadinessStatus finalizationStatus,
            String freshness,
            String freshnessSummary
    ) {
        DemoReadinessStatus archiveStatus = archive
                .map(item -> item.sendReady() ? DemoReadinessStatus.READY : DemoReadinessStatus.BLOCKED)
                .orElse(DemoReadinessStatus.NEEDS_ATTENTION);
        DemoReadinessStatus receiptStatus = "FRESH".equals(freshness)
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        DemoReadinessStatus acceptanceStatus = finalizationStatus == DemoReadinessStatus.BLOCKED
                ? DemoReadinessStatus.BLOCKED
                : receiptStatus;
        return List.of(
                new DemoFinalAcceptanceShareFinalizationVo.Check(
                        "Final acceptance package archive",
                        archiveStatus,
                        archive.map(DemoFinalAcceptanceSharePackageArchiveVo::summary)
                                .orElse("No final acceptance share package archive is available."),
                        archive.map(DemoFinalAcceptanceSharePackageArchiveVo::nextAction)
                                .orElse("Archive the final acceptance share package.")
                ),
                new DemoFinalAcceptanceShareFinalizationVo.Check(
                        "Delivery receipt freshness",
                        receiptStatus,
                        freshnessSummary,
                        "FRESH".equals(freshness)
                                ? "No action needed."
                                : receiptNextAction(archive, receipt, freshness)
                ),
                new DemoFinalAcceptanceShareFinalizationVo.Check(
                        "Final acceptance delivery evidence",
                        acceptanceStatus,
                        acceptanceStatus == DemoReadinessStatus.READY
                                ? "Finalization report is ready as the external-review delivery record."
                                : "Finalization report is not yet acceptable as external-review delivery evidence.",
                        acceptanceStatus == DemoReadinessStatus.READY
                                ? "Download the finalization report."
                                : nextAction(archive, receipt, finalizationStatus)
                )
        );
    }

    private static String receiptNextAction(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt,
            String freshness
    ) {
        if ("STALE".equals(freshness) && archive.isPresent()) {
            return "Record a new delivery receipt for final acceptance share package archive "
                    + archive.get().id() + ".";
        }
        return "Record a final acceptance share delivery receipt after sending the package.";
    }

    private static List<String> evidenceNotes(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt,
            boolean finalized,
            String freshnessSummary
    ) {
        List<String> notes = new ArrayList<>();
        if (archive.isPresent()) {
            DemoFinalAcceptanceSharePackageArchiveVo latestArchive = archive.get();
            notes.add("Latest final acceptance archive " + latestArchive.id()
                    + (latestArchive.sendReady() ? " is send-ready." : " is not send-ready."));
        } else {
            notes.add("No final acceptance share package archive is available.");
        }
        if (finalized && receipt.isPresent()) {
            notes.add("Latest delivery receipt " + receipt.get().id() + " is fresh for "
                    + receipt.get().finalAcceptanceSharePackageArchiveId() + ".");
            notes.add("Finalization report can be downloaded as the external-review acceptance delivery record.");
        } else {
            notes.add(freshnessSummary);
        }
        return List.copyOf(notes);
    }

    private static String formatMarkdown(
            Optional<DemoFinalAcceptanceSharePackageArchiveVo> archive,
            Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> receipt,
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            String freshness,
            boolean fresh,
            String freshnessSummary,
            List<DemoFinalAcceptanceShareFinalizationVo.Check> checks,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Demo Acceptance Share Finalization Gate\n\n");
        builder.append("- Status: `").append(status.name()).append("`\n");
        builder.append("- Finalized: `").append(finalized).append("`\n");
        builder.append("- Latest archive: `").append(archive.map(DemoFinalAcceptanceSharePackageArchiveVo::id).orElse("none")).append("`\n");
        builder.append("- Latest task: `").append(archive.map(DemoFinalAcceptanceSharePackageArchiveVo::latestTaskId).orElse("none")).append("`\n");
        builder.append("- Latest delivery receipt: `").append(receipt.map(DemoFinalAcceptanceShareDeliveryReceiptVo::id).orElse("none")).append("`\n");
        builder.append("- Delivery target: `").append(receipt.map(DemoFinalAcceptanceShareDeliveryReceiptVo::deliveryTarget).orElse("none")).append("`\n");
        builder.append("- Delivery channel: `").append(receipt.map(DemoFinalAcceptanceShareDeliveryReceiptVo::deliveryChannel).orElse("none")).append("`\n");
        builder.append("- Delivered at: `").append(receipt.map(item -> item.deliveredAt().toString()).orElse("none")).append("`\n");
        builder.append("- Delivery receipt freshness: `").append(freshness).append("`\n");
        builder.append("- Delivery receipt fresh: `").append(fresh).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        builder.append("## Freshness Summary\n\n").append(freshnessSummary).append("\n\n");
        builder.append("## Checks\n\n");
        for (DemoFinalAcceptanceShareFinalizationVo.Check check : checks) {
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
        archive.ifPresent(item -> builder
                .append("\n## Embedded Final Acceptance Share Package\n\n")
                .append(item.report())
                .append('\n'));
        receipt.ifPresent(item -> builder
                .append("\n## Embedded Delivery Receipt\n\n")
                .append(item.markdownReport())
                .append('\n'));
        builder.append("\n## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/final-acceptance-share-finalization is read-only: it does not create tasks, call the model, run tests, mutate Git, send messages, record receipts, or write to GitHub.\n");
        return builder.toString();
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value.trim();
    }
}
