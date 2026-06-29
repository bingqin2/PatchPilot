package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewDeliveryCertificateServiceTests {

    @Test
    void certifies_latest_ready_final_external_review_delivery_archive() {
        DemoFinalExternalReviewDeliveryCertificateService service =
                new DemoFinalExternalReviewDeliveryCertificateService(
                        () -> List.of(finalizationArchive()),
                        Clock.fixed(Instant.parse("2026-06-29T11:00:00Z"), ZoneOffset.UTC)
                );

        DemoFinalExternalReviewDeliveryCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(certificate.certified()).isTrue();
        assertThat(certificate.summary())
                .isEqualTo("Final external-review delivery is certified from the latest finalized archive.");
        assertThat(certificate.nextAction())
                .isEqualTo("Share the certificate report with reviewers as the final external-review delivery proof.");
        assertThat(certificate.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(certificate.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(certificate.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(certificate.latestTaskId()).isEqualTo("task-1");
        assertThat(certificate.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(certificate.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(certificate.latestDeliveryChannel()).isEqualTo("email");
        assertThat(certificate.latestDeliveredAt()).isEqualTo("2026-06-29T09:25:00Z");
        assertThat(certificate.latestArchivedAt()).isEqualTo(Instant.parse("2026-06-29T10:30:00Z"));
        assertThat(certificate.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(certificate.deliveryReceiptFresh()).isTrue();
        assertThat(certificate.checks())
                .extracting(DemoFinalExternalReviewDeliveryCertificateVo.Check::name)
                .containsExactly(
                        "Final external-review delivery finalization archive",
                        "Final external-review package delivery receipt",
                        "Final external-review Pull Request"
                );
        assertThat(certificate.evidenceNotes()).contains(
                "Final external-review delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is finalized.",
                "Fresh delivery receipt final-external-review-package-delivery-receipt-1 proves the frozen package was delivered."
        );
        assertThat(certificate.downloadActions()).contains(
                "Download final external-review delivery certificate report.",
                "Download final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1."
        );
        assertThat(certificate.sideEffectContract()).contains("read-only");
        assertThat(certificate.markdownReport()).contains(
                "# PatchPilot Final External Review Delivery Certificate",
                "final-external-review-package-delivery-receipt-1",
                "Certified: `true`"
        );
        assertThat(certificate.generatedAt()).isEqualTo(Instant.parse("2026-06-29T11:00:00Z"));
    }

    @Test
    void requires_a_finalization_archive_before_certification() {
        DemoFinalExternalReviewDeliveryCertificateService service =
                new DemoFinalExternalReviewDeliveryCertificateService(
                        List::of,
                        Clock.fixed(Instant.parse("2026-06-29T11:00:00Z"), ZoneOffset.UTC)
                );

        DemoFinalExternalReviewDeliveryCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary())
                .isEqualTo("No final external-review delivery finalization archive is available for certification.");
        assertThat(certificate.nextAction())
                .isEqualTo("Archive the READY final external-review package delivery finalization, then download the certificate.");
        assertThat(certificate.latestDeliveryFinalizationArchiveId()).isNull();
        assertThat(certificate.checks())
                .extracting(DemoFinalExternalReviewDeliveryCertificateVo.Check::status)
                .containsExactly(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(certificate.markdownReport()).contains("Certified: `false`");
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo finalizationArchive() {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                "final-external-review-package-delivery-finalization-archive-1",
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
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check(
                        "Frozen final external-review package",
                        DemoReadinessStatus.READY,
                        "Frozen final external-review package is ready.",
                        "No action needed."
                )),
                List.of("Frozen final external-review package final-external-review-package-archive-1 is ready."),
                List.of("Download final external-review package delivery finalization report."),
                "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.parse("2026-06-29T10:00:00Z"),
                Instant.parse("2026-06-29T10:30:00Z")
        );
    }
}
