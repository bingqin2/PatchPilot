package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewDeliveryCertificateArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewDeliveryCertificateArchiveConvertTests {

    @Test
    void converts_final_external_review_delivery_certificate_archive_to_entity_and_back() {
        DemoFinalExternalReviewDeliveryCertificateArchiveVo archive = archive();

        DemoFinalExternalReviewDeliveryCertificateArchiveEntity entity =
                DemoFinalExternalReviewDeliveryCertificateArchiveConvert.toEntity(archive);
        DemoFinalExternalReviewDeliveryCertificateArchiveVo converted =
                DemoFinalExternalReviewDeliveryCertificateArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getCertified()).isTrue();
        assertThat(entity.getLatestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(entity.getLatestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(entity.getLatestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(entity.getChecksJson()).contains("Final external-review delivery finalization archive");
        assertThat(entity.getEvidenceNotesJson()).contains("delivery finalization archive");
        assertThat(entity.getDownloadActionsJson())
                .contains("Download final external-review delivery certificate report.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalExternalReviewDeliveryCertificateArchiveEntity entity =
                DemoFinalExternalReviewDeliveryCertificateArchiveConvert.toEntity(archive());
        entity.setChecksJson("");
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalExternalReviewDeliveryCertificateArchiveVo converted =
                DemoFinalExternalReviewDeliveryCertificateArchiveConvert.toVo(entity);

        assertThat(converted.checks()).isEmpty();
        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalExternalReviewDeliveryCertificateArchiveVo archive() {
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
