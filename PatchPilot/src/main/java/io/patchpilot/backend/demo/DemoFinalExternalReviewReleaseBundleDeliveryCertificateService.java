package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleDeliveryCertificateService {

    private static final int MAX_ARCHIVES = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo>> archiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateService(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository archiveRepository
    ) {
        this(() -> archiveRepository.listRecentArchives(MAX_ARCHIVES), Clock.systemUTC());
    }

    DemoFinalExternalReviewReleaseBundleDeliveryCertificateService(
            Supplier<List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo>> archiveSupplier,
            Clock clock
    ) {
        this.archiveSupplier = archiveSupplier;
        this.clock = clock;
    }

    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo getCertificate() {
        Instant generatedAt = Instant.now(clock);
        List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> archives = archiveSupplier.get();
        if (archives.isEmpty()) {
            return missingCertificate(generatedAt);
        }
        return certificateFrom(archives.get(0), generatedAt);
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo missingCertificate(Instant generatedAt) {
        DemoReadinessStatus status = DemoReadinessStatus.NEEDS_ATTENTION;
        boolean certified = false;
        String summary = "No final external-review release bundle delivery finalization archive is available for certification.";
        String nextAction = "Archive the READY final external-review release bundle delivery finalization, then download the certificate.";
        List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check> checks = List.of(
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check(
                        "Final external-review release bundle delivery finalization archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No final external-review release bundle delivery finalization archive is available.",
                        nextAction
                )
        );
        List<String> evidenceNotes = List.of(
                "No final external-review release bundle delivery certificate can be issued until a finalized archive exists."
        );
        List<String> downloadActions = List.of(
                "Archive the READY final external-review release bundle delivery finalization before downloading a certificate."
        );
        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo(
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

    private static DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certificateFrom(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
            Instant generatedAt
    ) {
        boolean certified = latest.status() == DemoReadinessStatus.READY
                && latest.finalized()
                && latest.releaseBundleDeliveryReceiptFresh();
        DemoReadinessStatus status = status(latest, certified);
        String summary = summary(latest, certified, status);
        String nextAction = nextAction(latest, certified, status);
        List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check> checks = checks(latest);
        List<String> evidenceNotes = evidenceNotes(latest, certified);
        List<String> downloadActions = downloadActions(latest, certified);

        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo(
                status,
                certified,
                summary,
                nextAction,
                latest.id(),
                latest.latestArchiveId(),
                latest.latestDeliveryReceiptId(),
                latest.latestCertificateArchiveId(),
                latest.latestPackageArchiveId(),
                latest.latestPackageDeliveryReceiptId(),
                latest.latestTaskId(),
                latest.latestPullRequestUrl(),
                latest.latestDeliveryTarget(),
                latest.latestDeliveryChannel(),
                latest.latestDeliveredAt(),
                latest.archivedAt(),
                latest.releaseBundleDeliveryReceiptFreshness(),
                latest.releaseBundleDeliveryReceiptFresh(),
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(status, certified, summary, nextAction, latest, checks, evidenceNotes, downloadActions, generatedAt),
                generatedAt
        );
    }

    private static DemoReadinessStatus status(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
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
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
            boolean certified,
            DemoReadinessStatus status
    ) {
        if (certified) {
            return "Final external-review release bundle delivery is certified from the latest finalized archive.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Latest final external-review release bundle delivery finalization archive is blocked.";
        }
        if (!latest.finalized()) {
            return "Latest final external-review release bundle delivery finalization archive is not finalized.";
        }
        return "Latest final external-review release bundle delivery finalization archive does not have a fresh release bundle delivery receipt.";
    }

    private static String nextAction(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
            boolean certified,
            DemoReadinessStatus status
    ) {
        if (certified) {
            return "Share the release bundle delivery certificate report as the terminal reviewer handoff proof.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return latest.nextAction();
        }
        if (!latest.finalized()) {
            return "Archive a finalized READY final external-review release bundle delivery record.";
        }
        return "Record a fresh final external-review release bundle delivery receipt, archive the READY finalization, then download the certificate.";
    }

    private static List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check> checks(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest
    ) {
        DemoReadinessStatus archiveStatus = latest.status() == DemoReadinessStatus.READY && latest.finalized()
                ? DemoReadinessStatus.READY
                : latest.status();
        DemoReadinessStatus receiptStatus = latest.releaseBundleDeliveryReceiptFresh()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        DemoReadinessStatus releaseBundleArchiveStatus = hasText(latest.latestArchiveId())
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        DemoReadinessStatus pullRequestStatus = hasText(latest.latestPullRequestUrl())
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        return List.of(
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check(
                        "Final external-review release bundle delivery finalization archive",
                        archiveStatus,
                        latest.finalized()
                                ? "Latest release bundle delivery finalization archive is finalized."
                                : "Latest release bundle delivery finalization archive is not finalized.",
                        latest.finalized() ? "No action needed." : "Archive a READY finalized release bundle delivery record."
                ),
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check(
                        "Final external-review release bundle delivery receipt",
                        receiptStatus,
                        latest.releaseBundleDeliveryReceiptFresh()
                                ? "Latest release bundle delivery receipt is fresh for the frozen release bundle."
                                : "Latest release bundle delivery receipt is missing or stale.",
                        latest.releaseBundleDeliveryReceiptFresh()
                                ? "No action needed."
                                : "Record a fresh release bundle delivery receipt and archive the READY finalization again."
                ),
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check(
                        "Final external-review release bundle archive",
                        releaseBundleArchiveStatus,
                        hasText(latest.latestArchiveId())
                                ? "Latest frozen release bundle archive is present."
                                : "Latest frozen release bundle archive is missing.",
                        hasText(latest.latestArchiveId()) ? "No action needed." : "Archive the READY final external-review release bundle."
                ),
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check(
                        "Final external-review Pull Request",
                        pullRequestStatus,
                        hasText(latest.latestPullRequestUrl())
                                ? "Latest release bundle delivery finalization archive is tied to a Pull Request."
                                : "Latest release bundle delivery finalization archive has no Pull Request URL.",
                        hasText(latest.latestPullRequestUrl()) ? "No action needed." : "Complete a task that opens a Pull Request."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
            boolean certified
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Release bundle delivery finalization archive " + latest.id() + " is "
                + (latest.finalized() ? "finalized." : "not finalized."));
        if (hasText(latest.latestDeliveryReceiptId())) {
            notes.add((latest.releaseBundleDeliveryReceiptFresh() ? "Fresh" : "Non-fresh")
                    + " release bundle delivery receipt " + latest.latestDeliveryReceiptId()
                    + " proves the frozen release bundle was delivered.");
        } else {
            notes.add("No final external-review release bundle delivery receipt is recorded in the latest archive.");
        }
        if (hasText(latest.latestArchiveId())) {
            notes.add("Release bundle archive " + latest.latestArchiveId() + " is the frozen payload.");
        }
        if (hasText(latest.latestCertificateArchiveId())) {
            notes.add("Delivery certificate archive " + latest.latestCertificateArchiveId()
                    + " remains the package-level proof root.");
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
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
            boolean certified
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final external-review release bundle delivery certificate report.");
        actions.add("Download final external-review release bundle delivery finalization archive " + latest.id() + ".");
        if (hasText(latest.latestArchiveId())) {
            actions.add("Download final external-review release bundle archive " + latest.latestArchiveId() + ".");
        }
        if (hasText(latest.latestDeliveryReceiptId())) {
            actions.add("Download final external-review release bundle delivery receipt "
                    + latest.latestDeliveryReceiptId() + ".");
        }
        if (hasText(latest.latestCertificateArchiveId())) {
            actions.add("Download final external-review delivery certificate archive "
                    + latest.latestCertificateArchiveId() + ".");
        }
        if (hasText(latest.latestPackageArchiveId())) {
            actions.add("Download final external-review package archive " + latest.latestPackageArchiveId() + ".");
        }
        if (hasText(latest.latestPackageDeliveryReceiptId())) {
            actions.add("Download final external-review package delivery receipt "
                    + latest.latestPackageDeliveryReceiptId() + ".");
        }
        if (hasText(latest.latestPullRequestUrl())) {
            actions.add("Open Pull Request " + latest.latestPullRequestUrl() + " for external review.");
        }
        if (!certified) {
            actions.add("Do not share this certificate as terminal proof until certified is true.");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean certified,
            String summary,
            String nextAction,
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo latest,
            List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final External Review Release Bundle Delivery Certificate\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Certified: `").append(certified).append("`\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        if (latest != null) {
            builder.append("- Release bundle delivery finalization archive: `").append(latest.id()).append("`\n");
            builder.append("- Release bundle archive: `").append(valueOrNone(latest.latestArchiveId())).append("`\n");
            builder.append("- Release bundle delivery receipt: `")
                    .append(valueOrNone(latest.latestDeliveryReceiptId()))
                    .append("`\n");
            builder.append("- Delivery certificate archive: `")
                    .append(valueOrNone(latest.latestCertificateArchiveId()))
                    .append("`\n");
            builder.append("- Package archive: `").append(valueOrNone(latest.latestPackageArchiveId())).append("`\n");
            builder.append("- Package delivery receipt: `")
                    .append(valueOrNone(latest.latestPackageDeliveryReceiptId()))
                    .append("`\n");
            builder.append("- Task: `").append(valueOrNone(latest.latestTaskId())).append("`\n");
            builder.append("- Pull Request: ").append(valueOrNone(latest.latestPullRequestUrl())).append("\n");
            builder.append("- Delivery target: `").append(valueOrNone(latest.latestDeliveryTarget())).append("`\n");
            builder.append("- Delivery channel: `").append(valueOrNone(latest.latestDeliveryChannel())).append("`\n");
            builder.append("- Delivered at: `").append(valueOrNone(latest.latestDeliveredAt())).append("`\n");
            builder.append("- Release bundle delivery receipt freshness: `")
                    .append(valueOrNone(latest.releaseBundleDeliveryReceiptFreshness()))
                    .append("`\n");
            builder.append("- Release bundle delivery receipt fresh: `")
                    .append(latest.releaseBundleDeliveryReceiptFresh())
                    .append("`\n");
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
            List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check> checks
    ) {
        builder.append("## Checks\n\n");
        for (DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check check : checks) {
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
