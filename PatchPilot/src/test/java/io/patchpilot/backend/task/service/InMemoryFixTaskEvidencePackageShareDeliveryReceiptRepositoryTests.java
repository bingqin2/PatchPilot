package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepositoryTests {

    @Test
    void should_list_recent_receipts_by_created_time_and_find_by_id() {
        InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository repository =
                new InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository();
        FixTaskEvidencePackageShareDeliveryReceiptVo older =
                receipt("task-evidence-delivery-receipt-old", "2026-06-28T06:00:00Z");
        FixTaskEvidencePackageShareDeliveryReceiptVo newer =
                receipt("task-evidence-delivery-receipt-new", "2026-06-28T06:10:00Z");

        repository.save(older);
        repository.save(newer);

        assertThat(repository.listRecentReceipts(1)).containsExactly(newer);
        assertThat(repository.listRecentReceipts(20)).containsExactly(newer, older);
        assertThat(repository.findById("task-evidence-delivery-receipt-old")).contains(older);
        assertThat(repository.findById("missing-receipt")).isEmpty();
    }

    private static FixTaskEvidencePackageShareDeliveryReceiptVo receipt(String id, String createdAt) {
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
                Instant.parse(createdAt),
                "# PatchPilot Task Evidence Delivery Receipt"
        );
    }
}
