package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalExposureOperatorHandoffChecklistControllerTests {

    private final ExternalExposureOperatorHandoffChecklistService checklistService =
            mock(ExternalExposureOperatorHandoffChecklistService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureOperatorHandoffChecklistController(checklistService))
            .build();

    @Test
    void should_get_external_exposure_operator_handoff_checklist() throws Exception {
        when(checklistService.getChecklist()).thenReturn(readyChecklist());

        mockMvc.perform(get("/api/security/external-exposure-operator-handoff-checklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyForNextLiveStep").value(true))
                .andExpect(jsonPath("$.data.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.latestCloseoutArchiveId").value("closeout-archive-1"))
                .andExpect(jsonPath("$.data.livePublishStatus").value("READY"))
                .andExpect(jsonPath("$.data.checks[0].name").value("Closeout archive"))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("GET /api/security/external-exposure-operator-handoff-checklist/report/download"));
    }

    @Test
    void should_download_external_exposure_operator_handoff_checklist_report() throws Exception {
        when(checklistService.getChecklist()).thenReturn(readyChecklist());

        mockMvc.perform(get("/api/security/external-exposure-operator-handoff-checklist/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"patchpilot-external-exposure-operator-handoff-checklist.md\""
                ))
                .andExpect(content().string("# PatchPilot External Exposure Operator Handoff Checklist"));
    }

    private static ExternalExposureOperatorHandoffChecklistVo readyChecklist() {
        return new ExternalExposureOperatorHandoffChecklistVo(
                "READY",
                true,
                "External exposure evidence is closed and ready for the next live step.",
                "Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.",
                "bingqin2/PatchPilot",
                "closeout-archive-1",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "READY",
                "CURRENT",
                "READY",
                true,
                0,
                4,
                0,
                0,
                4,
                List.of("Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend."),
                List.of("Latest closeout archive closeout-archive-1 is READY."),
                List.of("GET /api/security/external-exposure-operator-handoff-checklist/report/download"),
                "GET /api/security/external-exposure-operator-handoff-checklist is read-only.",
                List.of(new ExternalExposureOperatorHandoffChecklistCheckVo(
                        "Closeout archive",
                        "READY",
                        "Latest closeout archive closeout-archive-1 is READY.",
                        "Ready."
                )),
                Instant.parse("2026-07-01T19:00:00Z"),
                "# PatchPilot External Exposure Operator Handoff Checklist"
        );
    }
}
