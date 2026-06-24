package io.patchpilot.backend.github.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.task.service.FixTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
@TestPropertySource(properties = {
        "patchpilot.github.webhook-secret=test-secret",
        "patchpilot.security.admin-token=test-admin-token"
})
class WebhookPayloadDiagnosticControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FixTaskService fixTaskService;

    @Test
    void should_evaluate_valid_issue_comment_payload_without_creating_task() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix touch docs/webhook-diagnostic.md");

        mockMvc.perform(post("/api/github/webhook-diagnostics/evaluate-payload")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType("application/json")
                        .content(request("issue_comment", "diagnostic-delivery", signature(payload), payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY_FOR_WEBHOOK"))
                .andExpect(jsonPath("$.data.signatureStatus").value("VALID"))
                .andExpect(jsonPath("$.data.validJson").value(true))
                .andExpect(jsonPath("$.data.supportedEvent").value(true))
                .andExpect(jsonPath("$.data.supportedAction").value(true))
                .andExpect(jsonPath("$.data.agentFixCommand").value(true))
                .andExpect(jsonPath("$.data.repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data.repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data.issueNumber").value(42))
                .andExpect(jsonPath("$.data.triggerUser").value("alice"))
                .andExpect(jsonPath("$.data.triggerComment").value("/agent fix touch docs/webhook-diagnostic.md"))
                .andExpect(jsonPath("$.data.nextAction", containsString("redeliver")));

        assertThat(fixTaskService.listTasks()).isEmpty();
    }

    @Test
    void should_report_invalid_signature_without_blocking_payload_diagnostics() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix touch docs/webhook-diagnostic.md");

        mockMvc.perform(post("/api/github/webhook-diagnostics/evaluate-payload")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType("application/json")
                        .content(request("issue_comment", "diagnostic-delivery", "sha256=invalid", payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INVALID_SIGNATURE"))
                .andExpect(jsonPath("$.data.signatureStatus").value("INVALID"))
                .andExpect(jsonPath("$.data.validJson").value(true))
                .andExpect(jsonPath("$.data.repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data.nextAction", containsString("webhook secret")));

        assertThat(fixTaskService.listTasks()).isEmpty();
    }

    @Test
    void should_return_malformed_payload_diagnostic_instead_of_bad_request() throws Exception {
        mockMvc.perform(post("/api/github/webhook-diagnostics/evaluate-payload")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType("application/json")
                        .content(request("issue_comment", "diagnostic-delivery", null, "{not-json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("MALFORMED_PAYLOAD"))
                .andExpect(jsonPath("$.data.signatureStatus").value("NOT_PROVIDED"))
                .andExpect(jsonPath("$.data.validJson").value(false))
                .andExpect(jsonPath("$.data.supportedEvent").value(true))
                .andExpect(jsonPath("$.data.agentFixCommand").value(false))
                .andExpect(jsonPath("$.data.nextAction", containsString("GitHub delivery payload")));

        assertThat(fixTaskService.listTasks()).isEmpty();
    }

    private String request(String event, String deliveryId, String signature, String payload) throws Exception {
        return objectMapper.writeValueAsString(new Request(event, deliveryId, signature, payload));
    }

    private static String issueCommentPayload(String action, String commentBody) {
        return """
                {
                  "action": "%s",
                  "repository": {
                    "name": "hello-world",
                    "owner": {
                      "login": "octocat"
                    }
                  },
                  "issue": {
                    "number": 42
                  },
                  "comment": {
                    "id": 98765,
                    "body": "%s",
                    "user": {
                      "login": "alice"
                    }
                  }
                }
                """.formatted(action, commentBody);
    }

    private static String signature(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("test-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            hex.append(String.format("%02x", value));
        }
        return "sha256=" + hex;
    }

    private record Request(String event, String deliveryId, String signature, String payload) {
    }
}
