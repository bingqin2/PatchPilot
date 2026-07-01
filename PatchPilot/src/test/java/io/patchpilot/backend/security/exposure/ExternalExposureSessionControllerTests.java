package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCloseRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCreateRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalExposureSessionControllerTests {

    private final ExternalExposureSessionService sessionService = mock(ExternalExposureSessionService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureSessionController(sessionService))
            .build();

    @Test
    void should_start_external_exposure_session() throws Exception {
        when(sessionService.startSession(any(ExternalExposureSessionCreateRequestDto.class))).thenReturn(activeSession());

        mockMvc.perform(post("/api/security/external-exposure-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "publicUrl": "https://demo.trycloudflare.com",
                                  "webhookUrl": "https://demo.trycloudflare.com/api/github/webhook",
                                  "purpose": "Live GitHub webhook smoke test",
                                  "operator": "bingqin2",
                                  "expectedShutdownAt": "2026-07-01T17:00:00Z",
                                  "notes": "Keep terminal visible during test."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("exposure-session-1"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.publicUrl").value("https://demo.trycloudflare.com"));

        verify(sessionService).startSession(any(ExternalExposureSessionCreateRequestDto.class));
    }

    @Test
    void should_return_bad_request_when_handoff_package_is_not_ready() throws Exception {
        when(sessionService.startSession(any(ExternalExposureSessionCreateRequestDto.class)))
                .thenThrow(new IllegalStateException("external exposure handoff package is not ready"));

        mockMvc.perform(post("/api/security/external-exposure-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "publicUrl": "https://demo.trycloudflare.com",
                                  "webhookUrl": "https://demo.trycloudflare.com/api/github/webhook",
                                  "purpose": "Live GitHub webhook smoke test",
                                  "operator": "bingqin2"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("external exposure handoff package is not ready"));
    }

    @Test
    void should_close_external_exposure_session() throws Exception {
        when(sessionService.closeSession(eq("exposure-session-1"), any(ExternalExposureSessionCloseRequestDto.class)))
                .thenReturn(closedSession());

        mockMvc.perform(post("/api/security/external-exposure-sessions/exposure-session-1/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "closedBy": "bingqin2",
                                  "closedAt": "2026-07-01T16:30:00Z",
                                  "closeNotes": "Tunnel process stopped."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"))
                .andExpect(jsonPath("$.data.closedBy").value("bingqin2"))
                .andExpect(jsonPath("$.data.closeNotes").value("Tunnel process stopped."));

        verify(sessionService).closeSession(eq("exposure-session-1"), any(ExternalExposureSessionCloseRequestDto.class));
    }

    @Test
    void should_list_external_exposure_sessions() throws Exception {
        when(sessionService.listRecentSessions()).thenReturn(List.of(activeSession()));

        mockMvc.perform(get("/api/security/external-exposure-sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("exposure-session-1"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
    }

    @Test
    void should_download_external_exposure_session_report() throws Exception {
        when(sessionService.findSession("exposure-session-1")).thenReturn(Optional.of(activeSession()));

        mockMvc.perform(get("/api/security/external-exposure-sessions/exposure-session-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"patchpilot-external-exposure-session-exposure-session-1.md\""))
                .andExpect(content().string("# PatchPilot External Exposure Session"));
    }

    @Test
    void should_return_not_found_when_session_report_is_missing() throws Exception {
        when(sessionService.findSession("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/security/external-exposure-sessions/missing/report/download"))
                .andExpect(status().isNotFound());
    }

    private static ExternalExposureSessionVo activeSession() {
        return new ExternalExposureSessionVo(
                "exposure-session-1",
                "ACTIVE",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                Instant.parse("2026-07-01T15:00:00Z"),
                null,
                null,
                null,
                "# PatchPilot External Exposure Session"
        );
    }

    private static ExternalExposureSessionVo closedSession() {
        return new ExternalExposureSessionVo(
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped.",
                "# PatchPilot External Exposure Session"
        );
    }
}
