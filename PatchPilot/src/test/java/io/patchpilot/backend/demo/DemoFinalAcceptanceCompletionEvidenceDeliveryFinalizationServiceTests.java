package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-29T05:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_mark_completion_evidence_delivery_finalized_when_latest_receipt_matches_bundle() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationServiceTests::readyBundle,
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo gate = service.getFinalizationGate();

        assertThat(gate.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(gate.finalized()).isTrue();
        assertThat(gate.summary()).isEqualTo(
                "Final acceptance completion evidence delivery is finalized with a fresh delivery receipt."
        );
        assertThat(gate.nextAction()).isEqualTo(
                "Use the finalization report as the reviewer-facing completion delivery record."
        );
        assertThat(gate.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(gate.latestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(gate.latestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(gate.latestTaskId()).isEqualTo("task-1");
        assertThat(gate.latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(gate.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(gate.deliveryReceiptFresh()).isTrue();
        assertThat(gate.deliveryReceiptFreshnessSummary())
                .isEqualTo("Latest completion evidence delivery receipt matches the current completion evidence bundle.");
        assertThat(gate.checks())
                .extracting(DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo.Check::name)
                .containsExactly("Completion evidence bundle", "Completion evidence delivery receipt");
        assertThat(gate.evidenceNotes()).contains(
                "Completion evidence bundle final-acceptance-completion-archive-1 is ready to share.",
                "Completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1 is fresh."
        );
        assertThat(gate.downloadActions()).contains(
                "Download final acceptance completion evidence delivery finalization report.",
                "Download final acceptance completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1."
        );
        assertThat(gate.sideEffectContract()).contains("read-only");
        assertThat(gate.markdownReport())
                .contains("# PatchPilot Final Acceptance Completion Evidence Delivery Finalization")
                .contains("- Status: `READY`")
                .contains("- Delivery receipt freshness: `FRESH`")
                .contains("final-acceptance-completion-evidence-delivery-receipt-1")
                .contains("does not create tasks, call the model, run tests");
        assertThat(gate.generatedAt()).isEqualTo(Instant.parse("2026-06-29T05:00:00Z"));
    }

    @Test
    void should_need_attention_when_completion_evidence_bundle_is_ready_but_delivery_receipt_is_missing() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationServiceTests::readyBundle,
                        List::of,
                        CLOCK
                );

        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo gate = service.getFinalizationGate();

        assertThat(gate.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(gate.finalized()).isFalse();
        assertThat(gate.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(gate.deliveryReceiptFresh()).isFalse();
        assertThat(gate.latestCompletionEvidenceDeliveryReceiptId()).isNull();
        assertThat(gate.nextAction()).isEqualTo(
                "Share the current final acceptance completion evidence bundle, record a delivery receipt, then download the finalization report."
        );
        assertThat(gate.evidenceNotes()).contains("No completion evidence delivery receipt is available.");
    }

    @Test
    void should_need_attention_when_latest_delivery_receipt_is_stale_for_current_bundle() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationServiceTests::readyBundle,
                        () -> List.of(staleReceipt()),
                        CLOCK
                );

        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo gate = service.getFinalizationGate();

        assertThat(gate.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(gate.finalized()).isFalse();
        assertThat(gate.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(gate.deliveryReceiptFresh()).isFalse();
        assertThat(gate.deliveryReceiptFreshnessSummary())
                .contains("does not match the current completion evidence bundle");
        assertThat(gate.nextAction()).isEqualTo(
                "Record a new final acceptance completion evidence delivery receipt for completion archive final-acceptance-completion-archive-1."
        );
        assertThat(gate.evidenceNotes()).contains(
                "Latest completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-old is stale."
        );
    }

    @Test
    void should_block_finalization_when_completion_evidence_bundle_is_not_ready() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService service =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService(
                        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationServiceTests::blockedBundle,
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo gate = service.getFinalizationGate();

        assertThat(gate.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(gate.finalized()).isFalse();
        assertThat(gate.deliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(gate.deliveryReceiptFresh()).isFalse();
        assertThat(gate.summary()).isEqualTo(
                "Final acceptance completion evidence delivery finalization is blocked because the completion evidence bundle is not ready."
        );
        assertThat(gate.nextAction()).isEqualTo(
                "Resolve final acceptance finalization blockers before sharing the completion evidence bundle."
        );
        assertThat(gate.evidenceNotes()).contains("Completion evidence bundle is not ready to share.");
    }

    private static DemoFinalAcceptanceCompletionEvidenceBundleVo readyBundle() {
        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share this final acceptance completion evidence bundle with reviewers.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "task-1",
                1,
                Instant.parse("2026-06-29T04:00:00Z"),
                Instant.parse("2026-06-29T04:05:00Z"),
                List.of("Latest completion archive final-acceptance-completion-archive-1 is finalized."),
                List.of("Download final acceptance completion evidence bundle."),
                "GET /api/demo/final-acceptance-completion-evidence-bundle is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Bundle"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceBundleVo blockedBundle() {
        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "Final acceptance completion evidence bundle is blocked by finalization.",
                "Resolve final acceptance finalization blockers before sharing the completion evidence bundle.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "task-1",
                1,
                Instant.parse("2026-06-29T04:00:00Z"),
                Instant.parse("2026-06-29T04:05:00Z"),
                List.of("Final acceptance share finalization is BLOCKED."),
                List.of("Resolve finalization blockers."),
                "GET /api/demo/final-acceptance-completion-evidence-bundle is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Bundle"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo freshReceipt() {
        return receipt(
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo staleReceipt() {
        return receipt(
                "final-acceptance-completion-evidence-delivery-receipt-old",
                "final-acceptance-completion-archive-old",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt(
            String id,
            String completionArchiveId,
            String sharePackageArchiveId,
            String deliveryReceiptId,
            String taskId
    ) {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                id,
                DemoReadinessStatus.READY,
                true,
                DemoReadinessStatus.READY,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share this final acceptance completion evidence bundle with reviewers.",
                completionArchiveId,
                sharePackageArchiveId,
                deliveryReceiptId,
                taskId,
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final completion evidence bundle to the reviewer.",
                Instant.parse("2026-06-29T04:25:00Z"),
                Instant.parse("2026-06-29T04:30:00Z"),
                "# PatchPilot Final Acceptance Completion Evidence Delivery Receipt"
        );
    }
}
