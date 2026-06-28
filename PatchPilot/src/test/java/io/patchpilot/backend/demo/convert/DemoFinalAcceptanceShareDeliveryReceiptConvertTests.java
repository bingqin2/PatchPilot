package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceShareDeliveryReceiptEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceShareDeliveryReceiptConvertTests {

    @Test
    void should_convert_between_receipt_vo_and_entity() {
        DemoFinalAcceptanceShareDeliveryReceiptVo receipt = new DemoFinalAcceptanceShareDeliveryReceiptVo(
                "final-acceptance-delivery-receipt-1",
                DemoReadinessStatus.READY,
                "final-acceptance-share-package-archive-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final acceptance share package to the reviewer.",
                "PatchPilot final demo acceptance: task-1",
                Instant.parse("2026-06-29T03:05:00Z"),
                Instant.parse("2026-06-29T03:10:00Z"),
                "# PatchPilot Final Demo Acceptance Share Delivery Receipt"
        );

        DemoFinalAcceptanceShareDeliveryReceiptEntity entity =
                DemoFinalAcceptanceShareDeliveryReceiptConvert.toEntity(receipt);
        DemoFinalAcceptanceShareDeliveryReceiptVo roundTrip =
                DemoFinalAcceptanceShareDeliveryReceiptConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getFinalAcceptanceSharePackageArchiveId())
                .isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(entity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(roundTrip).isEqualTo(receipt);
    }
}
