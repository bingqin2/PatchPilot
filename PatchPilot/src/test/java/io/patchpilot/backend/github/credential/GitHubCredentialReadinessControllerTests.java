package io.patchpilot.backend.github.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
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

    @Test
    void should_return_non_sensitive_github_publish_readiness() throws Exception {
        GitHubPublishReadinessService publishReadinessService = new GitHubPublishReadinessService(
                (owner, repository) -> new GitHubPublishReadinessVo(
                        "READY",
                        true,
                        true,
                        true,
                        owner + "/" + repository,
                        "main",
                        "GitHub publish path is ready for PatchPilot push and Pull Request creation.",
                        "Continue with the live /agent fix demo.",
                        "git push origin HEAD:<patchpilot-branch>",
                        "Read-only readiness probe: this endpoint does not run git push, does not create branches, does not create Pull Requests, does not write issue comments, and does not expose tokens.",
                        List.of(
                                new GitHubPublishReadinessCheckVo(
                                        "GitHub token",
                                        "READY",
                                        "GitHub API accepted the configured token.",
                                        "No action needed."
                                )
                        ),
                        List.of("Token configured: true"),
                        Instant.parse("2026-06-30T01:00:00Z")
                )
        );
        MockMvc mockMvc = mockMvc(
                new GitHubCredentialReadinessService(() -> null),
                new GitHubWebhookUrlReadinessService(() -> null),
                new GitHubWebhookSetupReadinessService(() -> null),
                publishReadinessService
        );

        mockMvc.perform(get("/api/github/publish-readiness")
                        .param("owner", "bingqin2")
                        .param("repository", "PatchPilot")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.publishReady").value(true))
                .andExpect(jsonPath("$.data.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.defaultBranch").value("main"))
                .andExpect(jsonPath("$.data.safePublishCommand").value("git push origin HEAD:<patchpilot-branch>"))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("does not run git push")))
                .andExpect(jsonPath("$.data.checks[0].name").value("GitHub token"))
                .andExpect(content().string(not(containsString("github-token"))));
    }

    @Test
    void should_return_non_sensitive_github_publish_permission_readiness() throws Exception {
        GitHubPublishPermissionReadinessService permissionReadinessService = new GitHubPublishPermissionReadinessService(
                (owner, repository) -> new GitHubPublishPermissionReadinessVo(
                        "READY",
                        true,
                        true,
                        true,
                        owner + "/" + repository,
                        "main",
                        true,
                        true,
                        true,
                        true,
                        "GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.",
                        "Continue with the live /agent fix demo.",
                        "Read-only permission probe: this endpoint does not run git push, does not create branches, does not create Pull Requests, does not write issue comments, and does not expose tokens.",
                        List.of(
                                new GitHubPublishPermissionReadinessCheckVo(
                                        "Branch push",
                                        "READY",
                                        "Token can publish PatchPilot branches.",
                                        "No action needed."
                                )
                        ),
                        List.of("Repository: " + owner + "/" + repository),
                        35,
                        Instant.parse("2026-06-30T06:00:00Z")
                )
        );
        MockMvc mockMvc = mockMvc(
                new GitHubCredentialReadinessService(() -> null),
                new GitHubWebhookUrlReadinessService(() -> null),
                new GitHubWebhookSetupReadinessService(() -> null),
                new GitHubPublishReadinessService((owner, repository) -> null),
                permissionReadinessService
        );

        mockMvc.perform(get("/api/github/publish-permission-readiness")
                        .param("owner", "bingqin2")
                        .param("repository", "PatchPilot")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.publishPermissionReady").value(true))
                .andExpect(jsonPath("$.data.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.defaultBranch").value("main"))
                .andExpect(jsonPath("$.data.canReadRepository").value(true))
                .andExpect(jsonPath("$.data.canPushBranches").value(true))
                .andExpect(jsonPath("$.data.canCreatePullRequests").value(true))
                .andExpect(jsonPath("$.data.issueFeedbackPermissionLikely").value(true))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("does not run git push")))
                .andExpect(jsonPath("$.data.permissionChecks[0].name").value("Branch push"))
                .andExpect(content().string(not(containsString("github-token"))));
    }

    @Test
    void should_return_non_sensitive_github_live_publish_preflight() throws Exception {
        GitHubLivePublishPreflightService livePublishPreflightService = new GitHubLivePublishPreflightService(
                (owner, repository) -> new GitHubLivePublishPreflightVo(
                        "NEEDS_ATTENTION",
                        false,
                        true,
                        true,
                        owner + "/" + repository,
                        "main",
                        List.of("patchpilot/task-1"),
                        List.of("https://github.com/bingqin2/PatchPilot/pull/4"),
                        "Live GitHub publish preflight found existing PatchPilot publish artifacts.",
                        "Close or merge stale PatchPilot Pull Requests and delete old patchpilot/* branches before the live demo.",
                        "Read-only live publish preflight: this endpoint does not run git push, does not create branches, does not open Pull Requests, does not write issue comments, and does not expose tokens.",
                        List.of(
                                new GitHubLivePublishPreflightCheckVo(
                                        "Open PatchPilot Pull Requests",
                                        "NEEDS_ATTENTION",
                                        "Found 1 open PatchPilot Pull Request.",
                                        "Close, merge, or intentionally keep the existing PatchPilot Pull Request before demo launch."
                                )
                        ),
                        List.of("Repository: " + owner + "/" + repository, "Open PatchPilot Pull Request count: 1"),
                        42,
                        Instant.parse("2026-06-30T09:00:00Z")
                )
        );
        MockMvc mockMvc = mockMvc(
                new GitHubCredentialReadinessService(() -> null),
                new GitHubWebhookUrlReadinessService(() -> null),
                new GitHubWebhookSetupReadinessService(() -> null),
                new GitHubPublishReadinessService((owner, repository) -> null),
                new GitHubPublishPermissionReadinessService((owner, repository) -> null),
                livePublishPreflightService
        );

        mockMvc.perform(get("/api/github/live-publish-preflight")
                        .param("owner", "bingqin2")
                        .param("repository", "PatchPilot")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NEEDS_ATTENTION"))
                .andExpect(jsonPath("$.data.livePublishReady").value(false))
                .andExpect(jsonPath("$.data.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.defaultBranch").value("main"))
                .andExpect(jsonPath("$.data.patchpilotBranches[0]").value("patchpilot/task-1"))
                .andExpect(jsonPath("$.data.openPatchpilotPullRequests[0]").value("https://github.com/bingqin2/PatchPilot/pull/4"))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("does not run git push")))
                .andExpect(jsonPath("$.data.sideEffectContract").value(containsString("does not open Pull Requests")))
                .andExpect(jsonPath("$.data.checks[0].name").value("Open PatchPilot Pull Requests"))
                .andExpect(content().string(not(containsString("github-token"))));
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
        return mockMvc(
                service,
                webhookUrlReadinessService,
                setupReadinessService,
                new GitHubPublishReadinessService((owner, repository) -> null),
                new GitHubPublishPermissionReadinessService((owner, repository) -> null),
                new GitHubLivePublishPreflightService((owner, repository) -> null)
        );
    }

    private static MockMvc mockMvc(
            GitHubCredentialReadinessService service,
            GitHubWebhookUrlReadinessService webhookUrlReadinessService,
            GitHubWebhookSetupReadinessService setupReadinessService,
            GitHubPublishReadinessService publishReadinessService
    ) {
        return mockMvc(
                service,
                webhookUrlReadinessService,
                setupReadinessService,
                publishReadinessService,
                new GitHubPublishPermissionReadinessService((owner, repository) -> null),
                new GitHubLivePublishPreflightService((owner, repository) -> null)
        );
    }

    private static MockMvc mockMvc(
            GitHubCredentialReadinessService service,
            GitHubWebhookUrlReadinessService webhookUrlReadinessService,
            GitHubWebhookSetupReadinessService setupReadinessService,
            GitHubPublishReadinessService publishReadinessService,
            GitHubPublishPermissionReadinessService publishPermissionReadinessService
    ) {
        return mockMvc(
                service,
                webhookUrlReadinessService,
                setupReadinessService,
                publishReadinessService,
                publishPermissionReadinessService,
                new GitHubLivePublishPreflightService((owner, repository) -> null)
        );
    }

    private static MockMvc mockMvc(
            GitHubCredentialReadinessService service,
            GitHubWebhookUrlReadinessService webhookUrlReadinessService,
            GitHubWebhookSetupReadinessService setupReadinessService,
            GitHubPublishReadinessService publishReadinessService,
            GitHubPublishPermissionReadinessService publishPermissionReadinessService,
            GitHubLivePublishPreflightService livePublishPreflightService
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
                        setupReadinessService,
                        publishReadinessService,
                        publishPermissionReadinessService,
                        livePublishPreflightService
                ))
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .addFilters(new AdminApiSecurityFilter(properties, objectMapper))
                .build();
    }
}
