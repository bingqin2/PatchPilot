package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepositoryTests {

    @Test
    void should_keep_latest_receipts_first_and_trim_to_twenty() {
        InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository repository =
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository();

        IntStream.rangeClosed(1, 22)
                .mapToObj(index -> receipt("receipt-" + index, "archive-" + index))
                .forEach(repository::save);

        assertThat(repository.listRecentReceipts(25))
                .hasSize(20)
                .extracting(DemoFinalAcceptanceShareDeliveryReceiptVo::id)
                .startsWith("receipt-22", "receipt-21")
                .doesNotContain("receipt-1", "receipt-2");
        assertThat(repository.findById("receipt-22")).isPresent();
        assertThat(repository.findById("receipt-1")).isEmpty();
    }

    private static DemoFinalAcceptanceShareDeliveryReceiptVo receipt(String id, String archiveId) {
        return new DemoFinalAcceptanceShareDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                archiveId,
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "notes",
                "PatchPilot final demo acceptance: task-1",
                Instant.parse("2026-06-29T03:05:00Z"),
                Instant.parse("2026-06-29T03:10:00Z"),
                "# PatchPilot Final Demo Acceptance Share Delivery Receipt"
        );
    }
}
