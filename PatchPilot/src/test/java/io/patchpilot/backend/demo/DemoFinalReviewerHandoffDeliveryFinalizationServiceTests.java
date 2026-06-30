package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalReviewerHandoffDeliveryFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-30T08:00:00Z"), ZoneOffset.UTC);

    @Test
    void finalizes_reviewer_handoff_delivery_when_latest_receipt_matches_current_package() {
        DemoFinalReviewerHandoffDeliveryFinalizationService service =
                new DemoFinalReviewerHandoffDeliveryFinalizationService(
                        () -> DemoFinalReviewerHandoffDeliveryReceiptServiceTests.readyPackage(),
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalReviewerHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary()).isEqualTo(
                "Final reviewer handoff delivery is finalized with a fresh handoff delivery receipt."
        );
        assertThat(finalization.nextAction()).isEqualTo(
                "Use the final reviewer handoff delivery finalization report as the terminal demo closeout record."
        );
        assertThat(finalization.latestDeliveryReceiptId())
                .isEqualTo("final-reviewer-handoff-delivery-receipt-1");
        assertThat(finalization.latestCertificateArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-certificate-archive-1");
        assertThat(finalization.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(finalization.latestReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(finalization.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(finalization.latestTaskId()).isEqualTo("task-1");
        assertThat(finalization.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(finalization.latestDeliveryTarget()).isEqualTo("external-reviewer@example.com");
        assertThat(finalization.latestDeliveryChannel()).isEqualTo("email");
        assertThat(finalization.handoffDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.handoffDeliveryReceiptFresh()).isTrue();
        assertThat(finalization.checks())
                .extracting(DemoFinalReviewerHandoffDeliveryFinalizationVo.Check::name)
                .containsExactly("Final reviewer handoff package", "Final reviewer handoff delivery receipt");
        assertThat(finalization.evidenceNotes()).contains(
                "Final reviewer handoff package is ready.",
                "Final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1 is fresh."
        );
        assertThat(finalization.downloadActions()).contains(
                "Download final reviewer handoff delivery finalization report.",
                "Download final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1."
        );
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Final Reviewer Handoff Delivery Finalization")
                .contains("- Status: `READY`")
                .contains("- Handoff delivery receipt freshness: `FRESH`")
                .contains("does not create tasks, call the model, run tests");
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-06-30T08:00:00Z"));
    }

    @Test
    void needs_attention_when_ready_package_has_no_delivery_receipt() {
        DemoFinalReviewerHandoffDeliveryFinalizationService service =
                new DemoFinalReviewerHandoffDeliveryFinalizationService(
                        () -> DemoFinalReviewerHandoffDeliveryReceiptServiceTests.readyPackage(),
                        List::of,
                        CLOCK
                );

        DemoFinalReviewerHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.handoffDeliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.handoffDeliveryReceiptFresh()).isFalse();
        assertThat(finalization.latestDeliveryReceiptId()).isNull();
        assertThat(finalization.nextAction()).isEqualTo(
                "Send the final reviewer handoff package, record a handoff delivery receipt, then download the finalization report."
        );
    }

    @Test
    void needs_attention_when_latest_receipt_is_stale_for_current_package() {
        DemoFinalReviewerHandoffDeliveryFinalizationService service =
                new DemoFinalReviewerHandoffDeliveryFinalizationService(
                        () -> DemoFinalReviewerHandoffDeliveryReceiptServiceTests.readyPackage(),
                        () -> List.of(staleReceipt()),
                        CLOCK
                );

        DemoFinalReviewerHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.handoffDeliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.nextAction()).isEqualTo(
                "Record a new final reviewer handoff delivery receipt for terminal certificate archive final-external-review-release-bundle-delivery-certificate-archive-1."
        );
        assertThat(finalization.evidenceNotes()).contains(
                "Latest final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-old is stale."
        );
    }

    @Test
    void blocks_finalization_when_package_is_not_ready() {
        DemoFinalReviewerHandoffDeliveryFinalizationService service =
                new DemoFinalReviewerHandoffDeliveryFinalizationService(
                        () -> DemoFinalReviewerHandoffDeliveryReceiptServiceTests.blockedPackage(),
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalReviewerHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.handoffDeliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(finalization.summary()).isEqualTo(
                "Final reviewer handoff delivery finalization is blocked because the package is not ready."
        );
    }

    private static DemoFinalReviewerHandoffDeliveryReceiptVo freshReceipt() {
        return receipt(
                "final-reviewer-handoff-delivery-receipt-1",
                "final-external-review-release-bundle-delivery-certificate-archive-1",
                "final-external-review-release-bundle-delivery-finalization-archive-1",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8"
        );
    }

    private static DemoFinalReviewerHandoffDeliveryReceiptVo staleReceipt() {
        return receipt(
                "final-reviewer-handoff-delivery-receipt-old",
                "final-external-review-release-bundle-delivery-certificate-archive-old",
                "final-external-review-release-bundle-delivery-finalization-archive-1",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8"
        );
    }

    private static DemoFinalReviewerHandoffDeliveryReceiptVo receipt(
            String id,
            String certificateArchiveId,
            String deliveryFinalizationArchiveId,
            String releaseBundleArchiveId,
            String deliveryReceiptId,
            String packageCertificateArchiveId,
            String packageArchiveId,
            String packageDeliveryReceiptId,
            String taskId,
            String pullRequestUrl
    ) {
        return new DemoFinalReviewerHandoffDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                certificateArchiveId,
                deliveryFinalizationArchiveId,
                releaseBundleArchiveId,
                deliveryReceiptId,
                packageCertificateArchiveId,
                packageArchiveId,
                packageDeliveryReceiptId,
                taskId,
                pullRequestUrl,
                "Final reviewer handoff package is ready.",
                "Send the handoff package report and listed attachments to the external reviewer.",
                "email",
                "external-reviewer@example.com",
                "local-operator",
                "Sent final reviewer handoff package.",
                Instant.parse("2026-06-30T07:25:00Z"),
                Instant.parse("2026-06-30T07:30:00Z"),
                "# PatchPilot Final Reviewer Handoff Delivery Receipt"
        );
    }
}
