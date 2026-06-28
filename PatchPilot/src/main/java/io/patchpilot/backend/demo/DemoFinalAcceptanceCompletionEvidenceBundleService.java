package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
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
public class DemoFinalAcceptanceCompletionEvidenceBundleService {

    private static final int MAX_ARCHIVES = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-acceptance-completion-evidence-bundle is read-only: it does not create tasks, "
                    + "call the model, run tests, archive records, record receipts, mutate Git, send messages, "
                    + "or write to GitHub.";

    private final Supplier<DemoFinalAcceptanceShareFinalizationVo> finalizationSupplier;
    private final Supplier<List<DemoFinalAcceptanceCompletionArchiveVo>> completionArchiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalAcceptanceCompletionEvidenceBundleService(
            DemoFinalAcceptanceShareFinalizationService finalizationService,
            DemoFinalAcceptanceCompletionArchiveService completionArchiveService
    ) {
        this(
                finalizationService::getFinalizationGate,
                () -> completionArchiveService.listRecentArchives().stream().limit(MAX_ARCHIVES).toList(),
                Clock.systemUTC()
        );
    }

    DemoFinalAcceptanceCompletionEvidenceBundleService(
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalizationSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionArchiveVo>> completionArchiveSupplier
    ) {
        this(finalizationSupplier, completionArchiveSupplier, Clock.systemUTC());
    }

    DemoFinalAcceptanceCompletionEvidenceBundleService(
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalizationSupplier,
            Supplier<List<DemoFinalAcceptanceCompletionArchiveVo>> completionArchiveSupplier,
            Clock clock
    ) {
        this.finalizationSupplier = finalizationSupplier;
        this.completionArchiveSupplier = completionArchiveSupplier;
        this.clock = clock;
    }

    public DemoFinalAcceptanceCompletionEvidenceBundleVo getBundle() {
        DemoFinalAcceptanceShareFinalizationVo finalization = finalizationSupplier.get();
        List<DemoFinalAcceptanceCompletionArchiveVo> archives = completionArchiveSupplier.get();
        DemoFinalAcceptanceCompletionArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        DemoReadinessStatus status = status(finalization, latestArchive);
        boolean readyToShare = status == DemoReadinessStatus.READY;
        List<String> evidenceNotes = evidenceNotes(finalization, latestArchive);
        List<String> downloadActions = downloadActions(finalization, latestArchive);
        String summary = summary(status, latestArchive);
        String nextAction = nextAction(status);
        Instant generatedAt = Instant.now(clock);

        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                status,
                readyToShare,
                summary,
                nextAction,
                latestArchive == null ? null : latestArchive.id(),
                finalization.latestArchiveId(),
                finalization.latestDeliveryReceiptId(),
                finalization.latestDeliveryTarget(),
                finalization.latestDeliveryChannel(),
                finalization.latestTaskId(),
                archives.size(),
                latestArchive == null ? null : latestArchive.archivedAt(),
                generatedAt,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        readyToShare,
                        summary,
                        nextAction,
                        finalization,
                        latestArchive,
                        archives.size(),
                        generatedAt,
                        evidenceNotes,
                        downloadActions
                )
        );
    }

    private static DemoReadinessStatus status(
            DemoFinalAcceptanceShareFinalizationVo finalization,
            DemoFinalAcceptanceCompletionArchiveVo latestArchive
    ) {
        if (!finalization.finalized() || finalization.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (finalization.status() != DemoReadinessStatus.READY || latestArchive == null) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return latestArchive.finalized() && latestArchive.status() == DemoReadinessStatus.READY
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            DemoReadinessStatus status,
            DemoFinalAcceptanceCompletionArchiveVo latestArchive
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "PatchPilot final acceptance completion evidence bundle is ready to share.";
        }
        if (latestArchive == null && status == DemoReadinessStatus.NEEDS_ATTENTION) {
            return "Final acceptance completion evidence bundle is waiting for a completion archive.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final acceptance completion evidence bundle is blocked by finalization.";
        }
        return "Final acceptance completion evidence bundle needs attention.";
    }

    private static String nextAction(DemoReadinessStatus status) {
        if (status == DemoReadinessStatus.READY) {
            return "Share this final acceptance completion evidence bundle with reviewers.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Resolve final acceptance finalization blockers before sharing the completion evidence bundle.";
        }
        return "Archive the finalized final acceptance completion before sharing the evidence bundle.";
    }

    private static List<String> evidenceNotes(
            DemoFinalAcceptanceShareFinalizationVo finalization,
            DemoFinalAcceptanceCompletionArchiveVo latestArchive
    ) {
        List<String> notes = new ArrayList<>();
        if (latestArchive == null) {
            notes.add("No final acceptance completion archive is available.");
        } else if (latestArchive.finalized()) {
            notes.add("Latest completion archive " + latestArchive.id() + " is finalized.");
        } else {
            notes.add("Latest completion archive " + latestArchive.id() + " is not finalized.");
        }
        notes.add("Final acceptance share finalization is " + finalization.status() + ".");
        notes.addAll(finalization.evidenceNotes());
        if (latestArchive != null) {
            notes.addAll(latestArchive.evidenceNotes());
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalAcceptanceShareFinalizationVo finalization,
            DemoFinalAcceptanceCompletionArchiveVo latestArchive
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final acceptance completion evidence bundle.");
        if (latestArchive != null) {
            actions.add("Download final acceptance completion archive " + latestArchive.id() + ".");
        }
        actions.add("Download final acceptance share finalization report.");
        if (hasText(finalization.latestArchiveId())) {
            actions.add("Download final acceptance share package archive " + finalization.latestArchiveId() + ".");
        }
        if (hasText(finalization.latestDeliveryReceiptId())) {
            actions.add("Download final acceptance delivery receipt " + finalization.latestDeliveryReceiptId() + ".");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean readyToShare,
            String summary,
            String nextAction,
            DemoFinalAcceptanceShareFinalizationVo finalization,
            DemoFinalAcceptanceCompletionArchiveVo latestArchive,
            int archiveCount,
            Instant generatedAt,
            List<String> evidenceNotes,
            List<String> downloadActions
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Final Acceptance Completion Evidence Bundle\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Ready to share: `").append(readyToShare).append("`\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Next action: ").append(nextAction).append("\n");
        report.append("- Latest completion archive: `")
                .append(latestArchive == null ? "none" : latestArchive.id())
                .append("`\n");
        report.append("- Latest share package archive: `")
                .append(nullToNone(finalization.latestArchiveId()))
                .append("`\n");
        report.append("- Latest delivery receipt: `")
                .append(nullToNone(finalization.latestDeliveryReceiptId()))
                .append("`\n");
        report.append("- Latest delivery target: `")
                .append(nullToNone(finalization.latestDeliveryTarget()))
                .append("`\n");
        report.append("- Latest task: `").append(nullToNone(finalization.latestTaskId())).append("`\n");
        report.append("- Completion archives: `").append(archiveCount).append("`\n");
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendList(report, "Evidence Notes", evidenceNotes);
        appendList(report, "Download Actions", downloadActions);
        report.append("\n## Side Effect Contract\n\n");
        report.append(SIDE_EFFECT_CONTRACT).append('\n');
        return report.toString();
    }

    private static void appendList(StringBuilder report, String heading, List<String> items) {
        report.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            report.append("- ").append(item).append('\n');
        }
        report.append('\n');
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String nullToNone(String value) {
        return hasText(value) ? value : "none";
    }
}
