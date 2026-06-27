package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.demo.domain.DemoLaunchCommandVo;
import io.patchpilot.backend.demo.domain.DemoLaunchPreflightVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoHandoffReadinessVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemoReadinessController.class)
class DemoReadinessControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemoReadinessService demoReadinessService;

    @MockitoBean
    private DemoSmokeChecklistService demoSmokeChecklistService;

    @MockitoBean
    private DemoEvidenceBundleService demoEvidenceBundleService;

    @MockitoBean
    private DemoRunbookService demoRunbookService;

    @MockitoBean
    private DemoScriptService demoScriptService;

    @MockitoBean
    private DemoSessionSnapshotService demoSessionSnapshotService;

    @MockitoBean
    private DemoSessionReportService demoSessionReportService;

    @MockitoBean
    private DemoSessionArchiveService demoSessionArchiveService;

    @MockitoBean
    private DemoHandoffPackageArchiveService demoHandoffPackageArchiveService;

    @MockitoBean
    private DemoReadinessSnapshotArchiveService demoReadinessSnapshotArchiveService;

    @MockitoBean
    private DemoReadinessSnapshotTrendService demoReadinessSnapshotTrendService;

    @MockitoBean
    private DemoLaunchPreflightService demoLaunchPreflightService;

    @MockitoBean
    private DemoLaunchCommandService demoLaunchCommandService;

    @MockitoBean
    private OperatorSafetyAuditService operatorSafetyAuditService;

    @Test
    void should_return_demo_readiness_summary() throws Exception {
        when(demoReadinessService.getReadiness()).thenReturn(new DemoReadinessVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                "PatchPilot needs attention before a live demo.",
                List.of(
                        new DemoReadinessCheckVo(
                                "Credentials",
                                DemoReadinessStatus.READY,
                                "Required credentials are configured.",
                                "No action needed."
                        ),
                        new DemoReadinessCheckVo(
                                "Recent Pull Request",
                                DemoReadinessStatus.NEEDS_ATTENTION,
                                "No completed task with a Pull Request URL was found.",
                                "Run one controlled issue-to-PR smoke task before a live demo."
                        ),
                        new DemoReadinessCheckVo(
                                "Evaluation baseline",
                                DemoReadinessStatus.BLOCKED,
                                "Latest fixture baseline regressed. Newly failed cases: node-npm-basic-fix.",
                                "Investigate newly failed fixture cases before using the baseline as demo evidence."
                        )
                ),
                List.of(
                        "Run one controlled issue-to-PR smoke task before a live demo.",
                        "Investigate newly failed fixture cases before using the baseline as demo evidence."
                )
        ));

        mockMvc.perform(get("/api/demo/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("PatchPilot needs attention before a live demo."))
                .andExpect(jsonPath("$.data.checks.length()").value(3))
                .andExpect(jsonPath("$.data.checks[0].name").value("Credentials"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[1].name").value("Recent Pull Request"))
                .andExpect(jsonPath("$.data.checks[2].name").value("Evaluation baseline"))
                .andExpect(jsonPath("$.data.checks[2].status").value("BLOCKED"))
                .andExpect(jsonPath("$.data.checks[2].message").value("Latest fixture baseline regressed. Newly failed cases: node-npm-basic-fix."))
                .andExpect(jsonPath("$.data.checks[2].action").value("Investigate newly failed fixture cases before using the baseline as demo evidence."))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run one controlled issue-to-PR smoke task before a live demo."))
                .andExpect(jsonPath("$.data.nextActions[1]").value("Investigate newly failed fixture cases before using the baseline as demo evidence."));
    }

    @Test
    void should_return_demo_smoke_checklist() throws Exception {
        when(demoSmokeChecklistService.getSmokeChecklist()).thenReturn(new DemoSmokeChecklistVo(
                DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                "Live demo smoke checklist needs attention.",
                List.of(new DemoSmokeChecklistStepVo(
                        2,
                        "Webhook delivery",
                        DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                        "Latest delivery needs redelivery.",
                        "delivery-invalid",
                        "Fix the webhook secret or URL, then use GitHub Redeliver before the live demo."
                )),
                List.of("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.")
        ));

        mockMvc.perform(get("/api/demo/smoke-checklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("Live demo smoke checklist needs attention."))
                .andExpect(jsonPath("$.data.steps[0].order").value(2))
                .andExpect(jsonPath("$.data.steps[0].name").value("Webhook delivery"))
                .andExpect(jsonPath("$.data.steps[0].status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.steps[0].evidence").value("delivery-invalid"))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Fix the webhook secret or URL, then use GitHub Redeliver before the live demo."));
    }

    @Test
    void should_return_demo_launch_preflight() throws Exception {
        when(demoLaunchPreflightService.preflight(argThat(request ->
                request.repositoryOwner().equals("bingqin2")
                        && request.repositoryName().equals("PatchPilot")
                        && request.issueNumber().equals(12L)
                        && request.triggerUser().equals("bingqin2")
                        && request.triggerComment().equals("/agent fix update docs/demo.md")
        ))).thenReturn(new DemoLaunchPreflightVo(
                DemoReadinessStatus.READY,
                true,
                "Demo launch preflight is ready to post the tested /agent fix comment.",
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(),
                        List.of()
                ),
                new TriggerEvaluationResultVo(
                        "WOULD_CREATE_TASK",
                        "ISSUE_COMMENT",
                        true,
                        null,
                        null,
                        new TriggerEvaluationDecisionVo(true, "Accepted", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "No active task exists for this issue", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "not blocked before task creation", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "not rate limited before task creation", "UNKNOWN"),
                        new TriggerEvaluationDecisionVo(true, "model accepted trigger: concrete request", "UNKNOWN"),
                        true,
                        "Create task is allowed for this trigger."
                ),
                List.of("Post the tested /agent fix comment on the controlled GitHub issue.")
        ));

        mockMvc.perform(post("/api/demo/launch-preflight")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 12,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "/agent fix update docs/demo.md"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToPost").value(true))
                .andExpect(jsonPath("$.data.summary").value("Demo launch preflight is ready to post the tested /agent fix comment."))
                .andExpect(jsonPath("$.data.readiness.status").value("READY"))
                .andExpect(jsonPath("$.data.triggerEvaluation.source").value("ISSUE_COMMENT"))
                .andExpect(jsonPath("$.data.triggerEvaluation.status").value("WOULD_CREATE_TASK"))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Post the tested /agent fix comment on the controlled GitHub issue."));
    }

    @Test
    void should_return_demo_launch_command() throws Exception {
        DemoLaunchPreflightRequestDto preflightInput = new DemoLaunchPreflightRequestDto(
                "bingqin2",
                "PatchPilot",
                12L,
                "bingqin2",
                "/agent fix replace docs/demo.md PatchPilot smoke test"
        );
        when(demoLaunchCommandService.compose(argThat(request ->
                request.repositoryOwner().equals("bingqin2")
                        && request.repositoryName().equals("PatchPilot")
                        && request.issueNumber().equals(12L)
                        && request.triggerUser().equals("bingqin2")
                        && request.operation().equals("replace")
                        && request.targetPath().equals("docs/demo.md")
                        && request.replacementText().equals("PatchPilot smoke test")
        ))).thenReturn(new DemoLaunchCommandVo(
                "/agent fix replace docs/demo.md PatchPilot smoke test",
                preflightInput,
                "https://github.com/bingqin2/PatchPilot/issues/12",
                "Prepared a demo /agent fix replace command for bingqin2/PatchPilot#12.",
                List.of("Run launch preflight with the generated command before posting it on GitHub.")
        ));

        mockMvc.perform(post("/api/demo/launch-command")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 12,
                                  "triggerUser": "bingqin2",
                                  "operation": "replace",
                                  "targetPath": "docs/demo.md",
                                  "replacementText": "PatchPilot smoke test"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.triggerComment").value("/agent fix replace docs/demo.md PatchPilot smoke test"))
                .andExpect(jsonPath("$.data.preflightInput.repositoryOwner").value("bingqin2"))
                .andExpect(jsonPath("$.data.preflightInput.repositoryName").value("PatchPilot"))
                .andExpect(jsonPath("$.data.preflightInput.issueNumber").value(12))
                .andExpect(jsonPath("$.data.preflightInput.triggerUser").value("bingqin2"))
                .andExpect(jsonPath("$.data.preflightInput.triggerComment").value("/agent fix replace docs/demo.md PatchPilot smoke test"))
                .andExpect(jsonPath("$.data.githubIssueUrl").value("https://github.com/bingqin2/PatchPilot/issues/12"))
                .andExpect(jsonPath("$.data.summary").value("Prepared a demo /agent fix replace command for bingqin2/PatchPilot#12."))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run launch preflight with the generated command before posting it on GitHub."));
    }

    @Test
    void should_return_bad_request_for_invalid_demo_launch_command_request() throws Exception {
        when(demoLaunchCommandService.compose(argThat(request -> request.targetPath().equals(".git/config"))))
                .thenThrow(new IllegalArgumentException("targetPath must not target protected repository metadata"));

        mockMvc.perform(post("/api/demo/launch-command")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 12,
                                  "triggerUser": "bingqin2",
                                  "operation": "touch",
                                  "targetPath": ".git/config"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("targetPath must not target protected repository metadata"));
    }

    @Test
    void should_return_bad_request_for_invalid_demo_launch_preflight_request() throws Exception {
        when(demoLaunchPreflightService.preflight(argThat(request -> request.issueNumber().equals(0L))))
                .thenThrow(new IllegalArgumentException("issueNumber must be positive"));

        mockMvc.perform(post("/api/demo/launch-preflight")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 0,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "/agent fix update docs/demo.md"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("issueNumber must be positive"));
    }

    @Test
    void should_return_demo_evidence_bundle() throws Exception {
        when(demoEvidenceBundleService.getEvidenceBundle()).thenReturn(new DemoEvidenceBundleVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                "Demo evidence bundle needs attention.",
                new DemoEvidenceBundleSummaryVo(
                        2,
                        1,
                        1,
                        1,
                        true
                ),
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(),
                        List.of()
                ),
                new DemoSmokeChecklistVo(
                        DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                        "Live demo smoke checklist needs attention.",
                        List.of(),
                        List.of("Run one controlled issue-to-PR smoke task before a live demo.")
                ),
                null,
                new DemoAdapterFixtureEvidenceVo(2, 1),
                new FixTaskQueueSummaryVo(1, 0, 0, 0, 0, 1, 0, 0),
                null,
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                null,
                List.of(),
                null,
                1,
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of("Run one controlled issue-to-PR smoke task before a live demo.")
        ));

        mockMvc.perform(get("/api/demo/evidence-bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("Demo evidence bundle needs attention."))
                .andExpect(jsonPath("$.data.summaryCounts.adapterFixtureCount").value(2))
                .andExpect(jsonPath("$.data.summaryCounts.failedAdapterFixtureCount").value(1))
                .andExpect(jsonPath("$.data.summaryCounts.activeQuarantineCount").value(1))
                .andExpect(jsonPath("$.data.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.recentWebhookDeliveries").isArray())
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run one controlled issue-to-PR smoke task before a live demo."));
    }

    @Test
    void should_return_demo_runbook_markdown() throws Exception {
        when(demoRunbookService.getRunbook()).thenReturn("""
                # PatchPilot Demo Runbook

                - Status: `NEEDS_ATTENTION`
                - Recent Pull Request: https://github.com/bingqin2/PatchPilot/pull/42
                """);

        mockMvc.perform(get("/api/demo/runbook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Runbook")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("`NEEDS_ATTENTION`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_return_demo_script() throws Exception {
        when(demoScriptService.getScript()).thenReturn(new DemoScriptVo(
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
                Instant.parse("2026-06-24T00:00:00Z")
        ));

        mockMvc.perform(get("/api/demo/script"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.steps[0].order").value(1))
                .andExpect(jsonPath("$.data.steps[0].name").value("Confirm backend and dashboard access"))
                .andExpect(jsonPath("$.data.steps[0].verificationCommand").value("curl http://127.0.0.1:8080/health"))
                .andExpect(jsonPath("$.data.healthContract[0]").value("The script endpoint is read-only."));
    }

    @Test
    void should_return_demo_session_snapshot() throws Exception {
        DemoEvidenceBundleVo bundle = new DemoEvidenceBundleVo(
                DemoReadinessStatus.READY,
                "Demo evidence bundle is ready.",
                new DemoEvidenceBundleSummaryVo(
                        12,
                        0,
                        2,
                        0,
                        true
                ),
                new DemoReadinessVo(
                        DemoReadinessStatus.READY,
                        "PatchPilot is ready for a controlled demo.",
                        List.of(),
                        List.of()
                ),
                new DemoSmokeChecklistVo(
                        DemoSmokeChecklistStatus.READY,
                        "Live demo smoke checklist is ready.",
                        List.of(),
                        List.of()
                ),
                null,
                new DemoAdapterFixtureEvidenceVo(12, 0),
                new FixTaskQueueSummaryVo(2, 0, 0, 0, 0, 2, 0, 0),
                null,
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                null,
                List.of(),
                null,
                0,
                Instant.parse("2026-06-24T00:00:00Z"),
                List.of("Follow the script from step 1 through Pull Request review.")
        );
        DemoScriptVo script = new DemoScriptVo(
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
        DemoReadinessSnapshotTrendVo trend = new DemoReadinessSnapshotTrendVo(
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
        when(demoSessionSnapshotService.getSessionSnapshot()).thenReturn(new DemoSessionSnapshotVo(
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                Instant.parse("2026-06-24T00:30:00Z"),
                bundle,
                script,
                "# PatchPilot Demo Runbook\n\n- Status: `READY`",
                trend,
                List.of("Open the dashboard and confirm the demo session snapshot status."),
                List.of("GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."),
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                List.of("Follow the script from step 1 through Pull Request review.")
        ));

        mockMvc.perform(get("/api/demo/session-snapshot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Demo session snapshot is ready."))
                .andExpect(jsonPath("$.data.evidenceBundle.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.script.steps[0].name").value("Confirm backend and dashboard access"))
                .andExpect(jsonPath("$.data.runbook").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Runbook")))
                .andExpect(jsonPath("$.data.readinessSnapshotTrend.status").value("IMPROVING"))
                .andExpect(jsonPath("$.data.readinessSnapshotTrend.latestSnapshotId").value("readiness-snapshot-new"))
                .andExpect(jsonPath("$.data.readinessSnapshotTrend.readyCheckDelta").value(4))
                .andExpect(jsonPath("$.data.operatorChecklist[0]").value("Open the dashboard and confirm the demo session snapshot status."))
                .andExpect(jsonPath("$.data.healthContract[0]").value("GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub."))
                .andExpect(jsonPath("$.data.shareSummary").value(org.hamcrest.Matchers.containsString("READY")))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Follow the script from step 1 through Pull Request review."));
    }

    @Test
    void should_return_demo_session_report_markdown() throws Exception {
        when(demoSessionReportService.getSessionReport()).thenReturn("""
                # PatchPilot Demo Session Report

                - Session: `demo-session-20260624T003000Z`
                - Status: `READY`
                """);

        mockMvc.perform(get("/api/demo/session-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Session Report")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("demo-session-20260624T003000Z")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("`READY`")));
    }

    @Test
    void should_download_demo_session_report_as_markdown_attachment() throws Exception {
        when(demoSessionReportService.getSessionReport()).thenReturn("""
                # PatchPilot Demo Session Report

                - Session: `demo-session-20260624T003000Z`
                - Status: `READY`
                """);

        mockMvc.perform(get("/api/demo/session-report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-session-report.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Session Report")))
                .andExpect(content().string(containsString("demo-session-20260624T003000Z")));
    }

    @Test
    void should_return_demo_session_report_markdown_with_prepared_launch_commands() throws Exception {
        when(demoSessionReportService.getSessionReport(argThat(request ->
                request.preparedLaunchCommands().size() == 1
                        && request.preparedLaunchCommands().get(0).triggerComment().equals("/agent fix replace docs/demo.md PatchPilot smoke test")
        ))).thenReturn("""
                # PatchPilot Demo Session Report

                ## Prepared Launch Commands
                """);

        mockMvc.perform(post("/api/demo/session-report")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "operation": "replace",
                                      "targetPath": "docs/demo.md",
                                      "replacementText": "PatchPilot smoke test",
                                      "savedAt": "2026-06-26T01:00:00Z"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("Prepared Launch Commands")));
    }

    @Test
    void should_return_demo_session_report_markdown_with_archived_launch_outcomes() throws Exception {
        when(demoSessionReportService.getSessionReport(argThat(request ->
                request.archivedLaunchOutcomes().size() == 1
                        && request.archivedLaunchOutcomes().get(0).taskId().equals("task-1")
                        && request.archivedLaunchOutcomes().get(0).pullRequestUrl().equals("https://github.com/bingqin2/PatchPilot/pull/42")
        ))).thenReturn("""
                # PatchPilot Demo Session Report

                ## Archived Launch Outcomes
                """);

        mockMvc.perform(post("/api/demo/session-report")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [],
                                  "archivedLaunchOutcomes": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "taskId": "task-1",
                                      "taskStatus": "COMPLETED",
                                      "pullRequestUrl": "https://github.com/bingqin2/PatchPilot/pull/42",
                                      "archivedAt": "2026-06-26T01:10:00Z",
                                      "report": "# PatchPilot Demo Launch Outcome Report"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("Archived Launch Outcomes")));
    }

    @Test
    void should_download_demo_session_report_with_prepared_launch_commands() throws Exception {
        when(demoSessionReportService.getSessionReport(any(DemoSessionReportRequestDto.class))).thenReturn("""
                # PatchPilot Demo Session Report

                ## Prepared Launch Commands
                """);

        mockMvc.perform(post("/api/demo/session-report/download")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix touch docs/history.md",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 2,
                                      "triggerUser": "bingqin2",
                                      "operation": "touch",
                                      "targetPath": "docs/history.md",
                                      "replacementText": null,
                                      "savedAt": "2026-06-26T01:05:00Z"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("Prepared Launch Commands")));
    }

    @Test
    void should_return_demo_handoff_package_with_browser_context() throws Exception {
        when(demoSessionReportService.getHandoffPackage(argThat(request ->
                request.preparedLaunchCommands().size() == 1
                        && request.archivedLaunchOutcomes().size() == 1
                        && request.archivedLaunchOutcomes().get(0).taskId().equals("task-1")
        ))).thenReturn("""
                # PatchPilot Demo Handoff Package

                ## Handoff Summary
                """);

        mockMvc.perform(post("/api/demo/handoff-package")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "operation": "replace",
                                      "targetPath": "docs/demo.md",
                                      "replacementText": "PatchPilot smoke test",
                                      "savedAt": "2026-06-26T01:00:00Z"
                                    }
                                  ],
                                  "archivedLaunchOutcomes": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "taskId": "task-1",
                                      "taskStatus": "COMPLETED",
                                      "pullRequestUrl": "https://github.com/bingqin2/PatchPilot/pull/42",
                                      "archivedAt": "2026-06-26T01:10:00Z",
                                      "report": "# PatchPilot Demo Launch Outcome Report"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Handoff Package")));
    }

    @Test
    void should_return_structured_handoff_readiness_with_browser_context() throws Exception {
        when(demoSessionReportService.getHandoffReadiness(argThat(request ->
                request.preparedLaunchCommands().size() == 1
                        && request.archivedLaunchOutcomes().size() == 1
                        && request.archivedLaunchOutcomes().get(0).taskId().equals("task-1")
        ))).thenReturn(new DemoHandoffReadinessVo(
                DemoReadinessStatus.READY,
                "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                "No missing handoff evidence.",
                List.of(
                        new DemoHandoffReadinessCheckVo(
                                "Webhook delivery evidence",
                                DemoReadinessStatus.READY,
                                "delivery-1 created task task-1.",
                                "No action needed."
                        ),
                        new DemoHandoffReadinessCheckVo(
                                "Prepared command context",
                                DemoReadinessStatus.READY,
                                "1 prepared command recorded.",
                                "No action needed."
                        )
                )
        ));

        mockMvc.perform(post("/api/demo/handoff-readiness")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "operation": "replace",
                                      "targetPath": "docs/demo.md",
                                      "replacementText": "PatchPilot smoke test",
                                      "savedAt": "2026-06-26T01:00:00Z"
                                    }
                                  ],
                                  "archivedLaunchOutcomes": [
                                    {
                                      "triggerComment": "/agent fix replace docs/demo.md PatchPilot smoke test",
                                      "repositoryOwner": "bingqin2",
                                      "repositoryName": "PatchPilot",
                                      "issueNumber": 1,
                                      "triggerUser": "bingqin2",
                                      "taskId": "task-1",
                                      "taskStatus": "COMPLETED",
                                      "pullRequestUrl": "https://github.com/bingqin2/PatchPilot/pull/42",
                                      "archivedAt": "2026-06-26T01:10:00Z",
                                      "report": "# PatchPilot Demo Launch Outcome Report"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence."))
                .andExpect(jsonPath("$.data.nextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data.checks[0].name").value("Webhook delivery evidence"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[0].summary").value("delivery-1 created task task-1."))
                .andExpect(jsonPath("$.data.checks[0].nextAction").value("No action needed."))
                .andExpect(jsonPath("$.data.checks[1].name").value("Prepared command context"));
    }

    @Test
    void should_download_demo_handoff_package_with_browser_context() throws Exception {
        when(demoSessionReportService.getHandoffPackage(any(DemoSessionReportRequestDto.class))).thenReturn("""
                # PatchPilot Demo Handoff Package

                ## Handoff Summary
                """);

        mockMvc.perform(post("/api/demo/handoff-package/download")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [],
                                  "archivedLaunchOutcomes": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-package.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Package")));
    }

    @Test
    void should_archive_current_demo_session_report() throws Exception {
        when(demoSessionArchiveService.archiveCurrentSession(any(DemoSessionReportRequestDto.class))).thenReturn(new DemoSessionArchiveVo(
                "archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Session Report\n\n- Status: `READY`"
        ));

        mockMvc.perform(post("/api/demo/session-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("archive-1"))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Session Report")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoSessionArchiveAudit));
    }

    private boolean isDemoSessionArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_SESSION_ARCHIVED".equals(command.action())
                && "DEMO_SESSION_ARCHIVE".equals(command.resourceType())
                && "archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo session demo-session-20260624T003000Z".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_session_archives() throws Exception {
        when(demoSessionArchiveService.listRecentArchives()).thenReturn(List.of(new DemoSessionArchiveVo(
                "archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Session Report\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/session-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("archive-1"))
                .andExpect(jsonPath("$.data[0].sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data[0].shareSummary").value(org.hamcrest.Matchers.containsString("READY")));
    }

    @Test
    void should_download_archived_demo_session_report_as_markdown_attachment() throws Exception {
        when(demoSessionArchiveService.findArchive("archive-1")).thenReturn(Optional.of(new DemoSessionArchiveVo(
                "archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Session Report\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/session-archives/archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-session-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Session Report")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_session_report_is_missing() throws Exception {
        when(demoSessionArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/session-archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_archive_current_demo_handoff_package() throws Exception {
        when(demoHandoffPackageArchiveService.archiveCurrentHandoffPackage(any(DemoSessionReportRequestDto.class)))
                .thenReturn(new DemoHandoffPackageArchiveVo(
                        "handoff-archive-1",
                        "demo-session-20260624T003000Z",
                        DemoReadinessStatus.READY,
                        "Demo session snapshot is ready.",
                        DemoReadinessStatus.READY,
                        "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                        "No missing handoff evidence.",
                        7,
                        0,
                        0,
                        "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                        "https://github.com/bingqin2/PatchPilot/pull/42",
                        Instant.parse("2026-06-24T04:00:00Z"),
                        "# PatchPilot Demo Handoff Package\n\n- Status: `READY`"
                ));

        mockMvc.perform(post("/api/demo/handoff-package-archives")
                        .contentType("application/json")
                        .content("""
                                {
                                  "preparedLaunchCommands": [],
                                  "archivedLaunchOutcomes": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.handoffReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.handoffReadinessNextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data.handoffReadyCheckCount").value(7))
                .andExpect(jsonPath("$.data.handoffNeedsAttentionCheckCount").value(0))
                .andExpect(jsonPath("$.data.handoffBlockedCheckCount").value(0))
                .andExpect(jsonPath("$.data.recentPullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Handoff Package")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoHandoffPackageArchiveAudit));
    }

    private boolean isDemoHandoffPackageArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_HANDOFF_PACKAGE_ARCHIVED".equals(command.action())
                && "DEMO_HANDOFF_PACKAGE_ARCHIVE".equals(command.resourceType())
                && "handoff-archive-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo handoff package demo-session-20260624T003000Z".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_handoff_package_archives() throws Exception {
        when(demoHandoffPackageArchiveService.listRecentArchives()).thenReturn(List.of(new DemoHandoffPackageArchiveVo(
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                DemoReadinessStatus.READY,
                "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                "No missing handoff evidence.",
                7,
                0,
                0,
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Handoff Package\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/handoff-package-archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data[0].sessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data[0].handoffReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data[0].handoffReadinessNextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data[0].shareSummary").value(org.hamcrest.Matchers.containsString("READY")));
    }

    @Test
    void should_return_demo_handoff_package_archive_summary() throws Exception {
        when(demoHandoffPackageArchiveService.getArchiveSummary()).thenReturn(new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                1,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:00:00Z"),
                "Latest archived handoff package is READY and can be shared.",
                "No missing handoff evidence.",
                "# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`"
        ));

        mockMvc.perform(get("/api/demo/handoff-package-archives/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.shareReady").value(true))
                .andExpect(jsonPath("$.data.archiveCount").value(1))
                .andExpect(jsonPath("$.data.latestArchiveId").value("handoff-archive-1"))
                .andExpect(jsonPath("$.data.latestSessionId").value("demo-session-20260624T003000Z"))
                .andExpect(jsonPath("$.data.latestHandoffReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.summary").value("Latest archived handoff package is READY and can be shared."))
                .andExpect(jsonPath("$.data.nextAction").value("No missing handoff evidence."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Handoff Package Archive Summary")));
    }

    @Test
    void should_download_demo_handoff_package_archive_summary_as_markdown_attachment() throws Exception {
        when(demoHandoffPackageArchiveService.getArchiveSummary()).thenReturn(new DemoHandoffPackageArchiveSummaryVo(
                "READY",
                true,
                1,
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                Instant.parse("2026-06-24T04:00:00Z"),
                "Latest archived handoff package is READY and can be shared.",
                "No missing handoff evidence.",
                "# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`\n- Latest archive: `handoff-archive-1`"
        ));

        mockMvc.perform(get("/api/demo/handoff-package-archives/summary-report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        containsString("patchpilot-demo-handoff-package-archive-summary.md")
                ))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Handoff Package Archive Summary")))
                .andExpect(content().string(containsString("`READY`")))
                .andExpect(content().string(containsString("`handoff-archive-1`")));
    }

    @Test
    void should_download_archived_demo_handoff_package_as_markdown_attachment() throws Exception {
        when(demoHandoffPackageArchiveService.findArchive("handoff-archive-1")).thenReturn(Optional.of(new DemoHandoffPackageArchiveVo(
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                "Demo session snapshot is ready.",
                DemoReadinessStatus.READY,
                "Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.",
                "No missing handoff evidence.",
                7,
                0,
                0,
                "Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-24T04:00:00Z"),
                "# PatchPilot Demo Handoff Package\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/handoff-package-archives/handoff-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-handoff-package-handoff-archive-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Handoff Package")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_handoff_package_is_missing() throws Exception {
        when(demoHandoffPackageArchiveService.findArchive("missing-archive")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/handoff-package-archives/missing-archive/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_archive_current_demo_readiness_snapshot() throws Exception {
        when(demoReadinessSnapshotArchiveService.archiveCurrentReadiness()).thenReturn(new DemoReadinessSnapshotArchiveVo(
                "readiness-snapshot-1",
                DemoReadinessStatus.BLOCKED,
                "PatchPilot is blocked before a live demo.",
                1,
                1,
                1,
                Instant.parse("2026-06-27T04:00:00Z"),
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `BLOCKED`"
        ));

        mockMvc.perform(post("/api/demo/readiness-snapshots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("readiness-snapshot-1"))
                .andExpect(jsonPath("$.data.status").value("BLOCKED"))
                .andExpect(jsonPath("$.data.readyCheckCount").value(1))
                .andExpect(jsonPath("$.data.needsAttentionCheckCount").value(1))
                .andExpect(jsonPath("$.data.blockedCheckCount").value(1))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Readiness Snapshot")));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isDemoReadinessSnapshotArchiveAudit));
    }

    private boolean isDemoReadinessSnapshotArchiveAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "DEMO_READINESS_SNAPSHOT_ARCHIVED".equals(command.action())
                && "DEMO_READINESS_SNAPSHOT_ARCHIVE".equals(command.resourceType())
                && "readiness-snapshot-1".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "patchpilot/local-demo".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Archived demo readiness snapshot BLOCKED".equals(command.reason());
    }

    @Test
    void should_return_recent_demo_readiness_snapshot_archives() throws Exception {
        when(demoReadinessSnapshotArchiveService.listRecentArchives()).thenReturn(List.of(new DemoReadinessSnapshotArchiveVo(
                "readiness-snapshot-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                "PatchPilot needs attention before a live demo.",
                7,
                2,
                0,
                Instant.parse("2026-06-27T04:00:00Z"),
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `NEEDS_ATTENTION`"
        )));

        mockMvc.perform(get("/api/demo/readiness-snapshots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("readiness-snapshot-1"))
                .andExpect(jsonPath("$.data[0].status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data[0].summary").value("PatchPilot needs attention before a live demo."))
                .andExpect(jsonPath("$.data[0].readyCheckCount").value(7));
    }

    @Test
    void should_download_archived_demo_readiness_snapshot_report_as_markdown_attachment() throws Exception {
        when(demoReadinessSnapshotArchiveService.findArchive("readiness-snapshot-1")).thenReturn(Optional.of(new DemoReadinessSnapshotArchiveVo(
                "readiness-snapshot-1",
                DemoReadinessStatus.READY,
                "PatchPilot is ready for a controlled demo.",
                9,
                0,
                0,
                Instant.parse("2026-06-27T04:00:00Z"),
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `READY`"
        )));

        mockMvc.perform(get("/api/demo/readiness-snapshots/readiness-snapshot-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment;")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("patchpilot-demo-readiness-readiness-snapshot-1.md")))
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().string(containsString("# PatchPilot Demo Readiness Snapshot")))
                .andExpect(content().string(containsString("`READY`")));
    }

    @Test
    void should_return_not_found_when_archived_demo_readiness_snapshot_report_is_missing() throws Exception {
        when(demoReadinessSnapshotArchiveService.findArchive("missing-snapshot")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/demo/readiness-snapshots/missing-snapshot/report/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_demo_readiness_snapshot_trend_summary() throws Exception {
        when(demoReadinessSnapshotTrendService.getTrendSummary()).thenReturn(new DemoReadinessSnapshotTrendVo(
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
        ));

        mockMvc.perform(get("/api/demo/readiness-snapshots/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IMPROVING"))
                .andExpect(jsonPath("$.data.summary").value("Demo readiness improved from BLOCKED to READY."))
                .andExpect(jsonPath("$.data.latestSnapshotId").value("readiness-snapshot-new"))
                .andExpect(jsonPath("$.data.previousSnapshotId").value("readiness-snapshot-old"))
                .andExpect(jsonPath("$.data.latestReadinessStatus").value("READY"))
                .andExpect(jsonPath("$.data.previousReadinessStatus").value("BLOCKED"))
                .andExpect(jsonPath("$.data.readyCheckDelta").value(4))
                .andExpect(jsonPath("$.data.needsAttentionCheckDelta").value(-2))
                .andExpect(jsonPath("$.data.blockedCheckDelta").value(-2))
                .andExpect(jsonPath("$.data.nextAction").value("Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run."))
                .andExpect(jsonPath("$.data.markdownReport").value(org.hamcrest.Matchers.containsString("# PatchPilot Demo Readiness Snapshot Trend")));
    }
}
