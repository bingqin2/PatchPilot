package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceShareDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceShareDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalAcceptanceShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalAcceptanceShareDeliveryReceiptRepositoryTests {

    private final DemoFinalAcceptanceShareDeliveryReceiptMapper receiptMapper =
            mock(DemoFinalAcceptanceShareDeliveryReceiptMapper.class);
    private final MyBatisDemoFinalAcceptanceShareDeliveryReceiptRepository repository =
            new MyBatisDemoFinalAcceptanceShareDeliveryReceiptRepository(receiptMapper);

    @Test
    void should_insert_final_acceptance_share_delivery_receipt() {
        when(receiptMapper.insert(any(DemoFinalAcceptanceShareDeliveryReceiptEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalAcceptanceShareDeliveryReceiptEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalAcceptanceShareDeliveryReceiptEntity.class);

        DemoFinalAcceptanceShareDeliveryReceiptVo receipt =
                receipt("final-acceptance-delivery-receipt-1", "2026-06-29T03:10:00Z");

        DemoFinalAcceptanceShareDeliveryReceiptVo savedReceipt = repository.save(receipt);

        verify(receiptMapper).insert(entityCaptor.capture());
        DemoFinalAcceptanceShareDeliveryReceiptEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getFinalAcceptanceSharePackageArchiveId())
                .isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(insertedEntity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(insertedEntity.getDeliveryChannel()).isEqualTo("email");
        assertThat(insertedEntity.getDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(insertedEntity.getOperator()).isEqualTo("local-operator");
        assertThat(insertedEntity.getNotes()).isEqualTo("Sent after the final demo acceptance review.");
        assertThat(insertedEntity.getMessageSubject()).isEqualTo("PatchPilot final demo acceptance: task-1");
        assertThat(insertedEntity.getDeliveredAt()).isEqualTo(Instant.parse("2026-06-29T03:05:00Z"));
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-29T03:10:00Z"));
        assertThat(insertedEntity.getMarkdownReport())
                .contains("# PatchPilot Final Demo Acceptance Share Delivery Receipt");
        assertThat(savedReceipt).isEqualTo(receipt);
    }

    @Test
    void should_list_recent_final_acceptance_share_delivery_receipts_newest_first() {
        DemoFinalAcceptanceShareDeliveryReceiptEntity newer =
                entity("final-acceptance-delivery-receipt-newer", "2026-06-29T03:10:00Z");
        DemoFinalAcceptanceShareDeliveryReceiptEntity older =
                entity("final-acceptance-delivery-receipt-older", "2026-06-29T03:00:00Z");
        when(receiptMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalAcceptanceShareDeliveryReceiptVo> receipts = repository.listRecentReceipts(20);

        assertThat(receipts)
                .extracting(DemoFinalAcceptanceShareDeliveryReceiptVo::id)
                .containsExactly("final-acceptance-delivery-receipt-newer", "final-acceptance-delivery-receipt-older");
        verify(receiptMapper).selectList(any());
    }

    @Test
    void should_find_final_acceptance_share_delivery_receipt_by_id() {
        DemoFinalAcceptanceShareDeliveryReceiptEntity entity =
                entity("final-acceptance-delivery-receipt-1", "2026-06-29T03:10:00Z");
        when(receiptMapper.selectById("final-acceptance-delivery-receipt-1")).thenReturn(entity);
        when(receiptMapper.selectById("missing-receipt")).thenReturn(null);

        assertThat(repository.findById("final-acceptance-delivery-receipt-1"))
                .map(DemoFinalAcceptanceShareDeliveryReceiptVo::deliveryTarget)
                .contains("reviewer@example.com");
        assertThat(repository.findById("missing-receipt")).isEmpty();
    }

    private static DemoFinalAcceptanceShareDeliveryReceiptVo receipt(String id, String createdAt) {
        return new DemoFinalAcceptanceShareDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                "final-acceptance-share-package-archive-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent after the final demo acceptance review.",
                "PatchPilot final demo acceptance: task-1",
                Instant.parse("2026-06-29T03:05:00Z"),
                Instant.parse(createdAt),
                "# PatchPilot Final Demo Acceptance Share Delivery Receipt"
        );
    }

    private static DemoFinalAcceptanceShareDeliveryReceiptEntity entity(String id, String createdAt) {
        DemoFinalAcceptanceShareDeliveryReceiptEntity entity = new DemoFinalAcceptanceShareDeliveryReceiptEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setFinalAcceptanceSharePackageArchiveId("final-acceptance-share-package-archive-1");
        entity.setLatestTaskId("task-1");
        entity.setDeliveryChannel("email");
        entity.setDeliveryTarget("reviewer@example.com");
        entity.setOperator("local-operator");
        entity.setNotes("Sent after the final demo acceptance review.");
        entity.setMessageSubject("PatchPilot final demo acceptance: task-1");
        entity.setDeliveredAt(Instant.parse("2026-06-29T03:05:00Z"));
        entity.setCreatedAt(Instant.parse(createdAt));
        entity.setMarkdownReport("# PatchPilot Final Demo Acceptance Share Delivery Receipt");
        return entity;
    }
}
