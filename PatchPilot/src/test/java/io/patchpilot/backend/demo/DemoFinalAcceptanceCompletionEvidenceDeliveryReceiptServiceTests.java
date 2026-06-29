package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptServiceTests {

    @Test
    void should_record_delivery_receipt_for_ready_completion_evidence_bundle() {
        InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository receiptRepository =
                new InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository();
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptServiceTests::readyBundle,
                        receiptRepository,
                        Clock.fixed(Instant.parse("2026-06-29T04:30:00Z"), ZoneOffset.UTC),
                        () -> "final-acceptance-completion-evidence-delivery-receipt-1"
                );

        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRequestDto(
                        "email",
                        "reviewer@example.com",
                        "local-operator",
                        "Sent final completion evidence bundle to the reviewer.",
                        Instant.parse("2026-06-29T04:25:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.readyToShare()).isTrue();
        assertThat(receipt.completionEvidenceBundleStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(receipt.latestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(receipt.latestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(receipt.latestTaskId()).isEqualTo("task-1");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent final completion evidence bundle to the reviewer.");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-29T04:25:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-29T04:30:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Final Acceptance Completion Evidence Delivery Receipt")
                .contains("- Completion evidence bundle status: `READY`")
                .contains("- Final completion archive: `final-acceptance-completion-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("POST /api/demo/final-acceptance-completion-evidence-delivery-receipts records local evidence only")
                .contains("does not send messages, create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("final-acceptance-completion-evidence-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void should_reject_delivery_receipt_when_completion_evidence_bundle_is_not_ready_to_share() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptServiceTests::notReadyBundle,
                        new InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T04:30:00Z"), ZoneOffset.UTC),
                        () -> "final-acceptance-completion-evidence-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final acceptance completion evidence bundle is not ready to share");
    }

    @Test
    void should_require_delivery_fields() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptServiceTests::readyBundle,
                        new InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T04:30:00Z"), ZoneOffset.UTC),
                        () -> "final-acceptance-completion-evidence-delivery-receipt-1"
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRequestDto(
                        " ",
                        "reviewer@example.com",
                        "local-operator",
                        "notes",
                        Instant.parse("2026-06-29T04:25:00Z")
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deliveryChannel is required");
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRequestDto validRequest() {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRequestDto(
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final completion evidence bundle to the reviewer.",
                Instant.parse("2026-06-29T04:25:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceBundleVo readyBundle() {
        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share the final acceptance completion evidence bundle with reviewers.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "task-1",
                1,
                Instant.parse("2026-06-29T04:00:00Z"),
                Instant.parse("2026-06-29T04:15:00Z"),
                List.of("Latest completion archive final-acceptance-completion-archive-1 is finalized."),
                List.of("Download final acceptance completion evidence bundle."),
                "GET /api/demo/final-acceptance-completion-evidence-bundle is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Bundle"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceBundleVo notReadyBundle() {
        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Final acceptance completion evidence bundle is waiting for a completion archive.",
                "Archive the finalized final acceptance completion before sharing the evidence bundle.",
                null,
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "task-1",
                0,
                null,
                Instant.parse("2026-06-29T04:15:00Z"),
                List.of("No final acceptance completion archive is available."),
                List.of("Create the final acceptance completion archive."),
                "GET /api/demo/final-acceptance-completion-evidence-bundle is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Bundle"
        );
    }
}
