package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCloseoutArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoLaunchAcceptanceCertificateService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoLaunchAcceptanceCloseoutArchiveRepository closeoutArchiveRepository;
    private final Clock clock;

    @Autowired
    public DemoLaunchAcceptanceCertificateService(
            DemoLaunchAcceptanceCloseoutArchiveRepository closeoutArchiveRepository
    ) {
        this(closeoutArchiveRepository, Clock.systemUTC());
    }

    DemoLaunchAcceptanceCertificateService(
            DemoLaunchAcceptanceCloseoutArchiveRepository closeoutArchiveRepository,
            Clock clock
    ) {
        this.closeoutArchiveRepository = closeoutArchiveRepository;
        this.clock = clock;
    }

    public DemoLaunchAcceptanceCertificateVo getCertificate() {
        List<DemoLaunchAcceptanceCloseoutArchiveVo> archives =
                closeoutArchiveRepository.listRecentArchives(MAX_ARCHIVES);
        Instant generatedAt = Instant.now(clock);
        if (archives.isEmpty()) {
            return missingCertificate(generatedAt);
        }
        DemoLaunchAcceptanceCloseoutArchiveVo latest = archives.get(0);
        boolean certified = latest.status() == DemoReadinessStatus.READY && latest.accepted();
        String summary = certified
                ? "PatchPilot launch acceptance is certified from the latest accepted closeout archive."
                : "Latest launch acceptance closeout archive is not accepted.";
        String nextAction = certified
                ? "Share the certificate and archived closeout report with reviewers."
                : "Resolve closeout blockers, archive a READY accepted closeout, then download the certificate.";
        List<String> downloadActions = certified
                ? certifiedDownloadActions(latest)
                : List.of("Archive a READY launch acceptance closeout before sharing a certificate.");
        return new DemoLaunchAcceptanceCertificateVo(
                certified ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION,
                certified,
                summary,
                nextAction,
                archives.size(),
                latest.id(),
                latest.latestArchiveId(),
                latest.latestDeliveryReceiptId(),
                latest.sessionId(),
                latest.latestTaskId(),
                latest.latestPullRequestUrl(),
                latest.latestWebhookDeliveryId(),
                latest.evaluationRunId(),
                latest.latestDeliveryTarget(),
                latest.latestDeliveryChannel(),
                latest.deliveryReceiptFreshness(),
                latest.createdAt(),
                generatedAt,
                downloadActions,
                markdownReport(
                        certified ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION,
                        certified,
                        summary,
                        nextAction,
                        archives.size(),
                        latest,
                        downloadActions,
                        generatedAt
                )
        );
    }

    private static DemoLaunchAcceptanceCertificateVo missingCertificate(Instant generatedAt) {
        String summary = "No launch acceptance closeout archive is available for certification.";
        String nextAction = "Archive an accepted launch acceptance closeout before downloading the certificate.";
        List<String> downloadActions = List.of("Archive a READY launch acceptance closeout before sharing a certificate.");
        return new DemoLaunchAcceptanceCertificateVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
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
                null,
                null,
                null,
                "MISSING",
                null,
                generatedAt,
                downloadActions,
                markdownReport(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        false,
                        summary,
                        nextAction,
                        0,
                        null,
                        downloadActions,
                        generatedAt
                )
        );
    }

    private static List<String> certifiedDownloadActions(DemoLaunchAcceptanceCloseoutArchiveVo latest) {
        List<String> actions = new ArrayList<>();
        actions.add("Download launch acceptance certificate.");
        actions.add("Download launch acceptance closeout archive " + latest.id() + ".");
        if (latest.latestPullRequestUrl() != null) {
            actions.add("Open Pull Request " + latest.latestPullRequestUrl() + " for review.");
        }
        return actions;
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean certified,
            String summary,
            String nextAction,
            int archiveCount,
            DemoLaunchAcceptanceCloseoutArchiveVo latest,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Launch Acceptance Certificate\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Certified: `").append(certified).append("`\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        builder.append("- Archive count: `").append(archiveCount).append("`\n");
        if (latest != null) {
            builder.append("- Closeout archive: `").append(latest.id()).append("`\n");
            builder.append("- Launch evidence archive: `").append(valueOrNone(latest.latestArchiveId())).append("`\n");
            builder.append("- Delivery receipt: `").append(valueOrNone(latest.latestDeliveryReceiptId())).append("`\n");
            builder.append("- Session: `").append(valueOrNone(latest.sessionId())).append("`\n");
            builder.append("- Pull Request: ").append(valueOrNone(latest.latestPullRequestUrl())).append("\n");
            builder.append("- Webhook delivery: `").append(valueOrNone(latest.latestWebhookDeliveryId())).append("`\n");
            builder.append("- Evaluation run: `").append(valueOrNone(latest.evaluationRunId())).append("`\n");
            builder.append("- Delivery target: `").append(valueOrNone(latest.latestDeliveryTarget())).append("`\n");
            builder.append("- Delivery channel: `").append(valueOrNone(latest.latestDeliveryChannel())).append("`\n");
            builder.append("- Delivery receipt freshness: `").append(valueOrNone(latest.deliveryReceiptFreshness())).append("`\n");
            builder.append("- Archived at: `").append(latest.createdAt()).append("`\n");
        }
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendList(builder, "## Download Actions", downloadActions);
        builder.append("## Side Effect Contract\n\n");
        builder.append("GET /api/demo/launch-acceptance-certificate is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.\n");
        return builder.toString();
    }

    private static void appendList(StringBuilder builder, String heading, List<String> items) {
        builder.append(heading).append("\n\n");
        for (String item : items) {
            builder.append("- ").append(item).append("\n");
        }
        builder.append("\n");
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
