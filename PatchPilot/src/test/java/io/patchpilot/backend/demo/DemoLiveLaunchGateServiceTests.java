package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCommand;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.GitHubTriggerDryRunService;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.TriggerEvaluationService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveLaunchGateServiceTests {

    private static final Instant NOW = Instant.parse("2026-07-01T10:00:00Z");

    @Test
    void should_report_ready_when_all_live_launch_inputs_are_ready() {
        DemoLiveLaunchGateService service = service(
                launchReadiness(DemoReadinessStatus.READY),
                webhookReadiness("READY"),
                publishPreflight("READY", true),
                readyTriggerEvaluation()
        );

        DemoLiveLaunchGateVo gate = service.getGate(command("/agent fix touch docs/live-gate.md"));

        assertThat(gate.status()).isEqualTo("READY");
        assertThat(gate.readyToPost()).isTrue();
        assertThat(gate.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(gate.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(gate.summary()).isEqualTo("PatchPilot is ready for a live /agent fix launch.");
        assertThat(gate.sideEffectContract()).contains("does not create tasks");
        assertThat(gate.sideEffectContract()).contains("does not write GitHub comments");
        assertThat(gate.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Self-hosted launch readiness:READY",
                        "Webhook setup:READY",
                        "Live GitHub publish preflight:READY",
                        "Live trigger dry run:READY"
                );
        assertThat(gate.nextActions()).containsExactly(
                "Post the exact /agent fix comment on the GitHub issue and watch webhook delivery, task execution, and Pull Request creation."
        );
        assertThat(gate.markdownReport())
                .contains("# PatchPilot Live Launch Gate")
                .contains("Status: READY")
                .contains("Trigger: `/agent fix touch docs/live-gate.md`");
    }

    @Test
    void should_block_when_trigger_dry_run_would_not_create_task() {
        DemoLiveLaunchGateService service = service(
                launchReadiness(DemoReadinessStatus.READY),
                webhookReadiness("READY"),
                publishPreflight("READY", true),
                blockedTriggerEvaluation("Unsafe request rejected")
        );

        DemoLiveLaunchGateVo gate = service.getGate(command("/agent fix delete the repository"));

        assertThat(gate.status()).isEqualTo("BLOCKED");
        assertThat(gate.readyToPost()).isFalse();
        assertThat(gate.summary()).isEqualTo("PatchPilot is blocked before live launch.");
        assertThat(gate.triggerDryRun().wouldCreateTask()).isFalse();
        assertThat(gate.nextActions()).contains("Revise the /agent fix comment before posting it on GitHub.");
        assertThat(gate.markdownReport()).contains("- Live trigger dry run: BLOCKED");
    }

    @Test
    void should_require_attention_when_publish_preflight_is_not_clean() {
        DemoLiveLaunchGateService service = service(
                launchReadiness(DemoReadinessStatus.READY),
                webhookReadiness("READY"),
                publishPreflight("NEEDS_ATTENTION", false),
                readyTriggerEvaluation()
        );

        DemoLiveLaunchGateVo gate = service.getGate(command("/agent fix touch docs/live-gate.md"));

        assertThat(gate.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(gate.readyToPost()).isFalse();
        assertThat(gate.summary()).isEqualTo("PatchPilot needs attention before live launch.");
        assertThat(gate.nextActions())
                .contains("Resolve live publish preflight warnings, then rerun this gate before posting.");
    }

    private static DemoLiveLaunchGateService service(
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            GitHubWebhookSetupReadinessVo webhookReadiness,
            GitHubLivePublishPreflightVo publishPreflight,
            TriggerEvaluationResultVo triggerEvaluation
    ) {
        TriggerEvaluationService triggerEvaluationService = command -> triggerEvaluation;
        return new DemoLiveLaunchGateService(
                () -> launchReadiness,
                () -> webhookReadiness,
                (owner, repository) -> publishPreflight,
                new GitHubTriggerDryRunService(triggerEvaluationService),
                () -> NOW
        );
    }

    private static DemoLiveLaunchGateCommand command(String triggerComment) {
        return new DemoLiveLaunchGateCommand(
                "bingqin2",
                "PatchPilot",
                1,
                "bingqin2",
                triggerComment
        );
    }

    private static DemoSelfHostedLaunchReadinessVo launchReadiness(DemoReadinessStatus status) {
        return new DemoSelfHostedLaunchReadinessVo(
                status,
                status == DemoReadinessStatus.READY,
                "Self-hosted launch readiness is " + status,
                List.of(new DemoSelfHostedLaunchCheckVo("runtime", status, "runtime " + status, "check runtime")),
                status == DemoReadinessStatus.READY ? List.of() : List.of("Fix self-hosted launch readiness."),
                NOW,
                "launch readiness report"
        );
    }

    private static GitHubWebhookSetupReadinessVo webhookReadiness(String status) {
        return new GitHubWebhookSetupReadinessVo(
                status,
                true,
                true,
                "https://example.trycloudflare.com",
                "https://example.trycloudflare.com/api/github/webhook",
                "https://example.trycloudflare.com/health",
                "OK",
                "delivery-1",
                false,
                "Webhook setup is " + status,
                status.equals("READY") ? List.of() : List.of("Fix webhook setup."),
                NOW,
                "webhook readiness report"
        );
    }

    private static GitHubLivePublishPreflightVo publishPreflight(String status, boolean ready) {
        return new GitHubLivePublishPreflightVo(
                status,
                ready,
                true,
                true,
                "bingqin2/PatchPilot",
                "main",
                List.of(),
                List.of(),
                "Live publish preflight is " + status,
                ready ? "Live publish is ready." : "Resolve live publish preflight warnings, then rerun this gate before posting.",
                "publish preflight side effect contract",
                List.of(new GitHubLivePublishPreflightCheckVo("publish", status, "publish " + status, "check publish")),
                List.of("publish evidence"),
                12,
                NOW
        );
    }

    private static TriggerEvaluationResultVo readyTriggerEvaluation() {
        return new TriggerEvaluationResultVo(
                "WOULD_CREATE_TASK",
                "ISSUE_COMMENT",
                true,
                null,
                null,
                new TriggerEvaluationDecisionVo(true, "safe", null),
                new TriggerEvaluationDecisionVo(true, "no active task", null),
                new TriggerEvaluationDecisionVo(true, "not quarantined", null),
                new TriggerEvaluationDecisionVo(true, "rate limit ok", null),
                new TriggerEvaluationDecisionVo(true, "intent accepted", null),
                true,
                "Create a task."
        );
    }

    private static TriggerEvaluationResultVo blockedTriggerEvaluation(String blockedReason) {
        return new TriggerEvaluationResultVo(
                "BLOCKED",
                "ISSUE_COMMENT",
                false,
                blockedReason,
                "DANGEROUS_INSTRUCTION",
                new TriggerEvaluationDecisionVo(false, blockedReason, "DANGEROUS_INSTRUCTION"),
                null,
                null,
                null,
                null,
                true,
                "Revise the request."
        );
    }
}
