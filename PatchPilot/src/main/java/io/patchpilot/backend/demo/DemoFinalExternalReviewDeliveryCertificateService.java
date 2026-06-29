package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewDeliveryCertificateService {

    private static final int MAX_ARCHIVES = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-external-review-delivery-certificate is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo>> archiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalExternalReviewDeliveryCertificateService(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository archiveRepository
    ) {
        this(() -> archiveRepository.listRecentArchives(MAX_ARCHIVES), Clock.systemUTC());
    }

    DemoFinalExternalReviewDeliveryCertificateService(
            Supplier<List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo>> archiveSupplier,
            Clock clock
    ) {
        this.archiveSupplier = archiveSupplier;
        this.clock = clock;
    }

    public DemoFinalExternalReviewDeliveryCertificateVo getCertificate() {
        List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> archives = archiveSupplier.get();
        Instant generatedAt = Instant.now(clock);
        if (archives.isEmpty()) {
            return missingCertificate(generatedAt);
        }
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest = archives.get(0);
        return certificateFrom(latest, generatedAt);
    }

    private static DemoFinalExternalReviewDeliveryCertificateVo missingCertificate(Instant generatedAt) {
        DemoReadinessStatus status = DemoReadinessStatus.NEEDS_ATTENTION;
        boolean certified = false;
        String summary = "No final external-review delivery finalization archive is available for certification.";
        String nextAction = "Archive the READY final external-review package delivery finalization, then download the certificate.";
        List<DemoFinalExternalReviewDeliveryCertificateVo.Check> checks = List.of(
                new DemoFinalExternalReviewDeliveryCertificateVo.Check(
                        "Final external-review delivery finalization archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No final external-review delivery finalization archive is available.",
                        nextAction
                )
        );
        List<String> evidenceNotes = List.of(
                "No final external-review delivery certificate can be issued until a finalized delivery archive exists."
        );
        List<String> downloadActions = List.of(
                "Archive the READY final external-review package delivery finalization before downloading a certificate."
        );
        return new DemoFinalExternalReviewDeliveryCertificateVo(
                status,
                certified,
                summary,
                nextAction,
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
                false,
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(status, certified, summary, nextAction, null, checks, evidenceNotes, downloadActions, generatedAt),
                generatedAt
        );
    }

    private static DemoFinalExternalReviewDeliveryCertificateVo certificateFrom(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            Instant generatedAt
    ) {
        boolean certified = latest.status() == DemoReadinessStatus.READY
                && latest.finalized()
                && latest.deliveryReceiptFresh();
        DemoReadinessStatus status = status(latest, certified);
        String summary = summary(latest, certified, status);
        String nextAction = nextAction(latest, certified, status);
        List<DemoFinalExternalReviewDeliveryCertificateVo.Check> checks = checks(latest);
        List<String> evidenceNotes = evidenceNotes(latest, certified);
        List<String> downloadActions = downloadActions(latest, certified);

        return new DemoFinalExternalReviewDeliveryCertificateVo(
                status,
                certified,
                summary,
                nextAction,
                latest.id(),
                latest.latestArchiveId(),
                latest.latestDeliveryReceiptId(),
                latest.latestTaskId(),
                latest.latestPullRequestUrl(),
                latest.latestDeliveryTarget(),
                latest.latestDeliveryChannel(),
                latest.latestDeliveredAt(),
                latest.archivedAt(),
                latest.deliveryReceiptFreshness(),
                latest.deliveryReceiptFresh(),
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(status, certified, summary, nextAction, latest, checks, evidenceNotes, downloadActions, generatedAt),
                generatedAt
        );
    }

    private static DemoReadinessStatus status(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            boolean certified
    ) {
        if (certified) {
            return DemoReadinessStatus.READY;
        }
        if (latest.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            boolean certified,
            DemoReadinessStatus status
    ) {
        if (certified) {
            return "Final external-review delivery is certified from the latest finalized archive.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Latest final external-review delivery finalization archive is blocked.";
        }
        if (!latest.finalized()) {
            return "Latest final external-review delivery finalization archive is not finalized.";
        }
        return "Latest final external-review delivery finalization archive does not have a fresh delivery receipt.";
    }

    private static String nextAction(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            boolean certified,
            DemoReadinessStatus status
    ) {
        if (certified) {
            return "Share the certificate report with reviewers as the final external-review delivery proof.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return latest.nextAction();
        }
        if (!latest.finalized()) {
            return "Archive a finalized READY final external-review package delivery record.";
        }
        return "Record a fresh final external-review package delivery receipt, archive the READY finalization, then download the certificate.";
    }

    private static List<DemoFinalExternalReviewDeliveryCertificateVo.Check> checks(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest
    ) {
        DemoReadinessStatus archiveStatus = latest.status() == DemoReadinessStatus.READY && latest.finalized()
                ? DemoReadinessStatus.READY
                : latest.status();
        DemoReadinessStatus receiptStatus = latest.deliveryReceiptFresh()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        DemoReadinessStatus pullRequestStatus = hasText(latest.latestPullRequestUrl())
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        return List.of(
                new DemoFinalExternalReviewDeliveryCertificateVo.Check(
                        "Final external-review delivery finalization archive",
                        archiveStatus,
                        latest.finalized()
                                ? "Latest final external-review delivery finalization archive is finalized."
                                : "Latest final external-review delivery finalization archive is not finalized.",
                        latest.finalized() ? "No action needed." : "Archive a READY finalized delivery record."
                ),
                new DemoFinalExternalReviewDeliveryCertificateVo.Check(
                        "Final external-review package delivery receipt",
                        receiptStatus,
                        latest.deliveryReceiptFresh()
                                ? "Latest package delivery receipt is fresh for the frozen package."
                                : "Latest package delivery receipt is missing or stale.",
                        latest.deliveryReceiptFresh()
                                ? "No action needed."
                                : "Record a fresh delivery receipt and archive the READY finalization again."
                ),
                new DemoFinalExternalReviewDeliveryCertificateVo.Check(
                        "Final external-review Pull Request",
                        pullRequestStatus,
                        hasText(latest.latestPullRequestUrl())
                                ? "Latest final external-review delivery archive is tied to a Pull Request."
                                : "Latest final external-review delivery archive has no Pull Request URL.",
                        hasText(latest.latestPullRequestUrl()) ? "No action needed." : "Complete a task that opens a Pull Request."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            boolean certified
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Final external-review delivery finalization archive " + latest.id() + " is "
                + (latest.finalized() ? "finalized." : "not finalized."));
        if (hasText(latest.latestDeliveryReceiptId())) {
            notes.add((latest.deliveryReceiptFresh() ? "Fresh" : "Non-fresh")
                    + " delivery receipt " + latest.latestDeliveryReceiptId()
                    + " proves the frozen package was delivered.");
        } else {
            notes.add("No final external-review delivery receipt is recorded in the latest archive.");
        }
        if (hasText(latest.latestPullRequestUrl())) {
            notes.add("Pull Request " + latest.latestPullRequestUrl() + " is included as review evidence.");
        }
        if (!certified) {
            notes.add("Certificate remains non-certified until the latest archive is READY, finalized, and receipt-fresh.");
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            boolean certified
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final external-review delivery certificate report.");
        actions.add("Download final external-review package delivery finalization archive " + latest.id() + ".");
        if (hasText(latest.latestArchiveId())) {
            actions.add("Download final external-review package archive " + latest.latestArchiveId() + ".");
        }
        if (hasText(latest.latestDeliveryReceiptId())) {
            actions.add("Download final external-review package delivery receipt "
                    + latest.latestDeliveryReceiptId() + ".");
        }
        if (hasText(latest.latestPullRequestUrl())) {
            actions.add("Open Pull Request " + latest.latestPullRequestUrl() + " for external review.");
        }
        if (!certified) {
            actions.add("Do not share this certificate as final proof until certified is true.");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean certified,
            String summary,
            String nextAction,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo latest,
            List<DemoFinalExternalReviewDeliveryCertificateVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final External Review Delivery Certificate\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Certified: `").append(certified).append("`\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        if (latest != null) {
            builder.append("- Delivery finalization archive: `").append(latest.id()).append("`\n");
            builder.append("- Package archive: `").append(valueOrNone(latest.latestArchiveId())).append("`\n");
            builder.append("- Delivery receipt: `").append(valueOrNone(latest.latestDeliveryReceiptId())).append("`\n");
            builder.append("- Task: `").append(valueOrNone(latest.latestTaskId())).append("`\n");
            builder.append("- Pull Request: ").append(valueOrNone(latest.latestPullRequestUrl())).append("\n");
            builder.append("- Delivery target: `").append(valueOrNone(latest.latestDeliveryTarget())).append("`\n");
            builder.append("- Delivery channel: `").append(valueOrNone(latest.latestDeliveryChannel())).append("`\n");
            builder.append("- Delivered at: `").append(valueOrNone(latest.latestDeliveredAt())).append("`\n");
            builder.append("- Delivery receipt freshness: `")
                    .append(valueOrNone(latest.deliveryReceiptFreshness()))
                    .append("`\n");
            builder.append("- Delivery receipt fresh: `").append(latest.deliveryReceiptFresh()).append("`\n");
            builder.append("- Archived at: `").append(latest.archivedAt()).append("`\n");
        }
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendChecks(builder, checks);
        appendList(builder, "## Evidence Notes", evidenceNotes);
        appendList(builder, "## Download Actions", downloadActions);
        builder.append("## Side Effect Contract\n\n");
        builder.append(SIDE_EFFECT_CONTRACT).append("\n");
        return builder.toString();
    }

    private static void appendChecks(
            StringBuilder builder,
            List<DemoFinalExternalReviewDeliveryCertificateVo.Check> checks
    ) {
        builder.append("## Checks\n\n");
        for (DemoFinalExternalReviewDeliveryCertificateVo.Check check : checks) {
            builder.append("- `")
                    .append(check.status())
                    .append("` ")
                    .append(check.name())
                    .append(": ")
                    .append(check.summary())
                    .append(" Next: ")
                    .append(check.nextAction())
                    .append("\n");
        }
        builder.append("\n");
    }

    private static void appendList(StringBuilder builder, String heading, List<String> items) {
        builder.append(heading).append("\n\n");
        for (String item : items) {
            builder.append("- ").append(item).append("\n");
        }
        builder.append("\n");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value : "none";
    }
}
