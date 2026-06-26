package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoSessionArchiveRepository;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoSessionArchiveServiceTests {

    @Test
    void should_archive_current_demo_session_report_and_keep_recent_archives_first() {
        DemoSessionArchiveService service = new DemoSessionArchiveService(
                new DemoSessionReportService(() -> snapshot("demo-session-1", DemoReadinessStatus.READY)),
                new InMemoryDemoSessionArchiveRepository(),
                () -> snapshot("demo-session-1", DemoReadinessStatus.READY),
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "archive-1"
        );

        DemoSessionArchiveVo archive = service.archiveCurrentSession();

        assertThat(archive.id()).isEqualTo("archive-1");
        assertThat(archive.sessionId()).isEqualTo("demo-session-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.summary()).isEqualTo("Demo session demo-session-1 is ready.");
        assertThat(archive.shareSummary()).contains("demo-session-1");
        assertThat(archive.recentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-24T04:00:00Z"));
        assertThat(archive.report()).contains("# PatchPilot Demo Session Report");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("archive-1")).contains(archive);
    }

    @Test
    void should_archive_current_demo_session_report_with_prepared_launch_commands() {
        DemoSessionArchiveService service = new DemoSessionArchiveService(
                new DemoSessionReportService(() -> snapshot("demo-session-1", DemoReadinessStatus.READY)),
                new InMemoryDemoSessionArchiveRepository(),
                () -> snapshot("demo-session-1", DemoReadinessStatus.READY),
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "archive-1"
        );
        DemoSessionReportRequestDto request = new DemoSessionReportRequestDto(List.of(
                new DemoPreparedLaunchCommandRequestDto(
                        "/agent fix replace docs/demo.md PatchPilot smoke test",
                        "bingqin2",
                        "PatchPilot",
                        1L,
                        "bingqin2",
                        "replace",
                        "docs/demo.md",
                        "PatchPilot smoke test",
                        "2026-06-26T01:00:00Z"
                )
        ));

        DemoSessionArchiveVo archive = service.archiveCurrentSession(request);

        assertThat(archive.report())
                .contains("## Prepared Launch Commands")
                .contains("- `/agent fix replace docs/demo.md PatchPilot smoke test`")
                .contains("  - Operation: `replace` on `docs/demo.md`");
    }

    @Test
    void should_cap_recent_archives_to_twenty_entries() {
        DemoSessionArchiveService service = new DemoSessionArchiveService(
                new DemoSessionReportService(() -> snapshot("demo-session-latest", DemoReadinessStatus.READY)),
                new InMemoryDemoSessionArchiveRepository(),
                new IncrementingSnapshotSupplier(),
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 0; index < 25; index++) {
            service.archiveCurrentSession();
        }

        List<DemoSessionArchiveVo> archives = service.listRecentArchives();

        assertThat(archives).hasSize(20);
        assertThat(archives.get(0).id()).isEqualTo("archive-25");
        assertThat(archives.get(0).sessionId()).isEqualTo("demo-session-25");
        assertThat(archives.get(19).id()).isEqualTo("archive-6");
    }

    private static DemoSessionSnapshotVo snapshot(String sessionId, DemoReadinessStatus status) {
        return new DemoSessionSnapshotVo(
                sessionId,
                status,
                "Demo session " + sessionId + " is ready.",
                Instant.parse("2026-06-24T00:30:00Z"),
                new DemoEvidenceBundleVo(
                        status,
                        "Demo evidence bundle is ready.",
                        new DemoEvidenceBundleSummaryVo(12, 0, 2, 0, true),
                        new DemoReadinessVo(status, "Ready.", List.of(), List.of()),
                        new DemoSmokeChecklistVo(DemoSmokeChecklistStatus.READY, "Ready.", List.of(), List.of()),
                        null,
                        new DemoAdapterFixtureEvidenceVo(12, 0),
                        FixTaskQueueSummaryVo.empty(),
                        null,
                        "https://github.com/bingqin2/PatchPilot/pull/42",
                        null,
                        null,
                        0,
                        Instant.parse("2026-06-24T00:00:00Z"),
                        List.of()
                ),
                new DemoScriptVo(status, "Demo script is ready.", List.of(), List.of(), List.of(), Instant.parse("2026-06-24T00:30:00Z")),
                "# PatchPilot Demo Runbook\n\n- Status: `" + status + "`",
                trend(status),
                List.of("Open the dashboard."),
                List.of("GET /api/demo/session-snapshot is read-only."),
                "Status " + status + "; session " + sessionId + ".",
                List.of("Follow the script.")
        );
    }

    private static DemoReadinessSnapshotTrendVo trend(DemoReadinessStatus status) {
        return new DemoReadinessSnapshotTrendVo(
                DemoReadinessSnapshotTrendStatus.STABLE,
                "Demo readiness stayed at " + status + ".",
                "readiness-snapshot-new",
                "readiness-snapshot-old",
                status,
                status,
                0,
                0,
                0,
                "Keep the latest readiness snapshot and resolve remaining warnings before the live run.",
                "# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `STABLE`"
        );
    }

    private static final class IncrementingSnapshotSupplier implements java.util.function.Supplier<DemoSessionSnapshotVo> {

        private int index;

        @Override
        public DemoSessionSnapshotVo get() {
            index++;
            return snapshot("demo-session-" + index, DemoReadinessStatus.READY);
        }
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int index;

        @Override
        public String get() {
            index++;
            return "archive-" + index;
        }
    }
}
