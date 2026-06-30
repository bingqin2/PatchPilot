package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleArchiveRepository;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-external-review-release-bundle/delivery-finalization is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<List<DemoFinalExternalReviewReleaseBundleArchiveVo>> archiveSupplier;
    private final Supplier<List<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo>> receiptSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
            DemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository receiptRepository
    ) {
        this(
                () -> archiveRepository.listRecentArchives(1),
                () -> receiptRepository.listRecentReceipts(1),
                Clock.systemUTC()
        );
    }

    DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
            Supplier<List<DemoFinalExternalReviewReleaseBundleArchiveVo>> archiveSupplier,
            Supplier<List<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo>> receiptSupplier,
            Clock clock
    ) {
        this.archiveSupplier = archiveSupplier;
        this.receiptSupplier = receiptSupplier;
        this.clock = clock;
    }

    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo getFinalizationGate() {
        DemoFinalExternalReviewReleaseBundleArchiveVo latestArchive = latestArchive();
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo latestReceipt = latestReceipt();
        String freshness = freshness(latestArchive, latestReceipt);
        boolean receiptFresh = "FRESH".equals(freshness);
        DemoReadinessStatus status = status(latestArchive, freshness);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(status, freshness);
        String nextAction = nextAction(status, freshness, latestArchive);
        String freshnessSummary = freshnessSummary(freshness);
        List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check> checks =
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

        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                latestArchive == null ? null : latestArchive.id(),
                latestReceipt == null ? null : latestReceipt.id(),
                latestArchive == null ? null : latestArchive.latestCertificateArchiveId(),
                latestArchive == null ? null : latestArchive.latestDeliveryFinalizationArchiveId(),
                latestArchive == null ? null : latestArchive.latestPackageArchiveId(),
                latestArchive == null ? null : latestArchive.latestDeliveryReceiptId(),
                latestArchive == null ? null : latestArchive.latestTaskId(),
                latestArchive == null ? null : latestArchive.latestPullRequestUrl(),
                latestReceipt == null ? latestArchive == null ? null : latestArchive.latestDeliveryTarget()
                        : latestReceipt.deliveryTarget(),
                latestReceipt == null ? latestArchive == null ? null : latestArchive.latestDeliveryChannel()
                        : latestReceipt.deliveryChannel(),
                latestReceipt == null || latestReceipt.deliveredAt() == null
                        ? latestArchive == null ? null : latestArchive.latestDeliveredAt()
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

    private DemoFinalExternalReviewReleaseBundleArchiveVo latestArchive() {
        List<DemoFinalExternalReviewReleaseBundleArchiveVo> archives = archiveSupplier.get();
        return archives.isEmpty() ? null : archives.get(0);
    }

    private DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo latestReceipt() {
        List<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> receipts = receiptSupplier.get();
        return receipts.isEmpty() ? null : receipts.get(0);
    }

    private static DemoReadinessStatus status(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            String freshness
    ) {
        if (!archiveReady(archive)) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "FRESH".equals(freshness) ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String freshness(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt
    ) {
        if (!archiveReady(archive)) {
            return "BLOCKED";
        }
        if (receipt == null) {
            return "MISSING";
        }
        return receiptMatchesArchive(archive, receipt) ? "FRESH" : "STALE";
    }

    private static boolean archiveReady(DemoFinalExternalReviewReleaseBundleArchiveVo archive) {
        return archive != null
                && archive.status() == DemoReadinessStatus.READY
                && archive.releaseReady();
    }

    private static boolean receiptMatchesArchive(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt
    ) {
        return same(archive.id(), receipt.releaseBundleArchiveId())
                && same(archive.latestCertificateArchiveId(), receipt.latestCertificateArchiveId())
                && same(archive.latestDeliveryFinalizationArchiveId(), receipt.latestDeliveryFinalizationArchiveId())
                && same(archive.latestPackageArchiveId(), receipt.latestPackageArchiveId())
                && same(archive.latestDeliveryReceiptId(), receipt.latestPackageDeliveryReceiptId())
                && same(archive.latestTaskId(), receipt.latestTaskId())
                && same(archive.latestPullRequestUrl(), receipt.latestPullRequestUrl());
    }

    private static String summary(DemoReadinessStatus status, String freshness) {
        if (status == DemoReadinessStatus.READY) {
            return "Final external-review release bundle delivery is finalized with a fresh release-bundle receipt.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final external-review release bundle delivery finalization is blocked because the latest release bundle archive is not ready.";
        }
        if ("MISSING".equals(freshness)) {
            return "Final external-review release bundle archive is ready but has no release-bundle delivery receipt.";
        }
        return "Final external-review release bundle archive is ready but release-bundle delivery evidence is not current.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            String freshness,
            DemoFinalExternalReviewReleaseBundleArchiveVo archive
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the release bundle delivery finalization report as the terminal reviewer handoff record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return archive == null
                    ? "Archive the READY final external-review release bundle before recording delivery finalization."
                    : archive.nextAction();
        }
        if ("STALE".equals(freshness)) {
            return "Record a new final external-review release bundle delivery receipt for release bundle archive "
                    + valueOrNone(archive.id()) + ".";
        }
        return "Deliver the frozen final external-review release bundle, record a release-bundle delivery receipt, "
                + "then download the finalization report.";
    }

    private static String freshnessSummary(String freshness) {
        return switch (freshness) {
            case "FRESH" ->
                    "Latest release bundle delivery receipt matches the current frozen final external-review release bundle.";
            case "MISSING" ->
                    "No release bundle delivery receipt is available for the current frozen final external-review release bundle.";
            case "STALE" ->
                    "Latest release bundle delivery receipt does not match the current frozen final external-review release bundle.";
            default ->
                    "Release bundle delivery receipt freshness cannot be evaluated until the latest release bundle archive is ready.";
        };
    }

    private static List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check> checks(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
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
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check(
                        "Frozen final external-review release bundle",
                        archiveStatus,
                        archiveReady(archive)
                                ? "Frozen final external-review release bundle is ready."
                                : "Frozen final external-review release bundle is not ready.",
                        archiveReady(archive) ? "No action needed." : nextAction(DemoReadinessStatus.BLOCKED, freshness, archive)
                ),
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check(
                        "Release bundle delivery receipt",
                        receiptStatus,
                        freshnessSummary(freshness),
                        receiptFresh
                                ? "No action needed."
                                : "Record a fresh delivery receipt for the current frozen final external-review release bundle."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt,
            String freshness
    ) {
        List<String> notes = new ArrayList<>();
        if (archiveReady(archive)) {
            notes.add("Frozen final external-review release bundle " + archive.id() + " is ready.");
        } else {
            notes.add("Frozen final external-review release bundle is not ready.");
        }
        if (receipt == null) {
            notes.add("No final external-review release bundle delivery receipt is available.");
        } else if ("FRESH".equals(freshness)) {
            notes.add("Release bundle delivery receipt " + receipt.id() + " is fresh.");
        } else if ("STALE".equals(freshness)) {
            notes.add("Latest release bundle delivery receipt " + receipt.id() + " is stale.");
        } else {
            notes.add("Release bundle delivery receipt " + receipt.id()
                    + " cannot finalize a blocked release bundle archive.");
        }
        if (archive != null) {
            notes.addAll(archive.evidenceNotes());
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final external-review release bundle delivery finalization report.");
        if (archive != null) {
            actions.add("Download final external-review release bundle archive " + archive.id() + ".");
        }
        if (receipt != null) {
            actions.add("Download final external-review release bundle delivery receipt " + receipt.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            DemoFinalExternalReviewReleaseBundleArchiveVo archive,
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt,
            String freshness,
            boolean receiptFresh,
            String freshnessSummary,
            List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final External Review Release Bundle Delivery Finalization\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Finalized: `").append(finalized).append("`\n");
        report.append("- Summary: ").append(summary).append('\n');
        report.append("- Next action: ").append(nextAction).append('\n');
        report.append("- Latest release bundle archive: `").append(archive == null ? "none" : archive.id()).append("`\n");
        report.append("- Latest release bundle delivery receipt: `")
                .append(receipt == null ? "none" : receipt.id()).append("`\n");
        report.append("- Latest certificate archive: `")
                .append(archive == null ? "none" : valueOrNone(archive.latestCertificateArchiveId())).append("`\n");
        report.append("- Latest package delivery finalization archive: `")
                .append(archive == null ? "none" : valueOrNone(archive.latestDeliveryFinalizationArchiveId()))
                .append("`\n");
        report.append("- Latest package archive: `")
                .append(archive == null ? "none" : valueOrNone(archive.latestPackageArchiveId())).append("`\n");
        report.append("- Latest package delivery receipt: `")
                .append(archive == null ? "none" : valueOrNone(archive.latestDeliveryReceiptId())).append("`\n");
        report.append("- Latest task: `").append(archive == null ? "none" : valueOrNone(archive.latestTaskId())).append("`\n");
        report.append("- Release bundle delivery receipt freshness: `").append(freshness).append("`\n");
        report.append("- Release bundle delivery receipt fresh: `").append(receiptFresh).append("`\n");
        report.append("- Release bundle delivery receipt freshness summary: ").append(freshnessSummary).append('\n');
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
            List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check check : checks) {
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
