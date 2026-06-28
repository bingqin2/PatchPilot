package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionArchiveConvertTests {

    @Test
    void converts_final_acceptance_completion_archive_to_entity_and_back() {
        DemoFinalAcceptanceCompletionArchiveVo archive = archive();

        DemoFinalAcceptanceCompletionArchiveEntity entity =
                DemoFinalAcceptanceCompletionArchiveConvert.toEntity(archive);
        DemoFinalAcceptanceCompletionArchiveVo converted =
                DemoFinalAcceptanceCompletionArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getFinalized()).isTrue();
        assertThat(entity.getLatestArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(entity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(entity.getLatestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(entity.getLatestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(entity.getLatestDeliveryChannel()).isEqualTo("email");
        assertThat(entity.getDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(entity.getEvidenceNotesJson()).contains("send-ready");
        assertThat(converted).isEqualTo(archive);
    }

    @Test
    void returns_empty_evidence_notes_when_json_is_blank() {
        DemoFinalAcceptanceCompletionArchiveEntity entity =
                DemoFinalAcceptanceCompletionArchiveConvert.toEntity(archive());
        entity.setEvidenceNotesJson("");

        DemoFinalAcceptanceCompletionArchiveVo converted =
                DemoFinalAcceptanceCompletionArchiveConvert.toVo(entity);

        assertThat(converted.evidenceNotes()).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionArchiveVo archive() {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                "final-acceptance-completion-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of("Latest final acceptance share package archive is send-ready."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z"),
                Instant.parse("2026-06-29T04:00:00Z")
        );
    }
}
