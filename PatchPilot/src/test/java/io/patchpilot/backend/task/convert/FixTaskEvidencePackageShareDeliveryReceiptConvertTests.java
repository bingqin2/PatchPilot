package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageShareDeliveryReceiptEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageShareDeliveryReceiptConvertTests {

    @Test
    void should_round_trip_task_evidence_share_delivery_receipt() {
        FixTaskEvidencePackageShareDeliveryReceiptVo receipt = receipt("task-evidence-delivery-receipt-1");

        FixTaskEvidencePackageShareDeliveryReceiptEntity entity =
                FixTaskEvidencePackageShareDeliveryReceiptConvert.toEntity(receipt);
        FixTaskEvidencePackageShareDeliveryReceiptVo converted =
                FixTaskEvidencePackageShareDeliveryReceiptConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(entity.getTaskEvidenceArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(entity.getMarkdownReport()).contains("# PatchPilot Task Evidence Delivery Receipt");
        assertThat(converted).isEqualTo(receipt);
    }

    static FixTaskEvidencePackageShareDeliveryReceiptVo receipt(String id) {
        return new FixTaskEvidencePackageShareDeliveryReceiptVo(
                id,
                "READY",
                "task-evidence-archive-1",
                "task-1",
                "bingqin2",
                "PatchPilot",
                1L,
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent task evidence after PR review.",
                "PatchPilot task evidence: task-1",
                Instant.parse("2026-06-28T06:05:00Z"),
                Instant.parse("2026-06-28T06:10:00Z"),
                "# PatchPilot Task Evidence Delivery Receipt"
        );
    }
}
