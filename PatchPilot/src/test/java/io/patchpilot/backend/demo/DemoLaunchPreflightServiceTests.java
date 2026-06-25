package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchPreflightVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.enums.TriggerEvaluationSource;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.TriggerEvaluationService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchPreflightServiceTests {

    @Test
    void should_report_ready_when_readiness_is_ready_and_issue_comment_trigger_would_create_task() {
        AtomicReference<EvaluateTriggerCommand> capturedCommand = new AtomicReference<>();
        DemoLaunchPreflightService service = new DemoLaunchPreflightService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                command -> {
                    capturedCommand.set(command);
                    return wouldCreateTask();
                }
        );

        DemoLaunchPreflightVo preflight = service.preflight(request(
                " bingqin2 ",
                " PatchPilot ",
                12L,
                " bingqin2 ",
                " /agent fix update docs/demo.md "
        ));

        assertThat(preflight.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(preflight.readyToPost()).isTrue();
        assertThat(preflight.summary()).isEqualTo("Demo launch preflight is ready to post the tested /agent fix comment.");
        assertThat(preflight.triggerEvaluation().source()).isEqualTo("ISSUE_COMMENT");
        assertThat(preflight.triggerEvaluation().wouldCreateTask()).isTrue();
        assertThat(preflight.nextActions()).containsExactly("Post the tested /agent fix comment on the controlled GitHub issue.");
        assertThat(capturedCommand.get()).isEqualTo(new EvaluateTriggerCommand(
                TriggerEvaluationSource.ISSUE_COMMENT,
                "bingqin2",
                "PatchPilot",
                12L,
                "bingqin2",
                "/agent fix update docs/demo.md"
        ));
    }

    @Test
    void should_block_when_issue_comment_trigger_would_not_create_task() {
        DemoLaunchPreflightService service = new DemoLaunchPreflightService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                command -> blockedTrigger()
        );

        DemoLaunchPreflightVo preflight = service.preflight(request(
                "bingqin2",
                "PatchPilot",
                12L,
                "intruder",
                "/agent fix delete everything"
        ));

        assertThat(preflight.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(preflight.readyToPost()).isFalse();
        assertThat(preflight.summary()).isEqualTo("Demo launch preflight is blocked because the tested /agent fix comment would not create a task.");
        assertThat(preflight.nextActions()).containsExactly(
                "Revise the tested /agent fix comment: Trigger user intruder is not allowed.",
                "Revise the /agent fix request before creating a task."
        );
    }

    @Test
    void should_need_attention_when_readiness_has_warnings_but_trigger_would_create_task() {
        DemoLaunchPreflightService service = new DemoLaunchPreflightService(
                () -> readiness(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        List.of("Run one controlled issue-to-PR smoke task before a live demo.")
                ),
                command -> wouldCreateTask()
        );

        DemoLaunchPreflightVo preflight = service.preflight(request(
                "bingqin2",
                "PatchPilot",
                12L,
                "bingqin2",
                "/agent fix update docs/demo.md"
        ));

        assertThat(preflight.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(preflight.readyToPost()).isFalse();
        assertThat(preflight.summary()).isEqualTo("Demo launch preflight needs attention before posting /agent fix.");
        assertThat(preflight.nextActions()).containsExactly(
                "Run one controlled issue-to-PR smoke task before a live demo.",
                "Resolve readiness warnings, then rerun this launch preflight."
        );
    }

    @Test
    void should_reject_invalid_preflight_request_before_evaluating_trigger() {
        DemoLaunchPreflightService service = new DemoLaunchPreflightService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                command -> {
                    throw new AssertionError("trigger evaluation should not run for invalid requests");
                }
        );

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.preflight(request(
                        "bingqin2",
                        "PatchPilot",
                        0L,
                        "bingqin2",
                        "please fix it"
                )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueNumber must be positive");
    }

    private static DemoLaunchPreflightRequestDto request(
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment
    ) {
        return new DemoLaunchPreflightRequestDto(
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment
        );
    }

    private static DemoReadinessVo readiness(DemoReadinessStatus status, List<String> nextActions) {
        return new DemoReadinessVo(
                status,
                switch (status) {
                    case READY -> "PatchPilot is ready for a controlled demo.";
                    case NEEDS_ATTENTION -> "PatchPilot needs attention before a live demo.";
                    case BLOCKED -> "PatchPilot is blocked for demo use.";
                },
                List.of(new DemoReadinessCheckVo("Backend", DemoReadinessStatus.READY, "Backend is reachable.", "No action needed.")),
                nextActions
        );
    }

    private static TriggerEvaluationResultVo wouldCreateTask() {
        return new TriggerEvaluationResultVo(
                "WOULD_CREATE_TASK",
                "ISSUE_COMMENT",
                true,
                null,
                null,
                decision(true, "Accepted", "UNKNOWN"),
                decision(true, "No active task exists for this issue", "UNKNOWN"),
                decision(true, "not blocked before task creation", "UNKNOWN"),
                decision(true, "not rate limited before task creation", "UNKNOWN"),
                decision(true, "model accepted trigger: concrete issue request", "UNKNOWN"),
                true,
                "Create task is allowed for this trigger."
        );
    }

    private static TriggerEvaluationResultVo blockedTrigger() {
        return new TriggerEvaluationResultVo(
                "BLOCKED",
                "ISSUE_COMMENT",
                false,
                "Trigger user intruder is not allowed",
                "TRIGGER_USER_NOT_ALLOWED",
                decision(false, "Trigger user intruder is not allowed", "TRIGGER_USER_NOT_ALLOWED"),
                null,
                null,
                null,
                null,
                false,
                "Revise the /agent fix request before creating a task."
        );
    }

    private static TriggerEvaluationDecisionVo decision(boolean allowed, String reason, String category) {
        return new TriggerEvaluationDecisionVo(allowed, reason, category);
    }
}
