package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidenceShareDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchEvidenceShareDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoLaunchEvidenceShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoLaunchEvidenceShareDeliveryReceiptRepositoryTests {

    private final DemoLaunchEvidenceShareDeliveryReceiptMapper receiptMapper =
            mock(DemoLaunchEvidenceShareDeliveryReceiptMapper.class);
    private final MyBatisDemoLaunchEvidenceShareDeliveryReceiptRepository repository =
            new MyBatisDemoLaunchEvidenceShareDeliveryReceiptRepository(receiptMapper);

    @Test
    void should_insert_launch_delivery_receipt() {
        when(receiptMapper.insert(any(DemoLaunchEvidenceShareDeliveryReceiptEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoLaunchEvidenceShareDeliveryReceiptEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoLaunchEvidenceShareDeliveryReceiptEntity.class);

        DemoLaunchEvidenceShareDeliveryReceiptVo receipt =
                receipt("launch-delivery-receipt-1", "2026-06-28T06:10:00Z");

        DemoLaunchEvidenceShareDeliveryReceiptVo savedReceipt = repository.save(receipt);

        verify(receiptMapper).insert(entityCaptor.capture());
        DemoLaunchEvidenceShareDeliveryReceiptEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getLaunchEvidenceArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(insertedEntity.getSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(insertedEntity.getDeliveryChannel()).isEqualTo("email");
        assertThat(insertedEntity.getDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(insertedEntity.getOperator()).isEqualTo("local-operator");
        assertThat(insertedEntity.getNotes()).isEqualTo("Sent after the demo review.");
        assertThat(insertedEntity.getMessageSubject()).isEqualTo("PatchPilot demo launch evidence: demo-session-20260624T003000Z");
        assertThat(insertedEntity.getDeliveredAt()).isEqualTo(Instant.parse("2026-06-28T06:05:00Z"));
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-28T06:10:00Z"));
        assertThat(insertedEntity.getMarkdownReport()).contains("# PatchPilot Demo Launch Evidence Delivery Receipt");
        assertThat(savedReceipt).isEqualTo(receipt);
    }

    @Test
    void should_list_recent_launch_delivery_receipts_newest_first() {
        DemoLaunchEvidenceShareDeliveryReceiptEntity newer =
                entity("launch-delivery-receipt-newer", "2026-06-28T06:10:00Z");
        DemoLaunchEvidenceShareDeliveryReceiptEntity older =
                entity("launch-delivery-receipt-older", "2026-06-28T06:00:00Z");
        when(receiptMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoLaunchEvidenceShareDeliveryReceiptVo> receipts = repository.listRecentReceipts(20);

        assertThat(receipts)
                .extracting(DemoLaunchEvidenceShareDeliveryReceiptVo::id)
                .containsExactly("launch-delivery-receipt-newer", "launch-delivery-receipt-older");
        verify(receiptMapper).selectList(any());
    }

    @Test
    void should_find_launch_delivery_receipt_by_id() {
        DemoLaunchEvidenceShareDeliveryReceiptEntity entity =
                entity("launch-delivery-receipt-1", "2026-06-28T06:10:00Z");
        when(receiptMapper.selectById("launch-delivery-receipt-1")).thenReturn(entity);
        when(receiptMapper.selectById("missing-receipt")).thenReturn(null);

        assertThat(repository.findById("launch-delivery-receipt-1"))
                .map(DemoLaunchEvidenceShareDeliveryReceiptVo::deliveryTarget)
                .contains("reviewer@example.com");
        assertThat(repository.findById("missing-receipt")).isEmpty();
    }

    private static DemoLaunchEvidenceShareDeliveryReceiptVo receipt(String id, String createdAt) {
        return new DemoLaunchEvidenceShareDeliveryReceiptVo(
                id,
                "READY",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent after the demo review.",
                "PatchPilot demo launch evidence: demo-session-20260624T003000Z",
                Instant.parse("2026-06-28T06:05:00Z"),
                Instant.parse(createdAt),
                "# PatchPilot Demo Launch Evidence Delivery Receipt"
        );
    }

    private static DemoLaunchEvidenceShareDeliveryReceiptEntity entity(String id, String createdAt) {
        DemoLaunchEvidenceShareDeliveryReceiptEntity entity = new DemoLaunchEvidenceShareDeliveryReceiptEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setLaunchEvidenceArchiveId("launch-evidence-archive-1");
        entity.setSessionId("demo-session-20260624T003000Z");
        entity.setDeliveryChannel("email");
        entity.setDeliveryTarget("reviewer@example.com");
        entity.setOperator("local-operator");
        entity.setNotes("Sent after the demo review.");
        entity.setMessageSubject("PatchPilot demo launch evidence: demo-session-20260624T003000Z");
        entity.setDeliveredAt(Instant.parse("2026-06-28T06:05:00Z"));
        entity.setCreatedAt(Instant.parse(createdAt));
        entity.setMarkdownReport("# PatchPilot Demo Launch Evidence Delivery Receipt");
        return entity;
    }
}
