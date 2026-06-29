package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleServiceTests {

    @Test
    void builds_release_bundle_from_latest_certified_certificate_archive() {
        DemoFinalExternalReviewReleaseBundleService service = new DemoFinalExternalReviewReleaseBundleService(
                () -> List.of(certificateArchive()),
                Clock.fixed(Instant.parse("2026-06-29T12:00:00Z"), ZoneOffset.UTC)
        );

        DemoFinalExternalReviewReleaseBundleVo bundle = service.getReleaseBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(bundle.releaseReady()).isTrue();
        assertThat(bundle.summary()).isEqualTo("PatchPilot final external-review release bundle is ready.");
        assertThat(bundle.nextAction()).isEqualTo("Share the release bundle report and listed attachments with external reviewers.");
        assertThat(bundle.latestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(bundle.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(bundle.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(bundle.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(bundle.latestTaskId()).isEqualTo("task-1");
        assertThat(bundle.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(bundle.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(bundle.latestDeliveryChannel()).isEqualTo("email");
        assertThat(bundle.latestDeliveredAt()).isEqualTo("2026-06-29T09:25:00Z");
        assertThat(bundle.latestCertificateArchivedAt()).isEqualTo(Instant.parse("2026-06-29T11:30:00Z"));
        assertThat(bundle.generatedAt()).isEqualTo(Instant.parse("2026-06-29T12:00:00Z"));
        assertThat(bundle.requiredAttachments()).containsExactly(
                "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1",
                "Final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1",
                "Final external-review package archive final-external-review-package-archive-1",
                "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1"
        );
        assertThat(bundle.releaseChecks())
                .extracting(DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck::name)
                .containsExactly(
                        "Final delivery certificate archive",
                        "Frozen reviewer package",
                        "Delivery receipt",
                        "Pull Request evidence"
                );
        assertThat(bundle.evidenceNotes()).contains(
                "Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth.",
                "Pull Request https://github.com/bingqin2/PatchPilot/pull/8 is included as reviewer evidence."
        );
        assertThat(bundle.downloadActions()).contains(
                "Download final external-review release bundle report.",
                "Download final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1.",
                "Download final external-review package archive final-external-review-package-archive-1."
        );
        assertThat(bundle.sideEffectContract()).contains("read-only");
        assertThat(bundle.markdownReport()).contains(
                "# PatchPilot Final External Review Release Bundle",
                "Release ready: `true`",
                "final-external-review-delivery-certificate-archive-1",
                "https://github.com/bingqin2/PatchPilot/pull/8"
        );
    }

    @Test
    void blocks_release_bundle_until_a_certificate_archive_exists() {
        DemoFinalExternalReviewReleaseBundleService service = new DemoFinalExternalReviewReleaseBundleService(
                List::of,
                Clock.fixed(Instant.parse("2026-06-29T12:00:00Z"), ZoneOffset.UTC)
        );

        DemoFinalExternalReviewReleaseBundleVo bundle = service.getReleaseBundle();

        assertThat(bundle.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.releaseReady()).isFalse();
        assertThat(bundle.summary())
                .isEqualTo("No final external-review delivery certificate archive is available for release.");
        assertThat(bundle.nextAction())
                .isEqualTo("Archive the certified final external-review delivery certificate, then download the release bundle.");
        assertThat(bundle.latestCertificateArchiveId()).isNull();
        assertThat(bundle.requiredAttachments()).isEmpty();
        assertThat(bundle.releaseChecks())
                .extracting(DemoFinalExternalReviewReleaseBundleVo.ReleaseCheck::status)
                .containsExactly(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(bundle.markdownReport()).contains("Release ready: `false`");
    }

    private static DemoFinalExternalReviewDeliveryCertificateArchiveVo certificateArchive() {
        return new DemoFinalExternalReviewDeliveryCertificateArchiveVo(
                "final-external-review-delivery-certificate-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final external-review delivery is certified from the latest finalized archive.",
                "Share the certificate report with reviewers as the final external-review delivery proof.",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                Instant.parse("2026-06-29T10:30:00Z"),
                "FRESH",
                true,
                List.of(new DemoFinalExternalReviewDeliveryCertificateArchiveVo.Check(
                        "Final external-review delivery finalization archive",
                        DemoReadinessStatus.READY,
                        "Latest final external-review delivery finalization archive is finalized.",
                        "No action needed."
                )),
                List.of("Final external-review delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is finalized."),
                List.of("Download final external-review delivery certificate report."),
                "GET /api/demo/final-external-review-delivery-certificate is read-only.",
                "# PatchPilot Final External Review Delivery Certificate",
                Instant.parse("2026-06-29T11:00:00Z"),
                Instant.parse("2026-06-29T11:30:00Z")
        );
    }
}
