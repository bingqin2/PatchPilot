package io.patchpilot.backend.agent.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.patchpilot.backend.agent.provider.domain.ModelProviderHealthVo;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModelProviderHealthControllerTests {

    @Test
    void should_return_non_sensitive_model_provider_health() throws Exception {
        ModelProviderHealthService service = new ModelProviderHealthService(
                () -> new ModelProviderHealthVo(
                        "openai-compatible",
                        "gpt-5.5",
                        true,
                        true,
                        "READY",
                        "Model provider responded to the health probe.",
                        43,
                        Instant.parse("2026-06-25T02:00:00Z"),
                        "No action needed."
                )
        );
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/model-provider/health")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.provider").value("openai-compatible"))
                .andExpect(jsonPath("$.data.model").value("gpt-5.5"))
                .andExpect(jsonPath("$.data.baseUrlConfigured").value(true))
                .andExpect(jsonPath("$.data.apiKeyConfigured").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.message").value("Model provider responded to the health probe."))
                .andExpect(jsonPath("$.data.latencyMs").value(43))
                .andExpect(jsonPath("$.data.checkedAt").value("2026-06-25T02:00:00Z"))
                .andExpect(jsonPath("$.data.operatorAction").value("No action needed."))
                .andExpect(content().string(not(containsString("test-agent-key"))));
    }

    @Test
    void should_require_admin_token_for_model_provider_health() throws Exception {
        MockMvc mockMvc = mockMvc(new ModelProviderHealthService(() -> null));

        mockMvc.perform(get("/api/model-provider/health"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc(ModelProviderHealthService service) {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return MockMvcBuilders.standaloneSetup(new ModelProviderHealthController(service))
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .addFilters(new AdminApiSecurityFilter(properties, objectMapper))
                .build();
    }
}
