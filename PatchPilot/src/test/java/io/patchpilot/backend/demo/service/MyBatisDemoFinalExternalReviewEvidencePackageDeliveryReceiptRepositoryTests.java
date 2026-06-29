package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewEvidencePackageDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepositoryTests {

    private final DemoFinalExternalReviewEvidencePackageDeliveryReceiptMapper mapper =
            mock(DemoFinalExternalReviewEvidencePackageDeliveryReceiptMapper.class);
    private final MyBatisDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository repository =
            new MyBatisDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository(mapper);

    @Test
    void inserts_delivery_receipt() {
        when(mapper.insert(any(DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity.class);
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt = receipt(
                "final-external-review-package-delivery-receipt-1",
                Instant.parse("2026-06-29T09:30:00Z")
        );

        DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo savedReceipt = repository.save(receipt);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getFinalExternalReviewPackageArchiveStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getFinalExternalReviewPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(insertedEntity.getCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(savedReceipt).isEqualTo(receipt);
    }

    @Test
    void lists_recent_delivery_receipts_newest_first() {
        when(mapper.selectList(any())).thenReturn(List.of(
                entity("receipt-newer", Instant.parse("2026-06-29T09:31:00Z")),
                entity("receipt-older", Instant.parse("2026-06-29T09:30:00Z"))
        ));

        List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> receipts = repository.listRecentReceipts(20);

        assertThat(receipts)
                .extracting(DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo::id)
                .containsExactly("receipt-newer", "receipt-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_delivery_receipt_by_id() {
        when(mapper.selectById("receipt-1")).thenReturn(entity("receipt-1", Instant.parse("2026-06-29T09:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("receipt-1"))
                .map(DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo::deliveryTarget)
                .contains("reviewer@example.com");
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt(String id, Instant createdAt) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
                id,
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
                "notes",
                Instant.parse("2026-06-29T09:25:00Z"),
                createdAt,
                "# PatchPilot Final External Review Package Delivery Receipt"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity entity(String id, Instant createdAt) {
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity entity =
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setFinalExternalReviewPackageArchiveStatus("READY");
        entity.setFinalExternalReviewPackageArchiveId("final-external-review-package-archive-1");
        entity.setCloseoutArchiveId("final-acceptance-completion-closeout-archive-1");
        entity.setCompletionArchiveId("final-acceptance-completion-archive-1");
        entity.setCompletionEvidenceDeliveryReceiptId("final-acceptance-completion-evidence-delivery-receipt-1");
        entity.setLatestTaskId("task-2");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setSummary("PatchPilot final external-review evidence package is ready.");
        entity.setNextAction("Share this package with reviewers as the frozen external-review record.");
        entity.setDeliveryChannel("email");
        entity.setDeliveryTarget("reviewer@example.com");
        entity.setOperator("local-operator");
        entity.setNotes("notes");
        entity.setDeliveredAt(Instant.parse("2026-06-29T09:25:00Z"));
        entity.setCreatedAt(createdAt);
        entity.setMarkdownReport("# PatchPilot Final External Review Package Delivery Receipt");
        return entity;
    }
}
