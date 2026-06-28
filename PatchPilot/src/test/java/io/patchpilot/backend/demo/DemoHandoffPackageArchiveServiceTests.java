package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
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
        InMemoryDemoHandoffPackageArchiveRepository repository = new InMemoryDemoHandoffPackageArchiveRepository();
        DemoHandoffPackageArchiveService service = new DemoHandoffPackageArchiveService(
                new DemoSessionReportService(DemoSessionReportServiceTests::snapshot),
                new DemoHandoffPackageArchiveSummaryService(repository),
                repository,
                DemoSessionReportServiceTests::snapshot,
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "handoff-archive-1"
        );

        DemoHandoffPackageArchiveVo archive = service.archiveCurrentHandoffPackage(
                DemoSessionReportServiceTests.readyHandoffRequest()
        );

        assertThat(archive.id()).isEqualTo("handoff-archive-1");
        assertThat(archive.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.summary()).isEqualTo("Demo session snapshot is ready.");
        assertThat(archive.handoffReadinessStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.handoffReadinessSummary())
                .isEqualTo("Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.");
        assertThat(archive.handoffReadinessNextAction()).isEqualTo("No missing handoff evidence.");
        assertThat(archive.handoffReadyCheckCount()).isEqualTo(8);
        assertThat(archive.handoffNeedsAttentionCheckCount()).isZero();
        assertThat(archive.handoffBlockedCheckCount()).isZero();
        assertThat(archive.shareSummary()).contains("task-1");
        assertThat(archive.recentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-24T04:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Demo Handoff Package")
                .contains("## Handoff Readiness")
                .contains("- Task evidence certificate: `READY` - Latest task evidence acceptance certificate archive is certified and ready.")
                .contains("## Embedded Session Report");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("handoff-archive-1")).contains(archive);
    }

    @Test
    void should_summarize_latest_share_ready_demo_handoff_package_archive() {
        InMemoryDemoHandoffPackageArchiveRepository repository = new InMemoryDemoHandoffPackageArchiveRepository();
        DemoHandoffPackageArchiveService service = new DemoHandoffPackageArchiveService(
                new DemoSessionReportService(DemoSessionReportServiceTests::snapshot),
                new DemoHandoffPackageArchiveSummaryService(repository),
                repository,
                DemoSessionReportServiceTests::snapshot,
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "handoff-archive-1"
        );
        service.archiveCurrentHandoffPackage(DemoSessionReportServiceTests.readyHandoffRequest());

        DemoHandoffPackageArchiveSummaryVo summary = service.getArchiveSummary();

        assertThat(summary.status()).isEqualTo("READY");
        assertThat(summary.shareReady()).isTrue();
        assertThat(summary.archiveCount()).isEqualTo(1);
        assertThat(summary.latestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(summary.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(summary.latestHandoffReadinessStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(summary.latestCreatedAt()).isEqualTo(Instant.parse("2026-06-24T04:00:00Z"));
        assertThat(summary.summary()).isEqualTo("Latest archived handoff package is READY and can be shared.");
        assertThat(summary.nextAction()).isEqualTo("No missing handoff evidence.");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Handoff Package Archive Summary")
                .contains("- Status: `READY`")
                .contains("- Share ready: `true`")
                .contains("- Latest archive: `handoff-archive-1`")
                .contains("read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub");
    }

    @Test
    void should_summarize_missing_demo_handoff_package_archives() {
        InMemoryDemoHandoffPackageArchiveRepository repository = new InMemoryDemoHandoffPackageArchiveRepository();
        DemoHandoffPackageArchiveService service = new DemoHandoffPackageArchiveService(
                new DemoSessionReportService(DemoSessionReportServiceTests::snapshot),
                new DemoHandoffPackageArchiveSummaryService(repository),
                repository,
                DemoSessionReportServiceTests::snapshot,
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "handoff-archive-1"
        );

        DemoHandoffPackageArchiveSummaryVo summary = service.getArchiveSummary();

        assertThat(summary.status()).isEqualTo("NO_ARCHIVE");
        assertThat(summary.shareReady()).isFalse();
        assertThat(summary.archiveCount()).isZero();
        assertThat(summary.latestArchiveId()).isNull();
        assertThat(summary.latestSessionId()).isNull();
        assertThat(summary.latestHandoffReadinessStatus()).isNull();
        assertThat(summary.latestCreatedAt()).isNull();
        assertThat(summary.summary()).isEqualTo("No handoff package archive has been captured.");
        assertThat(summary.nextAction())
                .isEqualTo("Archive a demo handoff package after a completed live run before sharing handoff evidence.");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Handoff Package Archive Summary")
                .contains("- Status: `NO_ARCHIVE`")
                .contains("- Share ready: `false`");
    }
}
