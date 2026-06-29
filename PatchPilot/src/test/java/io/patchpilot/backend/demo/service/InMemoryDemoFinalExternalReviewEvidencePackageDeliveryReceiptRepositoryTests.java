package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepositoryTests {

    @Test
    void stores_recent_receipts_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository repository =
                new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(receipt("receipt-" + index, Instant.parse("2026-06-29T09:00:00Z").plusSeconds(index)));
        }

        assertThat(repository.listRecentReceipts(5))
                .extracting(DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo::id)
                .containsExactly("receipt-22", "receipt-21", "receipt-20", "receipt-19", "receipt-18");
        assertThat(repository.findById("receipt-22")).isPresent();
        assertThat(repository.findById("receipt-1")).isEmpty();
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
}
