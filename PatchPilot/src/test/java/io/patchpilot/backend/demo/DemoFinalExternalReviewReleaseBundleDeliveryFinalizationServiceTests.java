package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleDeliveryFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-29T14:00:00Z"), ZoneOffset.UTC);

    @Test
    void finalizes_release_bundle_delivery_when_latest_receipt_matches_latest_archive() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
                        () -> List.of(readyArchive()),
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary()).isEqualTo(
                "Final external-review release bundle delivery is finalized with a fresh release-bundle receipt."
        );
        assertThat(finalization.nextAction()).isEqualTo(
                "Use the release bundle delivery finalization report as the terminal reviewer handoff record."
        );
        assertThat(finalization.latestArchiveId()).isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(finalization.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(finalization.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(finalization.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(finalization.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(finalization.latestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(finalization.latestTaskId()).isEqualTo("task-1");
        assertThat(finalization.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(finalization.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(finalization.latestDeliveryChannel()).isEqualTo("email");
        assertThat(finalization.releaseBundleDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.releaseBundleDeliveryReceiptFresh()).isTrue();
        assertThat(finalization.releaseBundleDeliveryReceiptFreshnessSummary())
                .isEqualTo("Latest release bundle delivery receipt matches the current frozen final external-review release bundle.");
        assertThat(finalization.checks())
                .extracting(DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check::name)
                .containsExactly("Frozen final external-review release bundle", "Release bundle delivery receipt");
        assertThat(finalization.evidenceNotes()).contains(
                "Frozen final external-review release bundle final-external-review-release-bundle-archive-1 is ready.",
                "Release bundle delivery receipt final-external-review-release-bundle-delivery-receipt-1 is fresh."
        );
        assertThat(finalization.downloadActions()).contains(
                "Download final external-review release bundle delivery finalization report.",
                "Download final external-review release bundle delivery receipt final-external-review-release-bundle-delivery-receipt-1."
        );
        assertThat(finalization.sideEffectContract()).contains("read-only");
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Final External Review Release Bundle Delivery Finalization")
                .contains("- Status: `READY`")
                .contains("- Release bundle delivery receipt freshness: `FRESH`")
                .contains("final-external-review-release-bundle-delivery-receipt-1")
                .contains("does not create tasks, call the model, run tests");
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-06-29T14:00:00Z"));
    }

    @Test
    void needs_attention_when_ready_archive_has_no_delivery_receipt() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
                        () -> List.of(readyArchive()),
                        List::of,
                        CLOCK
                );

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.releaseBundleDeliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.releaseBundleDeliveryReceiptFresh()).isFalse();
        assertThat(finalization.latestDeliveryReceiptId()).isNull();
        assertThat(finalization.nextAction()).isEqualTo(
                "Deliver the frozen final external-review release bundle, record a release-bundle delivery receipt, then download the finalization report."
        );
        assertThat(finalization.evidenceNotes())
                .contains("No final external-review release bundle delivery receipt is available.");
    }

    @Test
    void needs_attention_when_latest_delivery_receipt_is_stale_for_current_archive() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
                        () -> List.of(readyArchive()),
                        () -> List.of(staleReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.releaseBundleDeliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.releaseBundleDeliveryReceiptFresh()).isFalse();
        assertThat(finalization.releaseBundleDeliveryReceiptFreshnessSummary())
                .contains("does not match the current frozen final external-review release bundle");
        assertThat(finalization.nextAction()).isEqualTo(
                "Record a new final external-review release bundle delivery receipt for release bundle archive final-external-review-release-bundle-archive-1."
        );
        assertThat(finalization.evidenceNotes()).contains(
                "Latest release bundle delivery receipt final-external-review-release-bundle-delivery-receipt-old is stale."
        );
    }

    @Test
    void blocks_finalization_when_latest_archive_is_not_ready() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
                        () -> List.of(blockedArchive()),
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.releaseBundleDeliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(finalization.releaseBundleDeliveryReceiptFresh()).isFalse();
        assertThat(finalization.summary()).isEqualTo(
                "Final external-review release bundle delivery finalization is blocked because the latest release bundle archive is not ready."
        );
        assertThat(finalization.nextAction()).isEqualTo(
                "Share the release bundle report and listed attachments with external reviewers."
        );
        assertThat(finalization.evidenceNotes())
                .contains("Frozen final external-review release bundle is not ready.");
    }

    @Test
    void blocks_finalization_when_no_archive_exists() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService(
                        List::of,
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.latestArchiveId()).isNull();
        assertThat(finalization.releaseBundleDeliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(finalization.nextAction()).isEqualTo(
                "Archive the READY final external-review release bundle before recording delivery finalization."
        );
    }

    private static DemoFinalExternalReviewReleaseBundleArchiveVo readyArchive() {
        return DemoFinalExternalReviewReleaseBundleDeliveryReceiptServiceTests.archive(
                "final-external-review-release-bundle-archive-1",
                DemoReadinessStatus.READY,
                true
        );
    }

    private static DemoFinalExternalReviewReleaseBundleArchiveVo blockedArchive() {
        return DemoFinalExternalReviewReleaseBundleDeliveryReceiptServiceTests.archive(
                "final-external-review-release-bundle-archive-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                false
        );
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo freshReceipt() {
        return receipt(
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8"
        );
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo staleReceipt() {
        return receipt(
                "final-external-review-release-bundle-delivery-receipt-old",
                "final-external-review-release-bundle-archive-old",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8"
        );
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt(
            String id,
            String archiveId,
            String certificateArchiveId,
            String deliveryFinalizationArchiveId,
            String packageArchiveId,
            String packageDeliveryReceiptId,
            String taskId,
            String pullRequestUrl
    ) {
        return new DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                archiveId,
                certificateArchiveId,
                deliveryFinalizationArchiveId,
                packageArchiveId,
                packageDeliveryReceiptId,
                taskId,
                pullRequestUrl,
                "PatchPilot final external-review release bundle is ready.",
                "Share the release bundle report and listed attachments with external reviewers.",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent frozen final release bundle to reviewers.",
                Instant.parse("2026-06-29T13:25:00Z"),
                Instant.parse("2026-06-29T13:30:00Z"),
                "# PatchPilot Final External Review Release Bundle Delivery Receipt"
        );
    }
}
