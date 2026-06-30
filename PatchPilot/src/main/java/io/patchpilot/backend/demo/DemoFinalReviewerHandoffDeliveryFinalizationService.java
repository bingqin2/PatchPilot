package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalReviewerHandoffDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalReviewerHandoffDeliveryFinalizationService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-reviewer-handoff-package/delivery-finalization is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<DemoFinalReviewerHandoffPackageVo> packageSupplier;
    private final Supplier<List<DemoFinalReviewerHandoffDeliveryReceiptVo>> receiptSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalReviewerHandoffDeliveryFinalizationService(
            DemoFinalReviewerHandoffPackageService packageService,
            DemoFinalReviewerHandoffDeliveryReceiptRepository receiptRepository
    ) {
        this(
                packageService::getPackage,
                () -> receiptRepository.listRecentReceipts(1),
                Clock.systemUTC()
        );
    }

    DemoFinalReviewerHandoffDeliveryFinalizationService(
            Supplier<DemoFinalReviewerHandoffPackageVo> packageSupplier,
            Supplier<List<DemoFinalReviewerHandoffDeliveryReceiptVo>> receiptSupplier,
            Clock clock
    ) {
        this.packageSupplier = packageSupplier;
        this.receiptSupplier = receiptSupplier;
        this.clock = clock;
    }

    public DemoFinalReviewerHandoffDeliveryFinalizationVo getFinalizationGate() {
        DemoFinalReviewerHandoffPackageVo latestPackage = packageSupplier.get();
        DemoFinalReviewerHandoffDeliveryReceiptVo latestReceipt = latestReceipt();
        String freshness = freshness(latestPackage, latestReceipt);
        boolean receiptFresh = "FRESH".equals(freshness);
        DemoReadinessStatus status = status(latestPackage, freshness);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(status, freshness);
        String nextAction = nextAction(status, freshness, latestPackage);
        String freshnessSummary = freshnessSummary(freshness);
        List<DemoFinalReviewerHandoffDeliveryFinalizationVo.Check> checks =
                checks(latestPackage, freshness, receiptFresh);
        List<String> evidenceNotes = evidenceNotes(latestPackage, latestReceipt, freshness);
        List<String> downloadActions = downloadActions(latestPackage, latestReceipt);
        Instant generatedAt = Instant.now(clock);
        String markdownReport = markdownReport(
                status,
                finalized,
                summary,
                nextAction,
                latestPackage,
                latestReceipt,
                freshness,
                receiptFresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                downloadActions,
                generatedAt
        );

        return new DemoFinalReviewerHandoffDeliveryFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                latestReceipt == null ? null : latestReceipt.id(),
                latestPackage == null ? null : latestPackage.latestCertificateArchiveId(),
                latestPackage == null ? null : latestPackage.latestDeliveryFinalizationArchiveId(),
                latestPackage == null ? null : latestPackage.latestReleaseBundleArchiveId(),
                latestPackage == null ? null : latestPackage.latestDeliveryReceiptId(),
                latestPackage == null ? null : latestPackage.latestPackageCertificateArchiveId(),
                latestPackage == null ? null : latestPackage.latestPackageArchiveId(),
                latestPackage == null ? null : latestPackage.latestPackageDeliveryReceiptId(),
                latestPackage == null ? null : latestPackage.latestTaskId(),
                latestPackage == null ? null : latestPackage.latestPullRequestUrl(),
                latestReceipt == null ? latestPackage == null ? null : latestPackage.latestDeliveryTarget()
                        : latestReceipt.deliveryTarget(),
                latestReceipt == null ? latestPackage == null ? null : latestPackage.latestDeliveryChannel()
                        : latestReceipt.deliveryChannel(),
                latestReceipt == null || latestReceipt.deliveredAt() == null
                        ? latestPackage == null ? null : latestPackage.latestDeliveredAt()
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

    private DemoFinalReviewerHandoffDeliveryReceiptVo latestReceipt() {
        List<DemoFinalReviewerHandoffDeliveryReceiptVo> receipts = receiptSupplier.get();
        return receipts.isEmpty() ? null : receipts.get(0);
    }

    private static DemoReadinessStatus status(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            String freshness
    ) {
        if (!packageReady(handoffPackage)) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "FRESH".equals(freshness) ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String freshness(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    ) {
        if (!packageReady(handoffPackage)) {
            return "BLOCKED";
        }
        if (receipt == null) {
            return "MISSING";
        }
        return receiptMatchesPackage(handoffPackage, receipt) ? "FRESH" : "STALE";
    }

    private static boolean packageReady(DemoFinalReviewerHandoffPackageVo handoffPackage) {
        return handoffPackage != null
                && handoffPackage.status() == DemoReadinessStatus.READY
                && handoffPackage.readyForReview()
                && hasText(handoffPackage.latestCertificateArchiveId());
    }

    private static boolean receiptMatchesPackage(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    ) {
        return same(handoffPackage.latestCertificateArchiveId(), receipt.latestCertificateArchiveId())
                && same(handoffPackage.latestDeliveryFinalizationArchiveId(), receipt.latestDeliveryFinalizationArchiveId())
                && same(handoffPackage.latestReleaseBundleArchiveId(), receipt.latestReleaseBundleArchiveId())
                && same(handoffPackage.latestDeliveryReceiptId(), receipt.latestDeliveryReceiptId())
                && same(handoffPackage.latestPackageCertificateArchiveId(), receipt.latestPackageCertificateArchiveId())
                && same(handoffPackage.latestPackageArchiveId(), receipt.latestPackageArchiveId())
                && same(handoffPackage.latestPackageDeliveryReceiptId(), receipt.latestPackageDeliveryReceiptId())
                && same(handoffPackage.latestTaskId(), receipt.latestTaskId())
                && same(handoffPackage.latestPullRequestUrl(), receipt.latestPullRequestUrl());
    }

    private static String summary(DemoReadinessStatus status, String freshness) {
        if (status == DemoReadinessStatus.READY) {
            return "Final reviewer handoff delivery is finalized with a fresh handoff delivery receipt.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final reviewer handoff delivery finalization is blocked because the package is not ready.";
        }
        if ("MISSING".equals(freshness)) {
            return "Final reviewer handoff package is ready but has no handoff delivery receipt.";
        }
        return "Final reviewer handoff package is ready but handoff delivery evidence is not current.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            String freshness,
            DemoFinalReviewerHandoffPackageVo handoffPackage
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the final reviewer handoff delivery finalization report as the terminal demo closeout record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return handoffPackage == null
                    ? "Build the READY final reviewer handoff package before recording delivery finalization."
                    : handoffPackage.nextAction();
        }
        if ("STALE".equals(freshness)) {
            return "Record a new final reviewer handoff delivery receipt for terminal certificate archive "
                    + valueOrNone(handoffPackage.latestCertificateArchiveId()) + ".";
        }
        return "Send the final reviewer handoff package, record a handoff delivery receipt, "
                + "then download the finalization report.";
    }

    private static String freshnessSummary(String freshness) {
        return switch (freshness) {
            case "FRESH" ->
                    "Latest final reviewer handoff delivery receipt matches the current final reviewer handoff package.";
            case "MISSING" ->
                    "No handoff delivery receipt is available for the current final reviewer handoff package.";
            case "STALE" ->
                    "Latest handoff delivery receipt does not match the current final reviewer handoff package.";
            default ->
                    "Handoff delivery receipt freshness cannot be evaluated until the package is ready.";
        };
    }

    private static List<DemoFinalReviewerHandoffDeliveryFinalizationVo.Check> checks(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            String freshness,
            boolean receiptFresh
    ) {
        DemoReadinessStatus packageStatus = packageReady(handoffPackage)
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.BLOCKED;
        DemoReadinessStatus receiptStatus = receiptFresh ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
        if ("BLOCKED".equals(freshness)) {
            receiptStatus = DemoReadinessStatus.BLOCKED;
        }
        return List.of(
                new DemoFinalReviewerHandoffDeliveryFinalizationVo.Check(
                        "Final reviewer handoff package",
                        packageStatus,
                        packageReady(handoffPackage)
                                ? "Final reviewer handoff package is ready."
                                : "Final reviewer handoff package is not ready.",
                        packageReady(handoffPackage)
                                ? "No action needed."
                                : nextAction(DemoReadinessStatus.BLOCKED, freshness, handoffPackage)
                ),
                new DemoFinalReviewerHandoffDeliveryFinalizationVo.Check(
                        "Final reviewer handoff delivery receipt",
                        receiptStatus,
                        freshnessSummary(freshness),
                        receiptFresh
                                ? "No action needed."
                                : "Record a fresh delivery receipt for the current final reviewer handoff package."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt,
            String freshness
    ) {
        List<String> notes = new ArrayList<>();
        if (packageReady(handoffPackage)) {
            notes.add("Final reviewer handoff package is ready.");
        } else {
            notes.add("Final reviewer handoff package is not ready.");
        }
        if (receipt == null) {
            notes.add("No final reviewer handoff delivery receipt is available.");
        } else if ("FRESH".equals(freshness)) {
            notes.add("Final reviewer handoff delivery receipt " + receipt.id() + " is fresh.");
        } else if ("STALE".equals(freshness)) {
            notes.add("Latest final reviewer handoff delivery receipt " + receipt.id() + " is stale.");
        } else {
            notes.add("Final reviewer handoff delivery receipt " + receipt.id()
                    + " cannot finalize a blocked handoff package.");
        }
        if (handoffPackage != null) {
            notes.addAll(handoffPackage.evidenceNotes());
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final reviewer handoff delivery finalization report.");
        if (handoffPackage != null) {
            actions.add("Download final reviewer handoff package report.");
        }
        if (receipt != null) {
            actions.add("Download final reviewer handoff delivery receipt " + receipt.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            DemoFinalReviewerHandoffPackageVo handoffPackage,
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt,
            String freshness,
            boolean receiptFresh,
            String freshnessSummary,
            List<DemoFinalReviewerHandoffDeliveryFinalizationVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final Reviewer Handoff Delivery Finalization\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Finalized: `").append(finalized).append("`\n");
        report.append("- Summary: ").append(summary).append('\n');
        report.append("- Next action: ").append(nextAction).append('\n');
        report.append("- Latest handoff delivery receipt: `")
                .append(receipt == null ? "none" : receipt.id()).append("`\n");
        report.append("- Terminal certificate archive: `")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.latestCertificateArchiveId()))
                .append("`\n");
        report.append("- Release-bundle delivery finalization archive: `")
                .append(handoffPackage == null
                        ? "none"
                        : valueOrNone(handoffPackage.latestDeliveryFinalizationArchiveId()))
                .append("`\n");
        report.append("- Release bundle archive: `")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.latestReleaseBundleArchiveId()))
                .append("`\n");
        report.append("- Release-bundle delivery receipt: `")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.latestDeliveryReceiptId()))
                .append("`\n");
        report.append("- Task: `")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.latestTaskId()))
                .append("`\n");
        report.append("- Handoff delivery receipt freshness: `").append(freshness).append("`\n");
        report.append("- Handoff delivery receipt fresh: `").append(receiptFresh).append("`\n");
        report.append("- Handoff delivery receipt freshness summary: ").append(freshnessSummary).append('\n');
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
            List<DemoFinalReviewerHandoffDeliveryFinalizationVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoFinalReviewerHandoffDeliveryFinalizationVo.Check check : checks) {
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

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value.trim() : "none";
    }
}
