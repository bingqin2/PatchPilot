package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalHandoffReportPackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalHandoffReportPackageArchiveRepositoryTests {

    @Test
    void stores_recent_archives_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalHandoffReportPackageArchiveRepository repository =
                new InMemoryDemoFinalHandoffReportPackageArchiveRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(archive("archive-" + index, Instant.parse("2026-06-28T11:00:00Z").plusSeconds(index)));
        }

        List<DemoFinalHandoffReportPackageArchiveVo> archives = repository.listRecentArchives(5);

        assertThat(archives)
                .extracting(DemoFinalHandoffReportPackageArchiveVo::id)
                .containsExactly("archive-22", "archive-21", "archive-20", "archive-19", "archive-18");
        assertThat(repository.findById("archive-22")).isPresent();
        assertThat(repository.findById("archive-1")).isEmpty();
    }

    private static DemoFinalHandoffReportPackageArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalHandoffReportPackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "delivery-receipt-1",
                "task-evidence-certificate-archive-1",
                true,
                List.of("Finalization: READY"),
                List.of("Finalization report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Latest delivery receipt delivery-receipt-1 is fresh."),
                List.of("Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package",
                Instant.parse("2026-06-28T11:00:00Z"),
                archivedAt
        );
    }
}
