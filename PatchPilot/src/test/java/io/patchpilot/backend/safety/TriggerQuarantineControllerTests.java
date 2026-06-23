package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
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

@WebMvcTest(TriggerQuarantineController.class)
class TriggerQuarantineControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TriggerQuarantineRecordService quarantineRecordService;

    @Test
    void should_list_active_trigger_quarantines() throws Exception {
        when(quarantineRecordService.listQuarantines(true, 20)).thenReturn(List.of(new TriggerQuarantineVo(
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:30:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:05:00Z"),
                true
        )));

        mockMvc.perform(get("/api/trigger-quarantines")
                        .param("activeOnly", "true")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("quarantine-1"))
                .andExpect(jsonPath("$.data[0].scope").value("TRIGGER_USER"))
                .andExpect(jsonPath("$.data[0].scopeKey").value("alice"))
                .andExpect(jsonPath("$.data[0].category").value("ABUSE_QUARANTINED"))
                .andExpect(jsonPath("$.data[0].evidenceCount").value(5))
                .andExpect(jsonPath("$.data[0].windowMs").value(600000))
                .andExpect(jsonPath("$.data[0].active").value(true));
    }

    @Test
    void should_reject_invalid_limit() throws Exception {
        mockMvc.perform(get("/api/trigger-quarantines").param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }
}
