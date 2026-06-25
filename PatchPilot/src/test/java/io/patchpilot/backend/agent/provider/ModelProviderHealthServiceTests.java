package io.patchpilot.backend.agent.provider;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.agent.provider.domain.ModelProviderHealthVo;
import io.patchpilot.backend.agent.provider.domain.ModelProviderException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class ModelProviderHealthServiceTests {

    @Test
    void should_report_ready_when_openai_compatible_provider_returns_expected_health_content() {
        ModelProviderHealthService service = new ModelProviderHealthService(
                properties("test-agent-key"),
                (baseUrl, apiKey, model) -> {
                    assertThat(baseUrl).isEqualTo("https://api.example.test/v1");
                    assertThat(apiKey).isEqualTo("test-agent-key");
                    assertThat(model).isEqualTo("gpt-5.5");
                    return "ok";
                },
                () -> Instant.parse("2026-06-25T02:00:00Z"),
                new FixedTicker(100, 250)
        );

        ModelProviderHealthVo health = service.getHealth();

        assertThat(health.provider()).isEqualTo("openai-compatible");
        assertThat(health.model()).isEqualTo("gpt-5.5");
        assertThat(health.baseUrlConfigured()).isTrue();
        assertThat(health.apiKeyConfigured()).isTrue();
        assertThat(health.status()).isEqualTo("READY");
        assertThat(health.message()).isEqualTo("Model provider responded to the health probe.");
        assertThat(health.latencyMs()).isEqualTo(150);
        assertThat(health.checkedAt()).isEqualTo(Instant.parse("2026-06-25T02:00:00Z"));
        assertThat(health.operatorAction()).isEqualTo("No action needed.");
    }

    @Test
    void should_report_attention_without_sending_probe_when_api_key_is_missing() {
        AtomicBoolean called = new AtomicBoolean(false);
        ModelProviderHealthService service = new ModelProviderHealthService(
                properties(" "),
                (baseUrl, apiKey, model) -> {
                    called.set(true);
                    return "ok";
                },
                () -> Instant.parse("2026-06-25T02:00:00Z"),
                new FixedTicker(100, 250)
        );

        ModelProviderHealthVo health = service.getHealth();

        assertThat(called).isFalse();
        assertThat(health.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(health.apiKeyConfigured()).isFalse();
        assertThat(health.message()).isEqualTo("Model provider API key is not configured.");
        assertThat(health.operatorAction()).isEqualTo("Configure PATCHPILOT_AGENT_API_KEY and restart the backend.");
        assertThat(health.latencyMs()).isZero();
    }

    @Test
    void should_report_attention_when_probe_fails() {
        ModelProviderHealthService service = new ModelProviderHealthService(
                properties("test-agent-key"),
                (baseUrl, apiKey, model) -> {
                    throw new ModelProviderException("Model provider health probe failed: HTTP 401");
                },
                () -> Instant.parse("2026-06-25T02:00:00Z"),
                new FixedTicker(100, 125)
        );

        ModelProviderHealthVo health = service.getHealth();

        assertThat(health.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(health.message()).isEqualTo("Model provider health probe failed: HTTP 401");
        assertThat(health.operatorAction()).isEqualTo("Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.");
        assertThat(health.latencyMs()).isEqualTo(25);
    }

    private static AgentProperties properties(String apiKey) {
        AgentProperties properties = new AgentProperties();
        properties.setProvider("openai-compatible");
        properties.setModel("gpt-5.5");
        properties.setBaseUrl("https://api.example.test/v1");
        properties.setApiKey(apiKey);
        return properties;
    }

    private static final class FixedTicker implements java.util.function.LongSupplier {

        private final long[] values;
        private int index;

        private FixedTicker(long... values) {
            this.values = values;
        }

        @Override
        public long getAsLong() {
            return values[Math.min(index++, values.length - 1)];
        }
    }
}
