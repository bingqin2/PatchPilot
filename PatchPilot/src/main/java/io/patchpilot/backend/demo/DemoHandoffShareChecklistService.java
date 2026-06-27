package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistItemVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class DemoHandoffShareChecklistService {

    private final DemoHandoffPackageArchiveSummaryService summaryService;
    private final Clock clock;

    @Autowired
    public DemoHandoffShareChecklistService(DemoHandoffPackageArchiveSummaryService summaryService) {
        this(summaryService, Clock.systemUTC());
    }

    DemoHandoffShareChecklistService(DemoHandoffPackageArchiveSummaryService summaryService, Clock clock) {
        this.summaryService = summaryService;
        this.clock = clock;
    }

    public DemoHandoffShareChecklistVo getShareChecklist() {
        DemoHandoffPackageArchiveSummaryVo summary = summaryService.getArchiveSummary();
        List<DemoHandoffShareChecklistItemVo> checks = List.of(
                archiveCheck(summary),
                readinessCheck(summary),
                shareReadyCheck(summary),
                evidenceCheck(summary)
        );
        DemoReadinessStatus status = overallStatus(checks);
        Instant generatedAt = Instant.now(clock);
        String checklistSummary = checklistSummary(summary, status);
        String nextAction = checklistNextAction(summary, status);
        return new DemoHandoffShareChecklistVo(
                status,
                checklistSummary,
                nextAction,
                checks,
                formatMarkdown(status, checklistSummary, nextAction, checks, generatedAt),
                generatedAt
        );
    }

    private static DemoHandoffShareChecklistItemVo archiveCheck(DemoHandoffPackageArchiveSummaryVo summary) {
        if (summary.archiveCount() > 0) {
            return new DemoHandoffShareChecklistItemVo(
                    "Handoff package archive",
                    DemoReadinessStatus.READY,
                    summary.archiveCount() + " archived handoff package is available.",
                    "Use archive " + summary.latestArchiveId() + " as the latest package."
            );
        }
        return new DemoHandoffShareChecklistItemVo(
                "Handoff package archive",
                DemoReadinessStatus.NEEDS_ATTENTION,
                "No archived handoff package is available.",
                summary.nextAction()
        );
    }

    private static DemoHandoffShareChecklistItemVo readinessCheck(DemoHandoffPackageArchiveSummaryVo summary) {
        DemoReadinessStatus status = summary.latestHandoffReadinessStatus();
        if (status == null) {
            return new DemoHandoffShareChecklistItemVo(
                    "Latest handoff readiness",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No latest handoff readiness result is available.",
                    summary.nextAction()
            );
        }
        return new DemoHandoffShareChecklistItemVo(
                "Latest handoff readiness",
                status,
                "Latest archived package handoff readiness is " + status.name() + ".",
                summary.nextAction()
        );
    }

    private static DemoHandoffShareChecklistItemVo shareReadyCheck(DemoHandoffPackageArchiveSummaryVo summary) {
        if (summary.shareReady()) {
            return new DemoHandoffShareChecklistItemVo(
                    "Share-ready summary",
                    DemoReadinessStatus.READY,
                    summary.summary(),
                    "Share the latest archive summary with the reviewer."
            );
        }
        return new DemoHandoffShareChecklistItemVo(
                "Share-ready summary",
                summary.latestHandoffReadinessStatus() == DemoReadinessStatus.BLOCKED
                        ? DemoReadinessStatus.BLOCKED
                        : DemoReadinessStatus.NEEDS_ATTENTION,
                summary.summary(),
                summary.nextAction()
        );
    }

    private static DemoHandoffShareChecklistItemVo evidenceCheck(DemoHandoffPackageArchiveSummaryVo summary) {
        boolean hasPortableEvidence = hasText(summary.latestArchiveId()) && hasText(summary.markdownReport());
        if (hasPortableEvidence) {
            return new DemoHandoffShareChecklistItemVo(
                    "Portable evidence",
                    DemoReadinessStatus.READY,
                    "Markdown evidence is available for the latest handoff package.",
                    "Copy this checklist or download /api/demo/handoff-package-archives/summary-report/download."
            );
        }
        return new DemoHandoffShareChecklistItemVo(
                "Portable evidence",
                DemoReadinessStatus.NEEDS_ATTENTION,
                "No portable archived handoff evidence is available yet.",
                summary.nextAction()
        );
    }

    private static DemoReadinessStatus overallStatus(List<DemoHandoffShareChecklistItemVo> checks) {
        if (checks.stream().anyMatch(check -> check.status() == DemoReadinessStatus.BLOCKED)) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (checks.stream().anyMatch(check -> check.status() == DemoReadinessStatus.NEEDS_ATTENTION)) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static String checklistSummary(DemoHandoffPackageArchiveSummaryVo summary, DemoReadinessStatus status) {
        if (summary.archiveCount() == 0) {
            return "No handoff package archive is available for sharing.";
        }
        if (status == DemoReadinessStatus.READY) {
            return "Latest handoff archive is ready to share.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Latest handoff archive is blocked from sharing.";
        }
        return "Latest handoff archive needs attention before sharing.";
    }

    private static String checklistNextAction(DemoHandoffPackageArchiveSummaryVo summary, DemoReadinessStatus status) {
        if (status == DemoReadinessStatus.READY) {
            return "Share the latest handoff package summary and archived package with the reviewer.";
        }
        return summary.nextAction();
    }

    private static String formatMarkdown(
            DemoReadinessStatus status,
            String summary,
            String nextAction,
            List<DemoHandoffShareChecklistItemVo> checks,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Handoff Share Checklist\n\n");
        builder.append("- Status: `").append(status.name()).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        builder.append("## Checks\n\n");
        for (DemoHandoffShareChecklistItemVo check : checks) {
            builder.append("- ")
                    .append(check.name())
                    .append(": `")
                    .append(check.status().name())
                    .append("` - ")
                    .append(check.summary())
                    .append(" Next action: ")
                    .append(check.nextAction())
                    .append('\n');
        }
        builder.append("\n## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/handoff-share-checklist is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.\n");
        return builder.toString();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
