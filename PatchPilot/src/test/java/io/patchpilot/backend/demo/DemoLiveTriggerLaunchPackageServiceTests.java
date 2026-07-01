package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCheckVo;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCommand;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveTriggerLaunchPackageServiceTests {

    private static final Instant NOW = Instant.parse("2026-07-02T00:00:00Z");

    @Test
    void should_create_ready_launch_package_when_gate_and_operator_archive_are_ready() {
        DemoLiveTriggerLaunchPackageService service = service(
                liveGate("READY", true),
                () -> Optional.of(operatorArchive("operator-archive-1", true))
        );

        DemoLiveTriggerLaunchPackageVo launchPackage = service.createPackage(command());

        assertThat(launchPackage.status()).isEqualTo("READY");
        assertThat(launchPackage.readyToPost()).isTrue();
        assertThat(launchPackage.operatorHandoffArchiveId()).isEqualTo("operator-archive-1");
        assertThat(launchPackage.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(launchPackage.triggerComment()).isEqualTo("/agent fix touch docs/live-package.md");
        assertThat(launchPackage.evidenceNotes())
                .contains("Latest external exposure operator handoff archive operator-archive-1 is ready.");
        assertThat(launchPackage.nextActions()).containsExactly(
                "Post `/agent fix touch docs/live-package.md` on https://github.com/bingqin2/PatchPilot/issues/1.",
                "After GitHub delivers the webhook, watch the task, Pull Request, and launch outcome tracker."
        );
        assertThat(launchPackage.markdownReport())
                .contains("# PatchPilot Live Trigger Launch Package")
                .contains("- Status: `READY`")
                .contains("- Operator handoff archive: `operator-archive-1`")
                .contains("## Exact GitHub Comment")
                .contains("`/agent fix touch docs/live-package.md`");
    }

    @Test
    void should_block_launch_package_when_operator_archive_is_missing() {
        DemoLiveTriggerLaunchPackageService service = service(
                liveGate("READY", true),
                Optional::empty
        );

        DemoLiveTriggerLaunchPackageVo launchPackage = service.createPackage(command());

        assertThat(launchPackage.status()).isEqualTo("BLOCKED");
        assertThat(launchPackage.readyToPost()).isFalse();
        assertThat(launchPackage.operatorHandoffArchiveId()).isNull();
        assertThat(launchPackage.summary()).isEqualTo("PatchPilot is blocked before posting the live trigger.");
        assertThat(launchPackage.nextActions())
                .contains("Archive a ready external exposure operator handoff checklist, then rebuild this launch package.");
        assertThat(launchPackage.markdownReport()).contains("Operator handoff archive is missing.");
    }

    @Test
    void should_block_launch_package_when_gate_is_not_ready_even_if_archive_is_ready() {
        DemoLiveTriggerLaunchPackageService service = service(
                liveGate("NEEDS_ATTENTION", false),
                () -> Optional.of(operatorArchive("operator-archive-1", true))
        );

        DemoLiveTriggerLaunchPackageVo launchPackage = service.createPackage(command());

        assertThat(launchPackage.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(launchPackage.readyToPost()).isFalse();
        assertThat(launchPackage.nextActions())
                .contains("Rerun the live launch gate and resolve all launch package checks before posting.");
    }

    private static DemoLiveTriggerLaunchPackageService service(
            DemoLiveLaunchGateVo gate,
            Supplier<Optional<ExternalExposureOperatorHandoffChecklistArchiveVo>> latestArchiveSupplier
    ) {
        return new DemoLiveTriggerLaunchPackageService(command -> gate, latestArchiveSupplier, () -> NOW);
    }

    private static DemoLiveTriggerLaunchPackageCommand command() {
        return new DemoLiveTriggerLaunchPackageCommand(
                "bingqin2",
                "PatchPilot",
                1,
                "bingqin2",
                "/agent fix touch docs/live-package.md"
        );
    }

    private static DemoLiveLaunchGateVo liveGate(String status, boolean readyToPost) {
        DemoLiveLaunchGateCommand gateCommand = new DemoLiveLaunchGateCommand(
                "bingqin2",
                "PatchPilot",
                1,
                "bingqin2",
                "/agent fix touch docs/live-package.md"
        );
        return new DemoLiveLaunchGateVo(
                status,
                readyToPost,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                gateCommand.triggerUser(),
                gateCommand.triggerComment(),
                readyToPost ? "PatchPilot is ready for a live /agent fix launch." : "PatchPilot needs attention before live launch.",
                readyToPost ? List.of("Post the exact /agent fix comment.") : List.of("Fix launch gate."),
                "Read-only live launch gate: this endpoint does not create tasks.",
                null,
                null,
                null,
                null,
                List.of(new DemoLiveLaunchGateCheckVo("Live launch gate", status, "gate " + status, "rerun gate")),
                NOW,
                "# PatchPilot Live Launch Gate"
        );
    }

    private static ExternalExposureOperatorHandoffChecklistArchiveVo operatorArchive(String id, boolean ready) {
        return new ExternalExposureOperatorHandoffChecklistArchiveVo(
                id,
                ready ? "READY" : "BLOCKED",
                ready,
                ready ? "Ready for next live step." : "Blocked before next live step.",
                ready ? "Proceed." : "Resolve blockers.",
                "bingqin2/PatchPilot",
                "closeout-archive-1",
                "session-1",
                "CLOSED",
                "https://example.trycloudflare.com",
                "https://example.trycloudflare.com/api/github/webhook",
                "READY",
                "CURRENT",
                "READY",
                true,
                0,
                5,
                0,
                0,
                5,
                List.of("Proceed to launch package."),
                List.of("External exposure closeout archive is ready."),
                List.of("Download operator handoff archive."),
                "Archive side-effect contract.",
                List.of(),
                NOW.minusSeconds(60),
                NOW.minusSeconds(30),
                "# PatchPilot External Exposure Operator Handoff Checklist"
        );
    }
}
