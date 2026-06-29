package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleArchiveConvertTests {

    @Test
    void converts_final_external_review_release_bundle_archive_to_entity_and_back() {
        DemoFinalExternalReviewReleaseBundleArchiveVo archive = archive();

        DemoFinalExternalReviewReleaseBundleArchiveEntity entity =
                DemoFinalExternalReviewReleaseBundleArchiveConvert.toEntity(archive);
        DemoFinalExternalReviewReleaseBundleArchiveVo converted =
                DemoFinalExternalReviewReleaseBundleArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-external-review-release-bundle-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getReleaseReady()).isTrue();
        assertThat(entity.getLatestCertificateArchiveId())
                .isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(entity.getLatestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(entity.getRequiredAttachmentsJson())
                .contains("final-external-review-delivery-certificate-archive-1");
        assertThat(entity.getReleaseChecksJson()).contains("Final delivery certificate archive");
        assertThat(entity.getEvidenceNotesJson()).contains("release source of truth");
        assertThat(entity.getDownloadActionsJson()).contains("Download final external-review release bundle report.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalExternalReviewReleaseBundleArchiveEntity entity =
                DemoFinalExternalReviewReleaseBundleArchiveConvert.toEntity(archive());
        entity.setRequiredAttachmentsJson("");
        entity.setReleaseChecksJson("");
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalExternalReviewReleaseBundleArchiveVo converted =
                DemoFinalExternalReviewReleaseBundleArchiveConvert.toVo(entity);

        assertThat(converted.requiredAttachments()).isEmpty();
        assertThat(converted.releaseChecks()).isEmpty();
        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalExternalReviewReleaseBundleArchiveVo archive() {
        return new DemoFinalExternalReviewReleaseBundleArchiveVo(
                "final-external-review-release-bundle-archive-1",
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
                List.of(
                        "Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1",
                        "Final external-review package delivery receipt final-external-review-package-delivery-receipt-1"
                ),
                List.of(new DemoFinalExternalReviewReleaseBundleArchiveVo.ReleaseCheck(
                        "Final delivery certificate archive",
                        DemoReadinessStatus.READY,
                        "Latest final external-review delivery certificate archive is certified.",
                        "No action needed."
                )),
                List.of("Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth."),
                List.of("Download final external-review release bundle report."),
                "GET /api/demo/final-external-review-release-bundle is read-only.",
                "# PatchPilot Final External Review Release Bundle",
                Instant.parse("2026-06-29T12:00:00Z"),
                Instant.parse("2026-06-29T12:30:00Z")
        );
    }
}
