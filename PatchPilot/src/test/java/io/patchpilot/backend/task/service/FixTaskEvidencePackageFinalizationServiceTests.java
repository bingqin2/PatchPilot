package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageFinalizationVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixTaskEvidencePackageFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-28T06:30:00Z"), ZoneOffset.UTC);

    private final FixTaskEvidencePackageArchiveService archiveService = mock(FixTaskEvidencePackageArchiveService.class);
    private final InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository receiptRepository =
            new InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository();
    private final FixTaskEvidencePackageFinalizationService service =
            new FixTaskEvidencePackageFinalizationService(archiveService, receiptRepository, CLOCK);

    @Test
    void should_report_ready_when_current_shareable_archive_has_fresh_delivery_receipt() {
        when(archiveService.shareCenter(20)).thenReturn(FixTaskEvidencePackageShareDeliveryReceiptServiceTests.shareReadyCenter());
        receiptRepository.save(receipt("task-evidence-delivery-receipt-1", "task-evidence-archive-1", "task-1"));

        FixTaskEvidencePackageFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("READY");
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary())
                .isEqualTo("Task evidence is finalized with a fresh delivery receipt for the current shareable archive.");
        assertThat(finalization.latestArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(finalization.latestTaskId()).isEqualTo("task-1");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.deliveryReceiptFresh()).isTrue();
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Task evidence share readiness:READY",
                        "Delivery receipt freshness:READY",
                        "Task evidence acceptance:READY"
                );
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Task Evidence Finalization Gate")
                .contains("- Status: `READY`")
                .contains("- Latest delivery receipt: `task-evidence-delivery-receipt-1`")
                .contains("GET /api/tasks/evidence-packages/finalization is read-only");
    }

    @Test
    void should_need_attention_when_receipt_is_missing() {
        when(archiveService.shareCenter(20)).thenReturn(FixTaskEvidencePackageShareDeliveryReceiptServiceTests.shareReadyCenter());

        FixTaskEvidencePackageFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.nextAction())
                .isEqualTo("Share the current task evidence package, record a delivery receipt, then download the finalization report.");
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `MISSING`")
                .contains("Record a task evidence delivery receipt after sharing the package.");
    }

    @Test
    void should_need_attention_when_latest_receipt_belongs_to_old_archive() {
        when(archiveService.shareCenter(20)).thenReturn(FixTaskEvidencePackageShareDeliveryReceiptServiceTests.shareReadyCenter());
        receiptRepository.save(receipt("task-evidence-delivery-receipt-old", "old-task-evidence-archive", "old-task"));

        FixTaskEvidencePackageFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("task-evidence-delivery-receipt-old");
        assertThat(finalization.nextAction())
                .isEqualTo("Record a new delivery receipt for task evidence archive task-evidence-archive-1, then download the finalization report.");
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `STALE`")
                .contains("Latest delivery receipt task-evidence-delivery-receipt-old belongs to old-task-evidence-archive/old-task");
    }

    private static FixTaskEvidencePackageShareDeliveryReceiptVo receipt(String id, String archiveId, String taskId) {
        return new FixTaskEvidencePackageShareDeliveryReceiptVo(
                id,
                "READY",
                archiveId,
                taskId,
                "bingqin2",
                "PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent task evidence.",
                "PatchPilot task evidence: " + taskId,
                Instant.parse("2026-06-28T06:05:00Z"),
                Instant.parse("2026-06-28T06:10:00Z"),
                "# PatchPilot Task Evidence Delivery Receipt"
        );
    }
}
