package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepositoryTests {

    @Test
    void should_save_find_and_list_recent_launch_delivery_receipts() {
        InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository repository =
                new InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository();
        DemoLaunchEvidenceShareDeliveryReceiptVo first = receipt("launch-delivery-receipt-1", "2026-06-28T06:00:00Z");
        DemoLaunchEvidenceShareDeliveryReceiptVo second = receipt("launch-delivery-receipt-2", "2026-06-28T06:10:00Z");

        repository.save(first);
        repository.save(second);

        assertThat(repository.findById("launch-delivery-receipt-1")).contains(first);
        assertThat(repository.listRecentReceipts(10)).containsExactly(second, first);
        assertThat(repository.listRecentReceipts(1)).containsExactly(second);
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
}
