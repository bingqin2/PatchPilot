package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewReleaseBundleDeliveryReceiptServiceTests {

    @Test
    void records_delivery_receipt_for_latest_ready_release_bundle_archive() {
        InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository =
                new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository();
        archiveRepository.save(archive(
                "final-external-review-release-bundle-archive-1",
                DemoReadinessStatus.READY,
                true
        ));
        InMemoryDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository receiptRepository =
                new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository();
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
                        archiveRepository,
                        receiptRepository,
                        Clock.fixed(Instant.parse("2026-06-29T13:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-receipt-1"
                );

        DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptRequestDto(
                        "email",
                        "reviewer@example.com",
                        "local-operator",
                        "Sent frozen final release bundle to reviewers.",
                        Instant.parse("2026-06-29T13:25:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.releaseBundleArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.releaseBundleArchiveId()).isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(receipt.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(receipt.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(receipt.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(receipt.latestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(receipt.latestTaskId()).isEqualTo("task-1");
        assertThat(receipt.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent frozen final release bundle to reviewers.");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-29T13:25:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-29T13:30:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Final External Review Release Bundle Delivery Receipt")
                .contains("- Release bundle archive: `final-external-review-release-bundle-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("POST /api/demo/final-external-review-release-bundle/delivery-receipts records local evidence only")
                .contains("does not send messages, create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("final-external-review-release-bundle-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void rejects_receipt_when_no_release_bundle_archive_exists() {
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
                        new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository(),
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T13:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("no final external-review release bundle archive is available");
    }

    @Test
    void rejects_receipt_when_latest_release_bundle_archive_is_not_ready() {
        InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository =
                new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository();
        archiveRepository.save(archive(
                "final-external-review-release-bundle-archive-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                false
        ));
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
                        archiveRepository,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T13:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review release bundle archive is not ready for delivery");
    }

    @Test
    void requires_delivery_fields() {
        InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository =
                new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository();
        archiveRepository.save(archive(
                "final-external-review-release-bundle-archive-1",
                DemoReadinessStatus.READY,
                true
        ));
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptService(
                        archiveRepository,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T13:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptRequestDto(
                        " ",
                        "reviewer@example.com",
                        "local-operator",
                        "notes",
                        Instant.parse("2026-06-29T13:25:00Z")
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deliveryChannel is required");
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryReceiptRequestDto validRequest() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryReceiptRequestDto(
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent frozen final release bundle to reviewers.",
                Instant.parse("2026-06-29T13:25:00Z")
        );
    }

    static DemoFinalExternalReviewReleaseBundleArchiveVo archive(
            String id,
            DemoReadinessStatus status,
            boolean releaseReady
    ) {
        return new DemoFinalExternalReviewReleaseBundleArchiveVo(
                id,
                status,
                releaseReady,
                "PatchPilot final external-review release bundle is ready.",
                "Share the release bundle report and listed attachments with external reviewers.",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                Instant.parse("2026-06-29T11:30:00Z"),
                List.of(
                        "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1",
                        "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1"
                ),
                List.of(new DemoFinalExternalReviewReleaseBundleArchiveVo.ReleaseCheck(
                        "Final delivery certificate archive",
                        status,
                        "Latest final external-review delivery certificate archive is certified.",
                        releaseReady ? "No action needed." : "Archive a certified delivery certificate first."
                )),
                List.of("Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth."),
                List.of("Download final external-review release bundle report."),
                "GET /api/demo/final-external-review-release-bundle is read-only.",
                "# PatchPilot Final External Review Release Bundle",
                Instant.parse("2026-06-29T12:00:00Z"),
                Instant.parse("2026-06-29T12:30:00Z")
        );
    }
}
