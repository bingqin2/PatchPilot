package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoFinalReviewerHandoffPackageService {

    private static final int MAX_ARCHIVES = 20;
    private static final String SIDE_EFFECT_CONTRACT =
            "GET /api/demo/final-reviewer-handoff-package is read-only: "
                    + "it does not create tasks, call the model, run tests, archive records, record receipts, "
                    + "mutate Git, send messages, or write to GitHub.";

    private final Supplier<List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo>> certificateArchivesSupplier;
    private final Clock clock;

    @Autowired
    public DemoFinalReviewerHandoffPackageService(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository certificateArchiveRepository
    ) {
        this(() -> certificateArchiveRepository.listRecentArchives(MAX_ARCHIVES), Clock.systemUTC());
    }

    DemoFinalReviewerHandoffPackageService(
            Supplier<List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo>> certificateArchivesSupplier,
            Clock clock
    ) {
        this.certificateArchivesSupplier = certificateArchivesSupplier;
        this.clock = clock;
    }

    public DemoFinalReviewerHandoffPackageVo getPackage() {
        Instant generatedAt = Instant.now(clock);
        List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> archives =
                certificateArchivesSupplier.get();
        if (archives == null || archives.isEmpty()) {
            return missingPackage(generatedAt);
        }
        return packageFrom(archives.get(0), generatedAt);
    }

    private static DemoFinalReviewerHandoffPackageVo missingPackage(Instant generatedAt) {
        DemoReadinessStatus status = DemoReadinessStatus.NEEDS_ATTENTION;
        boolean readyForReview = false;
        String summary = "No terminal release-bundle delivery certificate archive is available for reviewer handoff.";
        String nextAction =
                "Archive the certified final external-review release bundle delivery certificate, "
                        + "then download the final reviewer handoff package.";
        List<DemoFinalReviewerHandoffPackageVo.Check> checks = List.of(
                new DemoFinalReviewerHandoffPackageVo.Check(
                        "Terminal delivery certificate archive",
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No terminal release-bundle delivery certificate archive is available.",
                        nextAction
                )
        );
        List<String> evidenceNotes = List.of(
                "Final reviewer handoff is unavailable until the terminal delivery certificate is archived."
        );
        List<String> downloadActions = List.of(
                "Archive the certified terminal release-bundle delivery certificate before downloading the final reviewer handoff package."
        );
        return new DemoFinalReviewerHandoffPackageVo(
                status,
                readyForReview,
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
                null,
                List.of(),
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        readyForReview,
                        summary,
                        nextAction,
                        null,
                        List.of(),
                        checks,
                        evidenceNotes,
                        downloadActions,
                        generatedAt
                ),
                generatedAt
        );
    }

    private static DemoFinalReviewerHandoffPackageVo packageFrom(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            Instant generatedAt
    ) {
        boolean readyForReview = archive.status() == DemoReadinessStatus.READY
                && archive.certified()
                && archive.releaseBundleDeliveryReceiptFresh()
                && hasText(archive.latestReleaseBundleArchiveId());
        DemoReadinessStatus status = status(archive, readyForReview);
        String summary = summary(archive, readyForReview, status);
        String nextAction = nextAction(archive, readyForReview, status);
        List<String> requiredAttachments = requiredAttachments(archive, readyForReview);
        List<DemoFinalReviewerHandoffPackageVo.Check> checks = checks(archive);
        List<String> evidenceNotes = evidenceNotes(archive, readyForReview);
        List<String> downloadActions = downloadActions(archive, readyForReview);
        return new DemoFinalReviewerHandoffPackageVo(
                status,
                readyForReview,
                summary,
                nextAction,
                archive.id(),
                archive.latestDeliveryFinalizationArchiveId(),
                archive.latestReleaseBundleArchiveId(),
                archive.latestDeliveryReceiptId(),
                archive.latestCertificateArchiveId(),
                archive.latestPackageArchiveId(),
                archive.latestPackageDeliveryReceiptId(),
                archive.latestTaskId(),
                archive.latestPullRequestUrl(),
                archive.latestDeliveryTarget(),
                archive.latestDeliveryChannel(),
                archive.latestDeliveredAt(),
                archive.archivedAt(),
                requiredAttachments,
                checks,
                evidenceNotes,
                downloadActions,
                SIDE_EFFECT_CONTRACT,
                markdownReport(
                        status,
                        readyForReview,
                        summary,
                        nextAction,
                        archive,
                        requiredAttachments,
                        checks,
                        evidenceNotes,
                        downloadActions,
                        generatedAt
                ),
                generatedAt
        );
    }

    private static DemoReadinessStatus status(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            boolean readyForReview
    ) {
        if (readyForReview) {
            return DemoReadinessStatus.READY;
        }
        if (archive.status() == DemoReadinessStatus.BLOCKED) {
            return DemoReadinessStatus.BLOCKED;
        }
        return DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String summary(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            boolean readyForReview,
            DemoReadinessStatus status
    ) {
        if (readyForReview) {
            return "Final reviewer handoff package is ready from the latest terminal delivery certificate archive.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return "Final reviewer handoff package is blocked by the latest terminal delivery certificate archive.";
        }
        if (!archive.certified()) {
            return "Final reviewer handoff package is waiting for a certified terminal delivery certificate archive.";
        }
        if (!archive.releaseBundleDeliveryReceiptFresh()) {
            return "Final reviewer handoff package is waiting for fresh release-bundle delivery proof.";
        }
        return "Final reviewer handoff package is waiting for a frozen release bundle archive.";
    }

    private static String nextAction(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            boolean readyForReview,
            DemoReadinessStatus status
    ) {
        if (readyForReview) {
            return "Send the handoff package report and listed attachments to the external reviewer.";
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            return archive.nextAction();
        }
        if (!archive.certified()) {
            return "Archive a certified final external-review release bundle delivery certificate.";
        }
        if (!archive.releaseBundleDeliveryReceiptFresh()) {
            return "Record fresh release-bundle delivery proof, archive the certificate again, then download the final reviewer handoff package.";
        }
        return "Archive the READY final external-review release bundle before downloading the final reviewer handoff package.";
    }

    private static List<String> requiredAttachments(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            boolean readyForReview
    ) {
        if (!readyForReview) {
            return List.of();
        }
        List<String> attachments = new ArrayList<>();
        attachments.add("Final reviewer handoff package report.");
        attachments.add("Terminal release-bundle delivery certificate archive " + archive.id() + ".");
        if (hasText(archive.latestReleaseBundleArchiveId())) {
            attachments.add("Frozen release bundle archive " + archive.latestReleaseBundleArchiveId() + ".");
        }
        if (hasText(archive.latestDeliveryFinalizationArchiveId())) {
            attachments.add("Release-bundle delivery finalization archive "
                    + archive.latestDeliveryFinalizationArchiveId() + ".");
        }
        if (hasText(archive.latestDeliveryReceiptId())) {
            attachments.add("Release-bundle delivery receipt " + archive.latestDeliveryReceiptId() + ".");
        }
        if (hasText(archive.latestCertificateArchiveId())) {
            attachments.add("Package-level delivery certificate archive " + archive.latestCertificateArchiveId() + ".");
        }
        if (hasText(archive.latestPackageArchiveId())) {
            attachments.add("Final external-review package archive " + archive.latestPackageArchiveId() + ".");
        }
        return List.copyOf(attachments);
    }

    private static List<DemoFinalReviewerHandoffPackageVo.Check> checks(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive
    ) {
        return List.of(
                new DemoFinalReviewerHandoffPackageVo.Check(
                        "Terminal delivery certificate archive",
                        archive.status() == DemoReadinessStatus.READY && archive.certified()
                                ? DemoReadinessStatus.READY
                                : archive.status(),
                        archive.certified()
                                ? "Latest terminal certificate archive is certified."
                                : "Latest terminal certificate archive is not certified.",
                        archive.certified() ? "No action needed." : "Archive a certified terminal certificate."
                ),
                new DemoFinalReviewerHandoffPackageVo.Check(
                        "Frozen release bundle archive",
                        hasText(archive.latestReleaseBundleArchiveId())
                                ? DemoReadinessStatus.READY
                                : DemoReadinessStatus.NEEDS_ATTENTION,
                        hasText(archive.latestReleaseBundleArchiveId())
                                ? "Latest terminal certificate archive points to a frozen release bundle archive."
                                : "Latest terminal certificate archive has no release bundle archive id.",
                        hasText(archive.latestReleaseBundleArchiveId())
                                ? "No action needed."
                                : "Archive the final external-review release bundle."
                ),
                new DemoFinalReviewerHandoffPackageVo.Check(
                        "Release-bundle delivery receipt",
                        archive.releaseBundleDeliveryReceiptFresh()
                                ? DemoReadinessStatus.READY
                                : DemoReadinessStatus.NEEDS_ATTENTION,
                        archive.releaseBundleDeliveryReceiptFresh()
                                ? "Latest terminal certificate archive contains fresh release-bundle delivery proof."
                                : "Latest terminal certificate archive does not contain fresh release-bundle delivery proof.",
                        archive.releaseBundleDeliveryReceiptFresh()
                                ? "No action needed."
                                : "Record fresh release-bundle delivery proof and archive the certificate again."
                ),
                new DemoFinalReviewerHandoffPackageVo.Check(
                        "Pull Request evidence",
                        hasText(archive.latestPullRequestUrl())
                                ? DemoReadinessStatus.READY
                                : DemoReadinessStatus.NEEDS_ATTENTION,
                        hasText(archive.latestPullRequestUrl())
                                ? "Latest terminal certificate archive includes Pull Request evidence."
                                : "Latest terminal certificate archive has no Pull Request URL.",
                        hasText(archive.latestPullRequestUrl())
                                ? "No action needed."
                                : "Complete a task that opens a Pull Request before reviewer handoff."
                )
        );
    }

    private static List<String> evidenceNotes(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            boolean readyForReview
    ) {
        List<String> notes = new ArrayList<>();
        notes.add("Terminal certificate archive " + archive.id()
                + (archive.certified() ? " is certified." : " is not certified."));
        if (hasText(archive.latestReleaseBundleArchiveId())) {
            notes.add("Release bundle archive " + archive.latestReleaseBundleArchiveId()
                    + " is the frozen reviewer payload.");
        }
        if (hasText(archive.latestDeliveryReceiptId())) {
            notes.add("Release-bundle delivery receipt " + archive.latestDeliveryReceiptId()
                    + " is marked " + archive.releaseBundleDeliveryReceiptFreshness() + ".");
        }
        if (hasText(archive.latestCertificateArchiveId())) {
            notes.add("Package-level certificate archive " + archive.latestCertificateArchiveId()
                    + " remains linked for reviewer traceability.");
        }
        if (hasText(archive.latestPullRequestUrl())) {
            notes.add("Pull Request " + archive.latestPullRequestUrl() + " is included for external review.");
        }
        if (!readyForReview) {
            notes.add("Do not send the final reviewer handoff package until all checks are READY.");
        }
        return List.copyOf(notes);
    }

    private static List<String> downloadActions(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            boolean readyForReview
    ) {
        List<String> actions = new ArrayList<>();
        actions.add("Download final reviewer handoff package report.");
        actions.add("Download final external-review release bundle delivery certificate archive " + archive.id() + ".");
        if (hasText(archive.latestDeliveryFinalizationArchiveId())) {
            actions.add("Download final external-review release bundle delivery finalization archive "
                    + archive.latestDeliveryFinalizationArchiveId() + ".");
        }
        if (hasText(archive.latestReleaseBundleArchiveId())) {
            actions.add("Download final external-review release bundle archive "
                    + archive.latestReleaseBundleArchiveId() + ".");
        }
        if (hasText(archive.latestDeliveryReceiptId())) {
            actions.add("Download final external-review release bundle delivery receipt "
                    + archive.latestDeliveryReceiptId() + ".");
        }
        if (hasText(archive.latestPullRequestUrl())) {
            actions.add("Open Pull Request " + archive.latestPullRequestUrl() + " for external review.");
        }
        if (!readyForReview) {
            actions.add("Do not send this package until readyForReview is true.");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            boolean readyForReview,
            String summary,
            String nextAction,
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive,
            List<String> requiredAttachments,
            List<DemoFinalReviewerHandoffPackageVo.Check> checks,
            List<String> evidenceNotes,
            List<String> downloadActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Final Reviewer Handoff Package\n\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Ready for review: `").append(readyForReview).append("`\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Next action: ").append(nextAction).append("\n");
        if (archive != null) {
            builder.append("- Terminal certificate archive: `").append(archive.id()).append("`\n");
            builder.append("- Release bundle archive: `")
                    .append(valueOrNone(archive.latestReleaseBundleArchiveId()))
                    .append("`\n");
            builder.append("- Release-bundle delivery finalization archive: `")
                    .append(valueOrNone(archive.latestDeliveryFinalizationArchiveId()))
                    .append("`\n");
            builder.append("- Release-bundle delivery receipt: `")
                    .append(valueOrNone(archive.latestDeliveryReceiptId()))
                    .append("`\n");
            builder.append("- Package-level certificate archive: `")
                    .append(valueOrNone(archive.latestCertificateArchiveId()))
                    .append("`\n");
            builder.append("- Package archive: `")
                    .append(valueOrNone(archive.latestPackageArchiveId()))
                    .append("`\n");
            builder.append("- Task: `").append(valueOrNone(archive.latestTaskId())).append("`\n");
            builder.append("- Pull Request: ").append(valueOrNone(archive.latestPullRequestUrl())).append("\n");
            builder.append("- Delivery target: `").append(valueOrNone(archive.latestDeliveryTarget())).append("`\n");
            builder.append("- Delivery channel: `").append(valueOrNone(archive.latestDeliveryChannel())).append("`\n");
            builder.append("- Delivered at: `").append(valueOrNone(archive.latestDeliveredAt())).append("`\n");
            builder.append("- Archived at: `").append(archive.archivedAt()).append("`\n");
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
            List<DemoFinalReviewerHandoffPackageVo.Check> checks
    ) {
        builder.append("## Checks\n\n");
        for (DemoFinalReviewerHandoffPackageVo.Check check : checks) {
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
