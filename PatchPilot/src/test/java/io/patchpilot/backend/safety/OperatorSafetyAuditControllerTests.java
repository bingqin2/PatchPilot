package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
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

@WebMvcTest(OperatorSafetyAuditController.class)
class OperatorSafetyAuditControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperatorSafetyAuditService auditService;

    @Test
    void should_list_recent_operator_safety_audits() throws Exception {
        when(auditService.listSafetyAudits(20)).thenReturn(List.of(new OperatorSafetyAuditVo(
                "audit-1",
                "MANUAL_QUARANTINE_CREATED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "local-admin",
                "Operator blocked noisy demo trigger user",
                Instant.parse("2026-06-24T01:00:00Z")
        )));

        mockMvc.perform(get("/api/operator-safety-audits").param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("audit-1"))
                .andExpect(jsonPath("$.data[0].action").value("MANUAL_QUARANTINE_CREATED"))
                .andExpect(jsonPath("$.data[0].resourceType").value("TRIGGER_QUARANTINE"))
                .andExpect(jsonPath("$.data[0].resourceId").value("quarantine-1"))
                .andExpect(jsonPath("$.data[0].scope").value("TRIGGER_USER"))
                .andExpect(jsonPath("$.data[0].scopeKey").value("drive-by-user"))
                .andExpect(jsonPath("$.data[0].operator").value("local-admin"))
                .andExpect(jsonPath("$.data[0].reason").value("Operator blocked noisy demo trigger user"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-06-24T01:00:00Z"));
    }

    @Test
    void should_list_recent_admin_audit_events_from_same_audit_stream() throws Exception {
        when(auditService.listSafetyAudits(20)).thenReturn(List.of(new OperatorSafetyAuditVo(
                "audit-task-retry",
                "TASK_RETRIED",
                "TASK",
                "task-123",
                TriggerQuarantineScope.REPOSITORY,
                "bingqin2/patchpilot",
                "admin-api",
                "Verified failure cause and requested a clean rerun",
                Instant.parse("2026-06-24T02:00:00Z")
        )));

        mockMvc.perform(get("/api/admin-audit-events").param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("audit-task-retry"))
                .andExpect(jsonPath("$.data[0].action").value("TASK_RETRIED"))
                .andExpect(jsonPath("$.data[0].resourceType").value("TASK"))
                .andExpect(jsonPath("$.data[0].resourceId").value("task-123"))
                .andExpect(jsonPath("$.data[0].scope").value("REPOSITORY"))
                .andExpect(jsonPath("$.data[0].scopeKey").value("bingqin2/patchpilot"))
                .andExpect(jsonPath("$.data[0].operator").value("admin-api"))
                .andExpect(jsonPath("$.data[0].reason").value("Verified failure cause and requested a clean rerun"));
    }

    @Test
    void should_reject_invalid_limit() throws Exception {
        mockMvc.perform(get("/api/operator-safety-audits").param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }

    @Test
    void should_reject_invalid_admin_audit_limit() throws Exception {
        mockMvc.perform(get("/api/admin-audit-events").param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }
}
