package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoHandoffShareCenterService {

    private final DemoHandoffPackageArchiveSummaryService archiveSummaryService;
    private final DemoHandoffShareChecklistService shareChecklistService;
    private final Clock clock;

    @Autowired
    public DemoHandoffShareCenterService(
            DemoHandoffPackageArchiveSummaryService archiveSummaryService,
            DemoHandoffShareChecklistService shareChecklistService
    ) {
        this(archiveSummaryService, shareChecklistService, Clock.systemUTC());
    }

    DemoHandoffShareCenterService(
            DemoHandoffPackageArchiveSummaryService archiveSummaryService,
            DemoHandoffShareChecklistService shareChecklistService,
            Clock clock
    ) {
        this.archiveSummaryService = archiveSummaryService;
        this.shareChecklistService = shareChecklistService;
        this.clock = clock;
    }

    public DemoHandoffShareCenterVo getShareCenter() {
        DemoHandoffPackageArchiveSummaryVo archiveSummary = archiveSummaryService.getArchiveSummary();
        DemoHandoffShareChecklistVo checklist = shareChecklistService.getShareChecklist();
        DemoReadinessStatus status = centerStatus(archiveSummary, checklist);
        boolean shareReady = archiveSummary.shareReady() && checklist.status() == DemoReadinessStatus.READY;
        Instant generatedAt = Instant.now(clock);
        List<String> downloadActions = downloadActions(archiveSummary, checklist);
        List<String> evidenceNotes = evidenceNotes(archiveSummary, checklist);
        String summary = centerSummary(archiveSummary, checklist, shareReady, status);
        String nextAction = centerNextAction(archiveSummary, checklist, shareReady);
        String latestCreatedAt = archiveSummary.latestCreatedAt() == null ? null : archiveSummary.latestCreatedAt().toString();
        String markdownReport = formatMarkdown(
                status,
                shareReady,
                summary,
                nextAction,
                archiveSummary,
                checklist,
                downloadActions,
                evidenceNotes,
                generatedAt
        );
        return new DemoHandoffShareCenterVo(
                status,
                shareReady,
                summary,
                nextAction,
                archiveSummary.latestArchiveId(),
                archiveSummary.latestSessionId(),
                latestCreatedAt,
                downloadActions,
                evidenceNotes,
                markdownReport,
                generatedAt
        );
    }

    private static DemoReadinessStatus centerStatus(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist
    ) {
        if (archiveSummary.latestHandoffReadinessStatus() == DemoReadinessStatus.BLOCKED
                || checklist.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!archiveSummary.shareReady() || checklist.status() == DemoReadinessStatus.NEEDS_ATTENTION) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static List<String> downloadActions(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist
    ) {
        List<String> actions = new ArrayList<>();
        if (hasText(archiveSummary.latestArchiveId())) {
            actions.add("Download handoff package archive " + archiveSummary.latestArchiveId() + ".");
        } else {
            actions.add("Archive a demo handoff package before downloading final handoff evidence.");
        }
        actions.add("Download handoff package archive summary.");
        actions.add("Download handoff share checklist.");
        if (checklist.status() != DemoReadinessStatus.READY) {
            actions.add("Resolve checklist warnings before sending the package.");
        }
        return List.copyOf(actions);
    }

    private static List<String> evidenceNotes(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Latest package archive status is " + archiveSummary.status() + ".");
        notes.add("Share checklist has " + checklist.checks().size() + " checks.");
        notes.add("Archive summary: " + archiveSummary.summary());
        notes.add("Checklist summary: " + checklist.summary());
        return List.copyOf(notes);
    }

    private static String centerSummary(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            boolean shareReady,
            DemoReadinessStatus status
    ) {
        if (shareReady) {
            return "Post-demo handoff package is ready to share.";
        }
        if (archiveSummary.archiveCount() == 0) {
            return "No archived handoff package is available for sharing.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Post-demo handoff package is blocked from sharing.";
        }
        if (checklist.status() != DemoReadinessStatus.READY) {
            return checklist.summary();
        }
        return archiveSummary.summary();
    }

    private static String centerNextAction(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            boolean shareReady
    ) {
        if (shareReady) {
            return "Download the package, archive summary, and share checklist before sending handoff evidence.";
        }
        if (checklist.status() != DemoReadinessStatus.READY) {
            return checklist.nextAction();
        }
        return archiveSummary.nextAction();
    }

    private static String formatMarkdown(
            DemoReadinessStatus status,
            boolean shareReady,
            String summary,
            String nextAction,
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            List<String> downloadActions,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Handoff Share Center\n\n");
        builder.append("- Status: `").append(status.name()).append("`\n");
        builder.append("- Share ready: `").append(shareReady).append("`\n");
        builder.append("- Latest archive: `").append(valueOrNone(archiveSummary.latestArchiveId())).append("`\n");
        builder.append("- Latest session: `").append(valueOrNone(archiveSummary.latestSessionId())).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        builder.append("## Downloads\n\n");
        for (String action : downloadActions) {
            builder.append("- ").append(action).append('\n');
        }
        builder.append("\n## Evidence\n\n");
        for (String note : evidenceNotes) {
            builder.append("- ").append(note).append('\n');
        }
        builder.append("\n## Embedded Archive Summary\n\n");
        builder.append(archiveSummary.markdownReport()).append('\n');
        builder.append("\n## Embedded Share Checklist\n\n");
        builder.append(checklist.markdownReport()).append('\n');
        builder.append("\n## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/handoff-share-center is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub.\n");
        return builder.toString();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value : "none";
    }
}
