package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoHandoffDeliveryFinalizationService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/live-demo-handoff-package/delivery-finalization is read-only: "
                    + "it does not send messages, write to GitHub, create tasks, call the model, run tests, "
                    + "archive records, record receipts, mutate Git, or change repositories.";

    private final Supplier<DemoLiveDemoHandoffPackageVo> packageSupplier;
    private final Supplier<List<DemoLiveDemoHandoffDeliveryReceiptVo>> receiptSupplier;
    private final Clock clock;

    @Autowired
    public DemoLiveDemoHandoffDeliveryFinalizationService(
            DemoLiveDemoHandoffPackageService packageService,
            DemoLiveDemoHandoffDeliveryReceiptRepository receiptRepository
    ) {
        this(packageService::createPackage, () -> receiptRepository.listRecentReceipts(1), Clock.systemUTC());
    }

    DemoLiveDemoHandoffDeliveryFinalizationService(
            Supplier<DemoLiveDemoHandoffPackageVo> packageSupplier,
            Supplier<List<DemoLiveDemoHandoffDeliveryReceiptVo>> receiptSupplier,
            Clock clock
    ) {
        this.packageSupplier = packageSupplier;
        this.receiptSupplier = receiptSupplier;
        this.clock = clock;
    }

    public DemoLiveDemoHandoffDeliveryFinalizationVo getFinalizationGate() {
        DemoLiveDemoHandoffPackageVo handoffPackage = packageSupplier.get();
        DemoLiveDemoHandoffDeliveryReceiptVo receipt = latestReceipt();
        String freshness = freshness(handoffPackage, receipt);
        boolean receiptFresh = "FRESH".equals(freshness);
        String status = status(handoffPackage, freshness);
        boolean finalized = "READY".equals(status);
        String summary = summary(status, freshness);
        String nextAction = nextAction(status, freshness, handoffPackage);
        String freshnessSummary = freshnessSummary(freshness);
        List<DemoLiveDemoHandoffDeliveryFinalizationVo.Check> checks =
                checks(handoffPackage, freshness, receiptFresh);
        List<String> evidenceNotes = evidenceNotes(handoffPackage, receipt, freshness);
        List<String> downloadActions = downloadActions(handoffPackage, receipt);
        Instant generatedAt = Instant.now(clock);
        String markdownReport = markdownReport(
                status,
                finalized,
                summary,
                nextAction,
                handoffPackage,
                receipt,
                freshness,
                receiptFresh,
                freshnessSummary,
                checks,
                evidenceNotes,
                downloadActions,
                generatedAt
        );

        return new DemoLiveDemoHandoffDeliveryFinalizationVo(
                status,
                finalized,
                summary,
                nextAction,
                receipt == null ? null : receipt.id(),
                handoffPackage == null ? null : handoffPackage.evidenceBundleArchiveId(),
                handoffPackage == null ? null : handoffPackage.repository(),
                handoffPackage == null ? 0 : handoffPackage.issueNumber(),
                handoffPackage == null ? null : handoffPackage.issueUrl(),
                handoffPackage == null ? null : handoffPackage.taskId(),
                handoffPackage == null ? null : handoffPackage.taskStatus(),
                handoffPackage == null ? null : handoffPackage.pullRequestUrl(),
                receipt == null ? null : receipt.deliveryTarget(),
                receipt == null ? null : receipt.deliveryChannel(),
                receipt == null || receipt.deliveredAt() == null ? null : receipt.deliveredAt().toString(),
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

    private DemoLiveDemoHandoffDeliveryReceiptVo latestReceipt() {
        List<DemoLiveDemoHandoffDeliveryReceiptVo> receipts = receiptSupplier.get();
        return receipts.isEmpty() ? null : receipts.get(0);
    }

    private static String status(DemoLiveDemoHandoffPackageVo handoffPackage, String freshness) {
        if (!packageReady(handoffPackage)) {
            return "BLOCKED";
        }
        return "FRESH".equals(freshness) ? "READY" : "NEEDS_ATTENTION";
    }

    private static String freshness(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoHandoffDeliveryReceiptVo receipt
    ) {
        if (!packageReady(handoffPackage)) {
            return "BLOCKED";
        }
        if (receipt == null) {
            return "MISSING";
        }
        return receiptMatchesPackage(handoffPackage, receipt) ? "FRESH" : "STALE";
    }

    private static boolean packageReady(DemoLiveDemoHandoffPackageVo handoffPackage) {
        return handoffPackage != null
                && "READY".equals(handoffPackage.status())
                && handoffPackage.readyForReview()
                && hasText(handoffPackage.evidenceBundleArchiveId());
    }

    private static boolean receiptMatchesPackage(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoHandoffDeliveryReceiptVo receipt
    ) {
        return same(handoffPackage.evidenceBundleArchiveId(), receipt.evidenceBundleArchiveId())
                && same(handoffPackage.repository(), receipt.repository())
                && handoffPackage.issueNumber() == receipt.issueNumber()
                && same(handoffPackage.issueUrl(), receipt.issueUrl())
                && same(handoffPackage.taskId(), receipt.taskId())
                && same(handoffPackage.taskStatus(), receipt.taskStatus())
                && same(handoffPackage.pullRequestUrl(), receipt.pullRequestUrl());
    }

    private static String summary(String status, String freshness) {
        if ("READY".equals(status)) {
            return "Live demo handoff delivery is finalized with a fresh delivery receipt.";
        }
        if ("BLOCKED".equals(status)) {
            return "Live demo handoff delivery finalization is blocked because the handoff package is not ready.";
        }
        if ("MISSING".equals(freshness)) {
            return "Live demo handoff package is ready but has no delivery receipt.";
        }
        return "Live demo handoff package is ready but delivery evidence is not current.";
    }

    private static String nextAction(
            String status,
            String freshness,
            DemoLiveDemoHandoffPackageVo handoffPackage
    ) {
        if ("READY".equals(status)) {
            return "Use this finalization report as the live demo reviewer handoff completion proof.";
        }
        if ("BLOCKED".equals(status)) {
            return handoffPackage == null
                    ? "Build the READY live demo handoff package before recording delivery finalization."
                    : firstOrDefault(
                            handoffPackage.deliveryInstructions(),
                            "Prepare a READY live demo handoff package before recording delivery finalization."
                    );
        }
        if ("STALE".equals(freshness)) {
            return "Record a new live demo handoff delivery receipt for evidence bundle archive "
                    + valueOrNone(handoffPackage.evidenceBundleArchiveId()) + ".";
        }
        return "Send the live demo handoff package, record a delivery receipt, "
                + "then download this finalization report.";
    }

    private static String freshnessSummary(String freshness) {
        return switch (freshness) {
            case "FRESH" -> "Latest live demo handoff delivery receipt matches the current handoff package.";
            case "MISSING" -> "No live demo handoff delivery receipt is available for the current handoff package.";
            case "STALE" -> "Latest live demo handoff delivery receipt does not match the current handoff package.";
            default -> "Delivery receipt freshness cannot be evaluated until the handoff package is ready.";
        };
    }

    private static List<DemoLiveDemoHandoffDeliveryFinalizationVo.Check> checks(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            String freshness,
            boolean receiptFresh
    ) {
        String packageStatus = packageReady(handoffPackage) ? "READY" : "BLOCKED";
        String receiptStatus = receiptFresh ? "READY" : "NEEDS_ATTENTION";
        if ("BLOCKED".equals(freshness)) {
            receiptStatus = "BLOCKED";
        }
        return List.of(
                new DemoLiveDemoHandoffDeliveryFinalizationVo.Check(
                        "Live demo handoff package",
                        packageStatus,
                        packageReady(handoffPackage)
                                ? "Live demo handoff package is ready."
                                : "Live demo handoff package is not ready.",
                        packageReady(handoffPackage)
                                ? "No action needed."
                                : nextAction("BLOCKED", freshness, handoffPackage)
                ),
                new DemoLiveDemoHandoffDeliveryFinalizationVo.Check(
                        "Live demo handoff delivery receipt",
                        receiptStatus,
                        freshnessSummary(freshness),
                        receiptFresh
                                ? "No action needed."
                                : "Record a fresh delivery receipt for the current live demo handoff package."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoHandoffDeliveryReceiptVo receipt,
            String freshness
    ) {
        List<String> notes = new ArrayList<>();
        if (packageReady(handoffPackage)) {
            notes.add("Live demo handoff package is ready.");
        } else {
            notes.add("Live demo handoff package is not ready.");
        }
        if (receipt == null) {
            notes.add("No live demo handoff delivery receipt is available.");
        } else if ("FRESH".equals(freshness)) {
            notes.add("Live demo handoff delivery receipt " + receipt.id() + " is fresh.");
        } else if ("STALE".equals(freshness)) {
            notes.add("Latest live demo handoff delivery receipt " + receipt.id() + " is stale.");
        } else {
            notes.add("Live demo handoff delivery receipt " + receipt.id()
                    + " cannot finalize a blocked handoff package.");
        }
        if (handoffPackage != null) {
            notes.addAll(handoffPackage.evidenceNotes());
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoHandoffDeliveryReceiptVo receipt
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download live demo handoff delivery finalization report.");
        if (handoffPackage != null) {
            actions.add("Download live demo handoff package report.");
        }
        if (receipt != null) {
            actions.add("Download live demo handoff delivery receipt " + receipt.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            String status,
            boolean finalized,
            String summary,
            String nextAction,
            DemoLiveDemoHandoffPackageVo handoffPackage,
            DemoLiveDemoHandoffDeliveryReceiptVo receipt,
            String freshness,
            boolean receiptFresh,
            String freshnessSummary,
            List<DemoLiveDemoHandoffDeliveryFinalizationVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Handoff Delivery Finalization\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Finalized: `").append(finalized).append("`\n");
        report.append("- Summary: ").append(summary).append('\n');
        report.append("- Next action: ").append(nextAction).append('\n');
        report.append("- Latest delivery receipt: `")
                .append(receipt == null ? "none" : receipt.id()).append("`\n");
        report.append("- Evidence bundle archive: `")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.evidenceBundleArchiveId()))
                .append("`\n");
        report.append("- Repository: ").append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.repository())).append('\n');
        report.append("- Issue: #").append(handoffPackage == null ? 0 : handoffPackage.issueNumber()).append('\n');
        report.append("- Task: `")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.taskId()))
                .append("`\n");
        report.append("- Pull Request: ")
                .append(handoffPackage == null ? "none" : valueOrNone(handoffPackage.pullRequestUrl()))
                .append('\n');
        report.append("- Delivery receipt freshness: `").append(freshness).append("`\n");
        report.append("- Delivery receipt fresh: `").append(receiptFresh).append("`\n");
        report.append("- Delivery receipt freshness summary: ").append(freshnessSummary).append('\n');
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendChecks(report, checks);
        appendList(report, "Evidence Notes", evidenceNotes);
        appendList(report, "Download Actions", downloadActions);
        report.append("## Side Effect Contract\n\n").append(SIDE_EFFECT_CONTRACT).append('\n');
        return report.toString();
    }

    private static void appendChecks(
            StringBuilder report,
            List<DemoLiveDemoHandoffDeliveryFinalizationVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoLiveDemoHandoffDeliveryFinalizationVo.Check check : checks) {
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

    private static String firstOrDefault(List<String> items, String fallback) {
        return items.isEmpty() ? fallback : items.get(0);
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
