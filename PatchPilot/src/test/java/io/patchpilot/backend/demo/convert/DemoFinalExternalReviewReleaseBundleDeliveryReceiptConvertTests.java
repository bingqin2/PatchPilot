package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvertTests {

    @Test
    void converts_final_external_review_release_bundle_delivery_receipt_to_entity_and_back() {
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt = receipt();

        DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity entity =
                DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert.toEntity(receipt);
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo converted =
                DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getReleaseBundleArchiveStatus()).isEqualTo("READY");
        assertThat(entity.getReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(entity.getLatestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(entity.getLatestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(entity.getLatestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(entity.getDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(entity.getMarkdownReport())
                .contains("# PatchPilot Final External Review Release Bundle Delivery Receipt");
        assertThat(converted).isEqualTo(receipt);
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo(
                "final-external-review-release-bundle-delivery-receipt-1",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "final-external-review-release-bundle-archive-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
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
