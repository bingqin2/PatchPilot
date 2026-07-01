package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveTriggerOutcomeCloseoutArchiveServiceTests {

    private static final DemoLiveTriggerOutcomeCloseoutCommand COMMAND =
            new DemoLiveTriggerOutcomeCloseoutCommand(
                    "bingqin2",
                    "PatchPilot",
                    1,
                    "bingqin2",
                    "/agent fix touch docs/live-outcome.md",
                    "launch-package-archive-1"
            );

    @Test
    void should_archive_and_find_live_trigger_outcome_closeout_snapshot() {
        DemoLiveTriggerOutcomeCloseoutArchiveService service = new DemoLiveTriggerOutcomeCloseoutArchiveService(
                new StubOutcomeCloseoutService(),
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository(),
                () -> "outcome-closeout-archive-1",
                () -> Instant.parse("2026-07-02T01:05:00Z")
        );

        DemoLiveTriggerOutcomeCloseoutArchiveVo archive = service.archiveCloseout(COMMAND);

        assertThat(archive.id()).isEqualTo("outcome-closeout-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.successful()).isTrue();
        assertThat(archive.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(archive.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(archive.launchPackageArchiveId()).isEqualTo("launch-package-archive-1");
        assertThat(archive.taskId()).isEqualTo("task-1");
        assertThat(archive.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.sideEffectContract())
                .contains("Archive creation writes only PatchPilot local archive records")
                .contains("read-only live trigger outcome closeout");
        assertThat(archive.closeoutGeneratedAt()).isEqualTo(Instant.parse("2026-07-02T01:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-07-02T01:05:00Z"));
        assertThat(archive.report()).contains("# PatchPilot Live Trigger Outcome Closeout");
        assertThat(service.listRecentArchives()).extracting(DemoLiveTriggerOutcomeCloseoutArchiveVo::id)
                .containsExactly("outcome-closeout-archive-1");
        assertThat(service.findArchive("outcome-closeout-archive-1")).contains(archive);
    }

    private static final class StubOutcomeCloseoutService extends DemoLiveTriggerOutcomeCloseoutService {

        private StubOutcomeCloseoutService() {
            super(
                    new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                    new io.patchpilot.backend.task.service.impl.InMemoryFixTaskService(),
                    Instant::now
            );
        }

        @Override
        public DemoLiveTriggerOutcomeCloseoutVo createCloseout(DemoLiveTriggerOutcomeCloseoutCommand command) {
            return new DemoLiveTriggerOutcomeCloseoutVo(
                    "READY",
                    true,
                    command.repositoryOwner() + "/" + command.repositoryName(),
                    command.issueNumber(),
                    "https://github.com/bingqin2/PatchPilot/issues/1",
                    command.triggerUser(),
                    command.triggerComment(),
                    command.launchPackageArchiveId(),
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
                    "Live trigger created task task-1 and created Pull Request https://github.com/bingqin2/PatchPilot/pull/42.",
                    List.of("Launch package archive launch-package-archive-1 was used.", "Task task-1 completed."),
                    List.of("Review and merge https://github.com/bingqin2/PatchPilot/pull/42."),
                    "read-only live trigger outcome closeout: this endpoint does not mutate GitHub or task state.",
                    Instant.parse("2026-07-02T01:00:00Z"),
                    "# PatchPilot Live Trigger Outcome Closeout\n\n- Pull Request: https://github.com/bingqin2/PatchPilot/pull/42"
            );
        }
    }
}
