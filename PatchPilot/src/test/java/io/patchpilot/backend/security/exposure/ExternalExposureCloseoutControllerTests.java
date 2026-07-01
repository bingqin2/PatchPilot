package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
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

class ExternalExposureCloseoutControllerTests {

    private final ExternalExposureCloseoutService closeoutService = mock(ExternalExposureCloseoutService.class);
    private final ExternalExposureCloseoutArchiveService archiveService = mock(ExternalExposureCloseoutArchiveService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureCloseoutController(closeoutService, archiveService))
            .build();

    @Test
    void should_get_external_exposure_closeout() throws Exception {
        when(closeoutService.getCloseout()).thenReturn(readyCloseout());

        mockMvc.perform(get("/api/security/external-exposure-closeout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.closeoutReady").value(true))
                .andExpect(jsonPath("$.data.latestSessionId").value("exposure-session-1"))
                .andExpect(jsonPath("$.data.linkedReadinessArchiveId").value("exposure-archive-1"))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("GET /api/security/external-exposure-closeout/report/download"));
    }

    @Test
    void should_download_external_exposure_closeout_report() throws Exception {
        when(closeoutService.getCloseout()).thenReturn(readyCloseout());

        mockMvc.perform(get("/api/security/external-exposure-closeout/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"patchpilot-external-exposure-closeout.md\""))
                .andExpect(content().string("# PatchPilot External Exposure Closeout"));
    }

    @Test
    void should_archive_external_exposure_closeout() throws Exception {
        when(archiveService.archiveCurrentCloseout()).thenReturn(readyCloseoutArchive());

        mockMvc.perform(post("/api/security/external-exposure-closeout/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("closeout-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.closeoutReady").value(true))
                .andExpect(jsonPath("$.data.latestSessionId").value("exposure-session-1"))
                .andExpect(jsonPath("$.data.linkedReadinessArchiveId").value("exposure-archive-1"));

        verify(archiveService).archiveCurrentCloseout();
    }

    @Test
    void should_list_external_exposure_closeout_archives() throws Exception {
        when(archiveService.listRecentArchives()).thenReturn(List.of(readyCloseoutArchive()));

        mockMvc.perform(get("/api/security/external-exposure-closeout/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("closeout-archive-1"))
                .andExpect(jsonPath("$.data[0].report").value("# PatchPilot External Exposure Closeout"));
    }

    @Test
    void should_download_external_exposure_closeout_archive_report() throws Exception {
        when(archiveService.findArchive("closeout archive 1")).thenReturn(Optional.of(readyCloseoutArchive()));

        mockMvc.perform(get("/api/security/external-exposure-closeout/archives/closeout archive 1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"patchpilot-external-exposure-closeout-closeout-archive-1.md\""
                ))
                .andExpect(content().string("# PatchPilot External Exposure Closeout"));
    }

    @Test
    void should_return_not_found_when_external_exposure_closeout_archive_is_missing() throws Exception {
        when(archiveService.findArchive("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/security/external-exposure-closeout/archives/missing/report/download"))
                .andExpect(status().isNotFound());
    }

    private static ExternalExposureCloseoutVo readyCloseout() {
        return new ExternalExposureCloseoutVo(
                "READY",
                true,
                "External exposure session is closed with complete local evidence.",
                "Keep the closeout report with the demo evidence bundle.",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped and GitHub webhook URL removed.",
                "exposure-archive-1",
                "READY",
                "CURRENT",
                4,
                0,
                0,
                4,
                List.of("Keep the closeout report with the demo evidence bundle."),
                List.of("Latest session exposure-session-1 is CLOSED."),
                List.of("GET /api/security/external-exposure-closeout/report/download"),
                "GET /api/security/external-exposure-closeout is read-only.",
                Instant.parse("2026-07-01T18:00:00Z"),
                "# PatchPilot External Exposure Closeout"
        );
    }

    private static ExternalExposureCloseoutArchiveVo readyCloseoutArchive() {
        return new ExternalExposureCloseoutArchiveVo(
                "closeout-archive-1",
                "READY",
                true,
                "External exposure session is closed with complete local evidence.",
                "Keep the closeout report with the demo evidence bundle.",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped and GitHub webhook URL removed.",
                "exposure-archive-1",
                "READY",
                "CURRENT",
                4,
                0,
                0,
                4,
                List.of("Keep the closeout report with the demo evidence bundle."),
                List.of("Latest session exposure-session-1 is CLOSED."),
                List.of("GET /api/security/external-exposure-closeout/report/download"),
                "GET /api/security/external-exposure-closeout is read-only.",
                Instant.parse("2026-07-01T18:00:00Z"),
                Instant.parse("2026-07-01T18:05:00Z"),
                "# PatchPilot External Exposure Closeout"
        );
    }
}
