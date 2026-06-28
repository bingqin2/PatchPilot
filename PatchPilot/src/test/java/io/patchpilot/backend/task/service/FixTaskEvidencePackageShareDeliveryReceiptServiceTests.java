package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.dto.FixTaskEvidencePackageShareDeliveryReceiptDto;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareCenterVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixTaskEvidencePackageShareDeliveryReceiptServiceTests {

    @Test
    void should_record_task_evidence_delivery_receipt_from_share_ready_center() {
        InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository repository =
                new InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository();
        FixTaskEvidencePackageShareDeliveryReceiptService service =
                new FixTaskEvidencePackageShareDeliveryReceiptService(
                        FixTaskEvidencePackageShareDeliveryReceiptServiceTests::shareReadyCenter,
                        repository,
                        Clock.fixed(Instant.parse("2026-06-28T06:10:00Z"), ZoneOffset.UTC),
                        () -> "task-evidence-delivery-receipt-1"
                );

        FixTaskEvidencePackageShareDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new FixTaskEvidencePackageShareDeliveryReceiptDto(
                        "email",
                        "reviewer@example.com",
                        "local-operator",
                        "Sent task evidence after PR review.",
                        Instant.parse("2026-06-28T06:05:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo("READY");
        assertThat(receipt.taskEvidenceArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(receipt.taskId()).isEqualTo("task-1");
        assertThat(receipt.repositoryOwner()).isEqualTo("bingqin2");
        assertThat(receipt.repositoryName()).isEqualTo("PatchPilot");
        assertThat(receipt.issueNumber()).isEqualTo(1);
        assertThat(receipt.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent task evidence after PR review.");
        assertThat(receipt.messageSubject()).isEqualTo("PatchPilot task evidence: task-1");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-28T06:05:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-28T06:10:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Task Evidence Delivery Receipt")
                .contains("- Task evidence archive: `task-evidence-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("POST /api/tasks/evidence-packages/share-delivery-receipts records local evidence only")
                .contains("does not send messages, create tasks, call the model, run tests, mutate Git, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("task-evidence-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void should_reject_receipt_when_task_evidence_is_not_share_ready() {
        InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository repository =
                new InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository();
        FixTaskEvidencePackageShareDeliveryReceiptService service =
                new FixTaskEvidencePackageShareDeliveryReceiptService(
                        FixTaskEvidencePackageShareDeliveryReceiptServiceTests::notReadyCenter,
                        repository,
                        Clock.fixed(Instant.parse("2026-06-28T06:10:00Z"), ZoneOffset.UTC),
                        () -> "task-evidence-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(new FixTaskEvidencePackageShareDeliveryReceiptDto(
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent task evidence after PR review.",
                Instant.parse("2026-06-28T06:05:00Z")
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("task evidence share center is not share-ready");
        assertThat(service.listRecentReceipts()).isEmpty();
    }

    static FixTaskEvidencePackageShareCenterVo shareReadyCenter() {
        return new FixTaskEvidencePackageShareCenterVo(
                "READY",
                true,
                "A shareable completed task evidence package is available for external review.",
                "Download archived task evidence task-evidence-archive-1 before sharing.",
                1,
                1,
                0,
                0,
                0,
                "task-evidence-archive-1",
                "task-1",
                "bingqin2",
                "PatchPilot",
                1L,
                Instant.parse("2026-06-20T01:05:00Z"),
                "task-evidence-archive-1",
                "task-1",
                "bingqin2",
                "PatchPilot",
                1L,
                "https://github.com/bingqin2/PatchPilot/pull/8",
                List.of("Download archived task evidence task-evidence-archive-1."),
                List.of("Shareable archive task-evidence-archive-1 completed with a Pull Request."),
                "Task evidence share center is read-only.",
                "# PatchPilot Task Evidence Share Center",
                Instant.parse("2026-06-20T01:12:00Z")
        );
    }

    private static FixTaskEvidencePackageShareCenterVo notReadyCenter() {
        return new FixTaskEvidencePackageShareCenterVo(
                "BLOCKED",
                false,
                "No archived task evidence packages are available for sharing.",
                "Archive a completed task with a Pull Request before preparing external evidence.",
                0,
                0,
                0,
                0,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of("Archive a completed task with Pull Request evidence before downloading a share package."),
                List.of("No latest archive is available."),
                "Task evidence share center is read-only.",
                "# PatchPilot Task Evidence Share Center",
                Instant.parse("2026-06-20T01:12:00Z")
        );
    }
}
