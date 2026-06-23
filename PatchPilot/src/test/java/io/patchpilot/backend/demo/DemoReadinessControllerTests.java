package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
