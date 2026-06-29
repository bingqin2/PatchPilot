package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvertTests {

    @Test
    void converts_delivery_receipt_between_vo_and_entity() {
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt = receipt();

        DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity entity =
                DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert.toEntity(receipt);
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo converted =
                DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getFinalExternalReviewPackageArchiveStatus()).isEqualTo("READY");
        assertThat(entity.getFinalExternalReviewPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(entity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(entity.getMarkdownReport())
                .contains("# PatchPilot Final External Review Package Delivery Receipt");
        assertThat(converted).isEqualTo(receipt);
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
                "final-external-review-package-delivery-receipt-1",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "final-external-review-package-archive-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent frozen final external-review package to the reviewer.",
                Instant.parse("2026-06-29T09:25:00Z"),
                Instant.parse("2026-06-29T09:30:00Z"),
                "# PatchPilot Final External Review Package Delivery Receipt"
        );
    }
}
