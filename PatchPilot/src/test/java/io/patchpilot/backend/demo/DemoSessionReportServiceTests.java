package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoSessionReportServiceTests {

    @Test
    void should_format_demo_session_snapshot_as_markdown_report() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshot());

        String report = service.getSessionReport();

        assertThat(report)
                .contains("# PatchPilot Demo Session Report")
                .contains("- Session: `demo-session-20260624T003000Z`")
                .contains("- Status: `READY`")
                .contains("- Summary: Demo session snapshot is ready.")
                .contains("- Share summary: Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.")
                .contains("- Recent Pull Request: https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("- Recent task: `task-1` (`COMPLETED`)")
                .contains("## Webhook Setup Readiness")
                .contains("- Status: `READY`")
                .contains("- Secret configured: `true`")
                .contains("- Public URL ready: `true`")
                .contains("- Payload URL: https://demo.trycloudflare.com/api/github/webhook")
                .contains("- Latest delivery: `TASK_CREATED` (`delivery-1`)")
                .contains("- Next action: Use the payload URL in GitHub Webhooks and continue the live demo.")
                .contains("## Readiness Snapshot Trend")
                .contains("- Trend: `IMPROVING`")
                .contains("- Latest snapshot: `readiness-snapshot-new`")
                .contains("- Previous snapshot: `readiness-snapshot-old`")
                .contains("- Delta: `+4 ready / -2 warning / -2 blocked`")
                .contains("- Next action: Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.")
                .contains("## Operator Checklist")
                .contains("- Open the dashboard and confirm the demo session snapshot status.")
                .contains("## Script Steps")
                .contains("- 1. `Confirm backend and dashboard access`: `READY`")
                .contains("  - Verify: `curl http://127.0.0.1:8080/health`")
                .contains("## Health Contract")
                .contains("- GET /api/demo/session-report is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.")
                .contains("## Next Actions")
                .contains("- Follow the script from step 1 through Pull Request review.")
                .contains("## Runbook")
                .contains("# PatchPilot Demo Runbook");
    }

    @Test
    void should_include_prepared_launch_commands_when_report_context_provides_them() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshot());
        DemoSessionReportRequestDto request = new DemoSessionReportRequestDto(List.of(
                new DemoPreparedLaunchCommandRequestDto(
                        "/agent fix replace docs/demo.md PatchPilot smoke test",
                        "bingqin2",
                        "PatchPilot",
                        1L,
                        "bingqin2",
                        "replace",
                        "docs/demo.md",
                        "PatchPilot smoke test",
                        "2026-06-26T01:00:00Z"
                ),
                new DemoPreparedLaunchCommandRequestDto(
                        "/agent fix touch docs/history.md",
                        "bingqin2",
                        "PatchPilot",
                        2L,
                        "bingqin2",
                        "touch",
                        "docs/history.md",
                        null,
                        "2026-06-26T01:05:00Z"
                )
        ), List.of());

        String report = service.getSessionReport(request);

        assertThat(report)
                .contains("## Prepared Launch Commands")
                .contains("- `/agent fix replace docs/demo.md PatchPilot smoke test`")
                .contains("  - Target: `bingqin2/PatchPilot#1`")
                .contains("  - Operation: `replace` on `docs/demo.md`")
                .contains("  - Replacement: `PatchPilot smoke test`")
                .contains("  - Saved at: `2026-06-26T01:00:00Z`")
                .contains("- `/agent fix touch docs/history.md`")
                .contains("  - Target: `bingqin2/PatchPilot#2`")
                .contains("  - Operation: `touch` on `docs/history.md`")
                .contains("  - Saved at: `2026-06-26T01:05:00Z`");
    }

    @Test
    void should_include_archived_launch_outcomes_when_report_context_provides_them() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshot());
        DemoSessionReportRequestDto request = new DemoSessionReportRequestDto(List.of(), List.of(
                new DemoArchivedLaunchOutcomeRequestDto(
                        "/agent fix replace docs/demo.md PatchPilot smoke test",
                        "bingqin2",
                        "PatchPilot",
                        1L,
                        "bingqin2",
                        "task-1",
                        "COMPLETED",
                        "https://github.com/bingqin2/PatchPilot/pull/42",
                        "2026-06-26T01:10:00Z",
                        "# PatchPilot Demo Launch Outcome Report\n\n- Task: `task-1`\n- Pull Request: https://github.com/bingqin2/PatchPilot/pull/42"
                ),
                new DemoArchivedLaunchOutcomeRequestDto(
                        "/agent fix touch docs/history.md",
                        "bingqin2",
                        "PatchPilot",
                        2L,
                        "bingqin2",
                        null,
                        "PENDING",
                        null,
                        "2026-06-26T01:12:00Z",
                        ""
                )
        ));

        String report = service.getSessionReport(request);

        assertThat(report)
                .contains("## Archived Launch Outcomes")
                .contains("- `/agent fix replace docs/demo.md PatchPilot smoke test`")
                .contains("  - Target: `bingqin2/PatchPilot#1`")
                .contains("  - Task: `task-1` (`COMPLETED`)")
                .contains("  - Pull Request: https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("  - Archived at: `2026-06-26T01:10:00Z`")
                .contains("  - Report: `# PatchPilot Demo Launch Outcome Report - Task: 'task-1' - Pull Request: https://github.com/bingqin2/PatchPilot/pull/42`")
                .contains("- `/agent fix touch docs/history.md`")
                .contains("  - Target: `bingqin2/PatchPilot#2`")
                .contains("  - Task: `none` (`PENDING`)")
                .contains("  - Pull Request: none")
                .contains("  - Archived at: `2026-06-26T01:12:00Z`");
    }

    @Test
    void should_format_demo_handoff_package_with_session_context_and_outcome_evidence() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshot());
        DemoSessionReportRequestDto request = new DemoSessionReportRequestDto(List.of(
                new DemoPreparedLaunchCommandRequestDto(
                        "/agent fix replace docs/demo.md PatchPilot smoke test",
                        "bingqin2",
                        "PatchPilot",
                        1L,
                        "bingqin2",
                        "replace",
                        "docs/demo.md",
                        "PatchPilot smoke test",
                        "2026-06-26T01:00:00Z"
                )
        ), List.of(
                new DemoArchivedLaunchOutcomeRequestDto(
                        "/agent fix replace docs/demo.md PatchPilot smoke test",
                        "bingqin2",
                        "PatchPilot",
                        1L,
                        "bingqin2",
                        "task-1",
                        "COMPLETED",
                        "https://github.com/bingqin2/PatchPilot/pull/42",
                        "2026-06-26T01:10:00Z",
                        "# PatchPilot Demo Launch Outcome Report\n\n- Task: `task-1`"
                )
        ));

        String packageReport = service.getHandoffPackage(request);

        assertThat(packageReport)
                .contains("# PatchPilot Demo Handoff Package")
                .contains("- Session: `demo-session-20260624T003000Z`")
                .contains("- Demo status: `READY`")
                .contains("- Recent task: `task-1` (`COMPLETED`)")
                .contains("- Recent Pull Request: https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("- Prepared commands: `1`")
                .contains("- Archived launch outcomes: `1`")
                .contains("## Handoff Summary")
                .contains("- Demo evidence bundle is ready.")
                .contains("- Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.")
                .contains("- Readiness trend: `IMPROVING` - Demo readiness improved from BLOCKED to READY.")
                .contains("## Handoff Readiness")
                .contains("- Overall: `READY` - Handoff package has current PR, command, outcome, and readiness trend evidence.")
                .contains("- Demo snapshot status: `READY` - Demo session snapshot is ready.")
                .contains("- Recent task evidence: `READY` - task-1 is completed.")
                .contains("- Recent Pull Request evidence: `READY` - https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("- Prepared command context: `READY` - 1 prepared command recorded.")
                .contains("- Archived launch outcome context: `READY` - 1 archived outcome has completed task or Pull Request evidence.")
                .contains("- Readiness trend baseline: `READY` - IMPROVING; latest readiness READY.")
                .contains("## Readiness Snapshot Trend")
                .contains("- Delta: `+4 ready / -2 warning / -2 blocked`")
                .contains("## Prepared Launch Commands")
                .contains("- `/agent fix replace docs/demo.md PatchPilot smoke test`")
                .contains("## Archived Launch Outcomes")
                .contains("- `task-1` (`COMPLETED`) -> https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("## Embedded Session Report")
                .contains("# PatchPilot Demo Session Report")
                .contains("GET /api/demo/handoff-package is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.");
    }

    @Test
    void should_mark_handoff_readiness_needs_attention_when_required_context_is_missing() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshotWithoutTaskOrPullRequest());

        String packageReport = service.getHandoffPackage(new DemoSessionReportRequestDto(List.of(), List.of()));

        assertThat(packageReport)
                .contains("## Handoff Readiness")
                .contains("- Overall: `NEEDS_ATTENTION` - Handoff package is missing evidence required for a credible live-demo handoff.")
                .contains("- Recent task evidence: `NEEDS_ATTENTION` - No recent completed task is available in the session snapshot.")
                .contains("- Recent Pull Request evidence: `NEEDS_ATTENTION` - No recent Pull Request URL is available.")
                .contains("- Prepared command context: `NEEDS_ATTENTION` - No prepared launch command was captured in this browser session.")
                .contains("- Archived launch outcome context: `NEEDS_ATTENTION` - No archived launch outcome with completed task or Pull Request evidence was captured.")
                .contains("- Readiness trend baseline: `READY` - IMPROVING; latest readiness READY.");
    }

    @Test
    void should_render_empty_lists_and_missing_evidence_as_none() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshotWithoutTaskOrPullRequest());

        String report = service.getSessionReport();

        assertThat(report)
                .contains("- Recent Pull Request: none")
                .contains("- Recent task: none")
                .contains("- No operator checklist items recorded.")
                .contains("- No script steps recorded.")
                .contains("- GET /api/demo/session-report is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.")
                .contains("- No next actions recorded.");
    }

    private static DemoSessionSnapshotVo snapshotWithoutTaskOrPullRequest() {
        return new DemoSessionSnapshotVo(
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                Instant.parse("2026-06-24T00:30:00Z"),
                new DemoEvidenceBundleVo(
                        DemoReadinessStatus.READY,
                        "Demo evidence bundle is ready.",
                        new DemoEvidenceBundleSummaryVo(1, 0, 0, 0, false),
                        new DemoReadinessVo(DemoReadinessStatus.READY, "Ready.", List.of(), List.of()),
                        new DemoSmokeChecklistVo(DemoSmokeChecklistStatus.READY, "Ready.", List.of(), List.of()),
                        null,
                        new DemoAdapterFixtureEvidenceVo(1, 0),
                        FixTaskQueueSummaryVo.empty(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        Instant.parse("2026-06-24T00:00:00Z"),
                        List.of()
                ),
                new DemoScriptVo(DemoReadinessStatus.READY, "Demo script is ready.", List.of(), List.of(), List.of(), Instant.parse("2026-06-24T00:30:00Z")),
                "# PatchPilot Demo Runbook\n\n- Status: `READY`",
                trend(),
                List.of(),
                List.of(),
                "Status READY; recent task none; recent PR none.",
                List.of()
        );
    }

    private static DemoSessionSnapshotVo snapshot() {
        return new DemoSessionSnapshotVo(
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                Instant.parse("2026-06-24T00:30:00Z"),
                evidenceBundle(),
                script(),
                "# PatchPilot Demo Runbook\n\n- Status: `READY`",
                trend(),
                List.of("Open the dashboard and confirm the demo session snapshot status."),
                List.of("GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."),
                "Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                List.of("Follow the script from step 1 through Pull Request review.")
        );
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

    private static DemoEvidenceBundleVo evidenceBundle() {
        return new DemoEvidenceBundleVo(
                DemoReadinessStatus.READY,
                "Demo evidence bundle is ready.",
                new DemoEvidenceBundleSummaryVo(12, 0, 2, 0, true),
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(new DemoReadinessCheckVo("Backend", DemoReadinessStatus.READY, "Backend readiness endpoint is reachable.", "No action needed.")),
                        List.of()
                ),
                new DemoSmokeChecklistVo(DemoSmokeChecklistStatus.READY, "Live demo smoke checklist is ready.", List.of(), List.of()),
                null,
                new DemoAdapterFixtureEvidenceVo(12, 0),
                new FixTaskQueueSummaryVo(2, 0, 0, 0, 0, 2, 0, 0),
                task(),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                webhookSetupReadiness(),
                null,
                null,
                0,
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of()
        );
    }

    private static GitHubWebhookSetupReadinessVo webhookSetupReadiness() {
        return new GitHubWebhookSetupReadinessVo(
                "READY",
                true,
                true,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "https://demo.trycloudflare.com/health",
                "TASK_CREATED",
                "delivery-1",
                false,
                "Webhook setup is ready for GitHub deliveries.",
                List.of("Use the payload URL in GitHub Webhooks and continue the live demo."),
                Instant.parse("2026-06-27T01:00:00Z"),
                "# PatchPilot Webhook Setup Readiness"
        );
    }

    private static DemoScriptVo script() {
        return new DemoScriptVo(
                DemoReadinessStatus.READY,
                "Demo script is ready.",
                List.of(new DemoScriptStepVo(
                        1,
                        "Confirm backend and dashboard access",
                        DemoReadinessStatus.READY,
                        "Open the dashboard and confirm protected APIs load.",
                        "curl http://127.0.0.1:8080/health",
                        "Backend reports UP and dashboard data loads.",
                        "Connectivity panel",
                        "Backend readiness endpoint is reachable."
                )),
                List.of("The script endpoint is read-only."),
                List.of("Follow the script from step 1 through Pull Request review."),
                Instant.parse("2026-06-24T00:30:00Z")
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
}
