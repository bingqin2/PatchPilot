package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoScriptServiceTests {

    @Test
    void should_build_ready_demo_script_from_evidence_bundle() {
        DemoScriptService service = new DemoScriptService(() -> bundle(
                DemoReadinessStatus.READY,
                List.of("Follow the script from step 1 through Pull Request review.")
        ));

        DemoScriptVo script = service.getScript();

        assertThat(script.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(script.summary()).isEqualTo("Demo script is ready.");
        assertThat(script.steps()).hasSize(6);
        assertThat(script.steps()).extracting("name").containsExactly(
                "Confirm backend and dashboard access",
                "Confirm configuration and safety posture",
                "Verify repository support",
                "Create controlled /agent fix trigger",
                "Track task execution",
                "Review Pull Request and export evidence"
        );
        assertThat(script.steps().get(0).verificationCommand()).isEqualTo("curl http://127.0.0.1:8080/health");
        assertThat(script.steps().get(2).verificationCommand()).contains("/api/language-adapters/fixtures");
        assertThat(script.steps().get(2).verificationCommand()).contains("/api/language-adapters/runtime-readiness");
        assertThat(script.steps().get(2).evidence()).contains("adapter runtimes");
        assertThat(script.steps().get(3).operatorAction()).contains("/agent fix replace docs/demo.md PatchPilot smoke test");
        assertThat(script.steps().get(5).evidence()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(script.healthContract()).contains(
                "GET /api/demo/script is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.",
                "Live execution still starts from a controlled GitHub issue comment or manual task creation."
        );
        assertThat(script.nextActions()).containsExactly("Follow the script from step 1 through Pull Request review.");
    }

    @Test
    void should_propagate_attention_and_next_actions_from_evidence_bundle() {
        DemoScriptService service = new DemoScriptService(() -> bundle(
                DemoReadinessStatus.NEEDS_ATTENTION,
                List.of("Fix failing adapter fixtures before a live demo.")
        ));

        DemoScriptVo script = service.getScript();

        assertThat(script.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(script.summary()).isEqualTo("Demo script needs attention before use.");
        assertThat(script.steps().get(1).status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(script.nextActions()).containsExactly("Fix failing adapter fixtures before a live demo.");
    }

    private static DemoEvidenceBundleVo bundle(DemoReadinessStatus status, List<String> nextActions) {
        return new DemoEvidenceBundleVo(
                status,
                status == DemoReadinessStatus.READY ? "Demo evidence bundle is ready." : "Demo evidence bundle needs attention.",
                new DemoEvidenceBundleSummaryVo(12, status == DemoReadinessStatus.READY ? 0 : 1, 2, 0, true),
                readiness(status),
                smokeChecklist(status),
                null,
                new DemoAdapterFixtureEvidenceVo(12, status == DemoReadinessStatus.READY ? 0 : 1),
                new FixTaskQueueSummaryVo(2, 0, 0, 0, 0, 2, 0, 0),
                task(),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                webhookDelivery(),
                List.of(webhookDelivery()),
                new RejectedTriggerAuditSummaryVo(0, List.of(), List.of(), List.of(), List.of()),
                0,
                Instant.parse("2026-06-24T00:00:00Z"),
                nextActions
        );
    }

    private static DemoReadinessVo readiness(DemoReadinessStatus status) {
        return new DemoReadinessVo(
                status,
                status == DemoReadinessStatus.READY ? "PatchPilot is ready for a controlled demo." : "PatchPilot needs attention before a live demo.",
                List.of(
                        new DemoReadinessCheckVo("Backend", DemoReadinessStatus.READY, "Backend readiness endpoint is reachable.", "No action needed."),
                        new DemoReadinessCheckVo("Safety policy", status, "Safety policy evidence.", "Fix safety policy."),
                        new DemoReadinessCheckVo("Adapter fixtures", status, "Adapter fixture evidence.", "Fix adapter fixtures."),
                        new DemoReadinessCheckVo("Adapter runtimes", status, "Adapter runtime evidence.", "Fix adapter runtimes.")
                ),
                status == DemoReadinessStatus.READY ? List.of() : List.of("Fix adapter fixtures.")
        );
    }

    private static DemoSmokeChecklistVo smokeChecklist(DemoReadinessStatus status) {
        DemoSmokeChecklistStatus smokeStatus = status == DemoReadinessStatus.READY
                ? DemoSmokeChecklistStatus.READY
                : DemoSmokeChecklistStatus.NEEDS_ATTENTION;
        return new DemoSmokeChecklistVo(
                smokeStatus,
                smokeStatus == DemoSmokeChecklistStatus.READY ? "Live demo smoke checklist is ready." : "Live demo smoke checklist needs attention.",
                List.of(
                        new DemoSmokeChecklistStepVo(2, "Webhook delivery", smokeStatus, "Webhook delivery evidence.", "delivery-1", "Fix webhook delivery."),
                        new DemoSmokeChecklistStepVo(3, "Task execution", smokeStatus, "Task execution evidence.", "task-1", "Fix task execution."),
                        new DemoSmokeChecklistStepVo(4, "Pull Request evidence", smokeStatus, "Pull Request evidence.", "https://github.com/bingqin2/PatchPilot/pull/42", "Fix PR creation.")
                ),
                smokeStatus == DemoSmokeChecklistStatus.READY ? List.of() : List.of("Fix webhook delivery.")
        );
    }

    private static FixTaskVo task() {
        return new FixTaskVo(
                "task-1",
                "bingqin2",
                "PatchPilot",
                1,
                0,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "delivery-task-1",
                123,
                FixTaskStatus.COMPLETED,
                null,
                Instant.parse("2026-06-24T00:00:00Z"),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T00:05:00Z"),
                Instant.parse("2026-06-24T00:05:00Z"),
                "java",
                "maven",
                "mvn test",
                "Detected Maven project",
                456L,
                "https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456"
        );
    }

    private static WebhookDeliveryDiagnosticVo webhookDelivery() {
        return new WebhookDeliveryDiagnosticVo(
                "webhook-record-1",
                "delivery-1",
                "issue_comment",
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                "task-1",
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "Task created.",
                Instant.parse("2026-06-24T00:00:00Z")
        );
    }
}
