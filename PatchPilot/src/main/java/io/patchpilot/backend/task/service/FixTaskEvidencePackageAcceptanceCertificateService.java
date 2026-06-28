package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixTaskEvidencePackageAcceptanceCertificateService {

    private static final int MAX_ARCHIVES = 20;
    private static final String READY = "READY";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String MISSING = "MISSING";

    private final FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository closeoutArchiveRepository;
    private final Clock clock;

    @Autowired
    public FixTaskEvidencePackageAcceptanceCertificateService(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository closeoutArchiveRepository
    ) {
        this(closeoutArchiveRepository, Clock.systemUTC());
    }

    FixTaskEvidencePackageAcceptanceCertificateService(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository closeoutArchiveRepository,
            Clock clock
    ) {
        this.closeoutArchiveRepository = closeoutArchiveRepository;
        this.clock = clock;
    }

    public FixTaskEvidencePackageAcceptanceCertificateVo getCertificate() {
        List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> archives =
                closeoutArchiveRepository.listRecentArchives(MAX_ARCHIVES);
        Instant generatedAt = Instant.now(clock);
        if (archives.isEmpty()) {
            return missingCertificate(generatedAt);
        }
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo latest = archives.get(0);
        boolean certified = READY.equals(latest.status()) && latest.accepted();
        String status = certified ? READY : NEEDS_ATTENTION;
        String summary = certified
                ? "Task evidence acceptance is certified from the latest accepted closeout archive."
                : "Latest task evidence acceptance closeout archive is not accepted.";
        String nextAction = certified
                ? "Share the certificate and archived closeout report with reviewers."
                : "Resolve closeout blockers, archive a READY accepted closeout, then download the certificate.";
        List<String> downloadActions = certified
                ? certifiedDownloadActions(latest)
                : List.of("Archive a READY task evidence acceptance closeout before sharing a certificate.");
        return new FixTaskEvidencePackageAcceptanceCertificateVo(
                status,
                certified,
                summary,
                nextAction,
                archives.size(),
                latest.id(),
                latest.latestArchiveId(),
                latest.latestDeliveryReceiptId(),
                latest.latestTaskId(),
                latest.latestPullRequestUrl(),
                latest.latestDeliveryTarget(),
                latest.latestDeliveryChannel(),
                latest.deliveryReceiptFreshness(),
                latest.createdAt(),
                generatedAt,
                downloadActions,
                markdownReport(status, certified, summary, nextAction, archives.size(), latest, downloadActions, generatedAt)
        );
    }

    public String report() {
        return getCertificate().markdownReport();
    }

    private static FixTaskEvidencePackageAcceptanceCertificateVo missingCertificate(Instant generatedAt) {
        String summary = "No task evidence acceptance closeout archive is available for certification.";
        String nextAction = "Archive an accepted task evidence closeout before downloading the certificate.";
        List<String> downloadActions = List.of("Archive a READY task evidence acceptance closeout before sharing a certificate.");
        return new FixTaskEvidencePackageAcceptanceCertificateVo(
                NEEDS_ATTENTION,
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
                MISSING,
                null,
                generatedAt,
                downloadActions,
                markdownReport(NEEDS_ATTENTION, false, summary, nextAction, 0, null, downloadActions, generatedAt)
        );
    }

    private static List<String> certifiedDownloadActions(FixTaskEvidencePackageAcceptanceCloseoutArchiveVo latest) {
        List<String> actions = new ArrayList<>();
        actions.add("Download task evidence acceptance certificate.");
        actions.add("Download task evidence acceptance closeout archive " + latest.id() + ".");
        if (latest.latestPullRequestUrl() != null) {
            actions.add("Open Pull Request " + latest.latestPullRequestUrl() + " for review.");
        }
        return actions;
    }

    private static String markdownReport(
            String status,
            boolean certified,
            String summary,
            String nextAction,
            int archiveCount,
            FixTaskEvidencePackageAcceptanceCloseoutArchiveVo latest,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Task Evidence Acceptance Certificate\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Certified: `").append(certified).append("`\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        builder.append("- Archive count: `").append(archiveCount).append("`\n");
        if (latest != null) {
            builder.append("- Closeout archive: `").append(latest.id()).append("`\n");
            builder.append("- Task evidence archive: `").append(valueOrNone(latest.latestArchiveId())).append("`\n");
            builder.append("- Delivery receipt: `").append(valueOrNone(latest.latestDeliveryReceiptId())).append("`\n");
            builder.append("- Task: `").append(valueOrNone(latest.latestTaskId())).append("`\n");
            builder.append("- Pull Request: ").append(valueOrNone(latest.latestPullRequestUrl())).append("\n");
            builder.append("- Delivery target: `").append(valueOrNone(latest.latestDeliveryTarget())).append("`\n");
            builder.append("- Delivery channel: `").append(valueOrNone(latest.latestDeliveryChannel())).append("`\n");
            builder.append("- Delivery receipt freshness: `").append(valueOrNone(latest.deliveryReceiptFreshness())).append("`\n");
            builder.append("- Archived at: `").append(latest.createdAt()).append("`\n");
        }
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendList(builder, "## Download Actions", downloadActions);
        builder.append("## Side Effect Contract\n\n");
        builder.append("GET /api/tasks/evidence-packages/acceptance-certificate is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.\n");
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
