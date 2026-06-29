package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageDeliveryFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-29T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_finalize_final_external_review_package_delivery_when_latest_receipt_matches_latest_archive() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
                        () -> List.of(readyArchive()),
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary()).isEqualTo(
                "Final external-review package delivery is finalized with a fresh package delivery receipt."
        );
        assertThat(finalization.nextAction()).isEqualTo(
                "Use the finalization report as proof that the frozen external-review package was delivered."
        );
        assertThat(finalization.latestArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(finalization.latestCloseoutArchiveId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(finalization.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(finalization.latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(finalization.latestTaskId()).isEqualTo("task-2");
        assertThat(finalization.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.deliveryReceiptFresh()).isTrue();
        assertThat(finalization.deliveryReceiptFreshnessSummary())
                .isEqualTo("Latest package delivery receipt matches the current frozen final external-review package.");
        assertThat(finalization.checks())
                .extracting(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check::name)
                .containsExactly("Frozen final external-review package", "Final external-review package delivery receipt");
        assertThat(finalization.evidenceNotes()).contains(
                "Frozen final external-review package final-external-review-package-archive-1 is ready.",
                "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1 is fresh."
        );
        assertThat(finalization.downloadActions()).contains(
                "Download final external-review package delivery finalization report.",
                "Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1."
        );
        assertThat(finalization.sideEffectContract()).contains("read-only");
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Final External Review Package Delivery Finalization")
                .contains("- Status: `READY`")
                .contains("- Delivery receipt freshness: `FRESH`")
                .contains("final-external-review-package-delivery-receipt-1")
                .contains("does not create tasks, call the model, run tests");
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-06-29T10:00:00Z"));
    }

    @Test
    void should_need_attention_when_ready_archive_has_no_delivery_receipt() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
                        () -> List.of(readyArchive()),
                        List::of,
                        CLOCK
                );

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.deliveryReceiptFresh()).isFalse();
        assertThat(finalization.latestDeliveryReceiptId()).isNull();
        assertThat(finalization.nextAction()).isEqualTo(
                "Deliver the frozen final external-review package, record a package delivery receipt, then download the finalization report."
        );
        assertThat(finalization.evidenceNotes()).contains("No final external-review package delivery receipt is available.");
    }

    @Test
    void should_need_attention_when_latest_delivery_receipt_is_stale_for_current_archive() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
                        () -> List.of(readyArchive()),
                        () -> List.of(staleReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.deliveryReceiptFresh()).isFalse();
        assertThat(finalization.deliveryReceiptFreshnessSummary())
                .contains("does not match the current frozen final external-review package");
        assertThat(finalization.nextAction()).isEqualTo(
                "Record a new final external-review package delivery receipt for package archive final-external-review-package-archive-1."
        );
        assertThat(finalization.evidenceNotes()).contains(
                "Latest final external-review package delivery receipt final-external-review-package-delivery-receipt-old is stale."
        );
    }

    @Test
    void should_block_finalization_when_latest_archive_is_not_ready() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
                        () -> List.of(blockedArchive()),
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(finalization.deliveryReceiptFresh()).isFalse();
        assertThat(finalization.summary()).isEqualTo(
                "Final external-review package delivery finalization is blocked because the latest package archive is not ready."
        );
        assertThat(finalization.nextAction()).isEqualTo(
                "Resolve final external-review package blockers before delivering the package."
        );
        assertThat(finalization.evidenceNotes()).contains("Frozen final external-review package is not ready.");
    }

    @Test
    void should_block_finalization_when_no_archive_exists() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService(
                        List::of,
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.latestArchiveId()).isNull();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(finalization.nextAction()).isEqualTo(
                "Archive the READY final external-review evidence package before recording delivery finalization."
        );
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo readyArchive() {
        return archive(
                "final-external-review-package-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Resolve final external-review package blockers before delivering the package."
        );
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo blockedArchive() {
        return archive(
                "final-external-review-package-archive-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Resolve final external-review package blockers before delivering the package."
        );
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo archive(
            String id,
            DemoReadinessStatus status,
            boolean readyForExternalReview,
            String nextAction
    ) {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                id,
                status,
                readyForExternalReview,
                "PatchPilot final external-review evidence package is ready.",
                nextAction,
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                "FRESH",
                Instant.parse("2026-06-29T08:30:00Z"),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of("Download final external-review evidence package."),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package",
                Instant.parse("2026-06-29T09:00:00Z"),
                Instant.parse("2026-06-29T09:15:00Z")
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo freshReceipt() {
        return receipt(
                "final-external-review-package-delivery-receipt-1",
                "final-external-review-package-archive-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo staleReceipt() {
        return receipt(
                "final-external-review-package-delivery-receipt-old",
                "final-external-review-package-archive-old",
                "final-acceptance-completion-closeout-archive-old",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt(
            String id,
            String archiveId,
            String closeoutArchiveId,
            String completionArchiveId,
            String completionEvidenceDeliveryReceiptId,
            String taskId,
            String pullRequestUrl
    ) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                archiveId,
                closeoutArchiveId,
                completionArchiveId,
                completionEvidenceDeliveryReceiptId,
                taskId,
                pullRequestUrl,
                "PatchPilot final external-review evidence package archive was delivered.",
                "Use the delivery receipt as proof that the frozen final external-review package was shared.",
                "email",
                "reviewer@example.com",
                "release-captain",
                "Sent to reviewer mailbox.",
                Instant.parse("2026-06-29T09:25:00Z"),
                Instant.parse("2026-06-29T09:30:00Z"),
                "# PatchPilot Final External Review Package Delivery Receipt"
        );
    }
}
