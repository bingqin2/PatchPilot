package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoLaunchEvidenceShareCenterService {

    private final DemoLaunchEvidencePackageArchiveService archiveService;
    private final Clock clock;

    @Autowired
    public DemoLaunchEvidenceShareCenterService(DemoLaunchEvidencePackageArchiveService archiveService) {
        this(archiveService, Clock.systemUTC());
    }

    DemoLaunchEvidenceShareCenterService(
            DemoLaunchEvidencePackageArchiveService archiveService,
            Clock clock
    ) {
        this.archiveService = archiveService;
        this.clock = clock;
    }

    public DemoLaunchEvidenceShareCenterVo getShareCenter() {
        List<DemoLaunchEvidencePackageArchiveVo> archives = archiveService.listRecentArchives();
        Instant generatedAt = Instant.now(clock);
        if (archives.isEmpty()) {
            String summary = "No archived launch evidence package is available for sharing.";
            String nextAction = "Archive a final demo launch evidence package after a completed live run before sharing launch evidence.";
            List<String> downloadActions = List.of(
                    "Archive a demo launch evidence package before downloading final launch evidence.",
                    "Download the current launch evidence package report for review."
            );
            List<String> evidenceNotes = List.of("No launch evidence archive has been captured yet.");
            return new DemoLaunchEvidenceShareCenterVo(
                    "NO_ARCHIVE",
                    false,
                    summary,
                    nextAction,
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    downloadActions,
                    evidenceNotes,
                    formatMarkdown(
                            "NO_ARCHIVE",
                            false,
                            summary,
                            nextAction,
                            0,
                            null,
                            downloadActions,
                            evidenceNotes,
                            generatedAt
                    ),
                    generatedAt
            );
        }

        DemoLaunchEvidencePackageArchiveVo latest = archives.get(0);
        boolean shareReady = latest.readyToShare() && latest.status() == DemoReadinessStatus.READY;
        String status = latest.status().name();
        String summary = shareReady
                ? "Latest archived launch evidence package is READY and can be shared."
                : "Latest archived launch evidence package needs attention before it is shared.";
        String nextAction = shareReady
                ? "Download the archived launch evidence package and share it with reviewers."
                : "Resolve launch evidence blockers, regenerate the package, and archive a new READY package.";
        List<String> downloadActions = downloadActions(latest, shareReady);
        List<String> evidenceNotes = evidenceNotes(latest);
        return new DemoLaunchEvidenceShareCenterVo(
                status,
                shareReady,
                summary,
                nextAction,
                archives.size(),
                latest.id(),
                latest.sessionId(),
                latest.createdAt().toString(),
                latest.latestTaskId(),
                latest.latestPullRequestUrl(),
                latest.latestWebhookDeliveryId(),
                latest.evaluationRunId(),
                downloadActions,
                evidenceNotes,
                formatMarkdown(status, shareReady, summary, nextAction, archives.size(), latest, downloadActions, evidenceNotes, generatedAt),
                generatedAt
        );
    }

    private static List<String> downloadActions(DemoLaunchEvidencePackageArchiveVo latest, boolean shareReady) {
        List<String> actions = new ArrayList<>();
        actions.add("Download launch evidence package archive " + latest.id() + ".");
        actions.add("Download launch evidence share center report.");
        if (shareReady && hasText(latest.latestPullRequestUrl())) {
            actions.add("Open Pull Request " + latest.latestPullRequestUrl() + " for review.");
        }
        if (!shareReady) {
            actions.add("Resolve launch evidence blockers and archive a new package.");
        }
        return List.copyOf(actions);
    }

    private static List<String> evidenceNotes(DemoLaunchEvidencePackageArchiveVo latest) {
        List<String> notes = new ArrayList<>();
        notes.add("Latest launch evidence archive status is " + latest.status().name() + ".");
        notes.add("Latest archive session is " + valueOrNone(latest.sessionId()) + ".");
        if (hasText(latest.latestTaskId())) {
            notes.add("Latest task " + latest.latestTaskId() + " completed before archive.");
        }
        if (hasText(latest.latestPullRequestUrl())) {
            notes.add("Latest Pull Request " + latest.latestPullRequestUrl() + " is ready for review.");
        }
        if (hasText(latest.latestWebhookDeliveryId())) {
            notes.add("Latest webhook delivery evidence is " + latest.latestWebhookDeliveryId() + ".");
        }
        if (hasText(latest.evaluationRunId())) {
            notes.add("Latest evaluation run evidence is " + latest.evaluationRunId() + ".");
        }
        notes.add("Archive summary: " + latest.summary());
        return List.copyOf(notes);
    }

    private static String formatMarkdown(
            String status,
            boolean shareReady,
            String summary,
            String nextAction,
            int archiveCount,
            DemoLaunchEvidencePackageArchiveVo latest,
            List<String> downloadActions,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Launch Evidence Share Center\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Share ready: `").append(shareReady).append("`\n");
        builder.append("- Archive count: `").append(archiveCount).append("`\n");
        builder.append("- Latest archive: `").append(latest == null ? "none" : latest.id()).append("`\n");
        builder.append("- Latest session: `").append(latest == null ? "none" : valueOrNone(latest.sessionId())).append("`\n");
        builder.append("- Latest task: `").append(latest == null ? "none" : valueOrNone(latest.latestTaskId())).append("`\n");
        builder.append("- Latest Pull Request: `").append(latest == null ? "none" : valueOrNone(latest.latestPullRequestUrl())).append("`\n");
        builder.append("- Latest webhook delivery: `").append(latest == null ? "none" : valueOrNone(latest.latestWebhookDeliveryId())).append("`\n");
        builder.append("- Evaluation run: `").append(latest == null ? "none" : valueOrNone(latest.evaluationRunId())).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        appendList(builder, "## Downloads", downloadActions);
        appendList(builder, "## Evidence", evidenceNotes);
        builder.append("## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/launch-evidence-share-center is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.\n");
        return builder.toString();
    }

    private static void appendList(StringBuilder builder, String title, List<String> items) {
        builder.append(title).append("\n\n");
        for (String item : items) {
            builder.append("- ").append(item).append('\n');
        }
        builder.append('\n');
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value : "none";
    }
}
