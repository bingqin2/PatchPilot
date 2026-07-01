package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalExposureHandoffPackageControllerTests {

    private final ExternalExposureHandoffPackageService handoffPackageService =
            mock(ExternalExposureHandoffPackageService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExternalExposureHandoffPackageController(handoffPackageService))
            .build();

    @Test
    void should_get_external_exposure_handoff_package() throws Exception {
        when(handoffPackageService.getHandoffPackage()).thenReturn(handoffPackage());

        mockMvc.perform(get("/api/security/external-exposure-handoff-package"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.handoffReady").value(true))
                .andExpect(jsonPath("$.data.latestArchiveId").value("exposure-archive-1"))
                .andExpect(jsonPath("$.data.archiveFreshness").value("CURRENT"))
                .andExpect(jsonPath("$.data.downloadActions[0]").value("GET /api/security/external-exposure-handoff-package/report/download"));

        verify(handoffPackageService).getHandoffPackage();
    }

    @Test
    void should_download_external_exposure_handoff_package_report() throws Exception {
        when(handoffPackageService.getHandoffPackage()).thenReturn(handoffPackage());

        mockMvc.perform(get("/api/security/external-exposure-handoff-package/report/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"patchpilot-external-exposure-handoff-package.md\""))
                .andExpect(header().string("Content-Type", "text/markdown;charset=UTF-8"))
                .andExpect(content().string("# PatchPilot External Exposure Handoff Package"));
    }

    private static ExternalExposureHandoffPackageVo handoffPackage() {
        return new ExternalExposureHandoffPackageVo(
                "READY",
                true,
                "External exposure handoff package is ready to share.",
                "Start the tunnel and keep monitoring.",
                "READY",
                true,
                10,
                0,
                0,
                10,
                "exposure-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T13:30:00Z"),
                "CURRENT",
                List.of("Start the tunnel and keep monitoring."),
                List.of("Latest archive exposure-archive-1 captures READY readiness evidence."),
                List.of("GET /api/security/external-exposure-handoff-package/report/download"),
                "read-only external exposure handoff package",
                Instant.parse("2026-07-01T14:00:00Z"),
                "# PatchPilot External Exposure Handoff Package"
        );
    }
}
