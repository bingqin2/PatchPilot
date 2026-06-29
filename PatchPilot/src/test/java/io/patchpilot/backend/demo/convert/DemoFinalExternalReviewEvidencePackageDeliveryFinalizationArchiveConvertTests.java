package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvertTests {

    @Test
    void converts_final_external_review_package_delivery_finalization_archive_to_entity_and_back() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive = archive();

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity entity =
                DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert.toEntity(archive);
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo converted =
                DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getFinalized()).isTrue();
        assertThat(entity.getLatestArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(entity.getLatestDeliveryReceiptId()).isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(entity.getLatestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(entity.getChecksJson()).contains("Frozen final external-review package");
        assertThat(entity.getEvidenceNotesJson()).contains("Frozen final external-review package");
        assertThat(entity.getDownloadActionsJson())
                .contains("Download final external-review package delivery finalization report.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity entity =
                DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert.toEntity(archive());
        entity.setChecksJson("");
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo converted =
                DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert.toVo(entity);

        assertThat(converted.checks()).isEmpty();
        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive() {
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
