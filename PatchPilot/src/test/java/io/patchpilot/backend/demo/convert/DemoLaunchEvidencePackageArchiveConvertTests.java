package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidencePackageArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchEvidencePackageArchiveConvertTests {

    @Test
    void converts_final_handoff_archive_proof_to_entity_and_back() {
        DemoLaunchEvidencePackageArchiveVo archive = archive();

        DemoLaunchEvidencePackageArchiveEntity entity =
                DemoLaunchEvidencePackageArchiveConvert.toEntity(archive);
        DemoLaunchEvidencePackageArchiveVo convertedArchive =
                DemoLaunchEvidencePackageArchiveConvert.toVo(entity);

        assertThat(entity.getFinalHandoffReportPackageArchiveStatus()).isEqualTo("READY");
        assertThat(entity.getFinalHandoffReportPackageArchiveReady()).isTrue();
        assertThat(entity.getFinalHandoffReportPackageArchiveId())
                .isEqualTo("final-handoff-report-package-archive-1");
        assertThat(entity.getFinalHandoffReportPackageArchiveSummary())
                .isEqualTo("Latest final handoff report package archive is download-ready and ready.");
        assertThat(convertedArchive).isEqualTo(archive);
    }

    private static DemoLaunchEvidencePackageArchiveVo archive() {
        return new DemoLaunchEvidencePackageArchiveVo(
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch evidence package is ready to share.",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                Instant.parse("2026-06-28T02:30:00Z"),
                "# PatchPilot Demo Launch Evidence Package"
        );
    }
}
