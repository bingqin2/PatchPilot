package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoEvaluationRunReadinessEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
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

import java.time.Instant;
import java.util.List;

final class DemoLaunchEvidenceFixtures {

    private DemoLaunchEvidenceFixtures() {
    }

    static DemoSelfHostedLaunchReadinessVo launchReadiness(DemoReadinessStatus status) {
        boolean ready = status == DemoReadinessStatus.READY;
        return new DemoSelfHostedLaunchReadinessVo(
                status,
                ready,
                ready
                        ? "Self-hosted PatchPilot is ready for a controlled issue-to-PR launch."
                        : "Self-hosted PatchPilot needs attention before launch.",
                List.of(
                        new DemoSelfHostedLaunchCheckVo(
                                "Demo readiness",
                                status,
                                ready ? "PatchPilot is ready for a controlled demo." : "PatchPilot needs attention before a live demo.",
                                ready ? "No action needed." : "Resolve launch package warnings, then rerun this readiness package."
                        ),
                        new DemoSelfHostedLaunchCheckVo(
                                "Evidence bundle",
                                status,
                                ready ? "Demo evidence bundle is ready." : "Demo evidence bundle needs attention.",
                                ready ? "No action needed." : "Refresh demo evidence bundle after fixing blockers."
                        )
                ),
                ready
                        ? List.of("Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review.")
                        : List.of("Resolve launch package warnings, then rerun this readiness package."),
                Instant.parse("2026-06-27T01:00:00Z"),
                "# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `" + status + "`\n"
        );
    }

    static DemoSessionSnapshotVo sessionSnapshot(DemoReadinessStatus status) {
        DemoEvidenceBundleVo bundle = evidenceBundle(status);
        return new DemoSessionSnapshotVo(
                "demo-session-20260624T003000Z",
                status,
                status == DemoReadinessStatus.READY
                        ? "Demo session snapshot is ready."
                        : "Demo session snapshot needs attention before use.",
                Instant.parse("2026-06-24T00:30:00Z"),
                bundle,
                script(status),
                "# PatchPilot Demo Runbook\n\n- Status: `" + status + "`\n- Recent Pull Request: https://github.com/bingqin2/PatchPilot/pull/42\n",
                readinessTrend(),
                List.of(
                        "Open the dashboard and confirm the demo session snapshot status.",
                        "Verify the latest webhook delivery and recent task before posting a live trigger."
                ),
                List.of(
                        "GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.",
                        "Live execution still starts from a controlled GitHub issue comment or manual task creation."
                ),
                "Status " + status + "; recent task task-1; latest delivery delivery-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                status == DemoReadinessStatus.READY
                        ? List.of("Follow the script from step 1 through Pull Request review.")
                        : List.of("Fix demo evidence bundle before launch.")
        );
    }

    static DemoEvidenceBundleVo evidenceBundle(DemoReadinessStatus status) {
        boolean ready = status == DemoReadinessStatus.READY;
        return new DemoEvidenceBundleVo(
                status,
                ready ? "Demo evidence bundle is ready." : "Demo evidence bundle needs attention.",
                new DemoEvidenceBundleSummaryVo(12, ready ? 0 : 1, 2, 0, true),
                readiness(status),
                smokeChecklist(status),
                null,
                new DemoAdapterFixtureEvidenceVo(12, ready ? 0 : 1),
                evaluationReadiness(status),
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
                List.of("Download handoff package archive handoff-archive-1."),
                true,
                "delivery-receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-27T01:00:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                ready ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION,
                ready,
                ready
                        ? "Demo handoff is finalized with a fresh delivery receipt for the current archive."
                        : "Demo handoff package is send-ready but final delivery evidence is not current.",
                ready
                        ? "Use the finalization report as the post-demo delivery acceptance record."
                        : "Send the current handoff package, record a delivery receipt, then download the finalization report.",
                ready ? "FRESH" : "MISSING",
                ready,
                ready ? "delivery-receipt-1" : null,
                Instant.parse("2026-06-24T00:00:00Z"),
                ready
                        ? List.of("Use this evidence bundle as the live demo baseline.")
                        : List.of("Fix demo evidence bundle before launch.")
        );
    }

    static DemoLaunchEvidencePackageVo launchEvidencePackage(DemoReadinessStatus status) {
        boolean ready = status == DemoReadinessStatus.READY;
        return new DemoLaunchEvidencePackageVo(
                status,
                ready,
                ready
                        ? "PatchPilot launch evidence package is ready to share."
                        : "PatchPilot launch evidence package needs attention before sharing.",
                "demo-session-20260624T003000Z",
                status,
                status,
                status,
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                List.of("java", "python", "maven", "pytest"),
                launchReadiness(status).checks(),
                List.of(
                        "Recent task task-1 reached COMPLETED.",
                        "Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.",
                        "Latest webhook delivery delivery-1 created task task-1."
                ),
                List.of(
                        "Handoff finalization is " + status + ".",
                        ready ? "Latest delivery receipt delivery-receipt-1 is fresh." : "Delivery receipt evidence is not current."
                ),
                ready
                        ? List.of("Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review.")
                        : List.of("Resolve launch evidence warnings, then regenerate this package."),
                List.of(
                        "GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.",
                        "GET /api/demo/launch-evidence-package/report/download only formats the same current package as Markdown."
                ),
                "# PatchPilot Demo Launch Evidence Package\n\n"
                        + "- Status: `" + status + "`\n"
                        + "- Ready to share: `" + ready + "`\n\n"
                        + "## Side Effect Contract\n\n"
                        + "- Read-only package evidence.\n",
                Instant.parse("2026-06-28T02:00:00Z")
        );
    }

