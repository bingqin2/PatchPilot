package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
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

class DemoSessionSnapshotServiceTests {

    @Test
    void should_build_ready_demo_session_snapshot_from_single_evidence_bundle() {
        DemoSessionSnapshotService service = new DemoSessionSnapshotService(
                () -> bundle(DemoReadinessStatus.READY, List.of("Follow the script from step 1 through Pull Request review.")),
                DemoSessionSnapshotServiceTests::trend,
                () -> Instant.parse("2026-06-24T00:30:00Z")
        );

        DemoSessionSnapshotVo snapshot = service.getSessionSnapshot();

        assertThat(snapshot.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(snapshot.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(snapshot.summary()).isEqualTo("Demo session snapshot is ready.");
        assertThat(snapshot.generatedAt()).isEqualTo(Instant.parse("2026-06-24T00:30:00Z"));
        assertThat(snapshot.evidenceBundle().recentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(snapshot.script().steps()).hasSize(6);
        assertThat(snapshot.runbook()).contains("# PatchPilot Demo Runbook");
        assertThat(snapshot.runbook()).contains("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(snapshot.readinessSnapshotTrend().status()).isEqualTo(DemoReadinessSnapshotTrendStatus.IMPROVING);
        assertThat(snapshot.readinessSnapshotTrend().latestSnapshotId()).isEqualTo("readiness-snapshot-new");
        assertThat(snapshot.readinessSnapshotTrend().nextAction()).contains("Use the latest readiness snapshot as demo evidence");
        assertThat(snapshot.operatorChecklist()).contains(
                "Open the dashboard and confirm the demo session snapshot status.",
                "Confirm adapter runtime executables are available on the backend PATH.",
                "Verify the latest webhook delivery and recent task before posting a live trigger.",
                "Copy the runbook after Pull Request evidence is visible."
        );
        assertThat(snapshot.healthContract()).contains(
                "GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.",
                "The snapshot only combines existing demo evidence, script, and runbook read models."
        );
        assertThat(snapshot.shareSummary()).contains("READY");
        assertThat(snapshot.shareSummary()).contains("task-1");
        assertThat(snapshot.shareSummary()).contains("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(snapshot.nextActions()).containsExactly("Follow the script from step 1 through Pull Request review.");
    }

    @Test
    void should_surface_attention_state_and_next_actions() {
        DemoSessionSnapshotService service = new DemoSessionSnapshotService(
                () -> bundle(DemoReadinessStatus.NEEDS_ATTENTION, List.of("Fix failing adapter fixtures before a live demo.")),
                DemoSessionSnapshotServiceTests::trend,
                () -> Instant.parse("2026-06-24T00:30:00Z")
        );

        DemoSessionSnapshotVo snapshot = service.getSessionSnapshot();

        assertThat(snapshot.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(snapshot.summary()).isEqualTo("Demo session snapshot needs attention before use.");
        assertThat(snapshot.shareSummary()).contains("NEEDS_ATTENTION");
        assertThat(snapshot.operatorChecklist()).contains("Install missing adapter executables on the backend PATH before demonstrating affected languages.");
        assertThat(snapshot.nextActions()).containsExactly("Fix failing adapter fixtures before a live demo.");
    }

    private static DemoReadinessSnapshotTrendVo trend() {
        return new DemoReadinessSnapshotTrendVo(
                DemoReadinessSnapshotTrendStatus.IMPROVING,
                "Demo readiness improved from BLOCKED to READY.",
                "readiness-snapshot-new",
                "readiness-snapshot-old",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.BLOCKED,
                4,
                -2,
                -2,
                "Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.",
                "# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`"
        );
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
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                DemoReadinessStatus.READY,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                List.of(
                        "Download handoff package archive handoff-archive-1.",
                        "Download handoff package archive summary.",
                        "Download handoff share checklist."
                ),
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
                        new DemoReadinessCheckVo(
                                "Adapter runtimes",
                                status,
                                status == DemoReadinessStatus.READY
                                        ? "13 adapter runtime executables are available on PATH."
                                        : "1 adapter runtime executable is missing: python-hatch requires `python`.",
                                status == DemoReadinessStatus.READY
                                        ? "No action needed."
                                        : "Install missing adapter executables on the backend PATH before demonstrating affected languages."
                        )
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
