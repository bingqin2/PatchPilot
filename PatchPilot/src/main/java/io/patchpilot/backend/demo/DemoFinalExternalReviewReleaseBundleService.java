package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewDeliveryCertificateArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleService {

    private static final int MAX_ARCHIVES = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-external-review-release-bundle is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<List<DemoFinalExternalReviewDeliveryCertificateArchiveVo>> archiveSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleService(
            DemoFinalExternalReviewDeliveryCertificateArchiveRepository archiveRepository
    ) {
        this(() -> archiveRepository.listRecentArchives(MAX_ARCHIVES), Clock.systemUTC());
    }

    DemoFinalExternalReviewReleaseBundleService(
            Supplier<List<DemoFinalExternalReviewDeliveryCertificateArchiveVo>> archiveSupplier,
            Clock clock
    ) {
        this.archiveSupplier = archiveSupplier;
        this.clock = clock;
    }

    public DemoFinalExternalReviewReleaseBundleVo getReleaseBundle() {
        List<DemoFinalExternalReviewDeliveryCertificateArchiveVo> archives = archiveSupplier.get();
        archives = archives == null ? List.of() : archives;
        Instant generatedAt = Instant.now(clock);
        if (archives.isEmpty()) {
            return missingReleaseBundle(generatedAt);
        }
        DemoFinalExternalReviewDeliveryCertificateArchiveVo latest = archives.get(0);
        return releaseBundleFrom(latest, generatedAt);
    }

    private static DemoFinalExternalReviewReleaseBundleVo missingReleaseBundle(Instant generatedAt) {
        DemoReadinessStatus status = DemoReadinessStatus.NEEDS_ATTENTION;
        boolean releaseReady = false;
        String summary = "No final external-review delivery certificate archive is available for release.";
        String nextAction =
                "Archive the certified final external-review delivery certificate, then download the release bundle.";
        List<DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck> checks = List.of(
                new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Final delivery certificate archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No certified final external-review delivery certificate archive is available.",
                        nextAction
                )
        );
        List<String> evidenceNotes = List.of(
                "The final external-review release bundle is unavailable until a certified certificate archive exists."
        );
        List<String> downloadActions = List.of(
                "Archive the certified final external-review delivery certificate before downloading the release bundle."
        );
        return new DemoFinalExternalReviewReleaseBundleVo(
                status,
                releaseReady,
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
                generatedAt,
                List.of(),
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        releaseReady,
                        summary,
                        nextAction,
                        null,
                        List.of(),
                        checks,
                        evidenceNotes,
                        downloadActions,
                        generatedAt
                )
        );
    }

    private static DemoFinalExternalReviewReleaseBundleVo releaseBundleFrom(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            Instant generatedAt
    ) {
        boolean releaseReady = latest.status() == DemoReadinessStatus.READY
                && latest.certified()
                && latest.deliveryReceiptFresh();
        DemoReadinessStatus status = status(latest, releaseReady);
        String summary = summary(latest, releaseReady, status);
        String nextAction = nextAction(latest, releaseReady, status);
        List<String> requiredAttachments = requiredAttachments(latest, releaseReady);
        List<DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck> checks = releaseChecks(latest);
        List<String> evidenceNotes = evidenceNotes(latest, releaseReady);
        List<String> downloadActions = downloadActions(latest, releaseReady);
        return new DemoFinalExternalReviewReleaseBundleVo(
                status,
                releaseReady,
                summary,
                nextAction,
                latest.id(),
                latest.latestDeliveryFinalizationArchiveId(),
                latest.latestPackageArchiveId(),
                latest.latestDeliveryReceiptId(),
                latest.latestTaskId(),
                latest.latestPullRequestUrl(),
                latest.latestDeliveryTarget(),
                latest.latestDeliveryChannel(),
                latest.latestDeliveredAt(),
                latest.archivedAt(),
                generatedAt,
                requiredAttachments,
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        releaseReady,
                        summary,
                        nextAction,
                        latest,
                        requiredAttachments,
                        checks,
                        evidenceNotes,
                        downloadActions,
                        generatedAt
                )
        );
    }

    private static DemoReadinessStatus status(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            boolean releaseReady
    ) {
        if (releaseReady) {
            return DemoReadinessStatus.READY;
        }
        if (latest.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            boolean releaseReady,
            DemoReadinessStatus status
    ) {
        if (releaseReady) {
            return "PatchPilot final external-review release bundle is ready.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "PatchPilot final external-review release bundle is blocked by the latest certificate archive.";
        }
        if (!latest.certified()) {
            return "PatchPilot final external-review release bundle is waiting for a certified certificate archive.";
        }
        return "PatchPilot final external-review release bundle is waiting for fresh delivery proof.";
    }

    private static String nextAction(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            boolean releaseReady,
            DemoReadinessStatus status
    ) {
        if (releaseReady) {
            return "Share the release bundle report and listed attachments with external reviewers.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return latest.nextAction();
        }
        if (!latest.certified()) {
            return "Archive a certified final external-review delivery certificate.";
        }
        return "Record fresh final external-review delivery proof, archive the certified certificate again, then download the release bundle.";
    }

    private static List<String> requiredAttachments(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            boolean releaseReady
    ) {
        if (!releaseReady) {
            return List.of();
        }
        List<String> attachments = new ArrayList<>();
        attachments.add("Final external-review delivery certificate archive " + latest.id());
        if (hasText(latest.latestDeliveryFinalizationArchiveId())) {
            attachments.add("Final external-review package delivery finalization archive "
                    + latest.latestDeliveryFinalizationArchiveId());
        }
        if (hasText(latest.latestPackageArchiveId())) {
            attachments.add("Final external-review package archive " + latest.latestPackageArchiveId());
        }
        if (hasText(latest.latestDeliveryReceiptId())) {
            attachments.add("Final external-review package delivery receipt " + latest.latestDeliveryReceiptId());
        }
        return List.copyOf(attachments);
    }

    private static List<DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck> releaseChecks(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest
    ) {
        return List.of(
                new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Final delivery certificate archive",
                        latest.status() == DemoReadinessStatus.READY && latest.certified()
                                ? DemoReadinessStatus.READY
                                : latest.status(),
                        latest.certified()
                                ? "Latest final external-review delivery certificate archive is certified."
                                : "Latest final external-review delivery certificate archive is not certified.",
                        latest.certified() ? "No action needed." : "Archive a certified delivery certificate."
                ),
                new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Frozen reviewer package",
                        hasText(latest.latestPackageArchiveId())
                                ? DemoReadinessStatus.READY
                                : DemoReadinessStatus.NEEDS_ATTENTION,
                        hasText(latest.latestPackageArchiveId())
                                ? "Latest certificate archive points to a frozen reviewer package."
                                : "Latest certificate archive has no package archive id.",
                        hasText(latest.latestPackageArchiveId())
                                ? "No action needed."
                                : "Archive the final external-review evidence package."
                ),
                new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Delivery receipt",
                        latest.deliveryReceiptFresh() ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION,
                        latest.deliveryReceiptFresh()
                                ? "Latest certificate archive includes fresh delivery receipt proof."
                                : "Latest certificate archive does not include fresh delivery receipt proof.",
                        latest.deliveryReceiptFresh()
                                ? "No action needed."
                                : "Record fresh delivery proof and archive the certificate again."
                ),
                new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Pull Request evidence",
                        hasText(latest.latestPullRequestUrl())
                                ? DemoReadinessStatus.READY
                                : DemoReadinessStatus.NEEDS_ATTENTION,
                        hasText(latest.latestPullRequestUrl())
                                ? "Latest certificate archive is tied to a Pull Request."
                                : "Latest certificate archive has no Pull Request URL.",
                        hasText(latest.latestPullRequestUrl())
                                ? "No action needed."
                                : "Complete a task that opens a Pull Request."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            boolean releaseReady
    ) {
        List<String> notes = new ArrayList<>();
        String certificatePrefix = latest.certified() ? "Certified" : "Latest";
        notes.add(certificatePrefix + " final external-review delivery certificate archive " + latest.id()
                + " is the release source of truth.");
        if (hasText(latest.latestDeliveryFinalizationArchiveId())) {
            notes.add("Delivery finalization archive " + latest.latestDeliveryFinalizationArchiveId()
                    + " closes the frozen reviewer package delivery loop.");
        }
        if (hasText(latest.latestDeliveryReceiptId())) {
            notes.add("Delivery receipt " + latest.latestDeliveryReceiptId()
                    + " proves the frozen package was delivered to " + valueOrNone(latest.latestDeliveryTarget())
                    + " through " + valueOrNone(latest.latestDeliveryChannel()) + ".");
        }
        if (hasText(latest.latestPullRequestUrl())) {
            notes.add("Pull Request " + latest.latestPullRequestUrl() + " is included as reviewer evidence.");
        }
        if (!releaseReady) {
            notes.add("Do not share this release bundle until releaseReady is true.");
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            boolean releaseReady
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final external-review release bundle report.");
        actions.add("Download final external-review delivery certificate archive " + latest.id() + ".");
        if (hasText(latest.latestDeliveryFinalizationArchiveId())) {
            actions.add("Download final external-review package delivery finalization archive "
                    + latest.latestDeliveryFinalizationArchiveId() + ".");
        }
        if (hasText(latest.latestPackageArchiveId())) {
            actions.add("Download final external-review package archive " + latest.latestPackageArchiveId() + ".");
        }
        if (hasText(latest.latestDeliveryReceiptId())) {
            actions.add("Download final external-review package delivery receipt "
                    + latest.latestDeliveryReceiptId() + ".");
        }
        if (!releaseReady) {
            actions.add("Do not distribute this release bundle until releaseReady is true.");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean releaseReady,
            String summary,
            String nextAction,
            DemoFinalExternalReviewDeliveryCertificateArchiveVo latest,
            List<String> requiredAttachments,
            List<DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final External Review Release Bundle\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Release ready: `").append(releaseReady).append("`\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        if (latest != null) {
            builder.append("- Certificate archive: `").append(latest.id()).append("`\n");
            builder.append("- Delivery finalization archive: `")
                    .append(valueOrNone(latest.latestDeliveryFinalizationArchiveId()))
                    .append("`\n");
            builder.append("- Package archive: `").append(valueOrNone(latest.latestPackageArchiveId())).append("`\n");
            builder.append("- Delivery receipt: `").append(valueOrNone(latest.latestDeliveryReceiptId())).append("`\n");
            builder.append("- Task: `").append(valueOrNone(latest.latestTaskId())).append("`\n");
            builder.append("- Pull Request: ").append(valueOrNone(latest.latestPullRequestUrl())).append("\n");
            builder.append("- Delivery target: `").append(valueOrNone(latest.latestDeliveryTarget())).append("`\n");
            builder.append("- Delivery channel: `").append(valueOrNone(latest.latestDeliveryChannel())).append("`\n");
            builder.append("- Delivered at: `").append(valueOrNone(latest.latestDeliveredAt())).append("`\n");
            builder.append("- Certificate archived at: `").append(latest.archivedAt()).append("`\n");
        }
        builder.append("- Generated at: `").append(generatedAt).append("`\n\n");
        appendList(builder, "## Required Attachments", requiredAttachments);
        appendChecks(builder, checks);
        appendList(builder, "## Evidence Notes", evidenceNotes);
        appendList(builder, "## Download Actions", downloadActions);
        builder.append("## Side Effect Contract\n\n");
        builder.append(SIDE_EFFECT_CONTRACT).append("\n");
        return builder.toString();
    }

    private static void appendChecks(
            StringBuilder builder,
            List<DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck> checks
    ) {
        builder.append("## Release Checks\n\n");
        for (DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck check : checks) {
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
