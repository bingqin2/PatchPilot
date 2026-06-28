package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageVo;
import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoFinalHandoffReportPackageService {

    private final DemoHandoffPackageArchiveService archiveService;
    private final DemoHandoffShareChecklistService checklistService;
    private final DemoHandoffShareCenterService shareCenterService;
    private final DemoHandoffFinalizationService finalizationService;
    private final Clock clock;

    @Autowired
    public DemoFinalHandoffReportPackageService(
            DemoHandoffPackageArchiveService archiveService,
            DemoHandoffShareChecklistService checklistService,
            DemoHandoffShareCenterService shareCenterService,
            DemoHandoffFinalizationService finalizationService
    ) {
        this(archiveService, checklistService, shareCenterService, finalizationService, Clock.systemUTC());
    }

    DemoFinalHandoffReportPackageService(
            DemoHandoffPackageArchiveService archiveService,
            DemoHandoffShareChecklistService checklistService,
            DemoHandoffShareCenterService shareCenterService,
            DemoHandoffFinalizationService finalizationService,
            Clock clock
    ) {
        this.archiveService = archiveService;
        this.checklistService = checklistService;
        this.shareCenterService = shareCenterService;
        this.finalizationService = finalizationService;
        this.clock = clock;
    }

    public DemoFinalHandoffReportPackageVo getReportPackage() {
        DemoHandoffPackageArchiveSummaryVo archiveSummary = archiveService.getArchiveSummary();
        DemoHandoffShareChecklistVo checklist = checklistService.getShareChecklist();
        DemoHandoffShareCenterVo shareCenter = shareCenterService.getShareCenter();
        DemoHandoffShareInstructionsVo instructions = shareCenterService.getShareInstructions();
        DemoHandoffFinalizationVo finalization = finalizationService.getFinalizationGate();
        Instant generatedAt = Instant.now(clock);

        DemoReadinessStatus status = packageStatus(archiveSummary, checklist, shareCenter, finalization);
        boolean downloadReady = status == DemoReadinessStatus.READY
                && archiveSummary.shareReady()
                && instructions.sendReady()
                && finalization.finalized()
                && shareCenter.taskCertificateReady();
        String summary = summary(status, finalization);
        String nextAction = nextAction(status, archiveSummary, checklist, shareCenter, instructions, finalization);
        List<String> readinessChecks = readinessChecks(archiveSummary, checklist, shareCenter, finalization);
        List<String> requiredAttachments = requiredAttachments(instructions);
        List<String> preSendChecks = preSendChecks(instructions, finalization);
        List<String> evidenceNotes = evidenceNotes(archiveSummary, shareCenter, finalization);
        List<String> sourceReports = List.of(
                "Handoff package archive summary",
                "Handoff share checklist",
                "Handoff share center",
                "Handoff share instructions",
                "Handoff finalization"
        );
        String markdownReport = formatMarkdown(
                status,
                downloadReady,
                summary,
                nextAction,
                archiveSummary,
                checklist,
                shareCenter,
                instructions,
                finalization,
                readinessChecks,
                requiredAttachments,
                preSendChecks,
                evidenceNotes,
                sourceReports,
                generatedAt
        );
        return new DemoFinalHandoffReportPackageVo(
                status,
                downloadReady,
                summary,
                nextAction,
                archiveSummary.latestArchiveId(),
                archiveSummary.latestSessionId(),
                finalization.latestDeliveryReceiptId(),
                shareCenter.taskCertificateArchiveId(),
                shareCenter.taskCertificateReady(),
                readinessChecks,
                requiredAttachments,
                preSendChecks,
                evidenceNotes,
                sourceReports,
                markdownReport,
                generatedAt
        );
    }

    private static DemoReadinessStatus packageStatus(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            DemoHandoffShareCenterVo shareCenter,
            DemoHandoffFinalizationVo finalization
    ) {
        if (archiveSummary.latestHandoffReadinessStatus() == DemoReadinessStatus.BLOCKED
                || checklist.status() == DemoReadinessStatus.BLOCKED
                || shareCenter.status() == DemoReadinessStatus.BLOCKED
                || finalization.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (!archiveSummary.shareReady()
                || checklist.status() != DemoReadinessStatus.READY
                || !shareCenter.shareReady()
                || !shareCenter.taskCertificateReady()
                || !finalization.finalized()) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static String summary(DemoReadinessStatus status, DemoHandoffFinalizationVo finalization) {
        if (status == DemoReadinessStatus.READY) {
            return "Final demo handoff report package is ready to deliver.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final demo handoff report package is blocked before delivery.";
        }
        if (!finalization.finalized()) {
            return "Final demo handoff report package needs current finalization evidence before delivery.";
        }
        return "Final demo handoff report package needs all handoff share evidence before delivery.";
    }

    private static String nextAction(
            DemoReadinessStatus status,
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            DemoHandoffShareCenterVo shareCenter,
            DemoHandoffShareInstructionsVo instructions,
            DemoHandoffFinalizationVo finalization
    ) {
        if (status == DemoReadinessStatus.READY) {
            return "Download this final handoff report package and attach the listed evidence files.";
        }
        if (!archiveSummary.shareReady()) {
            return archiveSummary.nextAction();
        }
        if (checklist.status() != DemoReadinessStatus.READY) {
            return checklist.nextAction();
        }
        if (!shareCenter.shareReady()) {
            return shareCenter.nextAction();
        }
        if (!instructions.sendReady()) {
            return instructions.nextAction();
        }
        return finalization.nextAction();
    }

    private static List<String> readinessChecks(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            DemoHandoffShareCenterVo shareCenter,
            DemoHandoffFinalizationVo finalization
    ) {
        return List.of(
                "Archive summary: " + archiveSummary.status(),
                "Share checklist: " + checklist.status().name(),
                "Share center: " + shareCenter.status().name(),
                "Task evidence certificate: " + (shareCenter.taskCertificateReady() ? "READY" : shareCenter.taskCertificateStatus().name()),
                "Finalization: " + finalization.status().name()
        );
    }

    private static List<String> requiredAttachments(DemoHandoffShareInstructionsVo instructions) {
        List<String> attachments = new ArrayList<>(instructions.requiredAttachments());
        if (attachments.stream().noneMatch("Finalization report"::equals)) {
            attachments.add("Finalization report");
        }
        if (attachments.stream().noneMatch("Final handoff report package"::equals)) {
            attachments.add("Final handoff report package");
        }
        return List.copyOf(attachments);
    }

    private static List<String> preSendChecks(DemoHandoffShareInstructionsVo instructions, DemoHandoffFinalizationVo finalization) {
        List<String> checks = new ArrayList<>(instructions.preSendChecks());
        finalization.checks().stream()
                .map(check -> check.nextAction())
                .filter(nextAction -> !"No action needed.".equals(nextAction))
                .filter(nextAction -> checks.stream().noneMatch(nextAction::equals))
                .forEach(checks::add);
        checks.add("Confirm the final handoff report package status is READY before delivery.");
        return List.copyOf(checks);
    }

    private static List<String> evidenceNotes(
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareCenterVo shareCenter,
            DemoHandoffFinalizationVo finalization
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Archive summary status is " + archiveSummary.status() + ".");
        notes.addAll(shareCenter.evidenceNotes());
        notes.addAll(finalization.evidenceNotes());
        return notes.stream().distinct().toList();
    }

    private static String formatMarkdown(
            DemoReadinessStatus status,
            boolean downloadReady,
            String summary,
            String nextAction,
            DemoHandoffPackageArchiveSummaryVo archiveSummary,
            DemoHandoffShareChecklistVo checklist,
            DemoHandoffShareCenterVo shareCenter,
            DemoHandoffShareInstructionsVo instructions,
            DemoHandoffFinalizationVo finalization,
            List<String> readinessChecks,
            List<String> requiredAttachments,
            List<String> preSendChecks,
            List<String> evidenceNotes,
            List<String> sourceReports,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Demo Handoff Report Package\n\n");
        builder.append("- Status: `").append(status.name()).append("`\n");
        builder.append("- Download ready: `").append(downloadReady).append("`\n");
        builder.append("- Latest archive: `").append(valueOrNone(archiveSummary.latestArchiveId())).append("`\n");
        builder.append("- Latest session: `").append(valueOrNone(archiveSummary.latestSessionId())).append("`\n");
        builder.append("- Latest delivery receipt: `").append(valueOrNone(finalization.latestDeliveryReceiptId())).append("`\n");
        builder.append("- Task certificate archive: `").append(valueOrNone(shareCenter.taskCertificateArchiveId())).append("`\n");
        builder.append("- Task certificate ready: `").append(shareCenter.taskCertificateReady()).append("`\n");
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        builder.append("## Summary\n\n").append(summary).append("\n\n");
        builder.append("## Next Action\n\n").append(nextAction).append("\n\n");
        appendList(builder, "Readiness Checks", readinessChecks);
        appendList(builder, "Required Attachments", requiredAttachments);
        appendList(builder, "Pre-Send Checks", preSendChecks);
        appendList(builder, "Evidence Notes", evidenceNotes);
        appendList(builder, "Source Reports", sourceReports);
        builder.append("## Message Subject\n\n").append(instructions.messageSubject()).append("\n\n");
        builder.append("## Message Body\n\n").append(instructions.messageBody()).append("\n\n");
        builder.append("## Embedded Reports\n\n");
        builder.append("### Handoff Package Archive Summary\n\n").append(archiveSummary.markdownReport()).append("\n\n");
        builder.append("### Handoff Share Checklist\n\n").append(checklist.markdownReport()).append("\n\n");
        builder.append("### Handoff Share Center\n\n").append(shareCenter.markdownReport()).append("\n\n");
        builder.append("### Handoff Share Instructions\n\n").append(instructions.markdownReport()).append("\n\n");
        builder.append("### Handoff Finalization\n\n").append(finalization.markdownReport()).append("\n\n");
        builder.append("## Side-Effect Contract\n\n");
        builder.append("GET /api/demo/final-handoff-report-package is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, send messages, or write to GitHub.\n");
        return builder.toString();
    }

    private static void appendList(StringBuilder builder, String title, List<String> items) {
        builder.append("## ").append(title).append("\n\n");
        for (String item : items) {
            builder.append("- ").append(item).append('\n');
        }
        builder.append('\n');
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
