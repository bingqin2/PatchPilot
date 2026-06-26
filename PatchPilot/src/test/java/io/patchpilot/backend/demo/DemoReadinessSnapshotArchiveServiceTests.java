package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoReadinessSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoReadinessSnapshotArchiveServiceTests {

    @Test
    void should_archive_current_demo_readiness_as_markdown_evidence() {
        DemoReadinessSnapshotArchiveService service = new DemoReadinessSnapshotArchiveService(
                () -> readiness(DemoReadinessStatus.BLOCKED),
                new InMemoryDemoReadinessSnapshotArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-27T04:00:00Z"), ZoneOffset.UTC),
                () -> "readiness-snapshot-1"
        );

        DemoReadinessSnapshotArchiveVo archive = service.archiveCurrentReadiness();

        assertThat(archive.id()).isEqualTo("readiness-snapshot-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(archive.summary()).isEqualTo("PatchPilot is blocked before a live demo.");
        assertThat(archive.readyCheckCount()).isEqualTo(1);
        assertThat(archive.needsAttentionCheckCount()).isEqualTo(1);
        assertThat(archive.blockedCheckCount()).isEqualTo(1);
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-27T04:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Demo Readiness Snapshot")
                .contains("- Status: `BLOCKED`")
                .contains("- Ready checks: `1`")
                .contains("- Needs attention checks: `1`")
                .contains("- Blocked checks: `1`")
                .contains("## Checks")
                .contains("- `READY` Credentials: Credentials configured.")
                .contains("  - Action: No action needed.")
                .contains("- `NEEDS_ATTENTION` Evaluation baseline: Only one archive exists.")
                .contains("  - Action: Archive another baseline run.")
                .contains("- `BLOCKED` Recent Pull Request: No successful Pull Request found.")
                .contains("  - Action: Run a controlled smoke task.")
                .contains("## Next Actions")
                .contains("- Archive another baseline run.")
                .contains("- Run a controlled smoke task.")
                .contains("## Side Effect Contract")
                .contains("Archiving demo readiness stores PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("readiness-snapshot-1")).contains(archive);
    }

    @Test
    void should_keep_twenty_recent_readiness_snapshots_newest_first() {
        DemoReadinessSnapshotArchiveService service = new DemoReadinessSnapshotArchiveService(
                () -> readiness(DemoReadinessStatus.READY),
                new InMemoryDemoReadinessSnapshotArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-27T04:00:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 0; index < 25; index++) {
            service.archiveCurrentReadiness();
        }

        List<DemoReadinessSnapshotArchiveVo> archives = service.listRecentArchives();

        assertThat(archives).hasSize(20);
        assertThat(archives.get(0).id()).isEqualTo("readiness-snapshot-25");
        assertThat(archives.get(19).id()).isEqualTo("readiness-snapshot-6");
    }

    private static DemoReadinessVo readiness(DemoReadinessStatus status) {
        return new DemoReadinessVo(
                status,
                status == DemoReadinessStatus.READY
                        ? "PatchPilot is ready for a controlled demo."
                        : "PatchPilot is blocked before a live demo.",
                List.of(
                        new DemoReadinessCheckVo(
                                "Credentials",
                                DemoReadinessStatus.READY,
                                "Credentials configured.",
                                "No action needed."
                        ),
                        new DemoReadinessCheckVo(
                                "Evaluation baseline",
                                DemoReadinessStatus.NEEDS_ATTENTION,
                                "Only one archive exists.",
                                "Archive another baseline run."
                        ),
                        new DemoReadinessCheckVo(
                                "Recent Pull Request",
                                status == DemoReadinessStatus.BLOCKED ? DemoReadinessStatus.BLOCKED : DemoReadinessStatus.READY,
                                status == DemoReadinessStatus.BLOCKED
                                        ? "No successful Pull Request found."
                                        : "Recent Pull Request evidence is available.",
                                status == DemoReadinessStatus.BLOCKED
                                        ? "Run a controlled smoke task."
                                        : "No action needed."
                        )
                ),
                status == DemoReadinessStatus.BLOCKED
                        ? List.of("Archive another baseline run.", "Run a controlled smoke task.")
                        : List.of()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int index;

        @Override
        public String get() {
            index++;
            return "readiness-snapshot-" + index;
        }
    }
}
