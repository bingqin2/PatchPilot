package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepositoryTests {

    @Test
    void should_keep_latest_receipts_first_and_trim_to_twenty() {
        InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository repository =
                new InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository();

        IntStream.rangeClosed(1, 22)
                .mapToObj(index -> receipt("receipt-" + index, "completion-archive-" + index))
                .forEach(repository::save);

        assertThat(repository.listRecentReceipts(25))
                .hasSize(20)
                .extracting(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo::id)
                .startsWith("receipt-22", "receipt-21")
                .doesNotContain("receipt-1", "receipt-2");
        assertThat(repository.findById("receipt-22")).isPresent();
        assertThat(repository.findById("receipt-1")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt(String id, String archiveId) {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                true,
                DemoReadinessStatus.READY,
                "ready",
                "share",
                archiveId,
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "notes",
                Instant.parse("2026-06-29T04:25:00Z"),
                Instant.parse("2026-06-29T04:30:00Z"),
                "# PatchPilot Final Acceptance Completion Evidence Delivery Receipt"
        );
    }
}
