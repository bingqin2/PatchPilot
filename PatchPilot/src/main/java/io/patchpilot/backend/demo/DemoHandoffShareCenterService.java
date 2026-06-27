package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoHandoffShareDeliveryReceiptRepository;
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
    private final DemoHandoffShareDeliveryReceiptRepository receiptRepository;
    private final Clock clock;

    @Autowired
    public DemoHandoffShareCenterService(
            DemoHandoffPackageArchiveSummaryService archiveSummaryService,
            DemoHandoffShareChecklistService shareChecklistService,
            DemoHandoffShareDeliveryReceiptRepository receiptRepository
    ) {
        this(archiveSummaryService, shareChecklistService, receiptRepository, Clock.systemUTC());
    }

    DemoHandoffShareCenterService(
            DemoHandoffPackageArchiveSummaryService archiveSummaryService,
            DemoHandoffShareChecklistService shareChecklistService,
            DemoHandoffShareDeliveryReceiptRepository receiptRepository,
            Clock clock
    ) {
        this.archiveSummaryService = archiveSummaryService;
        this.shareChecklistService = shareChecklistService;
        this.receiptRepository = receiptRepository;
        this.clock = clock;
    }

    public DemoHandoffShareCenterVo getShareCenter() {
        DemoHandoffPackageArchiveSummaryVo archiveSummary = archiveSummaryService.getArchiveSummary();
        DemoHandoffShareChecklistVo checklist = shareChecklistService.getShareChecklist();
        DemoHandoffShareDeliveryReceiptVo latestReceipt = receiptRepository.listRecentReceipts(1).stream()
                .findFirst()
                .orElse(null);
        DemoReadinessStatus status = centerStatus(archiveSummary, checklist);
        boolean shareReady = archiveSummary.shareReady() && checklist.status() == DemoReadinessStatus.READY;
        DeliveryReceiptFreshness receiptFreshness = deliveryReceiptFreshness(archiveSummary, latestReceipt);
        Instant generatedAt = Instant.now(clock);
        List<String> downloadActions = downloadActions(archiveSummary, checklist, latestReceipt, receiptFreshness);
        List<String> evidenceNotes = evidenceNotes(archiveSummary, checklist, latestReceipt, receiptFreshness);
        String summary = centerSummary(archiveSummary, checklist, shareReady, status);
        String nextAction = centerNextAction(archiveSummary, checklist, shareReady, receiptFreshness);
        String latestCreatedAt = archiveSummary.latestCreatedAt() == null ? null : archiveSummary.latestCreatedAt().toString();
        String latestDeliveredAt = latestReceipt == null ? null : latestReceipt.deliveredAt().toString();
        String markdownReport = formatMarkdown(
                status,
                shareReady,
                summary,
                nextAction,
                archiveSummary,
                checklist,
                latestReceipt,
                receiptFreshness,
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
                latestReceipt == null ? null : latestReceipt.id(),
                latestReceipt == null ? null : latestReceipt.deliveryTarget(),
                latestReceipt == null ? null : latestReceipt.deliveryChannel(),
                latestDeliveredAt,
                latestReceipt != null,
                receiptFreshness.status(),
                receiptFreshness.fresh(),
                receiptFreshness.summary(),
                downloadActions,
                evidenceNotes,
                markdownReport,
                generatedAt
        );
    }

    public DemoHandoffShareInstructionsVo getShareInstructions() {
        DemoHandoffShareCenterVo center = getShareCenter();
        Instant generatedAt = Instant.now(clock);
        List<String> recipients = recommendedRecipients();
        List<String> attachments = requiredAttachments(center);
        List<String> preSendChecks = preSendChecks(center);
        String summary = shareInstructionsSummary(center);
        String nextAction = shareInstructionsNextAction(center);
        String messageSubject = messageSubject(center);
        String messageBody = messageBody(center);
        String markdownReport = formatInstructionsMarkdown(
                center,
                summary,
                nextAction,
                recipients,
                attachments,
                preSendChecks,
                messageSubject,
                messageBody,
                generatedAt
        );
        return new DemoHandoffShareInstructionsVo(
                center.status(),
                center.shareReady(),
                summary,
                nextAction,
                center.latestArchiveId(),
                center.latestSessionId(),
                recipients,
                attachments,
                preSendChecks,
                messageSubject,
                messageBody,
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
            DemoHandoffShareChecklistVo checklist,
            DemoHandoffShareDeliveryReceiptVo latestReceipt,
            DeliveryReceiptFreshness receiptFreshness
    ) {
        List<String> actions = new ArrayList<>();
        if (hasText(archiveSummary.latestArchiveId())) {
            actions.add("Download handoff package archive " + archiveSummary.latestArchiveId() + ".");
        } else {
            actions.add("Archive a demo handoff package before downloading final handoff evidence.");
        }
        actions.add("Download handoff package archive summary.");
        actions.add("Download handoff share checklist.");
        if (latestReceipt == null) {
            actions.add("Record a handoff share delivery receipt after sending the package.");
        } else if (!receiptFreshness.fresh()) {
            actions.add("Record a new handoff share delivery receipt for archive "
                    + valueOrNone(archiveSummary.latestArchiveId()) + ".");
        } else {
            actions.add("Download handoff share delivery receipt " + latestReceipt.id() + ".");
        }
        if (checklist.status() != DemoReadinessStatus.READY) {
            actions.add("Resolve checklist warnings before sending the package.");
        }
        return List.copyOf(actions);
    }

    private static List<String> evidenceNotes(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            DemoHandoffShareDeliveryReceiptVo latestReceipt,
            DeliveryReceiptFreshness receiptFreshness
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Latest package archive status is " + archiveSummary.status() + ".");
        notes.add("Share checklist has " + checklist.checks().size() + " checks.");
        if (latestReceipt == null) {
            notes.add("No handoff share delivery receipt has been recorded yet.");
        } else {
            notes.add("Latest delivery receipt " + latestReceipt.id()
                    + " was recorded for " + latestReceipt.deliveryTarget()
                    + " via " + latestReceipt.deliveryChannel() + ".");
        }
        notes.add(receiptFreshness.summary());
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
            boolean shareReady,
            DeliveryReceiptFreshness receiptFreshness
    ) {
        if (shareReady) {
            if ("MISSING".equals(receiptFreshness.status())) {
                return "Download the package, send the prepared handoff message, then record a delivery receipt.";
            }
            if (!receiptFreshness.fresh()) {
                return "Send the current handoff package and record a new delivery receipt for archive "
                        + valueOrNone(archiveSummary.latestArchiveId()) + ".";
            }
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
            DemoHandoffShareDeliveryReceiptVo latestReceipt,
            DeliveryReceiptFreshness receiptFreshness,
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

    private static List<String> recommendedRecipients() {
        return List.of("Repository owner or maintainer", "Demo reviewer");
    }

    private static List<String> requiredAttachments(DemoHandoffShareCenterVo center) {
        if (!hasText(center.latestArchiveId())) {
            return List.of("Create a handoff package archive before attaching final handoff evidence.");
        }
        return List.of(
                "Handoff package archive " + center.latestArchiveId(),
                "Handoff package archive summary",
                "Handoff share checklist",
                "Handoff share center report"
        );
    }

    private static List<String> preSendChecks(DemoHandoffShareCenterVo center) {
        List<String> checks = new ArrayList<>();
        if (center.shareReady()) {
            checks.add("Confirm the Pull Request link in the handoff package opens correctly.");
            checks.add("Confirm the archived package, archive summary, checklist, and share-center report were downloaded.");
            checks.add("Confirm no handoff share checklist warnings remain.");
        } else {
            checks.add("Resolve the share center next action before sending: " + center.nextAction());
            checks.add("Do not send the handoff message until the share center reports READY.");
        }
        return List.copyOf(checks);
    }

    private static String shareInstructionsSummary(DemoHandoffShareCenterVo center) {
        if (center.shareReady()) {
            return "Share the current handoff package with repository maintainers and demo reviewers.";
        }
        return "The handoff package is not ready to send.";
    }

    private static String shareInstructionsNextAction(DemoHandoffShareCenterVo center) {
        if (center.shareReady()) {
            return "Send the prepared handoff message with all required attachments.";
        }
        return center.nextAction();
    }

    private static String messageSubject(DemoHandoffShareCenterVo center) {
        if (center.shareReady() && hasText(center.latestSessionId())) {
            return "PatchPilot demo handoff: " + center.latestSessionId();
        }
        return "PatchPilot demo handoff: not ready";
    }

    private static String messageBody(DemoHandoffShareCenterVo center) {
        if (!center.shareReady()) {
            return "The PatchPilot demo handoff package is not ready to send yet.\n\n"
                    + "Current blocker: " + center.nextAction() + "\n\n"
                    + "Please resolve this before sending post-demo evidence.";
        }
        return "The PatchPilot demo handoff package is ready to share.\n\n"
                + "Session: " + valueOrNone(center.latestSessionId()) + "\n"
                + "Archive: " + valueOrNone(center.latestArchiveId()) + "\n\n"
                + "Attached evidence should include the handoff package archive, archive summary, share checklist, and share-center report.\n\n"
                + "Next action: " + center.nextAction();
    }

    private static String formatInstructionsMarkdown(
            DemoHandoffShareCenterVo center,
            String summary,
            String nextAction,
            List<String> recipients,
            List<String> attachments,
            List<String> preSendChecks,
            String messageSubject,
            String messageBody,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Demo Handoff Share Instructions\n\n");
        builder.append("- Status: `").append(center.status().name()).append("`\n");
        builder.append("- Send ready: `").append(center.shareReady()).append("`\n");
        builder.append("- Latest archive: `").append(valueOrNone(center.latestArchiveId())).append("`\n");
        builder.append("- Latest session: `").append(valueOrNone(center.latestSessionId())).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        appendList(builder, "## Recommended Recipients", recipients);
        appendList(builder, "## Required Attachments", attachments);
        appendList(builder, "## Pre-Send Checks", preSendChecks);
        builder.append("## Message Template\n\n");
        builder.append("Subject: ").append(messageSubject).append("\n\n");
        builder.append(messageBody).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/handoff-share-instructions is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, send messages, or write to GitHub.\n");
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

    private static DeliveryReceiptFreshness deliveryReceiptFreshness(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareDeliveryReceiptVo latestReceipt
    ) {
        if (latestReceipt == null) {
            return new DeliveryReceiptFreshness(
                    "MISSING",
                    false,
                    "No delivery receipt has been recorded for the current handoff package."
            );
        }
        boolean archiveMatches = safeEquals(latestReceipt.handoffArchiveId(), archiveSummary.latestArchiveId());
        boolean sessionMatches = safeEquals(latestReceipt.sessionId(), archiveSummary.latestSessionId());
        if (archiveMatches && sessionMatches) {
            return new DeliveryReceiptFreshness(
                    "FRESH",
                    true,
                    "Latest delivery receipt matches the current handoff archive and session."
            );
        }
        return new DeliveryReceiptFreshness(
                "STALE",
                false,
                "Latest delivery receipt " + latestReceipt.id()
                        + " belongs to " + valueOrNone(latestReceipt.handoffArchiveId())
                        + "/" + valueOrNone(latestReceipt.sessionId())
                        + ", not current " + valueOrNone(archiveSummary.latestArchiveId())
                        + "/" + valueOrNone(archiveSummary.latestSessionId()) + "."
        );
    }

    private static boolean safeEquals(String first, String second) {
        if (!hasText(first) || !hasText(second)) {
            return false;
        }
        return first.equals(second);
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value : "none";
    }

    private record DeliveryReceiptFreshness(String status, boolean fresh, String summary) {
    }
}
