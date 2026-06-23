package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TriggerQuarantineController.class)
class TriggerQuarantineControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TriggerQuarantineRecordService quarantineRecordService;

    @MockitoBean
    private OperatorSafetyAuditService operatorSafetyAuditService;

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
                null,
                null,
                null,
                null,
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

    @Test
    void should_create_manual_trigger_quarantine() throws Exception {
        when(quarantineRecordService.createManualQuarantine(
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Operator blocked noisy demo trigger user",
                1800000,
                "local-admin"
        )).thenReturn(new TriggerQuarantineVo(
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Operator blocked noisy demo trigger user",
                "MANUAL_QUARANTINE",
                0,
                0,
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:30:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                "local-admin",
                null,
                null,
                null,
                true
        ));

        mockMvc.perform(post("/api/trigger-quarantines")
                        .contentType("application/json")
                        .content("""
                                {
                                  "scope": "TRIGGER_USER",
                                  "scopeKey": "alice",
                                  "reason": "Operator blocked noisy demo trigger user",
                                  "durationMs": 1800000,
                                  "operator": "local-admin"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("quarantine-1"))
                .andExpect(jsonPath("$.data.category").value("MANUAL_QUARANTINE"))
                .andExpect(jsonPath("$.data.createdBy").value("local-admin"))
                .andExpect(jsonPath("$.data.active").value(true));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("MANUAL_QUARANTINE_CREATED")
                        && command.resourceType().equals("TRIGGER_QUARANTINE")
                        && command.resourceId().equals("quarantine-1")
                        && command.scope() == TriggerQuarantineScope.TRIGGER_USER
                        && command.scopeKey().equals("alice")
                        && command.operator().equals("local-admin")
                        && command.reason().equals("Operator blocked noisy demo trigger user")
        ));
    }

    @Test
    void should_release_trigger_quarantine() throws Exception {
        when(quarantineRecordService.releaseQuarantine(
                "quarantine-1",
                "local-admin",
                "False positive during demo"
        )).thenReturn(new TriggerQuarantineVo(
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Operator blocked noisy demo trigger user",
                "MANUAL_QUARANTINE",
                0,
                0,
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:30:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:05:00Z"),
                "local-admin",
                Instant.parse("2026-06-24T00:05:00Z"),
                "local-admin",
                "False positive during demo",
                false
        ));

        mockMvc.perform(post("/api/trigger-quarantines/quarantine-1/release")
                        .contentType("application/json")
                        .content("""
                                {
                                  "operator": "local-admin",
                                  "reason": "False positive during demo"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("quarantine-1"))
                .andExpect(jsonPath("$.data.releasedBy").value("local-admin"))
                .andExpect(jsonPath("$.data.releaseReason").value("False positive during demo"))
                .andExpect(jsonPath("$.data.active").value(false));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(command ->
                command.action().equals("TRIGGER_QUARANTINE_RELEASED")
                        && command.resourceType().equals("TRIGGER_QUARANTINE")
                        && command.resourceId().equals("quarantine-1")
                        && command.scope() == TriggerQuarantineScope.TRIGGER_USER
                        && command.scopeKey().equals("alice")
                        && command.operator().equals("local-admin")
                        && command.reason().equals("False positive during demo")
        ));
    }
}
