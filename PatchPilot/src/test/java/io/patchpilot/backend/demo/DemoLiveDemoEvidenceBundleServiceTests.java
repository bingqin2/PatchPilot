package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoEvidenceBundleServiceTests {

    @Test
    void should_create_ready_bundle_from_matching_launch_and_successful_closeout_archives() {
        DemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        launchRepository.save(readyLaunchArchive("launch-package-archive-1"));
        closeoutRepository.save(successfulCloseoutArchive("outcome-closeout-archive-1", "launch-package-archive-1"));
        DemoLiveDemoEvidenceBundleService service = new DemoLiveDemoEvidenceBundleService(
                launchRepository,
                closeoutRepository,
                () -> Instant.parse("2026-07-02T02:00:00Z")
        );

        DemoLiveDemoEvidenceBundleVo bundle = service.createBundle();

        assertThat(bundle.status()).isEqualTo("READY");
        assertThat(bundle.readyForHandoff()).isTrue();
        assertThat(bundle.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(bundle.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(bundle.launchPackageArchiveId()).isEqualTo("launch-package-archive-1");
        assertThat(bundle.outcomeCloseoutArchiveId()).isEqualTo("outcome-closeout-archive-1");
        assertThat(bundle.taskId()).isEqualTo("task-1");
        assertThat(bundle.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(bundle.summary()).contains("ready for handoff");
        assertThat(bundle.evidenceNotes()).anySatisfy(note -> assertThat(note).contains("launch-package-archive-1"));
        assertThat(bundle.nextActions()).contains("Review and merge https://github.com/bingqin2/PatchPilot/pull/42.");
        assertThat(bundle.sideEffectContract()).contains("read-only");
        assertThat(bundle.generatedAt()).isEqualTo(Instant.parse("2026-07-02T02:00:00Z"));
        assertThat(bundle.markdownReport()).contains("# PatchPilot Live Demo Evidence Bundle");
        assertThat(bundle.markdownReport()).contains("outcome-closeout-archive-1");
    }

    @Test
    void should_block_without_required_archives() {
        DemoLiveDemoEvidenceBundleService service = new DemoLiveDemoEvidenceBundleService(
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository(),
                () -> Instant.parse("2026-07-02T02:00:00Z")
        );

        DemoLiveDemoEvidenceBundleVo bundle = service.createBundle();

        assertThat(bundle.status()).isEqualTo("BLOCKED");
        assertThat(bundle.readyForHandoff()).isFalse();
        assertThat(bundle.summary()).contains("missing");
        assertThat(bundle.nextActions()).contains("Create and archive a live trigger launch package before posting the GitHub issue comment.");
        assertThat(bundle.markdownReport()).contains("PatchPilot Live Demo Evidence Bundle");
    }

    @Test
    void should_need_attention_when_closeout_does_not_match_latest_launch_archive() {
        DemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        launchRepository.save(readyLaunchArchive("launch-package-archive-2"));
        closeoutRepository.save(successfulCloseoutArchive("outcome-closeout-archive-1", "launch-package-archive-1"));
        DemoLiveDemoEvidenceBundleService service = new DemoLiveDemoEvidenceBundleService(
                launchRepository,
                closeoutRepository,
                () -> Instant.parse("2026-07-02T02:00:00Z")
        );

        DemoLiveDemoEvidenceBundleVo bundle = service.createBundle();

        assertThat(bundle.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(bundle.readyForHandoff()).isFalse();
        assertThat(bundle.summary()).contains("does not match");
        assertThat(bundle.nextActions()).contains("Generate and archive a new outcome closeout for launch package archive launch-package-archive-2.");
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
