package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageArchiveConvertTests {

    @Test
    void converts_final_external_review_package_archive_to_entity_and_back() {
        DemoFinalExternalReviewEvidencePackageArchiveVo archive = archive();

        DemoFinalExternalReviewEvidencePackageArchiveEntity entity =
                DemoFinalExternalReviewEvidencePackageArchiveConvert.toEntity(archive);
        DemoFinalExternalReviewEvidencePackageArchiveVo converted =
                DemoFinalExternalReviewEvidencePackageArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getReadyForExternalReview()).isTrue();
        assertThat(entity.getLatestTaskId()).isEqualTo("task-2");
        assertThat(entity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(entity.getFinalAcceptanceSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(entity.getCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(entity.getCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(entity.getCloseoutArchiveId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(entity.getEvidenceNotesJson()).contains("Frozen closeout archive");
        assertThat(entity.getDownloadActionsJson()).contains("Download final external-review evidence package.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalExternalReviewEvidencePackageArchiveEntity entity =
                DemoFinalExternalReviewEvidencePackageArchiveConvert.toEntity(archive());
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalExternalReviewEvidencePackageArchiveVo converted =
                DemoFinalExternalReviewEvidencePackageArchiveConvert.toVo(entity);

        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo archive() {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                "final-external-review-package-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T07:45:00Z",
                "FRESH",
                Instant.parse("2026-06-29T07:50:00Z"),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of("Download final external-review evidence package."),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package",
                Instant.parse("2026-06-29T08:00:00Z"),
                Instant.parse("2026-06-29T08:30:00Z")
        );
    }
}
