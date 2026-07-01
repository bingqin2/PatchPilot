package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
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

class ExternalExposureCloseoutControllerTests {

    private final ExternalExposureCloseoutService closeoutService = mock(ExternalExposureCloseoutService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureCloseoutController(closeoutService))
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
}
