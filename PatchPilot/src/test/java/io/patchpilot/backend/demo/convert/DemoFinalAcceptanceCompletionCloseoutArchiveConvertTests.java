package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionCloseoutArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionCloseoutArchiveConvertTests {

    @Test
    void converts_final_acceptance_completion_closeout_archive_to_entity_and_back() {
        DemoFinalAcceptanceCompletionCloseoutArchiveVo archive = archive();

        DemoFinalAcceptanceCompletionCloseoutArchiveEntity entity =
                DemoFinalAcceptanceCompletionCloseoutArchiveConvert.toEntity(archive);
        DemoFinalAcceptanceCompletionCloseoutArchiveVo converted =
                DemoFinalAcceptanceCompletionCloseoutArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getClosed()).isTrue();
        assertThat(entity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(entity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(entity.getLatestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(entity.getLatestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(entity.getLatestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(entity.getDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(entity.getEvidenceNotesJson()).contains("Final demo acceptance summary is accepted.");
        assertThat(entity.getDownloadActionsJson()).contains("Download final acceptance completion closeout report.");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_archive_json_is_blank() {
        DemoFinalAcceptanceCompletionCloseoutArchiveEntity entity =
                DemoFinalAcceptanceCompletionCloseoutArchiveConvert.toEntity(archive());
        entity.setEvidenceNotesJson("");
        entity.setDownloadActionsJson("");

        DemoFinalAcceptanceCompletionCloseoutArchiveVo converted =
                DemoFinalAcceptanceCompletionCloseoutArchiveConvert.toVo(entity);

        assertThat(converted.evidenceNotes()).isEmpty();
        assertThat(converted.downloadActions()).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveVo archive() {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                "final-acceptance-completion-closeout-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T06:00:00Z"),
                Instant.parse("2026-06-29T06:30:00Z")
        );
    }
}
