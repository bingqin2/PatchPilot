package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveServiceTests {

    @Test
    void archives_ready_final_external_review_package_delivery_finalization() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService(
                        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveServiceTests::readyFinalization,
                        new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T10:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-delivery-finalization-archive-1"
                );

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive =
                service.archiveCurrentFinalization();

        assertThat(archive.id()).isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.finalized()).isTrue();
        assertThat(archive.summary()).contains("finalized with a fresh package delivery receipt");
        assertThat(archive.nextAction()).contains("Use the finalization report");
        assertThat(archive.latestArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(archive.latestCloseoutArchiveId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(archive.latestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(archive.latestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(archive.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.latestDeliveryChannel()).isEqualTo("email");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.deliveryReceiptFresh()).isTrue();
        assertThat(archive.deliveryReceiptFreshnessSummary()).contains("matches the current frozen");
        assertThat(archive.checks()).extracting(
                        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check::name
                )
                .contains("Frozen final external-review package");
        assertThat(archive.evidenceNotes())
                .contains("Frozen final external-review package final-external-review-package-archive-1 is ready.");
        assertThat(archive.downloadActions())
                .contains("Download final external-review package delivery finalization report.");
        assertThat(archive.sideEffectContract()).contains("read-only");
        assertThat(archive.report()).contains("# PatchPilot Final External Review Package Delivery Finalization");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T10:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T10:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-external-review-package-delivery-finalization-archive-1"))
                .contains(archive);
    }

    @Test
    void rejects_archive_when_delivery_finalization_is_not_ready() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService(
                        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveServiceTests::blockedFinalization,
                        new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T10:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-delivery-finalization-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentFinalization)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review package delivery finalization is not ready");
    }

    @Test
    void keeps_only_twenty_recent_delivery_finalization_archives() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService service =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService(
                        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveServiceTests::readyFinalization,
                        new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T10:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentFinalization();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo::id)
                .containsExactly(
                        "final-external-review-package-delivery-finalization-archive-22",
                        "final-external-review-package-delivery-finalization-archive-21",
                        "final-external-review-package-delivery-finalization-archive-20",
                        "final-external-review-package-delivery-finalization-archive-19",
                        "final-external-review-package-delivery-finalization-archive-18",
                        "final-external-review-package-delivery-finalization-archive-17",
                        "final-external-review-package-delivery-finalization-archive-16",
                        "final-external-review-package-delivery-finalization-archive-15",
                        "final-external-review-package-delivery-finalization-archive-14",
                        "final-external-review-package-delivery-finalization-archive-13",
                        "final-external-review-package-delivery-finalization-archive-12",
                        "final-external-review-package-delivery-finalization-archive-11",
                        "final-external-review-package-delivery-finalization-archive-10",
                        "final-external-review-package-delivery-finalization-archive-9",
                        "final-external-review-package-delivery-finalization-archive-8",
                        "final-external-review-package-delivery-finalization-archive-7",
                        "final-external-review-package-delivery-finalization-archive-6",
                        "final-external-review-package-delivery-finalization-archive-5",
                        "final-external-review-package-delivery-finalization-archive-4",
                        "final-external-review-package-delivery-finalization-archive-3"
                );
        assertThat(service.findArchive("final-external-review-package-delivery-finalization-archive-1"))
                .isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo readyFinalization() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery is finalized with a fresh package delivery receipt.",
                "Use the finalization report as proof that the frozen external-review package was delivered.",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo.Check(
                        "Frozen final external-review package",
                        DemoReadinessStatus.READY,
                        "Frozen final external-review package is ready.",
                        "No action needed."
                )),
                List.of("Frozen final external-review package final-external-review-package-archive-1 is ready."),
                List.of("Download final external-review package delivery finalization report."),
                "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.parse("2026-06-29T10:00:00Z")
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo blockedFinalization() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo ready = readyFinalization();
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Final external-review package archive is ready but has no package delivery receipt.",
                "Deliver the frozen final external-review package, record a package delivery receipt, then download the finalization report.",
                ready.latestArchiveId(),
                null,
                ready.latestCloseoutArchiveId(),
                ready.latestCompletionArchiveId(),
                ready.latestCompletionEvidenceDeliveryReceiptId(),
                ready.latestTaskId(),
                ready.latestPullRequestUrl(),
                ready.latestDeliveryTarget(),
                ready.latestDeliveryChannel(),
                null,
                "MISSING",
                false,
                "No package delivery receipt is available for the current frozen final external-review package.",
                ready.checks(),
                ready.evidenceNotes(),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport(),
                ready.generatedAt()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-external-review-package-delivery-finalization-archive-" + nextId++;
        }
    }
}
