package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoHandoffShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoHandoffShareDeliveryReceiptRepositoryTests {

    @Test
    void should_save_find_and_list_recent_delivery_receipts() {
        InMemoryDemoHandoffShareDeliveryReceiptRepository repository =
                new InMemoryDemoHandoffShareDeliveryReceiptRepository();
        DemoHandoffShareDeliveryReceiptVo first = receipt("delivery-receipt-1", "2026-06-24T06:00:00Z");
        DemoHandoffShareDeliveryReceiptVo second = receipt("delivery-receipt-2", "2026-06-24T06:10:00Z");

        repository.save(first);
        repository.save(second);

        assertThat(repository.findById("delivery-receipt-1")).contains(first);
        assertThat(repository.listRecentReceipts(10)).containsExactly(second, first);
        assertThat(repository.listRecentReceipts(1)).containsExactly(second);
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
}
