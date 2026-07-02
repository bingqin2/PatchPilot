package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoEvidenceBundleArchiveServiceTests {

    @Test
    void should_archive_ready_live_demo_evidence_bundle_for_stable_handoff() {
        DemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        launchRepository.save(readyLaunchArchive("launch-package-archive-1"));
        closeoutRepository.save(successfulCloseoutArchive("outcome-closeout-archive-1", "launch-package-archive-1"));
        DemoLiveDemoEvidenceBundleArchiveService service = service(launchRepository, closeoutRepository);

        DemoLiveDemoEvidenceBundleArchiveVo archive = service.archiveBundle();

        assertThat(archive.id()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.readyForHandoff()).isTrue();
        assertThat(archive.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(archive.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(archive.launchPackageArchiveId()).isEqualTo("launch-package-archive-1");
        assertThat(archive.outcomeCloseoutArchiveId()).isEqualTo("outcome-closeout-archive-1");
        assertThat(archive.taskId()).isEqualTo("task-1");
        assertThat(archive.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.sideEffectContract()).contains("Archive creation writes only PatchPilot local archive records");
        assertThat(archive.bundleGeneratedAt()).isEqualTo(Instant.parse("2026-07-02T02:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-07-02T03:00:00Z"));
        assertThat(archive.report()).contains("# PatchPilot Live Demo Evidence Bundle Archive");
        assertThat(archive.report()).contains("outcome-closeout-archive-1");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("live-demo-evidence-bundle-archive-1")).contains(archive);
    }

    @Test
    void should_keep_latest_twenty_archives_first() {
        DemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        launchRepository.save(readyLaunchArchive("launch-package-archive-1"));
        closeoutRepository.save(successfulCloseoutArchive("outcome-closeout-archive-1", "launch-package-archive-1"));
        AtomicInteger nextId = new AtomicInteger();
        DemoLiveDemoEvidenceBundleService bundleService = new DemoLiveDemoEvidenceBundleService(
                launchRepository,
                closeoutRepository,
                () -> Instant.parse("2026-07-02T02:00:00Z")
        );
        DemoLiveDemoEvidenceBundleArchiveService service = new DemoLiveDemoEvidenceBundleArchiveService(
                bundleService,
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository(),
                () -> "archive-" + nextId.incrementAndGet(),
                () -> Instant.parse("2026-07-02T03:00:00Z")
        );

        for (int index = 0; index < 21; index++) {
            service.archiveBundle();
        }

        assertThat(service.listRecentArchives()).hasSize(20);
        assertThat(service.listRecentArchives().get(0).id()).isEqualTo("archive-21");
        assertThat(service.listRecentArchives())
                .extracting(DemoLiveDemoEvidenceBundleArchiveVo::id)
                .doesNotContain("archive-1");
    }

    private static DemoLiveDemoEvidenceBundleArchiveService service(
            DemoLiveTriggerLaunchPackageArchiveRepository launchRepository,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository
    ) {
        DemoLiveDemoEvidenceBundleService bundleService = new DemoLiveDemoEvidenceBundleService(
                launchRepository,
                closeoutRepository,
                () -> Instant.parse("2026-07-02T02:00:00Z")
        );
        return new DemoLiveDemoEvidenceBundleArchiveService(
                bundleService,
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository(),
                () -> "live-demo-evidence-bundle-archive-1",
                () -> Instant.parse("2026-07-02T03:00:00Z")
        );
    }

    private static DemoLiveTriggerLaunchPackageArchiveVo readyLaunchArchive(String id) {
        return new DemoLiveTriggerLaunchPackageArchiveVo(
                id,
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "PatchPilot is ready for the operator to post the live trigger.",
                "operator-archive-1",
                true,
                Instant.parse("2026-07-02T00:00:00Z"),
                "READY",
                true,
                List.of("Launch package archive was ready."),
                List.of("Post the exact comment."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T00:00:01Z"),
                Instant.parse("2026-07-02T00:00:05Z"),
                "# PatchPilot Live Trigger Launch Package"
        );
    }

    private static DemoLiveTriggerOutcomeCloseoutArchiveVo successfulCloseoutArchive(String id, String launchArchiveId) {
        return new DemoLiveTriggerOutcomeCloseoutArchiveVo(
                id,
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                launchArchiveId,
                "READY",
                Instant.parse("2026-07-02T00:00:05Z"),
                "task-1",
                "COMPLETED",
                null,
                Instant.parse("2026-07-02T00:10:00Z"),
                Instant.parse("2026-07-02T00:11:00Z"),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "TASK_CREATED",
                "Live trigger created a Pull Request.",
                List.of("Task task-1 completed."),
                List.of("Review and merge https://github.com/bingqin2/PatchPilot/pull/42."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T01:00:00Z"),
                Instant.parse("2026-07-02T01:05:00Z"),
                "# PatchPilot Live Trigger Outcome Closeout"
        );
    }
}
