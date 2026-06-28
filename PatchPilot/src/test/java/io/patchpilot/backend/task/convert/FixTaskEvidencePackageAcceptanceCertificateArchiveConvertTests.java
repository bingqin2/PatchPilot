package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCertificateArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageAcceptanceCertificateArchiveConvertTests {

    @Test
    void should_round_trip_task_evidence_acceptance_certificate_archive() {
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive = archive();

        FixTaskEvidencePackageAcceptanceCertificateArchiveEntity entity =
                FixTaskEvidencePackageAcceptanceCertificateArchiveConvert.toEntity(archive);
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo converted =
                FixTaskEvidencePackageAcceptanceCertificateArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(entity.getDownloadActionsJson()).contains("Download task evidence acceptance certificate.");
        assertThat(converted).isEqualTo(archive);
    }

    private static FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive() {
        return new FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
                "task-evidence-certificate-archive-1",
                "READY",
                true,
                "Task evidence acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "task-evidence-closeout-archive-1",
                "task-evidence-archive-1",
                "task-evidence-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T07:00:00Z"),
                Instant.parse("2026-06-28T07:30:00Z"),
                Instant.parse("2026-06-28T07:35:00Z"),
                List.of(
                        "Download task evidence acceptance certificate.",
                        "Download task evidence acceptance closeout archive task-evidence-closeout-archive-1."
                ),
                "# PatchPilot Task Evidence Acceptance Certificate"
        );
    }
}
