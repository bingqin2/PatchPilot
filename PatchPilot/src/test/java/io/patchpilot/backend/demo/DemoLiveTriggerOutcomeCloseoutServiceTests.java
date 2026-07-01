package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutVo;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveTriggerOutcomeCloseoutServiceTests {

    private static final DemoLiveTriggerOutcomeCloseoutCommand COMMAND =
            new DemoLiveTriggerOutcomeCloseoutCommand(
                    "bingqin2",
                    "PatchPilot",
                    1,
                    "bingqin2",
                    "/agent fix touch docs/live-outcome.md",
                    null
            );

    @Test
    void should_close_out_completed_live_trigger_with_pull_request_and_launch_archive() {
        InMemoryDemoLiveTriggerLaunchPackageArchiveRepository archiveRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        archiveRepository.save(archive("launch-package-archive-1"));
        InMemoryFixTaskService taskService = new InMemoryFixTaskService();
        String taskId = taskService.createFixTask(createTaskCommand("delivery-1")).id();
        taskService.markCompleted(taskId, "https://github.com/bingqin2/PatchPilot/pull/42");
        DemoLiveTriggerOutcomeCloseoutService service = service(archiveRepository, taskService);

        DemoLiveTriggerOutcomeCloseoutVo closeout = service.createCloseout(COMMAND);

        assertThat(closeout.status()).isEqualTo("READY");
        assertThat(closeout.successful()).isTrue();
        assertThat(closeout.launchPackageArchiveId()).isEqualTo("launch-package-archive-1");
        assertThat(closeout.taskId()).isEqualTo(taskId);
        assertThat(closeout.taskStatus()).isEqualTo("COMPLETED");
        assertThat(closeout.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(closeout.webhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(closeout.summary()).contains("created Pull Request");
        assertThat(closeout.evidenceNotes()).anySatisfy(note ->
                assertThat(note).contains("launch-package-archive-1"));
        assertThat(closeout.nextActions()).contains("Review and merge https://github.com/bingqin2/PatchPilot/pull/42.");
        assertThat(closeout.sideEffectContract()).contains("read-only");
        assertThat(closeout.markdownReport()).contains("# PatchPilot Live Trigger Outcome Closeout");
        assertThat(closeout.markdownReport()).contains("https://github.com/bingqin2/PatchPilot/pull/42");
    }

    @Test
    void should_block_when_no_matching_task_exists_after_live_trigger() {
        InMemoryDemoLiveTriggerLaunchPackageArchiveRepository archiveRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        archiveRepository.save(archive("launch-package-archive-1"));
        DemoLiveTriggerOutcomeCloseoutService service = service(archiveRepository, new InMemoryFixTaskService());

        DemoLiveTriggerOutcomeCloseoutVo closeout = service.createCloseout(COMMAND);

        assertThat(closeout.status()).isEqualTo("BLOCKED");
        assertThat(closeout.successful()).isFalse();
        assertThat(closeout.taskId()).isNull();
        assertThat(closeout.summary()).contains("No matching task");
        assertThat(closeout.nextActions()).contains("Check GitHub webhook delivery for the exact issue comment and redeliver the event if GitHub did not reach PatchPilot.");
    }

    @Test
    void should_mark_failed_task_as_needing_attention_with_failure_reason() {
        InMemoryDemoLiveTriggerLaunchPackageArchiveRepository archiveRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        archiveRepository.save(archive("launch-package-archive-1"));
        InMemoryFixTaskService taskService = new InMemoryFixTaskService();
        String taskId = taskService.createFixTask(createTaskCommand("delivery-1")).id();
        taskService.markFailed(taskId, "maven test failed");
        DemoLiveTriggerOutcomeCloseoutService service = service(archiveRepository, taskService);

        DemoLiveTriggerOutcomeCloseoutVo closeout = service.createCloseout(COMMAND);

        assertThat(closeout.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(closeout.successful()).isFalse();
        assertThat(closeout.taskId()).isEqualTo(taskId);
        assertThat(closeout.taskStatus()).isEqualTo("FAILED");
        assertThat(closeout.failureReason()).isEqualTo("maven test failed");
        assertThat(closeout.summary()).contains("ended as FAILED");
        assertThat(closeout.nextActions()).anySatisfy(action -> assertThat(action).contains("maven test failed"));
    }

    @Test
    void should_block_when_requested_launch_package_archive_is_missing() {
        DemoLiveTriggerOutcomeCloseoutService service = service(
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                new InMemoryFixTaskService()
        );

        DemoLiveTriggerOutcomeCloseoutVo closeout = service.createCloseout(new DemoLiveTriggerOutcomeCloseoutCommand(
                "bingqin2",
                "PatchPilot",
                1,
                "bingqin2",
                "/agent fix touch docs/live-outcome.md",
                "missing-archive"
        ));

        assertThat(closeout.status()).isEqualTo("BLOCKED");
        assertThat(closeout.summary()).contains("No launch package archive");
        assertThat(closeout.launchPackageArchiveId()).isEqualTo("missing-archive");
    }

    private static DemoLiveTriggerOutcomeCloseoutService service(
            DemoLiveTriggerLaunchPackageArchiveRepository archiveRepository,
            InMemoryFixTaskService taskService
    ) {
        return new DemoLiveTriggerOutcomeCloseoutService(
                archiveRepository,
                taskService,
                () -> Instant.parse("2026-07-02T01:00:00Z")
        );
    }

    private static CreateFixTaskCommand createTaskCommand(String deliveryId) {
        return new CreateFixTaskCommand(
                COMMAND.repositoryOwner(),
                COMMAND.repositoryName(),
                COMMAND.issueNumber(),
                123L,
                COMMAND.triggerUser(),
                COMMAND.triggerComment(),
                deliveryId,
                456L
        );
    }

    private static DemoLiveTriggerLaunchPackageArchiveVo archive(String id) {
        return new DemoLiveTriggerLaunchPackageArchiveVo(
                id,
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-outcome.md",
                "PatchPilot is ready for live trigger posting.",
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
}
