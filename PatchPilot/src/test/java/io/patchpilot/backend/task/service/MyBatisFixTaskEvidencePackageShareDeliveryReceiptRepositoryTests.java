package io.patchpilot.backend.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageShareDeliveryReceiptEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageShareDeliveryReceiptMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskEvidencePackageShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskEvidencePackageShareDeliveryReceiptRepositoryTests {

    private final FixTaskEvidencePackageShareDeliveryReceiptMapper mapper =
            mock(FixTaskEvidencePackageShareDeliveryReceiptMapper.class);
    private final MyBatisFixTaskEvidencePackageShareDeliveryReceiptRepository repository =
            new MyBatisFixTaskEvidencePackageShareDeliveryReceiptRepository(mapper);

    @Test
    void should_insert_receipt_entity_when_saving() {
        FixTaskEvidencePackageShareDeliveryReceiptVo receipt = receipt("task-evidence-delivery-receipt-1");
        ArgumentCaptor<FixTaskEvidencePackageShareDeliveryReceiptEntity> captor =
                ArgumentCaptor.forClass(FixTaskEvidencePackageShareDeliveryReceiptEntity.class);

        FixTaskEvidencePackageShareDeliveryReceiptVo saved = repository.save(receipt);

        verify(mapper).insert(captor.capture());
        assertThat(saved).isEqualTo(receipt);
        assertThat(captor.getValue().getId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(captor.getValue().getTaskEvidenceArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(captor.getValue().getDeliveryTarget()).isEqualTo("reviewer@example.com");
    }

    @Test
    void should_convert_recent_receipts_from_mapper() {
        FixTaskEvidencePackageShareDeliveryReceiptEntity entity = entity("task-evidence-delivery-receipt-1");
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(entity));

        List<FixTaskEvidencePackageShareDeliveryReceiptVo> receipts = repository.listRecentReceipts(20);

        assertThat(receipts).extracting(FixTaskEvidencePackageShareDeliveryReceiptVo::id)
                .containsExactly("task-evidence-delivery-receipt-1");
        assertThat(receipts.get(0).markdownReport()).contains("# PatchPilot Task Evidence Delivery Receipt");
    }

    @Test
    void should_find_receipt_by_id() {
        when(mapper.selectById("task-evidence-delivery-receipt-1"))
                .thenReturn(entity("task-evidence-delivery-receipt-1"));

        assertThat(repository.findById("task-evidence-delivery-receipt-1"))
                .hasValueSatisfying(receipt -> assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com"));
    }

    private static FixTaskEvidencePackageShareDeliveryReceiptVo receipt(String id) {
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

    private static FixTaskEvidencePackageShareDeliveryReceiptEntity entity(String id) {
        FixTaskEvidencePackageShareDeliveryReceiptEntity entity =
                new FixTaskEvidencePackageShareDeliveryReceiptEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setTaskEvidenceArchiveId("task-evidence-archive-1");
        entity.setTaskId("task-1");
        entity.setRepositoryOwner("bingqin2");
        entity.setRepositoryName("PatchPilot");
        entity.setIssueNumber(1L);
        entity.setPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/8");
        entity.setDeliveryChannel("email");
        entity.setDeliveryTarget("reviewer@example.com");
        entity.setOperator("local-operator");
        entity.setNotes("Sent task evidence after PR review.");
        entity.setMessageSubject("PatchPilot task evidence: task-1");
        entity.setDeliveredAt(Instant.parse("2026-06-28T06:05:00Z"));
        entity.setCreatedAt(Instant.parse("2026-06-28T06:10:00Z"));
        entity.setMarkdownReport("# PatchPilot Task Evidence Delivery Receipt");
        return entity;
    }
}
