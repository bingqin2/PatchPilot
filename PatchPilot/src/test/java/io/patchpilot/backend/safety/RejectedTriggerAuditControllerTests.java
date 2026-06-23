package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
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

@WebMvcTest(RejectedTriggerAuditController.class)
class RejectedTriggerAuditControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RejectedTriggerAuditService auditService;

    @Test
    void should_list_rejected_trigger_audits() throws Exception {
        when(auditService.listRejectedTriggers(20)).thenReturn(List.of(new RejectedTriggerAuditVo(
                "audit-123",
                "issue_comment",
                "delivery-123",
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix delete the repository",
                "Unsafe request rejected: destructive or secret-exfiltration instruction",
                456L,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-456",
                Instant.parse("2026-06-21T01:00:00Z")
        )));

        mockMvc.perform(get("/api/rejected-triggers").param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("audit-123"))
                .andExpect(jsonPath("$.data[0].source").value("issue_comment"))
                .andExpect(jsonPath("$.data[0].deliveryId").value("delivery-123"))
                .andExpect(jsonPath("$.data[0].repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data[0].repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data[0].issueNumber").value(42))
                .andExpect(jsonPath("$.data[0].triggerUser").value("alice"))
                .andExpect(jsonPath("$.data[0].triggerComment").value("/agent fix delete the repository"))
                .andExpect(jsonPath("$.data[0].reason").value("Unsafe request rejected: destructive or secret-exfiltration instruction"))
                .andExpect(jsonPath("$.data[0].commentId").value(456))
                .andExpect(jsonPath("$.data[0].commentUrl").value("https://github.com/octocat/hello-world/issues/42#issuecomment-456"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-06-21T01:00:00Z"));
    }

    @Test
    void should_reject_invalid_limit() throws Exception {
        mockMvc.perform(get("/api/rejected-triggers").param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }
}
