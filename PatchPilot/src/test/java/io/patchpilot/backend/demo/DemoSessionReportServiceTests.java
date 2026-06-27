package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessVo;
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
import static org.assertj.core.groups.Tuple.tuple;

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
                .contains("## Recent Webhook Deliveries")
                .contains("- `delivery-1`: `TASK_CREATED` -> task-1")
                .contains("  - Repository: `bingqin2/PatchPilot#1`")
                .contains("  - Trigger: `/agent fix replace docs/demo.md demo`")
                .contains("- `delivery-2`: `REJECTED` -> rejected-1")
                .contains("  - Outcome: `REJECTED_TRIGGER`")
                .contains("  - Message: Webhook trigger was rejected before task creation.")
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
        DemoSessionReportRequestDto request = readyHandoffRequest();

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
                .contains("- Overall: `READY` - Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.")
                .contains("  - Next action: No missing handoff evidence.")
                .contains("- Demo snapshot status: `READY` - Demo session snapshot is ready.")
                .contains("  - Next action: No action needed.")
                .contains("- Recent task evidence: `READY` - task-1 is completed.")
                .contains("- Webhook delivery evidence: `READY` - delivery-1 created task task-1.")
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
    void should_return_structured_handoff_readiness_with_browser_context() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshot());

        DemoHandoffReadinessVo readiness = service.getHandoffReadiness(readyHandoffRequest());

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(readiness.summary())
                .isEqualTo("Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.");
        assertThat(readiness.nextAction()).isEqualTo("No missing handoff evidence.");
        assertThat(readiness.checks())
                .extracting(
                        DemoHandoffReadinessCheckVo::name,
                        DemoHandoffReadinessCheckVo::status,
                        DemoHandoffReadinessCheckVo::summary,
                        DemoHandoffReadinessCheckVo::nextAction
                )
                .contains(
                        tuple("Demo snapshot status", DemoReadinessStatus.READY, "Demo session snapshot is ready.", "No action needed."),
                        tuple("Recent task evidence", DemoReadinessStatus.READY, "task-1 is completed.", "No action needed."),
                        tuple("Webhook delivery evidence", DemoReadinessStatus.READY, "delivery-1 created task task-1.", "No action needed."),
                        tuple("Recent Pull Request evidence", DemoReadinessStatus.READY, "https://github.com/bingqin2/PatchPilot/pull/42", "No action needed."),
                        tuple("Prepared command context", DemoReadinessStatus.READY, "1 prepared command recorded.", "No action needed."),
                        tuple("Archived launch outcome context", DemoReadinessStatus.READY, "1 archived outcome has completed task or Pull Request evidence.", "No action needed."),
                        tuple("Readiness trend baseline", DemoReadinessStatus.READY, "IMPROVING; latest readiness READY.", "No action needed.")
                );
    }

    @Test
    void should_return_structured_handoff_readiness_blocked_by_redelivery_required_webhook() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshotWithWebhookDeliveries(List.of(
                webhookDeliveryRequiringRedelivery(),
                webhookDelivery("older-delivery-1", "TASK_CREATED", "task-1")
        )));

        DemoHandoffReadinessVo readiness = service.getHandoffReadiness(readyHandoffRequest());

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(readiness.summary())
                .isEqualTo("Handoff package has a blocking readiness signal that should be resolved before a live-demo handoff.");
        assertThat(readiness.nextAction()).isEqualTo("Resolve blocked handoff readiness checks before sharing the package.");
        assertThat(readiness.checks())
                .extracting(
                        DemoHandoffReadinessCheckVo::name,
                        DemoHandoffReadinessCheckVo::status,
                        DemoHandoffReadinessCheckVo::summary,
                        DemoHandoffReadinessCheckVo::nextAction
                )
                .contains(tuple(
                        "Webhook delivery evidence",
                        DemoReadinessStatus.BLOCKED,
                        "delivery-invalid is INVALID_SIGNATURE; Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery.",
                        "Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery."
                ));
    }

    @Test
    void should_mark_handoff_readiness_needs_attention_when_required_context_is_missing() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshotWithoutTaskOrPullRequest());

        String packageReport = service.getHandoffPackage(new DemoSessionReportRequestDto(List.of(), List.of()));

        assertThat(packageReport)
                .contains("## Handoff Readiness")
                .contains("- Overall: `NEEDS_ATTENTION` - Handoff package is missing evidence required for a credible live-demo handoff.")
                .contains("- Recent task evidence: `NEEDS_ATTENTION` - No recent completed task is available in the session snapshot.")
                .contains("- Webhook delivery evidence: `NEEDS_ATTENTION` - No recent webhook delivery evidence is available in the session snapshot.")
                .contains("- Recent Pull Request evidence: `NEEDS_ATTENTION` - No recent Pull Request URL is available.")
                .contains("- Prepared command context: `NEEDS_ATTENTION` - No prepared launch command was captured in this browser session.")
                .contains("  - Next action: Use the dashboard launch command composer before handoff.")
                .contains("- Archived launch outcome context: `NEEDS_ATTENTION` - No archived launch outcome with completed task or Pull Request evidence was captured.")
                .contains("  - Next action: Archive the launch outcome after the task completes or a Pull Request appears.")
                .contains("- Readiness trend baseline: `READY` - IMPROVING; latest readiness READY.");
    }

    @Test
    void should_block_handoff_readiness_when_latest_webhook_delivery_needs_redelivery() {
        DemoSessionReportService service = new DemoSessionReportService(() -> snapshotWithWebhookDeliveries(List.of(
                webhookDeliveryRequiringRedelivery(),
                webhookDelivery("older-delivery-1", "TASK_CREATED", "task-1")
        )));
        DemoSessionReportRequestDto request = readyHandoffRequest();

        String packageReport = service.getHandoffPackage(request);

        assertThat(packageReport)
                .contains("- Overall: `BLOCKED` - Handoff package has a blocking readiness signal that should be resolved before a live-demo handoff.")
                .contains("- Webhook delivery evidence: `BLOCKED` - delivery-invalid is INVALID_SIGNATURE; Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery.")
                .contains("  - Next action: Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery.");
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

    static DemoSessionReportRequestDto readyHandoffRequest() {
        return new DemoSessionReportRequestDto(List.of(
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
                        List.of(),
                        null,
                        0,
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        "No handoff package archive is available for sharing.",
                        "Archive a demo handoff package after a completed live run before sharing handoff evidence.",
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

    static DemoSessionSnapshotVo snapshot() {
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

    private static DemoSessionSnapshotVo snapshotWithWebhookDeliveries(
            List<io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo> deliveries
    ) {
        return new DemoSessionSnapshotVo(
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                Instant.parse("2026-06-24T00:30:00Z"),
                evidenceBundleWithWebhookDeliveries(deliveries),
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
        return evidenceBundleWithWebhookDeliveries(List.of(
                webhookDelivery("delivery-1", "TASK_CREATED", "task-1"),
                webhookDelivery("delivery-2", "REJECTED", "rejected-1")
        ));
    }

    private static DemoEvidenceBundleVo evidenceBundleWithWebhookDeliveries(
            List<io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo> deliveries
    ) {
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
                deliveries.isEmpty() ? null : deliveries.get(0),
                deliveries,
                null,
                0,
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
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

    private static io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo webhookDelivery(
            String deliveryId,
            String status,
            String outcomeId
    ) {
        return new io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo(
                "record-" + deliveryId,
                deliveryId,
                "issue_comment.created",
                io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus.valueOf(status),
                "TASK_CREATED".equals(status) ? outcomeId : null,
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "TASK_CREATED".equals(status)
                        ? "Webhook created a task."
                        : "Webhook trigger was rejected before task creation.",
                false,
                "TASK_CREATED".equals(status) ? "Task was created." : "Rejected trigger was recorded.",
                "TASK_CREATED".equals(status)
                        ? io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType.TASK
                        : io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType.REJECTED_TRIGGER,
                outcomeId,
                "TASK_CREATED".equals(status) ? "/tasks/" + outcomeId : "/rejected-triggers/" + outcomeId,
                Instant.parse("2026-06-24T00:00:00Z")
        );
    }

    private static io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo webhookDeliveryRequiringRedelivery() {
        return new io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo(
                "record-delivery-invalid",
                "delivery-invalid",
                "issue_comment.created",
                io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus.INVALID_SIGNATURE,
                null,
                "bingqin2",
                "PatchPilot",
                1L,
                "bingqin2",
                "/agent fix replace docs/demo.md demo",
                "Webhook signature verification failed.",
                true,
                "Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery.",
                io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType.ERROR,
                "error-delivery-invalid",
                "/webhook-deliveries/delivery-invalid",
                Instant.parse("2026-06-24T00:00:00Z")
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
