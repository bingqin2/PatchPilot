package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
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
    void should_render_empty_lists_and_missing_evidence_as_none() {
        DemoSessionReportService service = new DemoSessionReportService(() -> new DemoSessionSnapshotVo(
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
                        0,
                        Instant.parse("2026-06-24T00:00:00Z"),
                        List.of()
                ),
                new DemoScriptVo(DemoReadinessStatus.READY, "Demo script is ready.", List.of(), List.of(), List.of(), Instant.parse("2026-06-24T00:30:00Z")),
                "# PatchPilot Demo Runbook\n\n- Status: `READY`",
                List.of(),
                List.of(),
                "Status READY; recent task none; recent PR none.",
                List.of()
        ));

        String report = service.getSessionReport();

        assertThat(report)
                .contains("- Recent Pull Request: none")
                .contains("- Recent task: none")
                .contains("- No operator checklist items recorded.")
                .contains("- No script steps recorded.")
                .contains("- GET /api/demo/session-report is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.")
                .contains("- No next actions recorded.");
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
                List.of("Open the dashboard and confirm the demo session snapshot status."),
                List.of("GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."),
                "Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                List.of("Follow the script from step 1 through Pull Request review.")
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
                null,
                null,
                0,
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of()
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
