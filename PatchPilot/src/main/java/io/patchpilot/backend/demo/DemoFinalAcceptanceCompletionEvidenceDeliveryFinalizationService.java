package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-acceptance-completion-evidence-delivery-finalization is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> bundleSupplier;
    private final Supplier<List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo>> receiptSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService(
            DemoFinalAcceptanceCompletionEvidenceBundleService bundleService,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService receiptService
    ) {
        this(bundleService::getBundle, receiptService::listRecentReceipts, Clock.systemUTC());
    }

    DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService(
            Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> bundleSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo>> receiptSupplier,
            Clock clock
    ) {
        this.bundleSupplier = bundleSupplier;
        this.receiptSupplier = receiptSupplier;
        this.clock = clock;
    }

    public DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo getFinalizationGate() {
        DemoFinalAcceptanceCompletionEvidenceBundleVo bundle = bundleSupplier.get();
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt = latestReceipt();
        String freshness = freshness(bundle, latestReceipt);
        boolean receiptFresh = "FRESH".equals(freshness);
        DemoReadinessStatus status = status(bundle, freshness);
        boolean finalized = status == DemoReadinessStatus.READY;
        String summary = summary(status, freshness);
        String nextAction = nextAction(status, freshness, bundle);
        String freshnessSummary = freshnessSummary(freshness);
        List<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check> checks =
                checks(bundle, latestReceipt, freshness, receiptFresh);
        List<String> evidenceNotes = evidenceNotes(bundle, latestReceipt, freshness);
        List<String> downloadActions = downloadActions(latestReceipt);
        Instant generatedAt = Instant.now(clock);
        String markdownReport = markdownReport(
                status,
                finalized,
                summary,
                nextAction,
                bundle,
                latestReceipt,
                freshness,
                receiptFresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                downloadActions,
                generatedAt
        );

        return new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                bundle.latestCompletionArchiveId(),
                bundle.latestSharePackageArchiveId(),
                bundle.latestDeliveryReceiptId(),
                bundle.latestTaskId(),
                latestReceipt == null ? null : latestReceipt.id(),
                latestReceipt == null ? bundle.latestDeliveryTarget() : latestReceipt.deliveryTarget(),
                latestReceipt == null ? bundle.latestDeliveryChannel() : latestReceipt.deliveryChannel(),
                latestReceipt == null || latestReceipt.deliveredAt() == null ? null : latestReceipt.deliveredAt().toString(),
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

    private DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt() {
        List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> receipts = receiptSupplier.get();
        return receipts.isEmpty() ? null : receipts.get(0);
    }

    private static DemoReadinessStatus status(
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            String freshness
    ) {
        if (!bundle.readyToShare() || bundle.status() != DemoReadinessStatus.READY) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "FRESH".equals(freshness) ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String freshness(
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt
    ) {
        if (!bundle.readyToShare() || bundle.status() != DemoReadinessStatus.READY) {
            return "BLOCKED";
        }
        if (latestReceipt == null) {
            return "MISSING";
        }
        return receiptMatchesBundle(bundle, latestReceipt) ? "FRESH" : "STALE";
    }

    private static boolean receiptMatchesBundle(
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt
    ) {
        return same(bundle.latestCompletionArchiveId(), receipt.latestCompletionArchiveId())
                && same(bundle.latestSharePackageArchiveId(), receipt.latestSharePackageArchiveId())
                && same(bundle.latestDeliveryReceiptId(), receipt.latestDeliveryReceiptId())
                && same(bundle.latestTaskId(), receipt.latestTaskId());
    }

    private static String summary(DemoReadinessStatus status, String freshness) {
        if (status == DemoReadinessStatus.READY) {
            return "Final acceptance completion evidence delivery is finalized with a fresh delivery receipt.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final acceptance completion evidence delivery finalization is blocked because the completion evidence bundle is not ready.";
        }
        if ("MISSING".equals(freshness)) {
            return "Final acceptance completion evidence bundle is ready but has no completion evidence delivery receipt.";
        }
        return "Final acceptance completion evidence bundle is ready but delivery evidence is not current.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            String freshness,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use the finalization report as the reviewer-facing completion delivery record.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return bundle.nextAction();
        }
        if ("STALE".equals(freshness)) {
            return "Record a new final acceptance completion evidence delivery receipt for completion archive "
                    + valueOrNone(bundle.latestCompletionArchiveId()) + ".";
        }
        return "Share the current final acceptance completion evidence bundle, record a delivery receipt, "
                + "then download the finalization report.";
    }

    private static String freshnessSummary(String freshness) {
        return switch (freshness) {
            case "FRESH" -> "Latest completion evidence delivery receipt matches the current completion evidence bundle.";
            case "MISSING" -> "No completion evidence delivery receipt is available for the current completion evidence bundle.";
            case "STALE" -> "Latest completion evidence delivery receipt does not match the current completion evidence bundle.";
            default -> "Completion evidence delivery receipt freshness cannot be evaluated until the bundle is ready.";
        };
    }

    private static List<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check> checks(
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt,
            String freshness,
            boolean receiptFresh
    ) {
        DemoReadinessStatus bundleStatus =
                bundle.readyToShare() && bundle.status() == DemoReadinessStatus.READY
                        ? DemoReadinessStatus.READY
                        : DemoReadinessStatus.BLOCKED;
        DemoReadinessStatus receiptStatus = receiptFresh ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION;
        if ("BLOCKED".equals(freshness)) {
            receiptStatus = DemoReadinessStatus.BLOCKED;
        }
        return List.of(
                new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check(
                        "Completion evidence bundle",
                        bundleStatus,
                        bundle.readyToShare()
                                ? "Completion evidence bundle is ready to share."
                                : "Completion evidence bundle is not ready to share.",
                        bundle.readyToShare() ? "No action needed." : bundle.nextAction()
                ),
                new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check(
                        "Completion evidence delivery receipt",
                        receiptStatus,
                        freshnessSummary(freshness),
                        receiptFresh
                                ? "No action needed."
                                : "Record a fresh completion evidence delivery receipt for the current bundle."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt,
            String freshness
    ) {
        List<String> notes = new ArrayList<>();
        if (bundle.readyToShare()) {
            notes.add("Completion evidence bundle " + valueOrNone(bundle.latestCompletionArchiveId())
                    + " is ready to share.");
        } else {
            notes.add("Completion evidence bundle is not ready to share.");
        }
        if (latestReceipt == null) {
            notes.add("No completion evidence delivery receipt is available.");
        } else if ("FRESH".equals(freshness)) {
            notes.add("Completion evidence delivery receipt " + latestReceipt.id() + " is fresh.");
        } else if ("STALE".equals(freshness)) {
            notes.add("Latest completion evidence delivery receipt " + latestReceipt.id() + " is stale.");
        } else {
            notes.add("Completion evidence delivery receipt " + latestReceipt.id()
                    + " cannot finalize a blocked bundle.");
        }
        notes.addAll(bundle.evidenceNotes());
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final acceptance completion evidence delivery finalization report.");
        actions.add("Download final acceptance completion evidence bundle.");
        if (latestReceipt != null) {
            actions.add("Download final acceptance completion evidence delivery receipt " + latestReceipt.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean finalized,
            String summary,
            String nextAction,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt,
            String freshness,
            boolean receiptFresh,
            String freshnessSummary,
            List<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final Acceptance Completion Evidence Delivery Finalization\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Finalized: `").append(finalized).append("`\n");
        report.append("- Summary: ").append(summary).append('\n');
        report.append("- Next action: ").append(nextAction).append('\n');
        report.append("- Latest completion archive: `").append(valueOrNone(bundle.latestCompletionArchiveId())).append("`\n");
        report.append("- Latest share package archive: `").append(valueOrNone(bundle.latestSharePackageArchiveId())).append("`\n");
        report.append("- Latest final acceptance delivery receipt: `").append(valueOrNone(bundle.latestDeliveryReceiptId())).append("`\n");
        report.append("- Latest task: `").append(valueOrNone(bundle.latestTaskId())).append("`\n");
        report.append("- Latest completion evidence delivery receipt: `")
                .append(latestReceipt == null ? "none" : latestReceipt.id())
                .append("`\n");
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
            List<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check check : checks) {
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
