package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<List<DemoFinalExternalReviewEvidencePackageArchiveVo>> archiveSupplier;
    private final Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo>> receiptSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
            DemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository receiptRepository
    ) {
        this(
                () -> archiveRepository.listRecentArchives(1),
                () -> receiptRepository.listRecentReceipts(1),
                Clock.systemUTC()
        );
    }

    DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
            Supplier<List<DemoFinalExternalReviewEvidencePackageArchiveVo>> archiveSupplier,
            Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo>> receiptSupplier,
            Clock clock
    ) {
        this.archiveSupplier = archiveSupplier;
        this.receiptSupplier = receiptSupplier;
        this.clock = clock;
    }

    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo getFinalizationGate() {
        DemoFinalExternalReviewEvidencePackageArchiveVo latestArchive = latestArchive();
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo latestReceipt = latestReceipt();
        String freshness = freshness(latestArchive, latestReceipt);
        boolean receiptFresh = "FRESH".equals(freshness);
        DemoReadinessStatus status = status(latestArchive, freshness);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(status, freshness);
        String nextAction = nextAction(status, freshness, latestArchive);
        String freshnessSummary = freshnessSummary(freshness);
        List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check> checks =
                checks(latestArchive, freshness, receiptFresh);
        List<String> evidenceNotes = evidenceNotes(latestArchive, latestReceipt, freshness);
        List<String> downloadActions = downloadActions(latestArchive, latestReceipt);
        Instant generatedAt = Instant.now(clock);
        String markdownReport = markdownReport(
                status,
                finalized,
                summary,
                nextAction,
                latestArchive,
                latestReceipt,
                freshness,
                receiptFresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                downloadActions,
                generatedAt
        );

        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                latestArchive == null ? null : latestArchive.id(),
                latestReceipt == null ? null : latestReceipt.id(),
                latestArchive == null ? null : latestArchive.closeoutArchiveId(),
                latestArchive == null ? null : latestArchive.completionArchiveId(),
                latestArchive == null ? null : latestArchive.completionEvidenceDeliveryReceiptId(),
                latestArchive == null ? null : latestArchive.latestTaskId(),
                latestArchive == null ? null : latestArchive.latestPullRequestUrl(),
                latestReceipt == null ? latestArchive == null ? null : latestArchive.deliveryTarget()
                        : latestReceipt.deliveryTarget(),
                latestReceipt == null ? latestArchive == null ? null : latestArchive.deliveryChannel()
                        : latestReceipt.deliveryChannel(),
                latestReceipt == null || latestReceipt.deliveredAt() == null
                        ? latestArchive == null ? null : latestArchive.deliveredAt()
                        : latestReceipt.deliveredAt().toString(),
                freshness,
                receiptFresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport,
                generatedAt
        );
    }

    private DemoFinalExternalReviewEvidencePackageArchiveVo latestArchive() {
        List<DemoFinalExternalReviewEvidencePackageArchiveVo> archives = archiveSupplier.get();
        return archives.isEmpty() ? null : archives.get(0);
    }

    private DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo latestReceipt() {
        List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> receipts = receiptSupplier.get();
        return receipts.isEmpty() ? null : receipts.get(0);
    }

    private static DemoReadinessStatus status(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            String freshness
    ) {
        if (!archiveReady(archive)) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "FRESH".equals(freshness) ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String freshness(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    ) {
        if (!archiveReady(archive)) {
            return "BLOCKED";
        }
        if (receipt == null) {
            return "MISSING";
        }
        return receiptMatchesArchive(archive, receipt) ? "FRESH" : "STALE";
    }

    private static boolean archiveReady(DemoFinalExternalReviewEvidencePackageArchiveVo archive) {
        return archive != null
                && archive.status() == DemoReadinessStatus.READY
                && archive.readyForExternalReview();
    }

    private static boolean receiptMatchesArchive(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    ) {
        return same(archive.id(), receipt.finalExternalReviewPackageArchiveId())
                && same(archive.closeoutArchiveId(), receipt.closeoutArchiveId())
                && same(archive.completionArchiveId(), receipt.completionArchiveId())
                && same(archive.completionEvidenceDeliveryReceiptId(), receipt.completionEvidenceDeliveryReceiptId())
                && same(archive.latestTaskId(), receipt.latestTaskId())
                && same(archive.latestPullRequestUrl(), receipt.latestPullRequestUrl());
    }

    private static String summary(DemoReadinessStatus status, String freshness) {
        if (status == DemoReadinessStatus.READY) {
            return "Final external-review package delivery is finalized with a fresh package delivery receipt.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final external-review package delivery finalization is blocked because the latest package archive is not ready.";
        }
        if ("MISSING".equals(freshness)) {
            return "Final external-review package archive is ready but has no package delivery receipt.";
        }
        return "Final external-review package archive is ready but package delivery evidence is not current.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            String freshness,
            DemoFinalExternalReviewEvidencePackageArchiveVo archive
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the finalization report as proof that the frozen external-review package was delivered.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return archive == null
                    ? "Archive the READY final external-review evidence package before recording delivery finalization."
                    : archive.nextAction();
        }
        if ("STALE".equals(freshness)) {
            return "Record a new final external-review package delivery receipt for package archive "
                    + valueOrNone(archive.id()) + ".";
        }
        return "Deliver the frozen final external-review package, record a package delivery receipt, "
                + "then download the finalization report.";
    }

    private static String freshnessSummary(String freshness) {
        return switch (freshness) {
            case "FRESH" -> "Latest package delivery receipt matches the current frozen final external-review package.";
            case "MISSING" -> "No package delivery receipt is available for the current frozen final external-review package.";
            case "STALE" -> "Latest package delivery receipt does not match the current frozen final external-review package.";
            default -> "Package delivery receipt freshness cannot be evaluated until the latest package archive is ready.";
        };
    }

    private static List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check> checks(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            String freshness,
            boolean receiptFresh
    ) {
        DemoReadinessStatus archiveStatus = archiveReady(archive)
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.BLOCKED;
        DemoReadinessStatus receiptStatus = receiptFresh ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
        if ("BLOCKED".equals(freshness)) {
            receiptStatus = DemoReadinessStatus.BLOCKED;
        }
        return List.of(
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check(
                        "Frozen final external-review package",
                        archiveStatus,
                        archiveReady(archive)
                                ? "Frozen final external-review package is ready."
                                : "Frozen final external-review package is not ready.",
                        archiveReady(archive) ? "No action needed." : nextAction(DemoReadinessStatus.BLOCKED, freshness, archive)
                ),
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check(
                        "Final external-review package delivery receipt",
                        receiptStatus,
                        freshnessSummary(freshness),
                        receiptFresh
                                ? "No action needed."
                                : "Record a fresh delivery receipt for the current frozen final external-review package."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt,
            String freshness
    ) {
        List<String> notes = new ArrayList<>();
        if (archiveReady(archive)) {
            notes.add("Frozen final external-review package " + archive.id() + " is ready.");
        } else {
            notes.add("Frozen final external-review package is not ready.");
        }
        if (receipt == null) {
            notes.add("No final external-review package delivery receipt is available.");
        } else if ("FRESH".equals(freshness)) {
            notes.add("Final external-review package delivery receipt " + receipt.id() + " is fresh.");
        } else if ("STALE".equals(freshness)) {
            notes.add("Latest final external-review package delivery receipt " + receipt.id() + " is stale.");
        } else {
            notes.add("Final external-review package delivery receipt " + receipt.id()
                    + " cannot finalize a blocked package archive.");
        }
        if (archive != null) {
            notes.addAll(archive.evidenceNotes());
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final external-review package delivery finalization report.");
        if (archive != null) {
            actions.add("Download final external-review package archive " + archive.id() + ".");
        }
        if (receipt != null) {
            actions.add("Download final external-review package delivery receipt " + receipt.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            DemoFinalExternalReviewEvidencePackageArchiveVo archive,
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt,
            String freshness,
            boolean receiptFresh,
            String freshnessSummary,
            List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final External Review Package Delivery Finalization\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Finalized: `").append(finalized).append("`\n");
        report.append("- Summary: ").append(summary).append('\n');
        report.append("- Next action: ").append(nextAction).append('\n');
        report.append("- Latest package archive: `").append(archive == null ? "none" : archive.id()).append("`\n");
        report.append("- Latest package delivery receipt: `").append(receipt == null ? "none" : receipt.id()).append("`\n");
        report.append("- Latest closeout archive: `").append(archive == null ? "none" : valueOrNone(archive.closeoutArchiveId())).append("`\n");
        report.append("- Latest completion archive: `").append(archive == null ? "none" : valueOrNone(archive.completionArchiveId())).append("`\n");
        report.append("- Latest completion evidence delivery receipt: `")
                .append(archive == null ? "none" : valueOrNone(archive.completionEvidenceDeliveryReceiptId()))
                .append("`\n");
        report.append("- Latest task: `").append(archive == null ? "none" : valueOrNone(archive.latestTaskId())).append("`\n");
        report.append("- Delivery receipt freshness: `").append(freshness).append("`\n");
        report.append("- Delivery receipt fresh: `").append(receiptFresh).append("`\n");
        report.append("- Delivery receipt freshness summary: ").append(freshnessSummary).append('\n');
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendChecks(report, checks);
        appendList(report, "Evidence Notes", evidenceNotes);
        appendList(report, "Download Actions", downloadActions);
        report.append("## Side Effect Contract\n\n");
        report.append(SIDE_EFFECT_CONTRACT).append('\n');
        return report.toString();
    }

    private static void appendChecks(
            StringBuilder report,
            List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check check : checks) {
            report.append("- `").append(check.status()).append("` ")
                    .append(check.name()).append(": ")
                    .append(check.summary())
                    .append(" Next: ")
                    .append(check.nextAction())
                    .append('\n');
        }
        report.append('\n');
    }

    private static void appendList(StringBuilder report, String heading, List<String> items) {
        report.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            report.append("- ").append(item).append('\n');
        }
        report.append('\n');
    }

    private static boolean same(String expected, String actual) {
        return valueOrNone(expected).equals(valueOrNone(actual));
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value.trim();
    }
}
