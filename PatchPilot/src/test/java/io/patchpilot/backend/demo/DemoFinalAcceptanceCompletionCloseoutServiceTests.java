package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionCloseoutServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-29T06:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_close_final_acceptance_completion_when_all_final_evidence_is_ready() {
        DemoFinalAcceptanceCompletionCloseoutService service = readyService();

        DemoFinalAcceptanceCompletionCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(closeout.closed()).isTrue();
        assertThat(closeout.summary()).isEqualTo(
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof."
        );
        assertThat(closeout.nextAction()).isEqualTo(
                "Use this closeout report as the final external-review completion record."
        );
        assertThat(closeout.latestTaskId()).isEqualTo("task-1");
        assertThat(closeout.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(closeout.latestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(closeout.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(closeout.latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(closeout.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(closeout.checks())
                .extracting(DemoFinalAcceptanceCompletionCloseoutVo.Check::name)
                .containsExactly(
                        "Final acceptance summary",
                        "Reviewer package finalization",
                        "Completion evidence bundle",
                        "Completion evidence delivery finalization"
                );
        assertThat(closeout.evidenceNotes()).contains(
                "Final demo acceptance summary is accepted.",
                "Final acceptance share package is finalized.",
                "Completion evidence bundle final-acceptance-completion-archive-1 is ready to share.",
                "Completion evidence delivery finalization is fresh."
        );
        assertThat(closeout.downloadActions()).contains(
                "Download final demo acceptance summary report.",
                "Download final acceptance completion evidence bundle.",
                "Download final acceptance completion delivery finalization report.",
                "Download final acceptance completion closeout report."
        );
        assertThat(closeout.sideEffectContract()).contains("read-only");
        assertThat(closeout.markdownReport())
                .contains("# PatchPilot Final Acceptance Completion Closeout")
                .contains("- Status: `READY`")
                .contains("- Closed: `true`")
                .contains("final-acceptance-completion-evidence-delivery-receipt-1")
                .contains("does not create tasks, call the model, run tests");
        assertThat(closeout.generatedAt()).isEqualTo(Instant.parse("2026-06-29T06:00:00Z"));
    }

    @Test
    void should_need_attention_when_completion_delivery_finalization_is_not_finalized() {
        DemoFinalAcceptanceCompletionCloseoutService service = new DemoFinalAcceptanceCompletionCloseoutService(
                DemoFinalAcceptanceCompletionCloseoutServiceTests::acceptedSummary,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyShareFinalization,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyBundle,
                () -> List.of(completionArchive()),
                () -> List.of(completionReceipt()),
                DemoFinalAcceptanceCompletionCloseoutServiceTests::missingCompletionDeliveryFinalization,
                CLOCK
        );

        DemoFinalAcceptanceCompletionCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(closeout.closed()).isFalse();
        assertThat(closeout.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(closeout.nextAction()).isEqualTo(
                "Record a fresh completion evidence delivery receipt, then download the closeout report again."
        );
        assertThat(closeout.evidenceNotes()).contains(
                "Completion evidence delivery finalization is not finalized."
        );
    }

    @Test
    void should_block_when_final_acceptance_summary_is_not_accepted() {
        DemoFinalAcceptanceCompletionCloseoutService service = new DemoFinalAcceptanceCompletionCloseoutService(
                DemoFinalAcceptanceCompletionCloseoutServiceTests::blockedSummary,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyShareFinalization,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyBundle,
                () -> List.of(completionArchive()),
                () -> List.of(completionReceipt()),
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyCompletionDeliveryFinalization,
                CLOCK
        );

        DemoFinalAcceptanceCompletionCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(closeout.closed()).isFalse();
        assertThat(closeout.summary()).isEqualTo(
                "PatchPilot final acceptance completion closeout is blocked because final demo acceptance is not accepted."
        );
        assertThat(closeout.nextAction()).isEqualTo("Resolve launch and task certificate blockers.");
    }

    @Test
    void should_block_when_completion_evidence_bundle_is_not_ready_to_share() {
        DemoFinalAcceptanceCompletionCloseoutService service = new DemoFinalAcceptanceCompletionCloseoutService(
                DemoFinalAcceptanceCompletionCloseoutServiceTests::acceptedSummary,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyShareFinalization,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::blockedBundle,
                () -> List.of(completionArchive()),
                () -> List.of(completionReceipt()),
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyCompletionDeliveryFinalization,
                CLOCK
        );

        DemoFinalAcceptanceCompletionCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(closeout.closed()).isFalse();
        assertThat(closeout.summary()).isEqualTo(
                "PatchPilot final acceptance completion closeout is blocked because the completion evidence bundle is not ready."
        );
        assertThat(closeout.nextAction()).isEqualTo(
                "Resolve final acceptance finalization blockers before sharing the completion evidence bundle."
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutService readyService() {
        return new DemoFinalAcceptanceCompletionCloseoutService(
                DemoFinalAcceptanceCompletionCloseoutServiceTests::acceptedSummary,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyShareFinalization,
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyBundle,
                () -> List.of(completionArchive()),
                () -> List.of(completionReceipt()),
                DemoFinalAcceptanceCompletionCloseoutServiceTests::readyCompletionDeliveryFinalization,
                CLOCK
        );
    }

    private static DemoAcceptanceSummaryVo acceptedSummary() {
        return new DemoAcceptanceSummaryVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance is ready for external review.",
                "Share the final acceptance package.",
                DemoReadinessStatus.READY,
                true,
                true,
                "launch-certificate-archive-1",
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                "launch-delivery-receipt-1",
                DemoReadinessStatus.READY,
                true,
                true,
                "task-evidence-certificate-archive-1",
                "task-evidence-closeout-archive-1",
                "task-evidence-archive-1",
                "task-evidence-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                Instant.parse("2026-06-29T03:00:00Z"),
                List.of(),
                List.of("Launch and task certificates are certified."),
                List.of("Download final demo acceptance summary report."),
                "GET /api/demo/acceptance-summary is read-only.",
                "# PatchPilot Final Demo Acceptance"
        );
    }

    private static DemoAcceptanceSummaryVo blockedSummary() {
        DemoAcceptanceSummaryVo ready = acceptedSummary();
        return new DemoAcceptanceSummaryVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "PatchPilot final demo acceptance is blocked.",
                "Resolve launch and task certificate blockers.",
                ready.launchCertificateStatus(),
                ready.launchCertificateArchived(),
                false,
                ready.launchCertificateArchiveId(),
                ready.launchCloseoutArchiveId(),
                ready.launchEvidenceArchiveId(),
                ready.launchDeliveryReceiptId(),
                ready.taskCertificateStatus(),
                ready.taskCertificateArchived(),
                ready.taskCertificateCertified(),
                ready.taskCertificateArchiveId(),
                ready.taskCloseoutArchiveId(),
                ready.taskEvidenceArchiveId(),
                ready.taskDeliveryReceiptId(),
                ready.latestTaskId(),
                ready.latestPullRequestUrl(),
                ready.generatedAt(),
                ready.checks(),
                ready.evidenceNotes(),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport()
        );
    }

    private static DemoFinalAcceptanceShareFinalizationVo readyShareFinalization() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use finalization evidence for external review.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:30:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance package.",
                List.of(),
                List.of("Final acceptance share package is finalized."),
                "# PatchPilot Final Acceptance Share Finalization",
                Instant.parse("2026-06-29T03:35:00Z")
        );
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
        DemoFinalAcceptanceCompletionEvidenceBundleVo ready = readyBundle();
        return new DemoFinalAcceptanceCompletionEvidenceBundleVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "Final acceptance completion evidence bundle is blocked by finalization.",
                "Resolve final acceptance finalization blockers before sharing the completion evidence bundle.",
                ready.latestCompletionArchiveId(),
                ready.latestSharePackageArchiveId(),
                ready.latestDeliveryReceiptId(),
                ready.latestDeliveryTarget(),
                ready.latestDeliveryChannel(),
                ready.latestTaskId(),
                ready.completionArchiveCount(),
                ready.latestArchivedAt(),
                ready.generatedAt(),
                ready.evidenceNotes(),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport()
        );
    }

    private static DemoFinalAcceptanceCompletionArchiveVo completionArchive() {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                "final-acceptance-completion-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use archived completion proof for external review.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:30:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance package.",
                List.of("Final acceptance share package is finalized."),
                "# PatchPilot Final Acceptance Completion Archive",
                Instant.parse("2026-06-29T03:35:00Z"),
                Instant.parse("2026-06-29T04:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo completionReceipt() {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                "final-acceptance-completion-evidence-delivery-receipt-1",
                DemoReadinessStatus.READY,
                true,
                DemoReadinessStatus.READY,
                "PatchPilot final acceptance completion evidence bundle is ready to share.",
                "Share this final acceptance completion evidence bundle with reviewers.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1",
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final completion evidence bundle to the reviewer.",
                Instant.parse("2026-06-29T04:25:00Z"),
                Instant.parse("2026-06-29T04:30:00Z"),
                "# PatchPilot Final Acceptance Completion Evidence Delivery Receipt"
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo readyCompletionDeliveryFinalization() {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final acceptance completion evidence delivery is finalized with a fresh delivery receipt.",
                "Use the finalization report as the reviewer-facing completion delivery record.",
                "final-acceptance-completion-archive-1",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-delivery-receipt-1",
                "task-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                true,
                "Latest completion evidence delivery receipt matches the current completion evidence bundle.",
                List.of(),
                List.of("Completion evidence delivery finalization is fresh."),
                List.of("Download final acceptance completion evidence delivery finalization report."),
                "GET /api/demo/final-acceptance-completion-evidence-delivery-finalization is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Delivery Finalization",
                Instant.parse("2026-06-29T05:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo missingCompletionDeliveryFinalization() {
        DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo ready = readyCompletionDeliveryFinalization();
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Final acceptance completion evidence bundle is ready but has no completion evidence delivery receipt.",
                "Record a fresh completion evidence delivery receipt, then download the closeout report again.",
                ready.latestCompletionArchiveId(),
                ready.latestSharePackageArchiveId(),
                ready.latestDeliveryReceiptId(),
                ready.latestTaskId(),
                null,
                ready.latestDeliveryTarget(),
                ready.latestDeliveryChannel(),
                null,
                "MISSING",
                false,
                "No completion evidence delivery receipt is available for the current completion evidence bundle.",
                ready.checks(),
                List.of("Completion evidence delivery finalization is not finalized."),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport(),
                ready.generatedAt()
        );
    }
}
