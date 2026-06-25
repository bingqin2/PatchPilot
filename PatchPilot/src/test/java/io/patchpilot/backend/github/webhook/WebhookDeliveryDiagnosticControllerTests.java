package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
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

@WebMvcTest(WebhookDeliveryDiagnosticController.class)
class WebhookDeliveryDiagnosticControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookDeliveryDiagnosticService diagnosticService;

    @Test
    void should_list_recent_webhook_delivery_diagnostics() throws Exception {
        when(diagnosticService.listRecent(20)).thenReturn(List.of(new WebhookDeliveryDiagnosticVo(
                "diagnostic-123",
                "delivery-123",
                "issue_comment",
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                "task-123",
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix touch docs/demo.md",
                "Task created from /agent fix",
                false,
                "Task was created. Do not redeliver this webhook unless you intentionally want GitHub to report a duplicate delivery.",
                WebhookDeliveryOutcomeType.TASK,
                "task-123",
                "/tasks/task-123",
                Instant.parse("2026-06-22T01:00:00Z")
        )));

        mockMvc.perform(get("/api/github/webhook-deliveries").param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("diagnostic-123"))
                .andExpect(jsonPath("$.data[0].deliveryId").value("delivery-123"))
                .andExpect(jsonPath("$.data[0].event").value("issue_comment"))
                .andExpect(jsonPath("$.data[0].status").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data[0].taskId").value("task-123"))
                .andExpect(jsonPath("$.data[0].repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data[0].repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data[0].issueNumber").value(42))
                .andExpect(jsonPath("$.data[0].triggerUser").value("alice"))
                .andExpect(jsonPath("$.data[0].triggerComment").value("/agent fix touch docs/demo.md"))
                .andExpect(jsonPath("$.data[0].message").value("Task created from /agent fix"))
                .andExpect(jsonPath("$.data[0].redeliveryRecommended").value(false))
                .andExpect(jsonPath("$.data[0].operatorAction").value("Task was created. Do not redeliver this webhook unless you intentionally want GitHub to report a duplicate delivery."))
                .andExpect(jsonPath("$.data[0].outcomeType").value("TASK"))
                .andExpect(jsonPath("$.data[0].outcomeId").value("task-123"))
                .andExpect(jsonPath("$.data[0].outcomeUrl").value("/tasks/task-123"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-06-22T01:00:00Z"));
    }

    @Test
    void should_reject_invalid_limit() throws Exception {
        mockMvc.perform(get("/api/github/webhook-deliveries").param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }
}
