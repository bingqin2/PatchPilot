package io.patchpilot.backend.github.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GitHubCredentialReadinessControllerTests {

    @Test
    void should_return_non_sensitive_github_credential_readiness() throws Exception {
        GitHubCredentialReadinessService service = new GitHubCredentialReadinessService(
                () -> new GitHubCredentialReadinessVo(
                        true,
                        "READY",
                        "GitHub API accepted the configured token.",
                        31,
                        Instant.parse("2026-06-25T03:00:00Z"),
                        "No action needed."
                )
        );
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/github/credential-readiness")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tokenConfigured").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.message").value("GitHub API accepted the configured token."))
                .andExpect(jsonPath("$.data.latencyMs").value(31))
                .andExpect(jsonPath("$.data.checkedAt").value("2026-06-25T03:00:00Z"))
                .andExpect(jsonPath("$.data.operatorAction").value("No action needed."))
                .andExpect(content().string(not(containsString("github-token"))));
    }

    @Test
    void should_require_admin_token_for_github_credential_readiness() throws Exception {
        MockMvc mockMvc = mockMvc(new GitHubCredentialReadinessService(() -> null));

        mockMvc.perform(get("/api/github/credential-readiness"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    @Test
    void should_return_non_sensitive_github_webhook_url_readiness() throws Exception {
        GitHubCredentialReadinessService credentialService = new GitHubCredentialReadinessService(() -> null);
        GitHubWebhookUrlReadinessService webhookUrlReadinessService = new GitHubWebhookUrlReadinessService(
                () -> new GitHubWebhookUrlReadinessVo(
                        true,
                        "READY",
                        "https://demo.trycloudflare.com",
                        "https://demo.trycloudflare.com/api/github/webhook",
                        "https://demo.trycloudflare.com/health",
                        "Configured public webhook URL reaches PatchPilot health.",
                        44,
                        Instant.parse("2026-06-27T01:00:00Z"),
                        "Use the payload URL in the GitHub webhook settings."
                )
        );
        MockMvc mockMvc = mockMvc(credentialService, webhookUrlReadinessService);

        mockMvc.perform(get("/api/github/webhook-url-readiness")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.publicBaseUrlConfigured").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.publicBaseUrl").value("https://demo.trycloudflare.com"))
                .andExpect(jsonPath("$.data.payloadUrl").value("https://demo.trycloudflare.com/api/github/webhook"))
                .andExpect(jsonPath("$.data.healthUrl").value("https://demo.trycloudflare.com/health"))
                .andExpect(jsonPath("$.data.message").value("Configured public webhook URL reaches PatchPilot health."))
                .andExpect(jsonPath("$.data.latencyMs").value(44))
                .andExpect(jsonPath("$.data.checkedAt").value("2026-06-27T01:00:00Z"))
                .andExpect(jsonPath("$.data.operatorAction").value("Use the payload URL in the GitHub webhook settings."))
                .andExpect(content().string(not(containsString("webhook-secret"))));
    }

    @Test
    void should_return_non_sensitive_github_webhook_setup_readiness() throws Exception {
        GitHubCredentialReadinessService credentialService = new GitHubCredentialReadinessService(() -> null);
        GitHubWebhookSetupReadinessService setupReadinessService = new GitHubWebhookSetupReadinessService(
                () -> new GitHubWebhookSetupReadinessVo(
                        "READY",
                        true,
                        true,
                        "https://demo.trycloudflare.com",
                        "https://demo.trycloudflare.com/api/github/webhook",
                        "https://demo.trycloudflare.com/health",
                        "TASK_CREATED",
                        "delivery-1",
                        false,
                        "Webhook setup is ready for GitHub deliveries.",
                        List.of("Use the payload URL in GitHub Webhooks and continue the live demo."),
                        Instant.parse("2026-06-27T02:00:00Z"),
                        "# PatchPilot Webhook Setup Readiness\n\n- Status: `READY`"
                )
        );
        MockMvc mockMvc = mockMvc(credentialService, new GitHubWebhookUrlReadinessService(() -> null), setupReadinessService);

        mockMvc.perform(get("/api/github/webhook-setup-readiness")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.secretConfigured").value(true))
                .andExpect(jsonPath("$.data.publicUrlReady").value(true))
                .andExpect(jsonPath("$.data.payloadUrl").value("https://demo.trycloudflare.com/api/github/webhook"))
                .andExpect(jsonPath("$.data.latestDeliveryStatus").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data.redeliveryRecommended").value(false))
                .andExpect(jsonPath("$.data.nextActions[0]").value("Use the payload URL in GitHub Webhooks and continue the live demo."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("# PatchPilot Webhook Setup Readiness")))
                .andExpect(content().string(not(containsString("webhook-secret"))));
    }

    private static MockMvc mockMvc(GitHubCredentialReadinessService service) {
        return mockMvc(service, new GitHubWebhookUrlReadinessService(() -> null));
    }

    private static MockMvc mockMvc(
            GitHubCredentialReadinessService service,
            GitHubWebhookUrlReadinessService webhookUrlReadinessService
    ) {
        return mockMvc(service, webhookUrlReadinessService, new GitHubWebhookSetupReadinessService(() -> null));
    }

    private static MockMvc mockMvc(
            GitHubCredentialReadinessService service,
            GitHubWebhookUrlReadinessService webhookUrlReadinessService,
            GitHubWebhookSetupReadinessService setupReadinessService
    ) {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return MockMvcBuilders.standaloneSetup(new GitHubCredentialReadinessController(
                        service,
                        new GitHubRepositoryAccessReadinessService((owner, repository) -> null),
                        webhookUrlReadinessService,
                        setupReadinessService
                ))
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .addFilters(new AdminApiSecurityFilter(properties, objectMapper))
                .build();
    }
}
