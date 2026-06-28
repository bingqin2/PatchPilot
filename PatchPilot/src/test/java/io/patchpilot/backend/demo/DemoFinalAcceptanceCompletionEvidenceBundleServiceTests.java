package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionEvidenceBundleServiceTests {

    @Test
    void should_build_ready_completion_evidence_bundle_from_finalization_and_latest_archive() {
        DemoFinalAcceptanceCompletionEvidenceBundleService service =
                new DemoFinalAcceptanceCompletionEvidenceBundleService(
                        DemoFinalAcceptanceCompletionEvidenceBundleServiceTests::readyFinalization,
                        () -> List.of(completionArchive())
                );

        DemoFinalAcceptanceCompletionEvidenceBundleVo bundle = service.getBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.readyToShare()).isTrue();
        assertThat(bundle.summary()).isEqualTo("PatchPilot final acceptance completion evidence bundle is ready to share.");
        assertThat(bundle.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(bundle.latestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(bundle.latestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(bundle.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(bundle.latestTaskId()).isEqualTo("task-1");
        assertThat(bundle.completionArchiveCount()).isEqualTo(1);
        assertThat(bundle.evidenceNotes()).contains(
                "Latest completion archive final-acceptance-completion-archive-1 is finalized.",
                "Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."
        );
        assertThat(bundle.downloadActions()).contains(
                "Download final acceptance completion evidence bundle.",
                "Download final acceptance completion archive final-acceptance-completion-archive-1.",
                "Download final acceptance share finalization report."
        );
        assertThat(bundle.sideEffectContract()).contains("read-only");
        assertThat(bundle.markdownReport()).contains("# PatchPilot Final Acceptance Completion Evidence Bundle");
        assertThat(bundle.markdownReport()).contains("Latest completion archive: `final-acceptance-completion-archive-1`");
    }

    @Test
    void should_require_completion_archive_before_bundle_is_share_ready() {
        DemoFinalAcceptanceCompletionEvidenceBundleService service =
                new DemoFinalAcceptanceCompletionEvidenceBundleService(
                        DemoFinalAcceptanceCompletionEvidenceBundleServiceTests::readyFinalization,
                        List::of
                );

        DemoFinalAcceptanceCompletionEvidenceBundleVo bundle = service.getBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.readyToShare()).isFalse();
        assertThat(bundle.summary()).isEqualTo("Final acceptance completion evidence bundle is waiting for a completion archive.");
        assertThat(bundle.latestCompletionArchiveId()).isNull();
        assertThat(bundle.nextAction()).isEqualTo("Archive the finalized final acceptance completion before sharing the evidence bundle.");
        assertThat(bundle.evidenceNotes()).contains("No final acceptance completion archive is available.");
    }

    @Test
    void should_block_bundle_when_finalization_is_not_ready() {
        DemoFinalAcceptanceCompletionEvidenceBundleService service =
                new DemoFinalAcceptanceCompletionEvidenceBundleService(
                        DemoFinalAcceptanceCompletionEvidenceBundleServiceTests::blockedFinalization,
                        () -> List.of(completionArchive())
                );

        DemoFinalAcceptanceCompletionEvidenceBundleVo bundle = service.getBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(bundle.readyToShare()).isFalse();
        assertThat(bundle.summary()).isEqualTo("Final acceptance completion evidence bundle is blocked by finalization.");
        assertThat(bundle.nextAction()).isEqualTo("Resolve final acceptance finalization blockers before sharing the completion evidence bundle.");
        assertThat(bundle.evidenceNotes()).contains("Final acceptance share finalization is BLOCKED.");
    }

    private static DemoFinalAcceptanceShareFinalizationVo readyFinalization() {
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
                List.of("Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceShareFinalizationVo blockedFinalization() {
        return new DemoFinalAcceptanceShareFinalizationVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "Final demo acceptance share package is not finalized.",
                "Record a fresh delivery receipt before finalizing acceptance.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                null,
                null,
                null,
                null,
                "MISSING",
                false,
                "No delivery receipt is available.",
                List.of(),
                List.of("Delivery receipt is missing."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z")
        );
    }

    private static DemoFinalAcceptanceCompletionArchiveVo completionArchive() {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                "final-acceptance-completion-archive-1",
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
                List.of("Latest final acceptance share package archive is send-ready."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z"),
                Instant.parse("2026-06-29T04:00:00Z")
        );
    }
}
