package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvertTests {

    @Test
    void converts_final_external_review_release_bundle_delivery_certificate_archive_to_entity_and_back() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive = archive();

        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity entity =
                DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert.toEntity(archive);
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo converted =
                DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert.toVo(entity);

        assertThat(entity.getId())
                .isEqualTo("final-external-review-release-bundle-delivery-certificate-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getCertified()).isTrue();
        assertThat(entity.getLatestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(entity.getLatestReleaseBundleArchiveId())
                .isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(entity.getLatestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(entity.getLatestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(entity.getReleaseBundleDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(entity.getReleaseBundleDeliveryReceiptFresh()).isTrue();
        assertThat(entity.getChecksJson())
                .contains("Final external-review release bundle delivery finalization archive");
        assertThat(entity.getEvidenceNotesJson()).contains("Release bundle delivery finalization archive");
        assertThat(entity.getDownloadActionsJson())
                .contains("Download final external-review release bundle delivery certificate report.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity entity =
                DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert.toEntity(archive());
        entity.setChecksJson("");
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo converted =
                DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert.toVo(entity);

        assertThat(converted.checks()).isEmpty();
        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive() {
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
                List.of("Download final external-review release bundle delivery certificate report."),
                "GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only.",
                "# PatchPilot Final External Review Release Bundle Delivery Certificate",
                Instant.parse("2026-06-30T03:00:00Z"),
                Instant.parse("2026-06-30T03:30:00Z")
        );
    }
}
