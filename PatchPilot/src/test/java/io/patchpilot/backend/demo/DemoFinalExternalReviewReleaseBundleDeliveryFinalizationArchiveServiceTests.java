package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveServiceTests {

    @Test
    void archives_ready_final_external_review_release_bundle_delivery_finalization() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService(
                        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveServiceTests::readyFinalization,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T14:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-finalization-archive-1"
                );

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive =
                service.archiveCurrentFinalization();

        assertThat(archive.id())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.finalized()).isTrue();
        assertThat(archive.summary()).contains("finalized with a fresh release-bundle receipt");
        assertThat(archive.nextAction()).contains("terminal reviewer handoff record");
        assertThat(archive.latestArchiveId()).isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(archive.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(archive.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(archive.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(archive.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(archive.latestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/9");
        assertThat(archive.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.latestDeliveryChannel()).isEqualTo("email");
        assertThat(archive.releaseBundleDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.releaseBundleDeliveryReceiptFresh()).isTrue();
        assertThat(archive.releaseBundleDeliveryReceiptFreshnessSummary()).contains("matches the current frozen");
        assertThat(archive.checks()).extracting(
                        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo.Check::name
                )
                .contains("Frozen final external-review release bundle");
        assertThat(archive.evidenceNotes())
                .contains("Frozen final external-review release bundle final-external-review-release-bundle-archive-1 is ready.");
        assertThat(archive.downloadActions())
                .contains("Download final external-review release bundle delivery finalization report.");
        assertThat(archive.sideEffectContract()).contains("read-only");
        assertThat(archive.report()).contains("# PatchPilot Final External Review Release Bundle Delivery Finalization");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T14:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T14:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-external-review-release-bundle-delivery-finalization-archive-1"))
                .contains(archive);
    }

    @Test
    void rejects_archive_when_release_bundle_delivery_finalization_is_not_ready() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService(
                        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveServiceTests::blockedFinalization,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T14:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-finalization-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentFinalization)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review release bundle delivery finalization is not ready");
    }

    @Test
    void keeps_only_twenty_recent_delivery_finalization_archives() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService(
                        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveServiceTests::readyFinalization,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T14:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentFinalization();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo::id)
                .containsExactly(
                        "final-external-review-release-bundle-delivery-finalization-archive-22",
                        "final-external-review-release-bundle-delivery-finalization-archive-21",
                        "final-external-review-release-bundle-delivery-finalization-archive-20",
                        "final-external-review-release-bundle-delivery-finalization-archive-19",
                        "final-external-review-release-bundle-delivery-finalization-archive-18",
                        "final-external-review-release-bundle-delivery-finalization-archive-17",
                        "final-external-review-release-bundle-delivery-finalization-archive-16",
                        "final-external-review-release-bundle-delivery-finalization-archive-15",
                        "final-external-review-release-bundle-delivery-finalization-archive-14",
                        "final-external-review-release-bundle-delivery-finalization-archive-13",
                        "final-external-review-release-bundle-delivery-finalization-archive-12",
                        "final-external-review-release-bundle-delivery-finalization-archive-11",
                        "final-external-review-release-bundle-delivery-finalization-archive-10",
                        "final-external-review-release-bundle-delivery-finalization-archive-9",
                        "final-external-review-release-bundle-delivery-finalization-archive-8",
                        "final-external-review-release-bundle-delivery-finalization-archive-7",
                        "final-external-review-release-bundle-delivery-finalization-archive-6",
                        "final-external-review-release-bundle-delivery-finalization-archive-5",
                        "final-external-review-release-bundle-delivery-finalization-archive-4",
                        "final-external-review-release-bundle-delivery-finalization-archive-3"
                );
        assertThat(service.findArchive("final-external-review-release-bundle-delivery-finalization-archive-1"))
                .isEmpty();
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo readyFinalization() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo(
                DemoReadinessStatus.READY,
                true,
                "Final external-review release bundle delivery is finalized with a fresh release-bundle receipt.",
                "Use the release bundle delivery finalization report as the terminal reviewer handoff record.",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/9",
                "reviewer@example.com",
                "email",
                "2026-06-29T13:25:00Z",
                "FRESH",
                true,
                "Latest release bundle delivery receipt matches the current frozen final external-review release bundle.",
                List.of(new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo.Check(
                        "Frozen final external-review release bundle",
                        DemoReadinessStatus.READY,
                        "Frozen final external-review release bundle is ready.",
                        "No action needed."
                )),
                List.of("Frozen final external-review release bundle final-external-review-release-bundle-archive-1 is ready."),
                List.of("Download final external-review release bundle delivery finalization report."),
                "GET /api/demo/final-external-review-release-bundle/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Release Bundle Delivery Finalization",
                Instant.parse("2026-06-29T14:00:00Z")
        );
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo blockedFinalization() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo ready = readyFinalization();
        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Final external-review release bundle archive is ready but has no release-bundle delivery receipt.",
                "Deliver the frozen final external-review release bundle, record a release-bundle delivery receipt, then download the finalization report.",
                ready.latestArchiveId(),
                null,
                ready.latestCertificateArchiveId(),
                ready.latestDeliveryFinalizationArchiveId(),
                ready.latestPackageArchiveId(),
                ready.latestPackageDeliveryReceiptId(),
                ready.latestTaskId(),
                ready.latestPullRequestUrl(),
                ready.latestDeliveryTarget(),
                ready.latestDeliveryChannel(),
                null,
                "MISSING",
                false,
                "No release bundle delivery receipt is available for the current frozen final external-review release bundle.",
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
            return "final-external-review-release-bundle-delivery-finalization-archive-" + nextId++;
        }
    }
}
