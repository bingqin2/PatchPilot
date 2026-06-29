package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewReleaseBundleArchiveServiceTests {

    @Test
    void archives_ready_final_external_review_release_bundle() {
        DemoFinalExternalReviewReleaseBundleArchiveService service =
                new DemoFinalExternalReviewReleaseBundleArchiveService(
                        DemoFinalExternalReviewReleaseBundleArchiveServiceTests::readyReleaseBundle,
                        new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T12:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-archive-1"
                );

        DemoFinalExternalReviewReleaseBundleArchiveVo archive = service.archiveCurrentReleaseBundle();

        assertThat(archive.id()).isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.releaseReady()).isTrue();
        assertThat(archive.summary()).isEqualTo("PatchPilot final external-review release bundle is ready.");
        assertThat(archive.nextAction())
                .isEqualTo("Share the release bundle report and listed attachments with external reviewers.");
        assertThat(archive.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(archive.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(archive.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(archive.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(archive.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.latestDeliveryChannel()).isEqualTo("email");
        assertThat(archive.latestDeliveredAt()).isEqualTo("2026-06-29T09:25:00Z");
        assertThat(archive.latestCertificateArchivedAt()).isEqualTo(Instant.parse("2026-06-29T11:30:00Z"));
        assertThat(archive.requiredAttachments()).containsExactly(
                "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1",
                "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1"
        );
        assertThat(archive.releaseChecks())
                .extracting(DemoFinalExternalReviewReleaseBundleArchiveVo.ReleaseCheck::name)
                .containsExactly("Final delivery certificate archive");
        assertThat(archive.evidenceNotes())
                .contains("Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth.");
        assertThat(archive.downloadActions()).contains("Download final external-review release bundle report.");
        assertThat(archive.sideEffectContract()).contains("read-only");
        assertThat(archive.report()).contains("# PatchPilot Final External Review Release Bundle");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T12:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T12:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-external-review-release-bundle-archive-1")).contains(archive);
    }

    @Test
    void rejects_archive_when_release_bundle_is_not_ready() {
        DemoFinalExternalReviewReleaseBundleArchiveService service =
                new DemoFinalExternalReviewReleaseBundleArchiveService(
                        DemoFinalExternalReviewReleaseBundleArchiveServiceTests::missingReleaseBundle,
                        new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T12:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentReleaseBundle)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review release bundle is not ready");
    }

    @Test
    void keeps_only_twenty_recent_release_bundle_archives() {
        DemoFinalExternalReviewReleaseBundleArchiveService service =
                new DemoFinalExternalReviewReleaseBundleArchiveService(
                        DemoFinalExternalReviewReleaseBundleArchiveServiceTests::readyReleaseBundle,
                        new InMemoryDemoFinalExternalReviewReleaseBundleArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T12:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentReleaseBundle();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalExternalReviewReleaseBundleArchiveVo::id)
                .containsExactly(
                        "final-external-review-release-bundle-archive-22",
                        "final-external-review-release-bundle-archive-21",
                        "final-external-review-release-bundle-archive-20",
                        "final-external-review-release-bundle-archive-19",
                        "final-external-review-release-bundle-archive-18",
                        "final-external-review-release-bundle-archive-17",
                        "final-external-review-release-bundle-archive-16",
                        "final-external-review-release-bundle-archive-15",
                        "final-external-review-release-bundle-archive-14",
                        "final-external-review-release-bundle-archive-13",
                        "final-external-review-release-bundle-archive-12",
                        "final-external-review-release-bundle-archive-11",
                        "final-external-review-release-bundle-archive-10",
                        "final-external-review-release-bundle-archive-9",
                        "final-external-review-release-bundle-archive-8",
                        "final-external-review-release-bundle-archive-7",
                        "final-external-review-release-bundle-archive-6",
                        "final-external-review-release-bundle-archive-5",
                        "final-external-review-release-bundle-archive-4",
                        "final-external-review-release-bundle-archive-3"
                );
        assertThat(service.findArchive("final-external-review-release-bundle-archive-1")).isEmpty();
    }

    private static DemoFinalExternalReviewReleaseBundleVo readyReleaseBundle() {
        return new DemoFinalExternalReviewReleaseBundleVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review release bundle is ready.",
                "Share the release bundle report and listed attachments with external reviewers.",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                Instant.parse("2026-06-29T11:30:00Z"),
                Instant.parse("2026-06-29T12:00:00Z"),
                List.of(
                        "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1",
                        "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1"
                ),
                List.of(new DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck(
                        "Final delivery certificate archive",
                        DemoReadinessStatus.READY,
                        "Latest final external-review delivery certificate archive is certified.",
                        "No action needed."
                )),
                List.of("Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth."),
                List.of("Download final external-review release bundle report."),
                "GET /api/demo/final-external-review-release-bundle is read-only.",
                "# PatchPilot Final External Review Release Bundle"
        );
    }

    private static DemoFinalExternalReviewReleaseBundleVo missingReleaseBundle() {
        DemoFinalExternalReviewReleaseBundleVo ready = readyReleaseBundle();
        return new DemoFinalExternalReviewReleaseBundleVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "No final external-review release bundle is available.",
                "Archive the certified final external-review delivery certificate, then download the release bundle.",
                null,
                ready.latestDeliveryFinalizationArchiveId(),
                ready.latestPackageArchiveId(),
                ready.latestDeliveryReceiptId(),
                ready.latestTaskId(),
                ready.latestPullRequestUrl(),
                ready.latestDeliveryTarget(),
                ready.latestDeliveryChannel(),
                ready.latestDeliveredAt(),
                null,
                ready.generatedAt(),
                List.of(),
                ready.releaseChecks(),
                ready.evidenceNotes(),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-external-review-release-bundle-archive-" + nextId++;
        }
    }
}
