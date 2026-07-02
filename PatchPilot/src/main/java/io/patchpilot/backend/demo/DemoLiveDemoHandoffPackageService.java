package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoHandoffPackageService {

    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/live-demo-handoff-package is a read-only live demo handoff package: "
                    + "it does not create tasks, run model calls, mutate Git, write archives, send GitHub requests, "
                    + "or change repository state.";

    private final DemoLiveDemoEvidenceBundleArchiveRepository archiveRepository;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoHandoffPackageService(DemoLiveDemoEvidenceBundleArchiveRepository archiveRepository) {
        this(archiveRepository, Instant::now);
    }

    DemoLiveDemoHandoffPackageService(
            DemoLiveDemoEvidenceBundleArchiveRepository archiveRepository,
            Supplier<Instant> nowSupplier
    ) {
        this.archiveRepository = archiveRepository;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoHandoffPackageVo createPackage() {
        Instant generatedAt = nowSupplier.get();
        List<DemoLiveDemoEvidenceBundleArchiveVo> archives = archiveRepository.listRecentArchives(1);
        if (archives.isEmpty()) {
            return missingArchivePackage(generatedAt);
        }

        DemoLiveDemoEvidenceBundleArchiveVo archive = archives.get(0);
        if (!archive.readyForHandoff() || !"READY".equals(archive.status())) {
            return attentionPackage(archive, generatedAt);
        }
        return readyPackage(archive, generatedAt);
    }

    private static DemoLiveDemoHandoffPackageVo readyPackage(
            DemoLiveDemoEvidenceBundleArchiveVo archive,
            Instant generatedAt
    ) {
        List<String> reviewChecklist = List.of(
                "Open the Pull Request and review the files changed.",
                "Confirm the evidence bundle archive " + archive.id() + " matches the issue and task.",
                "Merge or close the Pull Request according to repository policy."
        );
        List<String> deliveryInstructions = List.of(
                "Share this handoff package and archived evidence report with the reviewer."
        );
        String summary = "Live demo handoff package is ready for reviewer handoff.";
        return packageFrom(
                "READY",
                true,
                archive,
                summary,
                reviewChecklist,
                deliveryInstructions,
                archive.evidenceNotes(),
                generatedAt
        );
    }

    private static DemoLiveDemoHandoffPackageVo attentionPackage(
            DemoLiveDemoEvidenceBundleArchiveVo archive,
            Instant generatedAt
    ) {
        List<String> deliveryInstructions = List.of(
                "Resolve the evidence bundle archive gaps before sending this handoff package."
        );
        String summary = "The latest evidence bundle archive is not ready for reviewer handoff.";
        return packageFrom(
                "NEEDS_ATTENTION",
                false,
                archive,
                summary,
                List.of(),
                deliveryInstructions,
                archive.evidenceNotes(),
                generatedAt
        );
    }

    private static DemoLiveDemoHandoffPackageVo missingArchivePackage(Instant generatedAt) {
        String summary = "PatchPilot is missing a live demo evidence bundle archive for reviewer handoff.";
        List<String> deliveryInstructions = List.of(
                "Archive a live demo evidence bundle before preparing the final reviewer handoff package."
        );
        return new DemoLiveDemoHandoffPackageVo(
                "BLOCKED",
                false,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                summary,
                List.of(),
                deliveryInstructions,
                List.of(),
                SIDE_EFFECT_CONTRACT,
                generatedAt,
                markdownReport(
                        "BLOCKED",
                        false,
                        null,
                        summary,
                        List.of(),
                        deliveryInstructions,
                        List.of(),
                        generatedAt
                )
        );
    }

    private static DemoLiveDemoHandoffPackageVo packageFrom(
            String status,
            boolean readyForReview,
            DemoLiveDemoEvidenceBundleArchiveVo archive,
            String summary,
            List<String> reviewChecklist,
            List<String> deliveryInstructions,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        return new DemoLiveDemoHandoffPackageVo(
                status,
                readyForReview,
                archive.id(),
                archive.repository(),
                archive.issueNumber(),
                archive.issueUrl(),
                archive.triggerUser(),
                archive.triggerComment(),
                archive.taskId(),
                archive.taskStatus(),
                archive.pullRequestUrl(),
                archive.webhookDeliveryId(),
                summary,
                reviewChecklist,
                deliveryInstructions,
                evidenceNotes,
                SIDE_EFFECT_CONTRACT,
                generatedAt,
                markdownReport(
                        status,
                        readyForReview,
                        archive,
                        summary,
                        reviewChecklist,
                        deliveryInstructions,
                        evidenceNotes,
                        generatedAt
                )
        );
    }

    private static String markdownReport(
            String status,
            boolean readyForReview,
            DemoLiveDemoEvidenceBundleArchiveVo archive,
            String summary,
            List<String> reviewChecklist,
            List<String> deliveryInstructions,
            List<String> evidenceNotes,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Handoff Package\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Ready for review: `").append(readyForReview).append("`\n");
        if (archive != null) {
            report.append("- Evidence bundle archive: ").append(archive.id()).append("\n");
            report.append("- Repository: ").append(valueOrMissing(archive.repository())).append("\n");
            report.append("- Issue: #").append(archive.issueNumber()).append("\n");
            report.append("- Issue URL: ").append(valueOrMissing(archive.issueUrl())).append("\n");
            report.append("- Trigger user: ").append(valueOrMissing(archive.triggerUser())).append("\n");
            report.append("- Trigger comment: `").append(valueOrMissing(archive.triggerComment())).append("`\n");
            report.append("- Task: ").append(valueOrMissing(archive.taskId())).append("\n");
            report.append("- Task status: ").append(valueOrMissing(archive.taskStatus())).append("\n");
            report.append("- Webhook delivery: ").append(valueOrMissing(archive.webhookDeliveryId())).append("\n");
            report.append("- Pull Request: ").append(valueOrMissing(archive.pullRequestUrl())).append("\n");
        } else {
            report.append("- Evidence bundle archive: missing\n");
        }
        report.append("- Generated at: ").append(generatedAt).append("\n\n");
        report.append("## Summary\n\n").append(summary).append("\n\n");
        appendList(report, "Review Checklist", reviewChecklist);
        appendList(report, "Delivery Instructions", deliveryInstructions);
        appendList(report, "Evidence Notes", evidenceNotes);
        report.append("## Side Effect Contract\n\n").append(SIDE_EFFECT_CONTRACT).append("\n");
        return report.toString();
    }

    private static void appendList(StringBuilder report, String title, List<String> items) {
        report.append("## ").append(title).append("\n\n");
        if (items.isEmpty()) {
            report.append("- None recorded.\n\n");
            return;
        }
        for (String item : items) {
            report.append("- ").append(item).append("\n");
        }
        report.append("\n");
    }

    private static String valueOrMissing(Object value) {
        return value == null ? "missing" : value.toString();
    }
}
