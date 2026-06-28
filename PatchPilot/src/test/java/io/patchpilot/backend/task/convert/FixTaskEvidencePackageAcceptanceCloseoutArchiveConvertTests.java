package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageAcceptanceCloseoutArchiveConvertTests {

    @Test
    void should_round_trip_task_evidence_acceptance_closeout_archive() {
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive =
                archive("task-evidence-closeout-archive-1");

        FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity entity =
                FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert.toEntity(archive);
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo converted =
                FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(entity.getAccepted()).isTrue();
        assertThat(entity.getLatestDeliveryReceiptId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(entity.getReport()).contains("# PatchPilot Task Evidence Acceptance Closeout Archive");
        assertThat(converted).isEqualTo(archive);
    }

    static FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive(String id) {
        return new FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
                id,
                "READY",
                true,
                "Task evidence is finalized with a fresh delivery receipt for the current shareable archive.",
                "task-evidence-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "task-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T07:00:00Z"),
                "# PatchPilot Task Evidence Acceptance Closeout Archive"
        );
    }
}
