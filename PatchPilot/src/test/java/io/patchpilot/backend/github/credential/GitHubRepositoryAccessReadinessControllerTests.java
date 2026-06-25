package io.patchpilot.backend.github.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GitHubRepositoryAccessReadinessControllerTests {

    @Test
    void should_return_non_sensitive_repository_access_readiness() throws Exception {
        GitHubRepositoryAccessReadinessService service = new GitHubRepositoryAccessReadinessService(
                (owner, repository) -> new GitHubRepositoryAccessReadinessVo(
                        true,
                        true,
                        owner + "/" + repository,
                        "READY",
                        "GitHub token can read repository " + owner + "/" + repository + ".",
                        "main",
                        42,
                        Instant.parse("2026-06-25T04:00:00Z"),
                        "No action needed."
                )
        );
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/github/repository-access-readiness")
                        .queryParam("owner", "bingqin2")
                        .queryParam("repository", "PatchPilot")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tokenConfigured").value(true))
                .andExpect(jsonPath("$.data.repositoryConfigured").value(true))
                .andExpect(jsonPath("$.data.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.message").value("GitHub token can read repository bingqin2/PatchPilot."))
                .andExpect(jsonPath("$.data.defaultBranch").value("main"))
                .andExpect(jsonPath("$.data.latencyMs").value(42))
                .andExpect(jsonPath("$.data.checkedAt").value("2026-06-25T04:00:00Z"))
                .andExpect(jsonPath("$.data.operatorAction").value("No action needed."))
                .andExpect(content().string(not(containsString("github-token"))));
    }

    @Test
    void should_require_admin_token_for_repository_access_readiness() throws Exception {
        MockMvc mockMvc = mockMvc(new GitHubRepositoryAccessReadinessService((owner, repository) -> null));

        mockMvc.perform(get("/api/github/repository-access-readiness")
                        .queryParam("owner", "bingqin2")
                        .queryParam("repository", "PatchPilot"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc(GitHubRepositoryAccessReadinessService service) {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return MockMvcBuilders.standaloneSetup(new GitHubCredentialReadinessController(
                        new GitHubCredentialReadinessService(() -> null),
                        service
                ))
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .addFilters(new AdminApiSecurityFilter(properties, objectMapper))
                .build();
    }
}
