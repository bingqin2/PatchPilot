package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalExposureReadinessArchiveControllerTests {

    private final ExternalExposureReadinessArchiveService archiveService = mock(ExternalExposureReadinessArchiveService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureReadinessArchiveController(archiveService))
            .build();

    @Test
    void should_archive_external_exposure_readiness() throws Exception {
        ExternalExposureReadinessArchiveVo archive = archive();
        when(archiveService.archiveCurrentReadiness()).thenReturn(archive);

        mockMvc.perform(post("/api/security/external-exposure-readiness/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("exposure-archive-1"))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.safeToExpose").value(false))
                .andExpect(jsonPath("$.data.readyCount").value(1));

        verify(archiveService).archiveCurrentReadiness();
    }

    @Test
    void should_list_external_exposure_readiness_archives() throws Exception {
        when(archiveService.listRecentArchives()).thenReturn(List.of(archive()));

        mockMvc.perform(get("/api/security/external-exposure-readiness/archives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("exposure-archive-1"))
                .andExpect(jsonPath("$.data[0].report").value("# PatchPilot External Exposure Readiness"));
    }

    @Test
    void should_download_external_exposure_readiness_archive_report() throws Exception {
        when(archiveService.findArchive("exposure-archive-1")).thenReturn(Optional.of(archive()));

        mockMvc.perform(get("/api/security/external-exposure-readiness/archives/exposure-archive-1/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"patchpilot-external-exposure-readiness-exposure-archive-1.md\""))
                .andExpect(content().string("# PatchPilot External Exposure Readiness"));
    }

    @Test
    void should_return_not_found_when_external_exposure_readiness_archive_is_missing() throws Exception {
        when(archiveService.findArchive("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/security/external-exposure-readiness/archives/missing/report/download"))
                .andExpect(status().isNotFound());
    }

    private static ExternalExposureReadinessArchiveVo archive() {
        return new ExternalExposureReadinessArchiveVo(
                "exposure-archive-1",
                "NEEDS_ATTENTION",
                false,
                "PatchPilot needs more safeguards before public exposure.",
                1,
                1,
                0,
                2,
                Instant.parse("2026-07-01T13:30:00Z"),
                "# PatchPilot External Exposure Readiness"
        );
    }
}
