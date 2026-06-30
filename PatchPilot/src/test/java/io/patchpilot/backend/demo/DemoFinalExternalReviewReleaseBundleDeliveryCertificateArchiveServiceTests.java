package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveServiceTests {

    @Test
    void archives_certified_final_external_review_release_bundle_delivery_certificate() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService(
                        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveServiceTests::certifiedCertificate,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-30T03:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-certificate-archive-1"
                );

        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive =
                service.archiveCurrentCertificate();

        assertThat(archive.id())
                .isEqualTo("final-external-review-release-bundle-delivery-certificate-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.certified()).isTrue();
        assertThat(archive.summary())
                .isEqualTo("Final external-review release bundle delivery is certified from the latest finalized archive.");
        assertThat(archive.nextAction())
                .isEqualTo("Share the release bundle delivery certificate report as the terminal reviewer handoff proof.");
        assertThat(archive.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(archive.latestReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(archive.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(archive.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(archive.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(archive.latestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(archive.releaseBundleDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.releaseBundleDeliveryReceiptFresh()).isTrue();
        assertThat(archive.checks())
                .extracting(DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo.Check::name)
                .containsExactly("Final external-review release bundle delivery finalization archive");
        assertThat(archive.evidenceNotes())
                .contains("Release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1 is finalized.");
        assertThat(archive.downloadActions())
                .contains("Download final external-review release bundle delivery certificate report.");
        assertThat(archive.report()).contains("# PatchPilot Final External Review Release Bundle Delivery Certificate");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-30T03:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-30T03:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-external-review-release-bundle-delivery-certificate-archive-1"))
                .contains(archive);
    }

    @Test
    void rejects_archive_when_certificate_is_not_certified() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService(
                        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveServiceTests::uncertifiedCertificate,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-30T03:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-release-bundle-delivery-certificate-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentCertificate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review release bundle delivery certificate is not certified");
    }

    @Test
    void keeps_only_twenty_recent_certificate_archives() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService(
                        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveServiceTests::certifiedCertificate,
                        new InMemoryDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-30T03:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentCertificate();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo::id)
                .containsExactly(
                        "final-external-review-release-bundle-delivery-certificate-archive-22",
                        "final-external-review-release-bundle-delivery-certificate-archive-21",
                        "final-external-review-release-bundle-delivery-certificate-archive-20",
                        "final-external-review-release-bundle-delivery-certificate-archive-19",
                        "final-external-review-release-bundle-delivery-certificate-archive-18",
                        "final-external-review-release-bundle-delivery-certificate-archive-17",
                        "final-external-review-release-bundle-delivery-certificate-archive-16",
                        "final-external-review-release-bundle-delivery-certificate-archive-15",
                        "final-external-review-release-bundle-delivery-certificate-archive-14",
                        "final-external-review-release-bundle-delivery-certificate-archive-13",
                        "final-external-review-release-bundle-delivery-certificate-archive-12",
                        "final-external-review-release-bundle-delivery-certificate-archive-11",
                        "final-external-review-release-bundle-delivery-certificate-archive-10",
                        "final-external-review-release-bundle-delivery-certificate-archive-9",
                        "final-external-review-release-bundle-delivery-certificate-archive-8",
                        "final-external-review-release-bundle-delivery-certificate-archive-7",
                        "final-external-review-release-bundle-delivery-certificate-archive-6",
                        "final-external-review-release-bundle-delivery-certificate-archive-5",
                        "final-external-review-release-bundle-delivery-certificate-archive-4",
                        "final-external-review-release-bundle-delivery-certificate-archive-3"
                );
        assertThat(service.findArchive("final-external-review-release-bundle-delivery-certificate-archive-1"))
                .isEmpty();
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certifiedCertificate() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo(
                DemoReadinessStatus.READY,
                true,
                "Final external-review release bundle delivery is certified from the latest finalized archive.",
                "Share the release bundle delivery certificate report as the terminal reviewer handoff proof.",
                "final-external-review-release-bundle-delivery-finalization-archive-1",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-30T02:10:00Z",
                Instant.parse("2026-06-30T02:45:00Z"),
                "FRESH",
                true,
                List.of(new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check(
                        "Final external-review release bundle delivery finalization archive",
                        DemoReadinessStatus.READY,
                        "Latest release bundle delivery finalization archive is finalized.",
                        "No action needed."
                )),
                List.of("Release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1 is finalized."),
                List.of("Download final external-review release bundle delivery certificate report."),
                "GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only.",
                "# PatchPilot Final External Review Release Bundle Delivery Certificate",
                Instant.parse("2026-06-30T03:00:00Z")
        );
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo uncertifiedCertificate() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certified = certifiedCertificate();
        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Latest final external-review release bundle delivery finalization archive is not finalized.",
                "Archive a finalized READY final external-review release bundle delivery record.",
                certified.latestDeliveryFinalizationArchiveId(),
                certified.latestReleaseBundleArchiveId(),
                null,
                certified.latestCertificateArchiveId(),
                certified.latestPackageArchiveId(),
                certified.latestPackageDeliveryReceiptId(),
                certified.latestTaskId(),
                certified.latestPullRequestUrl(),
                certified.latestDeliveryTarget(),
                certified.latestDeliveryChannel(),
                null,
                certified.latestArchivedAt(),
                "MISSING",
                false,
                certified.checks(),
                certified.evidenceNotes(),
                certified.downloadActions(),
                certified.sideEffectContract(),
                certified.markdownReport(),
                certified.generatedAt()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-external-review-release-bundle-delivery-certificate-archive-" + nextId++;
        }
    }
}
