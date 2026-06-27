package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoHandoffShareDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoHandoffShareDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoHandoffShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoHandoffShareDeliveryReceiptRepositoryTests {

    private final DemoHandoffShareDeliveryReceiptMapper receiptMapper = mock(DemoHandoffShareDeliveryReceiptMapper.class);
    private final MyBatisDemoHandoffShareDeliveryReceiptRepository repository =
            new MyBatisDemoHandoffShareDeliveryReceiptRepository(receiptMapper);

    @Test
    void should_insert_delivery_receipt() {
        when(receiptMapper.insert(any(DemoHandoffShareDeliveryReceiptEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoHandoffShareDeliveryReceiptEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoHandoffShareDeliveryReceiptEntity.class);

        DemoHandoffShareDeliveryReceiptVo receipt = receipt("delivery-receipt-1", "2026-06-24T06:10:00Z");

        DemoHandoffShareDeliveryReceiptVo savedReceipt = repository.save(receipt);

        verify(receiptMapper).insert(entityCaptor.capture());
        DemoHandoffShareDeliveryReceiptEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("delivery-receipt-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getHandoffArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(insertedEntity.getSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(insertedEntity.getDeliveryChannel()).isEqualTo("email");
        assertThat(insertedEntity.getDeliveryTarget()).isEqualTo("maintainer@example.com");
        assertThat(insertedEntity.getOperator()).isEqualTo("local-operator");
        assertThat(insertedEntity.getNotes()).isEqualTo("Sent after the demo review.");
        assertThat(insertedEntity.getMessageSubject()).isEqualTo("PatchPilot demo handoff: demo-session-20260624T003000Z");
        assertThat(insertedEntity.getDeliveredAt()).isEqualTo(Instant.parse("2026-06-24T06:05:00Z"));
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-24T06:10:00Z"));
        assertThat(insertedEntity.getMarkdownReport()).contains("# PatchPilot Demo Handoff Share Delivery Receipt");
        assertThat(savedReceipt).isEqualTo(receipt);
    }

    @Test
    void should_list_recent_delivery_receipts_newest_first() {
        DemoHandoffShareDeliveryReceiptEntity newer = entity("delivery-receipt-newer", "2026-06-24T06:10:00Z");
        DemoHandoffShareDeliveryReceiptEntity older = entity("delivery-receipt-older", "2026-06-24T06:00:00Z");
        when(receiptMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoHandoffShareDeliveryReceiptVo> receipts = repository.listRecentReceipts(20);

        assertThat(receipts)
                .extracting(DemoHandoffShareDeliveryReceiptVo::id)
                .containsExactly("delivery-receipt-newer", "delivery-receipt-older");
        verify(receiptMapper).selectList(any());
    }

    @Test
    void should_find_delivery_receipt_by_id() {
        DemoHandoffShareDeliveryReceiptEntity entity = entity("delivery-receipt-1", "2026-06-24T06:10:00Z");
        when(receiptMapper.selectById("delivery-receipt-1")).thenReturn(entity);
        when(receiptMapper.selectById("missing-receipt")).thenReturn(null);

        assertThat(repository.findById("delivery-receipt-1"))
                .map(DemoHandoffShareDeliveryReceiptVo::deliveryTarget)
                .contains("maintainer@example.com");
        assertThat(repository.findById("missing-receipt")).isEmpty();
    }

    private static DemoHandoffShareDeliveryReceiptVo receipt(String id, String createdAt) {
        return new DemoHandoffShareDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "maintainer@example.com",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                Instant.parse("2026-06-24T06:05:00Z"),
                Instant.parse(createdAt),
                "# PatchPilot Demo Handoff Share Delivery Receipt"
        );
    }

    private static DemoHandoffShareDeliveryReceiptEntity entity(String id, String createdAt) {
        DemoHandoffShareDeliveryReceiptEntity entity = new DemoHandoffShareDeliveryReceiptEntity();
        entity.setId(id);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setHandoffArchiveId("handoff-archive-1");
        entity.setSessionId("demo-session-20260624T003000Z");
        entity.setDeliveryChannel("email");
        entity.setDeliveryTarget("maintainer@example.com");
        entity.setOperator("local-operator");
        entity.setNotes("Sent after the demo review.");
        entity.setMessageSubject("PatchPilot demo handoff: demo-session-20260624T003000Z");
        entity.setDeliveredAt(Instant.parse("2026-06-24T06:05:00Z"));
        entity.setCreatedAt(Instant.parse(createdAt));
        entity.setMarkdownReport("# PatchPilot Demo Handoff Share Delivery Receipt");
        return entity;
    }
}
