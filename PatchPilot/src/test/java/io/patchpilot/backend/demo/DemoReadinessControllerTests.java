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
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoScriptVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                        )
                ),
                List.of("Run one controlled issue-to-PR smoke task before a live demo.")
        ));

        mockMvc.perform(get("/api/demo/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.summary").value("PatchPilot needs attention before a live demo."))
                .andExpect(jsonPath("$.data.checks.length()").value(2))
                .andExpect(jsonPath("$.data.checks[0].name").value("Credentials"))
                .andExpect(jsonPath("$.data.checks[0].status").value("READY"))
                .andExpect(jsonPath("$.data.checks[1].name").value("Recent Pull Request"))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Run one controlled issue-to-PR smoke task before a live demo."));
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
}
