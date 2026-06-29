package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-29T07:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_build_ready_external_review_package_when_all_final_evidence_is_ready_and_archived() {
        DemoFinalExternalReviewEvidencePackageService service = readyService();

        DemoFinalExternalReviewEvidencePackageVo evidencePackage = service.getPackage();

        assertThat(evidencePackage.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.readyForExternalReview()).isTrue();
        assertThat(evidencePackage.summary()).isEqualTo("PatchPilot final external-review evidence package is ready.");
        assertThat(evidencePackage.nextAction()).isEqualTo(
                "Share this package with reviewers as the frozen external-review record."
        );
        assertThat(evidencePackage.finalAcceptanceSummaryStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.finalAcceptanceShareFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.completionEvidenceBundleStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.completionDeliveryFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.completionCloseoutStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.closeoutArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.latestTaskId()).isEqualTo("task-1");
        assertThat(evidencePackage.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(evidencePackage.finalAcceptanceSharePackageArchiveId())
                .isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(evidencePackage.completionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(evidencePackage.completionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(evidencePackage.closeoutArchiveId())
                .isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(evidencePackage.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(evidencePackage.closeoutArchivedAt()).isEqualTo(Instant.parse("2026-06-29T06:30:00Z"));
        assertThat(evidencePackage.generatedAt()).isEqualTo(Instant.parse("2026-06-29T07:00:00Z"));
        assertThat(evidencePackage.checks())
                .extracting(DemoFinalExternalReviewEvidencePackageVo.Check::name)
                .containsExactly(
                        "Final demo acceptance summary",
                        "Reviewer package finalization",
                        "Completion evidence bundle",
                        "Completion evidence delivery finalization",
                        "Completion closeout",
                        "Frozen closeout archive"
                );
        assertThat(evidencePackage.evidenceNotes()).contains(
                "Final demo acceptance summary is accepted.",
                "Reviewer package finalization is READY.",
                "Completion evidence bundle final-acceptance-completion-archive-1 is ready to share.",
                "Completion delivery finalization is READY.",
                "Completion closeout is closed.",
                "Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."
        );
        assertThat(evidencePackage.downloadActions()).contains(
                "Download final external-review evidence package.",
                "Download final demo acceptance summary report.",
                "Download final acceptance share finalization report.",
                "Download final acceptance completion evidence bundle.",
                "Download final acceptance completion delivery finalization report.",
                "Download final acceptance completion closeout report.",
                "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1."
        );
        assertThat(evidencePackage.sideEffectContract()).contains("read-only");
        assertThat(evidencePackage.markdownReport())
                .contains("# PatchPilot Final External Review Evidence Package")
                .contains("- Ready for external review: `true`")
                .contains("- Closeout archive: `final-acceptance-completion-closeout-archive-1`")
                .contains("does not create tasks, call the model, run tests");
    }

    @Test
    void should_need_attention_when_frozen_closeout_archive_is_missing() {
        DemoFinalExternalReviewEvidencePackageService service = new DemoFinalExternalReviewEvidencePackageService(
                DemoFinalExternalReviewEvidencePackageServiceTests::acceptedSummary,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyShareFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionEvidenceBundle,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionDeliveryFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionCloseout,
                List::of,
                CLOCK
        );

        DemoFinalExternalReviewEvidencePackageVo evidencePackage = service.getPackage();

        assertThat(evidencePackage.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(evidencePackage.readyForExternalReview()).isFalse();
        assertThat(evidencePackage.summary()).isEqualTo(
                "PatchPilot final external-review evidence package is waiting for a frozen closeout archive."
        );
        assertThat(evidencePackage.nextAction()).isEqualTo(
                "Archive the READY final acceptance completion closeout before sharing the final external-review package."
        );
        assertThat(evidencePackage.closeoutArchiveId()).isNull();
        assertThat(evidencePackage.evidenceNotes()).contains("No frozen final acceptance completion closeout archive is available.");
    }

    @Test
    void should_block_when_final_acceptance_summary_is_not_accepted() {
        DemoFinalExternalReviewEvidencePackageService service = new DemoFinalExternalReviewEvidencePackageService(
                DemoFinalExternalReviewEvidencePackageServiceTests::blockedSummary,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyShareFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionEvidenceBundle,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionDeliveryFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionCloseout,
                () -> List.of(closeoutArchive(DemoReadinessStatus.READY, true)),
                CLOCK
        );

        DemoFinalExternalReviewEvidencePackageVo evidencePackage = service.getPackage();

        assertThat(evidencePackage.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(evidencePackage.readyForExternalReview()).isFalse();
        assertThat(evidencePackage.summary()).isEqualTo(
                "PatchPilot final external-review evidence package is blocked by final demo acceptance."
        );
        assertThat(evidencePackage.nextAction()).isEqualTo("Resolve launch and task certificate blockers.");
    }

    @Test
    void should_need_attention_when_latest_closeout_archive_is_not_closed() {
        DemoFinalExternalReviewEvidencePackageService service = new DemoFinalExternalReviewEvidencePackageService(
                DemoFinalExternalReviewEvidencePackageServiceTests::acceptedSummary,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyShareFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionEvidenceBundle,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionDeliveryFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionCloseout,
                () -> List.of(closeoutArchive(DemoReadinessStatus.NEEDS_ATTENTION, false)),
                CLOCK
        );

        DemoFinalExternalReviewEvidencePackageVo evidencePackage = service.getPackage();

        assertThat(evidencePackage.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(evidencePackage.readyForExternalReview()).isFalse();
        assertThat(evidencePackage.summary()).isEqualTo(
                "PatchPilot final external-review evidence package is waiting for a READY closed closeout archive."
        );
        assertThat(evidencePackage.nextAction()).isEqualTo(
                "Archive a READY and closed final acceptance completion closeout before sharing external-review evidence."
        );
        assertThat(evidencePackage.evidenceNotes()).contains(
                "Frozen closeout archive final-acceptance-completion-closeout-archive-1 is not closed."
        );
    }

    private static DemoFinalExternalReviewEvidencePackageService readyService() {
        return new DemoFinalExternalReviewEvidencePackageService(
                DemoFinalExternalReviewEvidencePackageServiceTests::acceptedSummary,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyShareFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionEvidenceBundle,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionDeliveryFinalization,
                DemoFinalExternalReviewEvidencePackageServiceTests::readyCompletionCloseout,
                () -> List.of(closeoutArchive(DemoReadinessStatus.READY, true)),
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
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of(),
                List.of("Latest final acceptance archive final-acceptance-share-package-archive-1 is send-ready."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionEvidenceBundleVo readyCompletionEvidenceBundle() {
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
                List.of("Completion evidence delivery receipt is fresh."),
                List.of("Download final acceptance completion evidence delivery finalization report."),
                "GET /api/demo/final-acceptance-completion-evidence-delivery-finalization is read-only.",
                "# PatchPilot Final Acceptance Completion Evidence Delivery Finalization",
                Instant.parse("2026-06-29T05:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutVo readyCompletionCloseout() {
        return new DemoFinalAcceptanceCompletionCloseoutVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of(),
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T06:00:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveVo closeoutArchive(
            DemoReadinessStatus status,
            boolean closed
    ) {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                "final-acceptance-completion-closeout-archive-1",
                status,
                closed,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T06:00:00Z"),
                Instant.parse("2026-06-29T06:30:00Z")
        );
    }
}
