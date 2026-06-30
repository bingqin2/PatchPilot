package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleDeliveryCertificateServiceTests {

    @Test
    void certifies_latest_ready_release_bundle_delivery_finalization_archive() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateService(
                        () -> List.of(finalizationArchive()),
                        Clock.fixed(Instant.parse("2026-06-30T03:00:00Z"), ZoneOffset.UTC)
                );

        DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(certificate.certified()).isTrue();
        assertThat(certificate.summary())
                .isEqualTo("Final external-review release bundle delivery is certified from the latest finalized archive.");
        assertThat(certificate.nextAction())
                .isEqualTo("Share the release bundle delivery certificate report as the terminal reviewer handoff proof.");
        assertThat(certificate.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(certificate.latestReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(certificate.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(certificate.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(certificate.latestPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(certificate.latestPackageDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(certificate.latestTaskId()).isEqualTo("task-1");
        assertThat(certificate.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(certificate.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(certificate.latestDeliveryChannel()).isEqualTo("email");
        assertThat(certificate.latestDeliveredAt()).isEqualTo("2026-06-30T02:10:00Z");
        assertThat(certificate.latestArchivedAt()).isEqualTo(Instant.parse("2026-06-30T02:45:00Z"));
        assertThat(certificate.releaseBundleDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(certificate.releaseBundleDeliveryReceiptFresh()).isTrue();
        assertThat(certificate.checks())
                .extracting(DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check::name)
                .containsExactly(
                        "Final external-review release bundle delivery finalization archive",
                        "Final external-review release bundle delivery receipt",
                        "Final external-review release bundle archive",
                        "Final external-review Pull Request"
                );
        assertThat(certificate.evidenceNotes()).contains(
                "Release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1 is finalized.",
                "Fresh release bundle delivery receipt final-external-review-release-bundle-delivery-receipt-1 proves the frozen release bundle was delivered."
        );
        assertThat(certificate.downloadActions()).contains(
                "Download final external-review release bundle delivery certificate report.",
                "Download final external-review release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1."
        );
        assertThat(certificate.sideEffectContract()).contains("read-only");
        assertThat(certificate.markdownReport()).contains(
                "# PatchPilot Final External Review Release Bundle Delivery Certificate",
                "final-external-review-release-bundle-delivery-receipt-1",
                "Certified: `true`"
        );
        assertThat(certificate.generatedAt()).isEqualTo(Instant.parse("2026-06-30T03:00:00Z"));
    }

    @Test
    void requires_a_release_bundle_delivery_finalization_archive_before_certification() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateService service =
                new DemoFinalExternalReviewReleaseBundleDeliveryCertificateService(
                        List::of,
                        Clock.fixed(Instant.parse("2026-06-30T03:00:00Z"), ZoneOffset.UTC)
                );

        DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary())
                .isEqualTo("No final external-review release bundle delivery finalization archive is available for certification.");
        assertThat(certificate.nextAction())
                .isEqualTo("Archive the READY final external-review release bundle delivery finalization, then download the certificate.");
        assertThat(certificate.latestDeliveryFinalizationArchiveId()).isNull();
        assertThat(certificate.checks())
                .extracting(DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo.Check::status)
                .containsExactly(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(certificate.markdownReport()).contains("Certified: `false`");
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo finalizationArchive() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo(
                "final-external-review-release-bundle-delivery-finalization-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final external-review release bundle delivery is finalized with a fresh receipt.",
                "Use the finalization archive as terminal release bundle delivery proof.",
                "final-external-review-release-bundle-archive-1",
                "final-external-review-release-bundle-delivery-receipt-1",
                "final-external-review-delivery-certificate-archive-1",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-30T02:10:00Z",
                "FRESH",
                true,
                "Latest release bundle delivery receipt matches the current frozen release bundle archive.",
                List.of(new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo.Check(
                        "Frozen final external-review release bundle",
                        DemoReadinessStatus.READY,
                        "Frozen final external-review release bundle is ready.",
                        "No action needed."
                )),
                List.of("Frozen final external-review release bundle final-external-review-release-bundle-archive-1 is ready."),
                List.of("Download final external-review release bundle delivery finalization report."),
                "GET /api/demo/final-external-review-release-bundle/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Release Bundle Delivery Finalization",
                Instant.parse("2026-06-30T02:30:00Z"),
                Instant.parse("2026-06-30T02:45:00Z")
        );
    }
}