    static DemoLaunchEvidencePackageArchiveVo launchEvidencePackageArchive(DemoReadinessStatus status) {
        boolean ready = status == DemoReadinessStatus.READY;
        return new DemoLaunchEvidencePackageArchiveVo(
                "launch-evidence-archive-1",
                status,
                ready,
                ready
                        ? "PatchPilot launch evidence package is ready to share."
                        : "PatchPilot launch evidence package needs attention before sharing.",
                "demo-session-20260624T003000Z",
                status,
                status,
                status,
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                Instant.parse("2026-06-28T02:30:00Z"),
                "# PatchPilot Demo Launch Evidence Package\n\n- Status: `" + status + "`"
        );
    }

    private static DemoEvaluationRunReadinessEvidenceVo evaluationReadiness(DemoReadinessStatus status) {
        return new DemoEvaluationRunReadinessEvidenceVo(
                status,
                "evaluation-run-2",
                "evaluation-run-1",
                1,
                0,
                0,
                List.of("java", "python"),
                List.of("maven", "pytest"),
                List.of("DANGEROUS_REQUEST", "SECRET_EXFILTRATION"),
                "Full evaluation run readiness is read-only: it does not create tasks, call the model, mutate Git, or write to GitHub.",
                status == DemoReadinessStatus.READY
                        ? "Full evaluation run archive is ready; use it as current demo evidence."
                        : "Run and archive a full evaluation before using it as demo readiness evidence."
        );
    }

    private static DemoReadinessVo readiness(DemoReadinessStatus status) {
        return new DemoReadinessVo(
                status,
                status == DemoReadinessStatus.READY
                        ? "PatchPilot is ready for a controlled demo."
                        : "PatchPilot needs attention before a live demo.",
                List.of(new DemoReadinessCheckVo("Backend", DemoReadinessStatus.READY, "Backend is reachable.", "No action needed.")),
                status == DemoReadinessStatus.READY ? List.of() : List.of("Fix demo readiness before launch.")
        );
    }

    private static DemoSmokeChecklistVo smokeChecklist(DemoReadinessStatus status) {
        DemoSmokeChecklistStatus smokeStatus = status == DemoReadinessStatus.READY
                ? DemoSmokeChecklistStatus.READY
                : DemoSmokeChecklistStatus.NEEDS_ATTENTION;
        return new DemoSmokeChecklistVo(
                smokeStatus,
                smokeStatus == DemoSmokeChecklistStatus.READY
                        ? "Live demo smoke checklist is ready."
                        : "Live demo smoke checklist needs attention.",
                List.of(new DemoSmokeChecklistStepVo(
                        1,
                        "Pull Request evidence",
                        smokeStatus,
                        "Pull Request evidence is available.",
                        "https://github.com/bingqin2/PatchPilot/pull/42",
                        "Fix PR creation before launch."
                )),
                smokeStatus == DemoSmokeChecklistStatus.READY ? List.of() : List.of("Fix PR evidence before launch.")
        );
    }

    private static DemoScriptVo script(DemoReadinessStatus status) {
        return new DemoScriptVo(
                status,
                status == DemoReadinessStatus.READY ? "Demo script is ready." : "Demo script needs attention.",
                List.of(new DemoScriptStepVo(
                        1,
                        "Confirm backend and dashboard access",
                        status,
                        "Open the dashboard and confirm protected APIs load.",
                        "curl http://127.0.0.1:8080/health",
                        "Backend reports UP and dashboard data loads.",
                        "Connectivity panel",
                        "Backend readiness endpoint is reachable."
                )),
                List.of("GET /api/demo/script is read-only."),
                List.of("Follow the script from step 1 through Pull Request review."),
                Instant.parse("2026-06-24T00:30:00Z")
        );
    }

    private static DemoReadinessSnapshotTrendVo readinessTrend() {
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

    private static FixTaskVo task() {
        return new FixTaskVo(
                "task-1",
                "bingqin2",
                "PatchPilot",
                1,
                0,
                "bingqin2",
                "/agent fix replace docs/demo.md PatchPilot smoke test",
                "delivery-1",
                123L,
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
                "delivery-diagnostic-1",
                "delivery-1",
                "issue_comment",
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                "task-1",
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md PatchPilot smoke test",
                "Task created from issue comment.",
                Instant.parse("2026-06-24T00:00:00Z")
        );
    }
}
