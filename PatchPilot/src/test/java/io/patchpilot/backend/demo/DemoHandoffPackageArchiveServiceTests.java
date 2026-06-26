package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoHandoffPackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DemoHandoffPackageArchiveServiceTests {

    @Test
    void should_archive_current_demo_handoff_package_and_keep_recent_archives_first() {
        DemoHandoffPackageArchiveService service = new DemoHandoffPackageArchiveService(
                new DemoSessionReportService(() -> DemoSessionArchiveServiceTests.snapshot("demo-session-1", DemoReadinessStatus.READY)),
                new InMemoryDemoHandoffPackageArchiveRepository(),
                () -> DemoSessionArchiveServiceTests.snapshot("demo-session-1", DemoReadinessStatus.READY),
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "handoff-archive-1"
        );

        DemoHandoffPackageArchiveVo archive = service.archiveCurrentHandoffPackage();

        assertThat(archive.id()).isEqualTo("handoff-archive-1");
        assertThat(archive.sessionId()).isEqualTo("demo-session-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.summary()).isEqualTo("Demo session demo-session-1 is ready.");
        assertThat(archive.shareSummary()).contains("demo-session-1");
        assertThat(archive.recentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-24T04:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Demo Handoff Package")
                .contains("## Handoff Readiness")
                .contains("## Embedded Session Report");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("handoff-archive-1")).contains(archive);
    }
}
