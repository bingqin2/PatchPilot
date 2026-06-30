package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalReviewerHandoffPackageServiceTests {

    @Test
    void builds_ready_reviewer_handoff_package_from_latest_certified_terminal_archive() {
        DemoFinalReviewerHandoffPackageService service = new DemoFinalReviewerHandoffPackageService(
                () -> List.of(certificateArchive()),
                Clock.fixed(Instant.parse("2026-06-30T05:00:00Z"), ZoneOffset.UTC)
        );

        DemoFinalReviewerHandoffPackageVo handoffPackage = service.getPackage();

        assertThat(handoffPackage.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(handoffPackage.readyForReview()).isTrue();
        assertThat(handoffPackage.summary())
                .isEqualTo("Final reviewer handoff package is ready from the latest terminal delivery certificate archive.");
        assertThat(handoffPackage.nextAction())
                .isEqualTo("Send the handoff package report and listed attachments to the external reviewer.");
        assertThat(handoffPackage.latestCertificateArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-certificate-archive-1");
        assertThat(handoffPackage.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(handoffPackage.latestReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(handoffPackage.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(handoffPackage.latestPackageArchiveId())
                .isEqualTo("final-external-review-package-archive-1");
        assertThat(handoffPackage.latestTaskId()).isEqualTo("task-1");
        assertThat(handoffPackage.latestPullRequestUrl())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(handoffPackage.requiredAttachments()).containsExactly(
                "Final reviewer handoff package report.",
                "Terminal release-bundle delivery certificate archive final-external-review-release-bundle-delivery-certificate-archive-1.",
                "Frozen release bundle archive final-external-review-release-bundle-archive-1.",
                "Release-bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1.",
                "Release-bundle delivery receipt final-external-review-release-bundle-delivery-receipt-1.",
                "Package-level delivery certificate archive final-external-review-delivery-certificate-archive-1.",
                "Final external-review package archive final-external-review-package-archive-1."
        );
        assertThat(handoffPackage.checks())
                .extracting(DemoFinalReviewerHandoffPackageVo.Check::name)
                .containsExactly(
                        "Terminal delivery certificate archive",
                        "Frozen release bundle archive",
                        "Release-bundle delivery receipt",
                        "Pull Request evidence"
                );
        assertThat(handoffPackage.downloadActions()).containsExactly(
                "Download final reviewer handoff package report.",
                "Download final external-review release bundle delivery certificate archive final-external-review-release-bundle-delivery-certificate-archive-1.",
                "Download final external-review release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1.",
                "Download final external-review release bundle archive final-external-review-release-bundle-archive-1.",
                "Download final external-review release bundle delivery receipt final-external-review-release-bundle-delivery-receipt-1.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/8 for external review."
        );
        assertThat(handoffPackage.sideEffectContract()).contains("read-only");
        assertThat(handoffPackage.markdownReport()).contains(
                "# PatchPilot Final Reviewer Handoff Package",
                "Ready for review: `true`",
                "final-external-review-release-bundle-delivery-certificate-archive-1",
                "https://github.com/bingqin2/PatchPilot/pull/8"
        );
        assertThat(handoffPackage.generatedAt()).isEqualTo(Instant.parse("2026-06-30T05:00:00Z"));
    }

    @Test
    void reports_attention_when_no_terminal_certificate_archive_exists() {
        DemoFinalReviewerHandoffPackageService service = new DemoFinalReviewerHandoffPackageService(
                List::of,
                Clock.fixed(Instant.parse("2026-06-30T05:00:00Z"), ZoneOffset.UTC)
        );

        DemoFinalReviewerHandoffPackageVo handoffPackage = service.getPackage();

        assertThat(handoffPackage.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(handoffPackage.readyForReview()).isFalse();
        assertThat(handoffPackage.summary())
                .isEqualTo("No terminal release-bundle delivery certificate archive is available for reviewer handoff.");
        assertThat(handoffPackage.nextAction())
                .isEqualTo("Archive the certified final external-review release bundle delivery certificate, then download the final reviewer handoff package.");
        assertThat(handoffPackage.latestCertificateArchiveId()).isNull();
        assertThat(handoffPackage.requiredAttachments()).isEmpty();
        assertThat(handoffPackage.downloadActions()).containsExactly(
                "Archive the certified terminal release-bundle delivery certificate before downloading the final reviewer handoff package."
        );
        assertThat(handoffPackage.markdownReport()).contains("Ready for review: `false`");
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo certificateArchive() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo(
                "final-external-review-release-bundle-delivery-certificate-archive-1",
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
                List.of(new DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo.Check(
                        "Final external-review release bundle delivery finalization archive",
                        DemoReadinessStatus.READY,
                        "Latest release bundle delivery finalization archive is finalized.",
                        "No action needed."
                )),
                List.of("Release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1 is finalized."),
                List.of(
                        "Download final external-review release bundle delivery certificate report.",
                        "Download final external-review release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1.",
                        "Download final external-review release bundle archive final-external-review-release-bundle-archive-1.",
                        "Download final external-review release bundle delivery receipt final-external-review-release-bundle-delivery-receipt-1.",
                        "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/8 for external review."
                ),
                "GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only.",
                "# PatchPilot Final External Review Release Bundle Delivery Certificate",
                Instant.parse("2026-06-30T03:00:00Z"),
                Instant.parse("2026-06-30T03:30:00Z")
        );
    }
}
