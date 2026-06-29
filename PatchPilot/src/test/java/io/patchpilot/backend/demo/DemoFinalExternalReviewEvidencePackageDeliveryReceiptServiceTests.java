package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewEvidencePackageDeliveryReceiptServiceTests {

    @Test
    void records_delivery_receipt_for_latest_ready_final_external_review_package_archive() {
        InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository =
                new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository();
        archiveRepository.save(archive("final-external-review-package-archive-1", DemoReadinessStatus.READY, true));
        InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository receiptRepository =
                new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository();
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptService(
                        archiveRepository,
                        receiptRepository,
                        Clock.fixed(Instant.parse("2026-06-29T09:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-delivery-receipt-1"
                );

        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptRequestDto(
                        "email",
                        "reviewer@example.com",
                        "local-operator",
                        "Sent frozen final external-review package to the reviewer.",
                        Instant.parse("2026-06-29T09:25:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.finalExternalReviewPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.finalExternalReviewPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(receipt.closeoutArchiveId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(receipt.completionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(receipt.completionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(receipt.latestTaskId()).isEqualTo("task-2");
        assertThat(receipt.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent frozen final external-review package to the reviewer.");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-29T09:25:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-29T09:30:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Final External Review Package Delivery Receipt")
                .contains("- Final external-review package archive: `final-external-review-package-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("POST /api/demo/final-external-review-evidence-package/delivery-receipts records local evidence only")
                .contains("does not send messages, create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("final-external-review-package-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void rejects_receipt_when_no_package_archive_exists() {
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptService(
                        new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository(),
                        new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T09:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("no final external-review evidence package archive is available");
    }

    @Test
    void rejects_receipt_when_latest_archive_is_not_ready() {
        InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository =
                new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository();
        archiveRepository.save(archive(
                "final-external-review-package-archive-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                false
        ));
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptService(
                        archiveRepository,
                        new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T09:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review evidence package archive is not ready for delivery");
    }

    @Test
    void requires_delivery_fields() {
        InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository =
                new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository();
        archiveRepository.save(archive("final-external-review-package-archive-1", DemoReadinessStatus.READY, true));
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptService(
                        archiveRepository,
                        new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T09:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptRequestDto(
                        " ",
                        "reviewer@example.com",
                        "local-operator",
                        "notes",
                        Instant.parse("2026-06-29T09:25:00Z")
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deliveryChannel is required");
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptRequestDto validRequest() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptRequestDto(
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent frozen final external-review package to the reviewer.",
                Instant.parse("2026-06-29T09:25:00Z")
        );
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo archive(
            String id,
            DemoReadinessStatus status,
            boolean readyForExternalReview
    ) {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                id,
                status,
                readyForExternalReview,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T07:45:00Z",
                "FRESH",
                Instant.parse("2026-06-29T07:50:00Z"),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of("Download final external-review evidence package."),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package",
                Instant.parse("2026-06-29T08:00:00Z"),
                Instant.parse("2026-06-29T08:30:00Z")
        );
    }
}
