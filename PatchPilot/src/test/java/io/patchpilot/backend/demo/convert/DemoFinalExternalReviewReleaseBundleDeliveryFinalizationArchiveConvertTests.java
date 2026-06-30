package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvertTests {

    @Test
    void converts_final_external_review_release_bundle_delivery_finalization_archive_to_entity_and_back() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive = archive();

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity entity =
                DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert.toEntity(archive);
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo converted =
                DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert.toVo(entity);

        assertThat(entity.getId())
                .isEqualTo("final-external-review-release-bundle-delivery-finalization-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getFinalized()).isTrue();
        assertThat(entity.getLatestArchiveId()).isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(entity.getLatestDeliveryReceiptId())
                .isEqualTo("final-external-review-release-bundle-delivery-receipt-1");
        assertThat(entity.getLatestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(entity.getLatestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(entity.getReleaseBundleDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(entity.getChecksJson()).contains("Frozen final external-review release bundle");
        assertThat(entity.getEvidenceNotesJson()).contains("Frozen final external-review release bundle");
        assertThat(entity.getDownloadActionsJson())
                .contains("Download final external-review release bundle delivery finalization report.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity entity =
                DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert.toEntity(archive());
        entity.setChecksJson("");
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo converted =
                DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert.toVo(entity);

        assertThat(converted.checks()).isEmpty();
        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive() {
        return new DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo(
                "final-external-review-release-bundle-delivery-finalization-archive-1",
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
                Instant.parse("2026-06-29T14:00:00Z"),
                Instant.parse("2026-06-29T14:30:00Z")
        );
    }
}
