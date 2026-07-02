package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoCompletionCertificateService {

    private static final int MAX_ARCHIVES = 20;
    private static final String READ_ONLY_CONTRACT =
            "GET /api/demo/live-demo-handoff-package/completion-certificate is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, mutate Git, "
                    + "send messages, or write to GitHub.";

    private final Supplier<List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo>> archiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoLiveDemoCompletionCertificateService(
            DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository archiveRepository
    ) {
        this(() -> archiveRepository.listRecentArchives(MAX_ARCHIVES), Clock.systemUTC());
    }

    DemoLiveDemoCompletionCertificateService(
            DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository archiveRepository,
            Clock clock
    ) {
        this(() -> archiveRepository.listRecentArchives(MAX_ARCHIVES), clock);
    }

    DemoLiveDemoCompletionCertificateService(
            Supplier<List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo>> archiveSupplier,
            Clock clock
    ) {
        this.archiveSupplier = archiveSupplier;
        this.clock = clock;
    }

    public DemoLiveDemoCompletionCertificateVo getCertificate() {
        Instant generatedAt = Instant.now(clock);
        List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> archives = archiveSupplier.get();
        if (archives.isEmpty()) {
            return missingCertificate(generatedAt);
        }
        DemoLiveDemoHandoffDeliveryFinalizationArchiveVo latest = archives.get(0);
        boolean certified = latest.finalized()
                && "READY".equals(latest.status())
                && latest.deliveryReceiptFresh();
        String summary = certified
                ? "PatchPilot live demo is certified from the latest handoff finalization archive."
                : "Latest live demo handoff finalization archive is not certified.";
        String nextAction = certified
                ? "Share the live demo completion certificate with reviewers."
                : "Resolve handoff finalization blockers, then archive a fresh READY finalization.";
        List<String> downloadActions = certified
                ? certifiedDownloadActions(latest)
                : List.of("Archive a READY live demo handoff delivery finalization before sharing a completion certificate.");
        return new DemoLiveDemoCompletionCertificateVo(
                certified ? "READY" : "NEEDS_ATTENTION",
                certified,
                summary,
                nextAction,
                latest.id(),
                latest.latestDeliveryReceiptId(),
                latest.evidenceBundleArchiveId(),
                latest.repository(),
                latest.issueNumber(),
                latest.issueUrl(),
                latest.taskId(),
                latest.taskStatus(),
                latest.pullRequestUrl(),
                latest.latestDeliveryTarget(),
                latest.latestDeliveryChannel(),
                latest.latestDeliveredAt(),
                latest.deliveryReceiptFreshness(),
                latest.finalizationGeneratedAt(),
                latest.archivedAt(),
                generatedAt,
                downloadActions,
                READ_ONLY_CONTRACT,
                markdownReport(
                        certified ? "READY" : "NEEDS_ATTENTION",
                        certified,
                        summary,
                        nextAction,
                        latest,
                        downloadActions,
                        generatedAt
                )
        );
    }

    private static DemoLiveDemoCompletionCertificateVo missingCertificate(Instant generatedAt) {
        String summary = "No live demo handoff delivery finalization archive is available for certification.";
        String nextAction =
                "Archive a READY live demo handoff delivery finalization before downloading the completion certificate.";
        List<String> downloadActions = List.of(
                "Archive a READY live demo handoff delivery finalization before sharing a completion certificate."
        );
        return new DemoLiveDemoCompletionCertificateVo(
                "NEEDS_ATTENTION",
                false,
                summary,
                nextAction,
                null,
                null,
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
                "MISSING",
                null,
                null,
                generatedAt,
                downloadActions,
                READ_ONLY_CONTRACT,
                markdownReport("NEEDS_ATTENTION", false, summary, nextAction, null, downloadActions, generatedAt)
        );
    }

    private static List<String> certifiedDownloadActions(
            DemoLiveDemoHandoffDeliveryFinalizationArchiveVo latest
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download live demo completion certificate.");
        actions.add("Download live demo handoff delivery finalization archive " + latest.id() + ".");
        if (latest.latestDeliveryReceiptId() != null) {
            actions.add("Download live demo handoff delivery receipt " + latest.latestDeliveryReceiptId() + ".");
        }
        if (latest.evidenceBundleArchiveId() != null) {
            actions.add("Download live demo evidence bundle archive " + latest.evidenceBundleArchiveId() + ".");
        }
        if (latest.pullRequestUrl() != null) {
            actions.add("Open Pull Request " + latest.pullRequestUrl() + " for review.");
        }
        return actions;
    }

    private static String markdownReport(
            String status,
            boolean certified,
            String summary,
            String nextAction,
            DemoLiveDemoHandoffDeliveryFinalizationArchiveVo latest,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Demo Completion Certificate\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Certified: `").append(certified).append("`\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Next action: ").append(nextAction).append("\n");
        if (latest != null) {
            report.append("- Handoff finalization archive: `").append(latest.id()).append("`\n");
            report.append("- Delivery receipt: `")
                    .append(valueOrMissing(latest.latestDeliveryReceiptId())).append("`\n");
            report.append("- Evidence bundle archive: `")
                    .append(valueOrMissing(latest.evidenceBundleArchiveId())).append("`\n");
            report.append("- Repository: ").append(valueOrMissing(latest.repository())).append("\n");
            report.append("- Issue: #").append(latest.issueNumber()).append("\n");
            report.append("- Issue URL: ").append(valueOrMissing(latest.issueUrl())).append("\n");
            report.append("- Task: `").append(valueOrMissing(latest.taskId())).append("`\n");
            report.append("- Task status: `").append(valueOrMissing(latest.taskStatus())).append("`\n");
            report.append("- Pull Request: ").append(valueOrMissing(latest.pullRequestUrl())).append("\n");
            report.append("- Delivery target: `")
                    .append(valueOrMissing(latest.latestDeliveryTarget())).append("`\n");
            report.append("- Delivery channel: `")
                    .append(valueOrMissing(latest.latestDeliveryChannel())).append("`\n");
            report.append("- Delivery receipt freshness: `").append(latest.deliveryReceiptFreshness()).append("`\n");
            report.append("- Finalization generated at: `").append(latest.finalizationGeneratedAt()).append("`\n");
            report.append("- Finalization archived at: `").append(latest.archivedAt()).append("`\n");
        }
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendList(report, "Download Actions", downloadActions);
        report.append("## Side Effect Contract\n\n").append(READ_ONLY_CONTRACT).append("\n");
        return report.toString();
    }

    private static void appendList(StringBuilder report, String heading, List<String> items) {
        report.append("## ").append(heading).append("\n\n");
        for (String item : items) {
            report.append("- ").append(item).append("\n");
        }
        report.append("\n");
    }

    private static String valueOrMissing(String value) {
        return value == null || value.isBlank() ? "missing" : value;
    }
}
