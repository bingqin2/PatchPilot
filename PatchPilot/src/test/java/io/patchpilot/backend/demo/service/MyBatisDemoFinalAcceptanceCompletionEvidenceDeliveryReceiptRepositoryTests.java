package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepositoryTests {

    private final DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMapper receiptMapper =
            mock(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMapper.class);
    private final MyBatisDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository repository =
            new MyBatisDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository(receiptMapper);

    @Test
    void should_insert_completion_evidence_delivery_receipt() {
        when(receiptMapper.insert(any(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity.class);

        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt =
                receipt("final-acceptance-completion-evidence-delivery-receipt-1", "2026-06-29T04:30:00Z");

        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo savedReceipt = repository.save(receipt);

        verify(receiptMapper).insert(entityCaptor.capture());
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getReadyToShare()).isTrue();
        assertThat(insertedEntity.getCompletionEvidenceBundleStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getLatestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(insertedEntity.getLatestSharePackageArchiveId())
                .isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(insertedEntity.getLatestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(insertedEntity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(insertedEntity.getDeliveryChannel()).isEqualTo("email");
        assertThat(insertedEntity.getDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(insertedEntity.getOperator()).isEqualTo("local-operator");
        assertThat(insertedEntity.getNotes()).isEqualTo("Sent final completion evidence bundle to the reviewer.");
        assertThat(insertedEntity.getDeliveredAt()).isEqualTo(Instant.parse("2026-06-29T04:25:00Z"));
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-29T04:30:00Z"));
        assertThat(insertedEntity.getMarkdownReport())
                .contains("# PatchPilot Final Acceptance Completion Evidence Delivery Receipt");
        assertThat(savedReceipt).isEqualTo(receipt);
    }

    @Test
    void should_list_recent_completion_evidence_delivery_receipts_newest_first() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity newer =
                entity("final-acceptance-completion-evidence-delivery-receipt-newer", "2026-06-29T04:30:00Z");
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity older =
                entity("final-acceptance-completion-evidence-delivery-receipt-older", "2026-06-29T04:20:00Z");
        when(receiptMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> receipts = repository.listRecentReceipts(20);

        assertThat(receipts)
                .extracting(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo::id)
                .containsExactly(
                        "final-acceptance-completion-evidence-delivery-receipt-newer",
                        "final-acceptance-completion-evidence-delivery-receipt-older"
                );
        verify(receiptMapper).selectList(any());
    }

    @Test
    void should_find_completion_evidence_delivery_receipt_by_id() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity entity =
                entity("final-acceptance-completion-evidence-delivery-receipt-1", "2026-06-29T04:30:00Z");
        when(receiptMapper.selectById("final-acceptance-completion-evidence-delivery-receipt-1")).thenReturn(entity);
        when(receiptMapper.selectById("missing-receipt")).thenReturn(null);

        assertThat(repository.findById("final-acceptance-completion-evidence-delivery-receipt-1"))
                .map(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo::deliveryTarget)
                .contains("reviewer@example.com");
        assertThat(repository.findById("missing-receipt")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt(String id, String createdAt) {
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

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity entity(String id, String createdAt) {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity entity =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setReadyToShare(true);
        entity.setCompletionEvidenceBundleStatus("READY");
        entity.setSummary("PatchPilot final acceptance completion evidence bundle is ready to share.");
        entity.setNextAction("Share the final acceptance completion evidence bundle with reviewers.");
        entity.setLatestCompletionArchiveId("final-acceptance-completion-archive-1");
        entity.setLatestSharePackageArchiveId("final-acceptance-share-package-archive-1");
        entity.setLatestDeliveryReceiptId("final-acceptance-delivery-receipt-1");
        entity.setLatestTaskId("task-1");
        entity.setDeliveryChannel("email");
        entity.setDeliveryTarget("reviewer@example.com");
        entity.setOperator("local-operator");
        entity.setNotes("Sent final completion evidence bundle to the reviewer.");
        entity.setDeliveredAt(Instant.parse("2026-06-29T04:25:00Z"));
        entity.setCreatedAt(Instant.parse(createdAt));
        entity.setMarkdownReport("# PatchPilot Final Acceptance Completion Evidence Delivery Receipt");
        return entity;
    }
}
