package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoLaunchEvidencePackageArchiveRepository;
import io.patchpilot.backend.demo.service.DemoLaunchEvidenceShareDeliveryReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoLaunchEvidenceShareCenterService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoLaunchEvidencePackageArchiveRepository archiveRepository;
    private final DemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;

    @Autowired
    public DemoLaunchEvidenceShareCenterService(
            DemoLaunchEvidencePackageArchiveRepository archiveRepository,
            DemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository
    ) {
        this(archiveRepository, receiptRepository, Clock.systemUTC());
    }

    DemoLaunchEvidenceShareCenterService(
            DemoLaunchEvidencePackageArchiveRepository archiveRepository,
            DemoLaunchEvidenceShareDeliveryReceiptRepository receiptRepository,
            Clock clock
    ) {
        this.archiveRepository = archiveRepository;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
    }

    public DemoLaunchEvidenceShareCenterVo getShareCenter() {
        List<DemoLaunchEvidencePackageArchiveVo> archives = archiveRepository.listRecentArchives(MAX_ARCHIVES);
        DemoLaunchEvidenceShareDeliveryReceiptVo latestReceipt = receiptRepository.listRecentReceipts(1).stream()
                .findFirst()
                .orElse(null);
        Instant generatedAt = Instant.now(clock);
        if (archives.isEmpty()) {
            String summary = "No archived launch evidence package is available for sharing.";
            String nextAction = "Archive a final demo launch evidence package after a completed live run before sharing launch evidence.";
            List<String> downloadActions = List.of(
                    "Archive a demo launch evidence package before downloading final launch evidence.",
                    "Download the current launch evidence package report for review."
            );
            List<String> evidenceNotes = List.of("No launch evidence archive has been captured yet.");
            DeliveryReceiptFreshness receiptFreshness = new DeliveryReceiptFreshness(
                    "MISSING",
                    false,
                    "No delivery receipt has been recorded for the current launch evidence package."
            );
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
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    "MISSING",
                    false,
                    "No delivery receipt has been recorded for the current launch evidence package.",
                    downloadActions,
                    evidenceNotes,
                    formatMarkdown(
                            "NO_ARCHIVE",
                            false,
                            summary,
                            nextAction,
                            0,
                            null,
                            null,
                            receiptFreshness,
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
        DeliveryReceiptFreshness receiptFreshness = deliveryReceiptFreshness(latest, latestReceipt);
        String summary = shareReady
                ? "Latest archived launch evidence package is READY and can be shared."
                : "Latest archived launch evidence package needs attention before it is shared.";
        String nextAction = centerNextAction(latest, shareReady, receiptFreshness);
        List<String> downloadActions = downloadActions(latest, shareReady, latestReceipt, receiptFreshness);
        List<String> evidenceNotes = evidenceNotes(latest, latestReceipt, receiptFreshness);
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
                latest.finalHandoffReportPackageArchiveStatus(),
                latest.finalHandoffReportPackageArchiveReady(),
                latest.finalHandoffReportPackageArchiveId(),
                latestReceipt == null ? null : latestReceipt.id(),
                latestReceipt == null ? null : latestReceipt.deliveryTarget(),
                latestReceipt == null ? null : latestReceipt.deliveryChannel(),
                latestReceipt == null ? null : latestReceipt.deliveredAt().toString(),
                latestReceipt != null,
                receiptFreshness.status(),
                receiptFreshness.fresh(),
                receiptFreshness.summary(),
                downloadActions,
                evidenceNotes,
                formatMarkdown(
                        status,
                        shareReady,
                        summary,
                        nextAction,
                        archives.size(),
                        latest,
                        latestReceipt,
                        receiptFreshness,
                        downloadActions,
                        evidenceNotes,
                        generatedAt
                ),
                generatedAt
        );
    }

    private static String centerNextAction(
            DemoLaunchEvidencePackageArchiveVo latest,
            boolean shareReady,
            DeliveryReceiptFreshness receiptFreshness
    ) {
        if (!shareReady) {
            return "Resolve launch evidence blockers, regenerate the package, and archive a new READY package.";
        }
        if ("MISSING".equals(receiptFreshness.status())) {
            return "Download the archived launch evidence package, share it with reviewers, then record a delivery receipt.";
        }
        if (!receiptFreshness.fresh()) {
            return "Share the current launch evidence package and record a new delivery receipt for archive "
                    + latest.id() + ".";
        }
        return "Download the archived launch evidence package and share it with reviewers.";
    }

    private static List<String> downloadActions(
            DemoLaunchEvidencePackageArchiveVo latest,
            boolean shareReady,
            DemoLaunchEvidenceShareDeliveryReceiptVo latestReceipt,
            DeliveryReceiptFreshness receiptFreshness
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download launch evidence package archive " + latest.id() + ".");
        actions.add("Download launch evidence share center report.");
        if (latest.finalHandoffReportPackageArchiveReady()
                && hasText(latest.finalHandoffReportPackageArchiveId())) {
            actions.add("Download final handoff report package archive "
                    + latest.finalHandoffReportPackageArchiveId() + ".");
        }
        if (shareReady && hasText(latest.latestPullRequestUrl())) {
            actions.add("Open Pull Request " + latest.latestPullRequestUrl() + " for review.");
        }
        if (!shareReady) {
            actions.add("Resolve launch evidence blockers and archive a new package.");
        }
        if (latestReceipt == null) {
            actions.add("Record a launch evidence delivery receipt after sharing the package.");
        } else if (!receiptFreshness.fresh()) {
            actions.add("Record a new launch evidence delivery receipt for archive " + latest.id() + ".");
        } else {
            actions.add("Download launch evidence delivery receipt " + latestReceipt.id() + ".");
        }
        return List.copyOf(actions);
    }

    private static List<String> evidenceNotes(
            DemoLaunchEvidencePackageArchiveVo latest,
            DemoLaunchEvidenceShareDeliveryReceiptVo latestReceipt,
            DeliveryReceiptFreshness receiptFreshness
    ) {
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
        if (hasText(latest.finalHandoffReportPackageArchiveId())) {
            notes.add("Final handoff report package archive "
                    + latest.finalHandoffReportPackageArchiveId()
                    + " is " + latest.finalHandoffReportPackageArchiveStatus().name()
                    + (latest.finalHandoffReportPackageArchiveReady()
                    ? " and download-ready."
                    : " and not download-ready."));
        } else {
            notes.add(latest.finalHandoffReportPackageArchiveSummary());
        }
        if (latestReceipt == null) {
            notes.add("No launch evidence delivery receipt has been recorded yet.");
        } else {
            notes.add("Latest delivery receipt " + latestReceipt.id()
                    + " was recorded for " + latestReceipt.deliveryTarget()
                    + " via " + latestReceipt.deliveryChannel() + ".");
        }
        notes.add(receiptFreshness.summary());
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
            DemoLaunchEvidenceShareDeliveryReceiptVo latestReceipt,
            DeliveryReceiptFreshness receiptFreshness,
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
        builder.append("- Final handoff report package archive: `")
                .append(latest == null ? "none" : valueOrNone(latest.finalHandoffReportPackageArchiveId()))
                .append("`\n");
        builder.append("- Final handoff report package archive status: `")
                .append(latest == null ? DemoReadinessStatus.NEEDS_ATTENTION : latest.finalHandoffReportPackageArchiveStatus())
                .append("`\n");
        builder.append("- Final handoff report package archive download-ready: `")
                .append(latest != null && latest.finalHandoffReportPackageArchiveReady())
                .append("`\n");
        builder.append("- Delivery receipt recorded: `").append(latestReceipt != null).append("`\n");
        builder.append("- Delivery receipt freshness: `").append(receiptFreshness.status()).append("`\n");
        builder.append("- Delivery receipt fresh: `").append(receiptFreshness.fresh()).append("`\n");
        builder.append("- Delivery receipt freshness summary: `").append(receiptFreshness.summary()).append("`\n");
        builder.append("- Latest delivery receipt: `").append(latestReceipt == null ? "none" : latestReceipt.id()).append("`\n");
        builder.append("- Delivery target: `").append(latestReceipt == null ? "none" : latestReceipt.deliveryTarget()).append("`\n");
        builder.append("- Delivery channel: `").append(latestReceipt == null ? "none" : latestReceipt.deliveryChannel()).append("`\n");
        builder.append("- Delivered at: `").append(latestReceipt == null ? "none" : latestReceipt.deliveredAt()).append("`\n");
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

    private static DeliveryReceiptFreshness deliveryReceiptFreshness(
            DemoLaunchEvidencePackageArchiveVo latest,
            DemoLaunchEvidenceShareDeliveryReceiptVo latestReceipt
    ) {
        if (latestReceipt == null) {
            return new DeliveryReceiptFreshness(
                    "MISSING",
                    false,
                    "No delivery receipt has been recorded for the current launch evidence package."
            );
        }
        boolean archiveMatches = safeEquals(latestReceipt.launchEvidenceArchiveId(), latest.id());
        boolean sessionMatches = safeEquals(latestReceipt.sessionId(), latest.sessionId());
        if (archiveMatches && sessionMatches) {
            return new DeliveryReceiptFreshness(
                    "FRESH",
                    true,
                    "Latest delivery receipt matches the current launch evidence archive and session."
            );
        }
        return new DeliveryReceiptFreshness(
                "STALE",
                false,
                "Latest delivery receipt " + latestReceipt.id()
                        + " belongs to " + valueOrNone(latestReceipt.launchEvidenceArchiveId())
                        + "/" + valueOrNone(latestReceipt.sessionId())
                        + ", not current " + valueOrNone(latest.id())
                        + "/" + valueOrNone(latest.sessionId()) + "."
        );
    }

    private static boolean safeEquals(String first, String second) {
        if (!hasText(first) || !hasText(second)) {
            return false;
        }
        return first.equals(second);
    }

    private record DeliveryReceiptFreshness(String status, boolean fresh, String summary) {
    }
}
