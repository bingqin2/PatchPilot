package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewEvidencePackageService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-external-review-evidence-package is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<DemoAcceptanceSummaryVo> acceptanceSummarySupplier;
    private final Supplier<DemoFinalAcceptanceShareFinalizationVo> shareFinalizationSupplier;
    private final Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> completionEvidenceBundleSupplier;
    private final Supplier<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo> completionDeliveryFinalizationSupplier;
    private final Supplier<DemoFinalAcceptanceCompletionCloseoutVo> completionCloseoutSupplier;
    private final Supplier<List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>> closeoutArchiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalExternalReviewEvidencePackageService(
            DemoAcceptanceSummaryService acceptanceSummaryService,
            DemoFinalAcceptanceShareFinalizationService shareFinalizationService,
            DemoFinalAcceptanceCompletionEvidenceBundleService completionEvidenceBundleService,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService completionDeliveryFinalizationService,
            DemoFinalAcceptanceCompletionCloseoutService completionCloseoutService,
            DemoFinalAcceptanceCompletionCloseoutArchiveRepository completionCloseoutArchiveRepository
    ) {
        this(
                acceptanceSummaryService::getSummary,
                shareFinalizationService::getFinalizationGate,
                completionEvidenceBundleService::getBundle,
                completionDeliveryFinalizationService::getFinalizationGate,
                completionCloseoutService::getCloseout,
                () -> completionCloseoutArchiveRepository.listRecentArchives(20),
                Clock.systemUTC()
        );
    }

    DemoFinalExternalReviewEvidencePackageService(
            Supplier<DemoAcceptanceSummaryVo> acceptanceSummarySupplier,
            Supplier<DemoFinalAcceptanceShareFinalizationVo> shareFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionEvidenceBundleVo> completionEvidenceBundleSupplier,
            Supplier<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo> completionDeliveryFinalizationSupplier,
            Supplier<DemoFinalAcceptanceCompletionCloseoutVo> completionCloseoutSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionCloseoutArchiveVo>> closeoutArchiveSupplier,
            Clock clock
    ) {
        this.acceptanceSummarySupplier = acceptanceSummarySupplier;
        this.shareFinalizationSupplier = shareFinalizationSupplier;
        this.completionEvidenceBundleSupplier = completionEvidenceBundleSupplier;
        this.completionDeliveryFinalizationSupplier = completionDeliveryFinalizationSupplier;
        this.completionCloseoutSupplier = completionCloseoutSupplier;
        this.closeoutArchiveSupplier = closeoutArchiveSupplier;
        this.clock = clock;
    }

    public DemoFinalExternalReviewEvidencePackageVo getPackage() {
        DemoAcceptanceSummaryVo acceptanceSummary = acceptanceSummarySupplier.get();
        DemoFinalAcceptanceShareFinalizationVo shareFinalization = shareFinalizationSupplier.get();
        DemoFinalAcceptanceCompletionEvidenceBundleVo completionEvidenceBundle = completionEvidenceBundleSupplier.get();
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo completionDeliveryFinalization =
                completionDeliveryFinalizationSupplier.get();
        DemoFinalAcceptanceCompletionCloseoutVo completionCloseout = completionCloseoutSupplier.get();
        List<DemoFinalAcceptanceCompletionCloseoutArchiveVo> closeoutArchives = closeoutArchiveSupplier.get();
        closeoutArchives = closeoutArchives == null ? List.of() : closeoutArchives;
        DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive =
                closeoutArchives.isEmpty() ? null : closeoutArchives.get(0);
        DemoReadinessStatus closeoutArchiveStatus = closeoutArchiveStatus(closeoutArchive);
        DemoReadinessStatus status = status(
                acceptanceSummary,
                shareFinalization,
                completionEvidenceBundle,
                completionDeliveryFinalization,
                completionCloseout,
                closeoutArchiveStatus
        );
        boolean readyForExternalReview = status == DemoReadinessStatus.READY;
        String summary = summary(status, acceptanceSummary, closeoutArchive);
        String nextAction = nextAction(
                status,
                acceptanceSummary,
                shareFinalization,
                completionEvidenceBundle,
                completionDeliveryFinalization,
                completionCloseout,
                closeoutArchive
        );
        List<DemoFinalExternalReviewEvidencePackageVo.Check> checks = checks(
                acceptanceSummary,
                shareFinalization,
                completionEvidenceBundle,
                completionDeliveryFinalization,
                completionCloseout,
                closeoutArchive,
                closeoutArchiveStatus
        );
        List<String> evidenceNotes = evidenceNotes(
                acceptanceSummary,
                shareFinalization,
                completionEvidenceBundle,
                completionDeliveryFinalization,
                completionCloseout,
                closeoutArchive
        );
        List<String> downloadActions = downloadActions(closeoutArchive);
        Instant generatedAt = Instant.now(clock);

        return new DemoFinalExternalReviewEvidencePackageVo(
                status,
                readyForExternalReview,
                summary,
                nextAction,
                acceptanceSummary.status(),
                shareFinalization.status(),
                completionEvidenceBundle.status(),
                completionDeliveryFinalization.status(),
                completionCloseout.status(),
                closeoutArchiveStatus,
                firstText(completionCloseout.latestTaskId(), completionEvidenceBundle.latestTaskId(), acceptanceSummary.latestTaskId()),
                firstText(completionCloseout.latestPullRequestUrl(), acceptanceSummary.latestPullRequestUrl()),
                firstText(completionCloseout.latestSharePackageArchiveId(), completionEvidenceBundle.latestSharePackageArchiveId()),
                firstText(completionCloseout.latestCompletionArchiveId(), completionEvidenceBundle.latestCompletionArchiveId()),
                firstText(
                        completionCloseout.latestCompletionEvidenceDeliveryReceiptId(),
                        completionDeliveryFinalization.latestCompletionEvidenceDeliveryReceiptId()
                ),
                closeoutArchive == null ? null : closeoutArchive.id(),
                firstText(completionCloseout.latestDeliveryTarget(), completionDeliveryFinalization.latestDeliveryTarget()),
                firstText(completionCloseout.latestDeliveryChannel(), completionDeliveryFinalization.latestDeliveryChannel()),
                firstText(completionCloseout.latestDeliveredAt(), completionDeliveryFinalization.latestDeliveredAt()),
                firstText(completionCloseout.deliveryReceiptFreshness(), completionDeliveryFinalization.deliveryReceiptFreshness()),
                closeoutArchive == null ? null : closeoutArchive.archivedAt(),
                generatedAt,
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        readyForExternalReview,
                        summary,
                        nextAction,
                        acceptanceSummary,
                        shareFinalization,
                        completionEvidenceBundle,
                        completionDeliveryFinalization,
                        completionCloseout,
                        closeoutArchive,
                        closeoutArchiveStatus,
                        checks,
                        evidenceNotes,
                        downloadActions,
                        generatedAt
                )
        );
    }

    private static DemoReadinessStatus status(
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo completionEvidenceBundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo completionDeliveryFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo completionCloseout,
            DemoReadinessStatus closeoutArchiveStatus
    ) {
        if (!acceptanceSummary.accepted() || acceptanceSummary.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!shareFinalization.finalized() || shareFinalization.status() == DemoReadinessStatus.BLOCKED) {
            return shareFinalization.status() == DemoReadinessStatus.BLOCKED
                    ? DemoReadinessStatus.BLOCKED
                    : DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if (!completionEvidenceBundle.readyToShare()
                || completionEvidenceBundle.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!completionDeliveryFinalization.finalized()
                || completionDeliveryFinalization.status() == DemoReadinessStatus.BLOCKED) {
            return completionDeliveryFinalization.status() == DemoReadinessStatus.BLOCKED
                    ? DemoReadinessStatus.BLOCKED
                    : DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if (!completionCloseout.closed() || completionCloseout.status() == DemoReadinessStatus.BLOCKED) {
            return completionCloseout.status() == DemoReadinessStatus.BLOCKED
                    ? DemoReadinessStatus.BLOCKED
                    : DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return closeoutArchiveStatus;
    }

    private static DemoReadinessStatus closeoutArchiveStatus(DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive) {
        if (closeoutArchive == null) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        if (closeoutArchive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return closeoutArchive.status() == DemoReadinessStatus.READY && closeoutArchive.closed()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            DemoReadinessStatus status,
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "PatchPilot final external-review evidence package is ready.";
        }
        if (!acceptanceSummary.accepted() || acceptanceSummary.status() == DemoReadinessStatus.BLOCKED) {
            return "PatchPilot final external-review evidence package is blocked by final demo acceptance.";
        }
        if (closeoutArchive == null) {
            return "PatchPilot final external-review evidence package is waiting for a frozen closeout archive.";
        }
        return "PatchPilot final external-review evidence package is waiting for a READY closed closeout archive.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo completionEvidenceBundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo completionDeliveryFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo completionCloseout,
            DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Share this package with reviewers as the frozen external-review record.";
        }
        if (!acceptanceSummary.accepted() || acceptanceSummary.status() == DemoReadinessStatus.BLOCKED) {
            return acceptanceSummary.nextAction();
        }
        if (!shareFinalization.finalized() || shareFinalization.status() != DemoReadinessStatus.READY) {
            return shareFinalization.nextAction();
        }
        if (!completionEvidenceBundle.readyToShare()
                || completionEvidenceBundle.status() != DemoReadinessStatus.READY) {
            return completionEvidenceBundle.nextAction();
        }
        if (!completionDeliveryFinalization.finalized()
                || completionDeliveryFinalization.status() != DemoReadinessStatus.READY) {
            return completionDeliveryFinalization.nextAction();
        }
        if (!completionCloseout.closed() || completionCloseout.status() != DemoReadinessStatus.READY) {
            return completionCloseout.nextAction();
        }
        if (closeoutArchive == null) {
            return "Archive the READY final acceptance completion closeout before sharing the final external-review package.";
        }
        return "Archive a READY and closed final acceptance completion closeout before sharing external-review evidence.";
    }

    private static List<DemoFinalExternalReviewEvidencePackageVo.Check> checks(
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo completionEvidenceBundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo completionDeliveryFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo completionCloseout,
            DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive,
            DemoReadinessStatus closeoutArchiveStatus
    ) {
        List<DemoFinalExternalReviewEvidencePackageVo.Check> checks = new ArrayList<>();
        checks.add(new DemoFinalExternalReviewEvidencePackageVo.Check(
                "Final demo acceptance summary",
                acceptanceSummary.status(),
                acceptanceSummary.summary(),
                acceptanceSummary.nextAction()
        ));
        checks.add(new DemoFinalExternalReviewEvidencePackageVo.Check(
                "Reviewer package finalization",
                shareFinalization.status(),
                shareFinalization.summary(),
                shareFinalization.nextAction()
        ));
        checks.add(new DemoFinalExternalReviewEvidencePackageVo.Check(
                "Completion evidence bundle",
                completionEvidenceBundle.status(),
                completionEvidenceBundle.summary(),
                completionEvidenceBundle.nextAction()
        ));
        checks.add(new DemoFinalExternalReviewEvidencePackageVo.Check(
                "Completion evidence delivery finalization",
                completionDeliveryFinalization.status(),
                completionDeliveryFinalization.summary(),
                completionDeliveryFinalization.nextAction()
        ));
        checks.add(new DemoFinalExternalReviewEvidencePackageVo.Check(
                "Completion closeout",
                completionCloseout.status(),
                completionCloseout.summary(),
                completionCloseout.nextAction()
        ));
        checks.add(new DemoFinalExternalReviewEvidencePackageVo.Check(
                "Frozen closeout archive",
                closeoutArchiveStatus,
                closeoutArchiveSummary(closeoutArchive),
                closeoutArchive == null
                        ? "Archive the READY final acceptance completion closeout."
                        : closeoutArchive.nextAction()
        ));
        return List.copyOf(checks);
    }

    private static String closeoutArchiveSummary(DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive) {
        if (closeoutArchive == null) {
            return "No frozen final acceptance completion closeout archive is available.";
        }
        if (closeoutArchive.closed()) {
            return "Frozen closeout archive " + closeoutArchive.id() + " is closed.";
        }
        return "Frozen closeout archive " + closeoutArchive.id() + " is not closed.";
    }

    private static List<String> evidenceNotes(
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo completionEvidenceBundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo completionDeliveryFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo completionCloseout,
            DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive
    ) {
        List<String> notes = new ArrayList<>();
        notes.add(acceptanceSummary.accepted()
                ? "Final demo acceptance summary is accepted."
                : "Final demo acceptance summary is not accepted.");
        notes.add("Reviewer package finalization is " + shareFinalization.status() + ".");
        if (hasText(completionEvidenceBundle.latestCompletionArchiveId())) {
            notes.add("Completion evidence bundle "
                    + completionEvidenceBundle.latestCompletionArchiveId()
                    + " is ready to share.");
        } else {
            notes.add("Completion evidence bundle has no completion archive.");
        }
        notes.add("Completion delivery finalization is " + completionDeliveryFinalization.status() + ".");
        notes.add(completionCloseout.closed()
                ? "Completion closeout is closed."
                : "Completion closeout is not closed.");
        if (closeoutArchive == null) {
            notes.add("No frozen final acceptance completion closeout archive is available.");
        } else if (closeoutArchive.closed()
                && closeoutArchive.status() == DemoReadinessStatus.READY) {
            notes.add("Frozen closeout archive " + closeoutArchive.id() + " is READY and closed.");
        } else {
            notes.add("Frozen closeout archive " + closeoutArchive.id() + " is not closed.");
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final external-review evidence package.");
        actions.add("Download final demo acceptance summary report.");
        actions.add("Download final acceptance share finalization report.");
        actions.add("Download final acceptance completion evidence bundle.");
        actions.add("Download final acceptance completion delivery finalization report.");
        actions.add("Download final acceptance completion closeout report.");
        if (closeoutArchive != null) {
            actions.add("Download final acceptance completion closeout archive " + closeoutArchive.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean readyForExternalReview,
            String summary,
            String nextAction,
            DemoAcceptanceSummaryVo acceptanceSummary,
            DemoFinalAcceptanceShareFinalizationVo shareFinalization,
            DemoFinalAcceptanceCompletionEvidenceBundleVo completionEvidenceBundle,
            DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo completionDeliveryFinalization,
            DemoFinalAcceptanceCompletionCloseoutVo completionCloseout,
            DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive,
            DemoReadinessStatus closeoutArchiveStatus,
            List<DemoFinalExternalReviewEvidencePackageVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final External Review Evidence Package\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Ready for external review: `").append(readyForExternalReview).append("`\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Next action: ").append(nextAction).append("\n");
        report.append("- Task: `").append(nullToNone(completionCloseout.latestTaskId())).append("`\n");
        report.append("- Pull Request: `").append(nullToNone(completionCloseout.latestPullRequestUrl())).append("`\n");
        report.append("- Share package archive: `")
                .append(nullToNone(shareFinalization.latestArchiveId()))
                .append("`\n");
        report.append("- Completion archive: `")
                .append(nullToNone(completionEvidenceBundle.latestCompletionArchiveId()))
                .append("`\n");
        report.append("- Completion delivery receipt: `")
                .append(nullToNone(completionDeliveryFinalization.latestCompletionEvidenceDeliveryReceiptId()))
                .append("`\n");
        report.append("- Closeout archive: `")
                .append(closeoutArchive == null ? "none" : closeoutArchive.id())
                .append("`\n");
        report.append("- Closeout archive status: `").append(closeoutArchiveStatus).append("`\n");
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
            List<DemoFinalExternalReviewEvidencePackageVo.Check> checks
    ) {
        report.append("## Checks\n\n");
        for (DemoFinalExternalReviewEvidencePackageVo.Check check : checks) {
            report.append("- `").append(check.status()).append("` ")
                    .append(check.name()).append(": ")
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

    private static String nullToNone(String value) {
        return hasText(value) ? value : "none";
    }
}
