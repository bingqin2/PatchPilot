package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalExposureOperatorHandoffChecklistControllerTests {

    private final ExternalExposureOperatorHandoffChecklistService checklistService =
            mock(ExternalExposureOperatorHandoffChecklistService.class);
    private final ExternalExposureOperatorHandoffChecklistArchiveService archiveService =
            mock(ExternalExposureOperatorHandoffChecklistArchiveService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureOperatorHandoffChecklistController(checklistService, archiveService))
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

    @Test
    void should_archive_external_exposure_operator_handoff_checklist() throws Exception {
        when(archiveService.archiveCurrentChecklist()).thenReturn(readyArchive());

        mockMvc.perform(post("/api/security/external-exposure-operator-handoff-checklist/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("operator-handoff-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyForNextLiveStep").value(true))
                .andExpect(jsonPath("$.data.latestCloseoutArchiveId").value("closeout-archive-1"));

        verify(archiveService).archiveCurrentChecklist();
    }

    @Test
    void should_list_external_exposure_operator_handoff_archives() throws Exception {
        when(archiveService.listRecentArchives()).thenReturn(List.of(readyArchive()));

        mockMvc.perform(get("/api/security/external-exposure-operator-handoff-checklist/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("operator-handoff-archive-1"))
                .andExpect(jsonPath("$.data[0].report").value("# PatchPilot External Exposure Operator Handoff Checklist"));
    }

    @Test
    void should_download_external_exposure_operator_handoff_archive_report() throws Exception {
        when(archiveService.findArchive("operator handoff archive 1")).thenReturn(Optional.of(readyArchive()));

        mockMvc.perform(get("/api/security/external-exposure-operator-handoff-checklist/archives/operator handoff archive 1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"patchpilot-external-exposure-operator-handoff-checklist-operator-handoff-archive-1.md\""
                ))
                .andExpect(content().string("# PatchPilot External Exposure Operator Handoff Checklist"));
    }

    @Test
    void should_return_not_found_when_operator_handoff_archive_is_missing() throws Exception {
        when(archiveService.findArchive("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/security/external-exposure-operator-handoff-checklist/archives/missing/report/download"))
                .andExpect(status().isNotFound());
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

    private static ExternalExposureOperatorHandoffChecklistArchiveVo readyArchive() {
        return new ExternalExposureOperatorHandoffChecklistArchiveVo(
                "operator-handoff-archive-1",
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
                Instant.parse("2026-07-01T19:05:00Z"),
                "# PatchPilot External Exposure Operator Handoff Checklist"
        );
    }
}
