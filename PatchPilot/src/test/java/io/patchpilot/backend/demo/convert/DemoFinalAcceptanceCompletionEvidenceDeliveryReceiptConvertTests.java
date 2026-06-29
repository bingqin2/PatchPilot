package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvertTests {

    @Test
    void should_convert_between_receipt_vo_and_entity() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt = receipt(
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "2026-06-29T04:30:00Z"
        );

        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity entity =
                DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert.toEntity(receipt);
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo roundTrip =
                DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getReadyToShare()).isTrue();
        assertThat(entity.getCompletionEvidenceBundleStatus()).isEqualTo("READY");
        assertThat(entity.getLatestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(entity.getLatestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(entity.getLatestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(entity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(roundTrip).isEqualTo(receipt);
    }

    static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt(String id, String createdAt) {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                true,
                DemoReadinessStatus.READY,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share the final acceptance completion evidence bundle with reviewers.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final completion evidence bundle to the reviewer.",
                Instant.parse("2026-06-29T04:25:00Z"),
                Instant.parse(createdAt),
                "# PatchPilot Final Acceptance Completion Evidence Delivery Receipt"
        );
    }
}
