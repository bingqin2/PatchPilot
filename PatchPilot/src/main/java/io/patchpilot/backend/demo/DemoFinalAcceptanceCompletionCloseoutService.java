package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceCompletionCloseoutService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-acceptance-completion-closeout is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<DemoAcceptanceSummaryVo> acceptanceSummarySupplier;
    private final Supplier<DemoFinalAcceptanceShareFinalizationVo> shareFinalizationSupplier;
    private final Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> completionEvidenceBundleSupplier;
    private final Supplier<List<DemoFinalAcceptanceCompletionArchiveVo>> completionArchiveSupplier;
    private final Supplier<List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo>> completionReceiptSupplier;
    private final Supplier<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo> completionDeliveryFinalizationSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalAcceptanceCompletionCloseoutService(
            DemoAcceptanceSummaryService acceptanceSummaryService,
            DemoFinalAcceptanceShareFinalizationService shareFinalizationService,
            DemoFinalAcceptanceCompletionEvidenceBundleService completionEvidenceBundleService,
            DemoFinalAcceptanceCompletionArchiveService completionArchiveService,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService completionEvidenceDeliveryReceiptService,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService completionDeliveryFinalizationService
    ) {
        this(
                acceptanceSummaryService::getSummary,
                shareFinalizationService::getFinalizationGate,
                completionEvidenceBundleService::getBundle,
                completionArchiveService::listRecentArchives,
                completionEvidenceDeliveryReceiptService::listRecentReceipts,
                completionDeliveryFinalizationService::getFinalizationGate,
                Clock.systemUTC()
        );
    }

    DemoFinalAcceptanceCompletionCloseoutService(
            Supplier<DemoAcceptanceSummaryVo> acceptanceSummarySupplier,
            Supplier<DemoFinalAcceptanceShareFinalizationVo> shareFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> completionEvidenceBundleSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionArchiveVo>> completionArchiveSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo>> completionReceiptSupplier,
            Supplier<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo> completionDeliveryFinalizationSupplier,
            Clock clock
    ) {
        this.acceptanceSummarySupplier = acceptanceSummarySupplier;
        this.shareFinalizationSupplier = shareFinalizationSupplier;
        this.completionEvidenceBundleSupplier = completionEvidenceBundleSupplier;
        this.completionArchiveSupplier = completionArchiveSupplier;
        this.completionReceiptSupplier = completionReceiptSupplier;
        this.completionDeliveryFinalizationSupplier = completionDeliveryFinalizationSupplier;
        this.clock = clock;
    }

    public DemoFinalAcceptanceCompletionCloseoutVo getCloseout() {
        DemoAcceptanceSummaryVo acceptanceSummary = acceptanceSummarySupplier.get();
        DemoFinalAcceptanceShareFinalizationVo shareFinalization = shareFinalizationSupplier.get();
        DemoFinalAcceptanceCompletionEvidenceBundleVo bundle = completionEvidenceBundleSupplier.get();
        List<DemoFinalAcceptanceCompletionArchiveVo> completionArchives = completionArchiveSupplier.get();
        List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> completionReceipts = completionReceiptSupplier.get();
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo deliveryFinalization =
                completionDeliveryFinalizationSupplier.get();

        DemoFinalAcceptanceCompletionArchiveVo latestArchive =
                completionArchives.isEmpty() ? null : completionArchives.get(0);
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt =
                completionReceipts.isEmpty() ? null : completionReceipts.get(0);
        DemoReadinessStatus status = status(
                acceptanceSummary,
                shareFinalization,
                bundle,
                deliveryFinalization,
                latestReceipt
        );
        boolean closed = status == DemoReadinessStatus.READY;
        String summary = summary(status, acceptanceSummary, shareFinalization, bundle);
        String nextAction = nextAction(status, acceptanceSummary, shareFinalization, bundle, deliveryFinalization);
        List<DemoFinalAcceptanceCompletionCloseoutVo.Check> checks = checks(
                acceptanceSummary,
                shareFinalization,
                bundle,
                deliveryFinalization,
                latestReceipt
        );
        List<String> evidenceNotes = evidenceNotes(
                acceptanceSummary,
                shareFinalization,
                bundle,
                deliveryFinalization
        );
        List<String> downloadActions = downloadActions(latestArchive, latestReceipt);
        Instant generatedAt = Instant.now(clock);

        return new DemoFinalAcceptanceCompletionCloseoutVo(
                status,
                closed,
                summary,
                nextAction,
                firstText(acceptanceSummary.latestTaskId(), bundle.latestTaskId(), deliveryFinalization.latestTaskId()),
                acceptanceSummary.latestPullRequestUrl(),
                firstText(bundle.latestSharePackageArchiveId(), shareFinalization.latestArchiveId()),
                firstText(bundle.latestCompletionArchiveId(), latestArchive == null ? null : latestArchive.id()),
                firstText(
                        deliveryFinalization.latestCompletionEvidenceDeliveryReceiptId(),
                        latestReceipt == null ? null : latestReceipt.id()
                ),
                firstText(deliveryFinalization.latestDeliveryTarget(), bundle.latestDeliveryTarget()),
                firstText(deliveryFinalization.latestDeliveryChannel(), bundle.latestDeliveryChannel()),
                deliveryFinalization.latestDeliveredAt(),
                deliveryFinalization.deliveryReceiptFreshness(),
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        closed,
                        summary,
                        nextAction,
                        acceptanceSummary,
                        shareFinalization,
                        bundle,
                        deliveryFinalization,
                        latestArchive,
                        latestReceipt,
                        checks,
                        evidenceNotes,
                        downloadActions,
                        generatedAt
                ),
                generatedAt
        );
    }

    private static DemoReadinessStatus status(
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo deliveryFinalization,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt
    ) {
        if (!acceptanceSummary.accepted() || acceptanceSummary.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!shareFinalization.finalized() || shareFinalization.status() == DemoReadinessStatus.BLOCKED) {
            return shareFinalization.status() == DemoReadinessStatus.BLOCKED
                    ? DemoReadinessStatus.BLOCKED
                    : DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if (!bundle.readyToShare() || bundle.status() != DemoReadinessStatus.READY) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (deliveryFinalization.finalized()
                && deliveryFinalization.status() == DemoReadinessStatus.READY
                && latestReceipt != null) {
            return DemoReadinessStatus.READY;
        }
        return DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            DemoReadinessStatus status,
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.";
        }
        if (!acceptanceSummary.accepted() || acceptanceSummary.status() == DemoReadinessStatus.BLOCKED) {
            return "PatchPilot final acceptance completion closeout is blocked because final demo acceptance is not accepted.";
        }
        if (shareFinalization.status() == DemoReadinessStatus.BLOCKED) {
            return "PatchPilot final acceptance completion closeout is blocked because reviewer package finalization is blocked.";
        }
        if (!bundle.readyToShare() || bundle.status() != DemoReadinessStatus.READY) {
            return "PatchPilot final acceptance completion closeout is blocked because the completion evidence bundle is not ready.";
        }
        return "PatchPilot final acceptance completion closeout needs a fresh completion evidence delivery finalization.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo deliveryFinalization
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Use this closeout report as the final external-review completion record.";
        }
        if (!acceptanceSummary.accepted() || acceptanceSummary.status() == DemoReadinessStatus.BLOCKED) {
            return acceptanceSummary.nextAction();
        }
        if (shareFinalization.status() == DemoReadinessStatus.BLOCKED || !shareFinalization.finalized()) {
            return shareFinalization.nextAction();
        }
        if (!bundle.readyToShare() || bundle.status() != DemoReadinessStatus.READY) {
            return bundle.nextAction();
        }
        return deliveryFinalization.nextAction();
    }

    private static List<DemoFinalAcceptanceCompletionCloseoutVo.Check> checks(
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo deliveryFinalization,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt
    ) {
        DemoReadinessStatus acceptanceStatus = acceptanceSummary.accepted()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.BLOCKED;
        DemoReadinessStatus shareStatus = shareFinalization.finalized()
                ? DemoReadinessStatus.READY
                : shareFinalization.status();
        DemoReadinessStatus bundleStatus = bundle.readyToShare() && bundle.status() == DemoReadinessStatus.READY
                ? DemoReadinessStatus.READY
                : bundle.status();
        DemoReadinessStatus deliveryStatus = deliveryFinalization.finalized() && latestReceipt != null
                ? DemoReadinessStatus.READY
                : deliveryFinalization.status();
        return List.of(
                new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Final acceptance summary",
                        acceptanceStatus,
                        acceptanceSummary.accepted()
                                ? "Final demo acceptance summary is accepted."
                                : "Final demo acceptance summary is not accepted.",
                        acceptanceSummary.accepted() ? "No action needed." : acceptanceSummary.nextAction()
                ),
                new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Reviewer package finalization",
                        shareStatus,
                        shareFinalization.finalized()
                                ? "Final acceptance share package is finalized."
                                : "Final acceptance share package is not finalized.",
                        shareFinalization.finalized() ? "No action needed." : shareFinalization.nextAction()
                ),
                new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Completion evidence bundle",
                        bundleStatus,
                        bundle.readyToShare()
                                ? "Completion evidence bundle " + valueOrNone(bundle.latestCompletionArchiveId())
                                + " is ready to share."
                                : "Completion evidence bundle is not ready to share.",
                        bundle.readyToShare() ? "No action needed." : bundle.nextAction()
                ),
                new DemoFinalAcceptanceCompletionCloseoutVo.Check(
                        "Completion evidence delivery finalization",
                        deliveryStatus,
                        deliveryFinalization.finalized() && latestReceipt != null
                                ? "Completion evidence delivery finalization is fresh."
                                : "Completion evidence delivery finalization is not finalized.",
                        deliveryFinalization.finalized() && latestReceipt != null
                                ? "No action needed."
                                : deliveryFinalization.nextAction()
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo deliveryFinalization
    ) {
        List<String> notes = new ArrayList<>();
        notes.add(acceptanceSummary.accepted()
                ? "Final demo acceptance summary is accepted."
                : "Final demo acceptance summary is not accepted.");
        notes.add(shareFinalization.finalized()
                ? "Final acceptance share package is finalized."
                : "Final acceptance share package is not finalized.");
        notes.add(bundle.readyToShare()
                ? "Completion evidence bundle " + valueOrNone(bundle.latestCompletionArchiveId()) + " is ready to share."
                : "Completion evidence bundle is not ready to share.");
        notes.add(deliveryFinalization.finalized()
                ? "Completion evidence delivery finalization is fresh."
                : "Completion evidence delivery finalization is not finalized.");
        notes.addAll(acceptanceSummary.evidenceNotes());
        notes.addAll(shareFinalization.evidenceNotes());
        notes.addAll(bundle.evidenceNotes());
        notes.addAll(deliveryFinalization.evidenceNotes());
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalAcceptanceCompletionArchiveVo latestArchive,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final demo acceptance summary report.");
        actions.add("Download final acceptance share finalization report.");
        actions.add("Download final acceptance completion evidence bundle.");
        actions.add("Download final acceptance completion delivery finalization report.");
        if (latestArchive != null) {
            actions.add("Download final acceptance completion archive " + latestArchive.id() + ".");
        }
        if (latestReceipt != null) {
            actions.add("Download final acceptance completion evidence delivery receipt " + latestReceipt.id() + ".");
        }
        actions.add("Download final acceptance completion closeout report.");
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean closed,
            String summary,
            String nextAction,
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo bundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo deliveryFinalization,
            DemoFinalAcceptanceCompletionArchiveVo latestArchive,
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo latestReceipt,
            List<DemoFinalAcceptanceCompletionCloseoutVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final Acceptance Completion Closeout\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Closed: `").append(closed).append("`\n");
        report.append("- Summary: ").append(summary).append('\n');
        report.append("- Next action: ").append(nextAction).append('\n');
        report.append("- Latest task: `")
                .append(valueOrNone(firstText(acceptanceSummary.latestTaskId(), bundle.latestTaskId())))
                .append("`\n");
        report.append("- Latest Pull Request: `")
                .append(valueOrNone(acceptanceSummary.latestPullRequestUrl()))
                .append("`\n");
        report.append("- Latest share package archive: `")
                .append(valueOrNone(firstText(bundle.latestSharePackageArchiveId(), shareFinalization.latestArchiveId())))
                .append("`\n");
        report.append("- Latest completion archive: `")
                .append(valueOrNone(firstText(
                        bundle.latestCompletionArchiveId(),
                        latestArchive == null ? null : latestArchive.id()
                )))
                .append("`\n");
        report.append("- Latest completion evidence delivery receipt: `")
                .append(valueOrNone(firstText(
                        deliveryFinalization.latestCompletionEvidenceDeliveryReceiptId(),
                        latestReceipt == null ? null : latestReceipt.id()
                )))
                .append("`\n");
        report.append("- Delivery receipt freshness: `")
                .append(valueOrNone(deliveryFinalization.deliveryReceiptFreshness()))
                .append("`\n");
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendChecks(report, checks);
        appendList(report, "Evidence Notes", evidenceNotes);
        appendList(report, "Download Actions", downloadActions);
        report.append("## Side Effect Contract\n\n");
        report.append(SIDE_EFFECT_CONTRACT).append('\n');
        return report.toString();
    }

    private static void appendChecks(StringBuilder report, List<DemoFinalAcceptanceCompletionCloseoutVo.Check> checks) {
        report.append("## Checks\n\n");
        for (DemoFinalAcceptanceCompletionCloseoutVo.Check check : checks) {
            report.append("- ").append(check.name()).append(": `")
                    .append(check.status()).append("` - ")
                    .append(check.summary()).append(" Next: ")
                    .append(check.nextAction()).append('\n');
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

    private static String firstText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value : "none";
    }
}
