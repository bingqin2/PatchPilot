package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalExposureReadinessControllerTests {

    @Test
    void should_return_external_exposure_readiness() throws Exception {
        ExternalExposureReadinessVo readiness = new ExternalExposureReadinessVo(
                "NEEDS_ATTENTION",
                false,
                1,
                1,
                0,
                2,
                "PatchPilot needs more safeguards before public exposure.",
                List.of("Configure trigger user allowlist."),
                "read-only external exposure readiness",
                List.of(
                        new ExternalExposureReadinessCheckVo(
                                "Admin API token",
                                "READY",
                                "Admin token is configured.",
                                "No action needed."
                        ),
                        new ExternalExposureReadinessCheckVo(
                                "Trigger user allowlist",
                                "NEEDS_ATTENTION",
                                "Trigger user allowlist is open.",
                                "Configure PATCHPILOT_ALLOWED_TRIGGER_USERS."
                        )
                ),
                Instant.parse("2026-07-01T12:00:00Z"),
                "# PatchPilot External Exposure Readiness"
        );
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ExternalExposureReadinessController(() -> readiness))
                .build();

        mockMvc.perform(get("/api/security/external-exposure-readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.safeToExpose").value(false))
                .andExpect(jsonPath("$.data.readyCount").value(1))
                .andExpect(jsonPath("$.data.checks[0].name").value("Admin API token"))
                .andExpect(jsonPath("$.data.markdownReport").value("# PatchPilot External Exposure Readiness"));
    }
}
