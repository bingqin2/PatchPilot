package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalReviewerHandoffDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalReviewerHandoffDeliveryReceiptServiceTests {

    @Test
    void records_delivery_receipt_for_current_ready_reviewer_handoff_package() {
        DemoFinalReviewerHandoffDeliveryReceiptService service =
                new DemoFinalReviewerHandoffDeliveryReceiptService(
                        () -> readyPackage(),
                        new InMemoryDemoFinalReviewerHandoffDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-30T07:30:00Z"), ZoneOffset.UTC),
                        () -> "final-reviewer-handoff-delivery-receipt-1"
                );

        DemoFinalReviewerHandoffDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoFinalReviewerHandoffDeliveryReceiptRequestDto(
                        "email",
                        "external-reviewer@example.com",
                        "local-operator",
                        "Sent final reviewer handoff package and all required attachments.",
                        Instant.parse("2026-06-30T07:25:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("final-reviewer-handoff-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.handoffPackageStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.latestCertificateArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-certificate-archive-1");
        assertThat(receipt.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(receipt.latestReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(receipt.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(receipt.latestPackageCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(receipt.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(receipt.latestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(receipt.latestTaskId()).isEqualTo("task-1");
        assertThat(receipt.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("external-reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent final reviewer handoff package and all required attachments.");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-30T07:25:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-30T07:30:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Final Reviewer Handoff Delivery Receipt")
                .contains("- Final reviewer handoff package status: `READY`")
                .contains("- Terminal certificate archive: `final-external-review-release-bundle-delivery-certificate-archive-1`")
                .contains("- Delivery target: `external-reviewer@example.com`")
                .contains("POST /api/demo/final-reviewer-handoff-package/delivery-receipts records local evidence only");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("final-reviewer-handoff-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void rejects_receipt_when_reviewer_handoff_package_is_not_ready() {
        DemoFinalReviewerHandoffDeliveryReceiptService service =
                new DemoFinalReviewerHandoffDeliveryReceiptService(
                        () -> blockedPackage(),
                        new InMemoryDemoFinalReviewerHandoffDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-30T07:30:00Z"), ZoneOffset.UTC),
                        () -> "final-reviewer-handoff-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final reviewer handoff package is not ready for delivery");
    }

    @Test
    void requires_delivery_fields() {
        DemoFinalReviewerHandoffDeliveryReceiptService service =
                new DemoFinalReviewerHandoffDeliveryReceiptService(
                        () -> readyPackage(),
                        new InMemoryDemoFinalReviewerHandoffDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-30T07:30:00Z"), ZoneOffset.UTC),
                        () -> "final-reviewer-handoff-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(
                new DemoFinalReviewerHandoffDeliveryReceiptRequestDto(
                        "email",
                        " ",
                        "local-operator",
                        "notes",
                        Instant.parse("2026-06-30T07:25:00Z")
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deliveryTarget is required");
    }

    static DemoFinalReviewerHandoffPackageVo readyPackage() {
        return packageVo(DemoReadinessStatus.READY, true);
    }

    static DemoFinalReviewerHandoffPackageVo blockedPackage() {
        return packageVo(DemoReadinessStatus.NEEDS_ATTENTION, false);
    }

    private static DemoFinalReviewerHandoffPackageVo packageVo(
            DemoReadinessStatus status,
            boolean readyForReview
    ) {
        return new DemoFinalReviewerHandoffPackageVo(
                status,
                readyForReview,
                readyForReview
                        ? "Final reviewer handoff package is ready from the latest terminal delivery certificate archive."
                        : "No terminal release-bundle delivery certificate archive is available for reviewer handoff.",
                readyForReview
                        ? "Send the handoff package report and listed attachments to the external reviewer."
                        : "Archive the certified final external-review release bundle delivery certificate.",
                "final-external-review-release-bundle-delivery-certificate-archive-1",
                "final-external-review-release-bundle-delivery-finalization-archive-1",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-30T02:10:00Z",
                Instant.parse("2026-06-30T03:30:00Z"),
                readyForReview
                        ? List.of(
                        "Final reviewer handoff package report.",
                        "Terminal release-bundle delivery certificate archive final-external-review-release-bundle-delivery-certificate-archive-1."
                )
                        : List.of(),
                List.of(new DemoFinalReviewerHandoffPackageVo.Check(
                        "Terminal delivery certificate archive",
                        status,
                        readyForReview
                                ? "Latest terminal certificate archive is certified."
                                : "Latest terminal certificate archive is not certified.",
                        readyForReview ? "No action needed." : "Archive a certified terminal certificate."
                )),
                List.of("Terminal certificate archive final-external-review-release-bundle-delivery-certificate-archive-1 is certified."),
                List.of("Download final reviewer handoff package report."),
                "GET /api/demo/final-reviewer-handoff-package is read-only.",
                "# PatchPilot Final Reviewer Handoff Package",
                Instant.parse("2026-06-30T05:00:00Z")
        );
    }

    private static DemoFinalReviewerHandoffDeliveryReceiptRequestDto validRequest() {
        return new DemoFinalReviewerHandoffDeliveryReceiptRequestDto(
                "email",
                "external-reviewer@example.com",
                "local-operator",
                "Sent final reviewer handoff package.",
                Instant.parse("2026-06-30T07:25:00Z")
        );
    }
}
