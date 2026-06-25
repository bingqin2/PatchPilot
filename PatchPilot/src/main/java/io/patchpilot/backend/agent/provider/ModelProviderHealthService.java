package io.patchpilot.backend.agent.provider;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.agent.provider.domain.ModelProviderException;
import io.patchpilot.backend.agent.provider.domain.ModelProviderHealthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Service
public class ModelProviderHealthService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";

    private static final String OPENAI_COMPATIBLE_PROVIDER = "openai-compatible";
    private static final String CHECK_PROVIDER_CONFIGURATION_ACTION =
            "Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.";

    private final Supplier<ModelProviderHealthVo> healthSupplier;

    @Autowired
    public ModelProviderHealthService(AgentProperties agentProperties, ModelProviderHealthProbe probe) {
        this(agentProperties, probe, Instant::now, System::currentTimeMillis);
    }

    ModelProviderHealthService(
            AgentProperties agentProperties,
            ModelProviderHealthProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        this(() -> buildHealth(agentProperties, probe, clock, ticker));
    }

    ModelProviderHealthService(Supplier<ModelProviderHealthVo> healthSupplier) {
        this.healthSupplier = healthSupplier;
    }

    public ModelProviderHealthVo getHealth() {
        return healthSupplier.get();
    }

    private static ModelProviderHealthVo buildHealth(
            AgentProperties agentProperties,
            ModelProviderHealthProbe probe,
            Supplier<Instant> clock,
            LongSupplier ticker
    ) {
        Instant checkedAt = clock.get();
        String provider = textOrBlank(agentProperties.getProvider());
        String model = textOrBlank(agentProperties.getModel());
        String baseUrl = textOrBlank(agentProperties.getBaseUrl());
        String apiKey = textOrBlank(agentProperties.getApiKey());

        if (!hasText(provider) || !OPENAI_COMPATIBLE_PROVIDER.equals(provider.toLowerCase(Locale.ROOT))) {
            return attention(
                    provider,
                    model,
                    hasText(baseUrl),
                    hasText(apiKey),
                    "Unsupported model provider: " + (hasText(provider) ? provider : "not configured") + ".",
                    "Set PATCHPILOT_AGENT_PROVIDER=openai-compatible before running PatchPilot.",
                    0,
                    checkedAt
            );
        }
        if (!hasText(model)) {
            return attention(provider, model, hasText(baseUrl), hasText(apiKey),
                    "Model provider model is not configured.",
                    "Configure PATCHPILOT_AGENT_MODEL and restart the backend.",
                    0,
                    checkedAt);
        }
        if (!hasText(baseUrl)) {
            return attention(provider, model, false, hasText(apiKey),
                    "Model provider base URL is not configured.",
                    "Configure PATCHPILOT_AGENT_BASE_URL and restart the backend.",
                    0,
                    checkedAt);
        }
        if (!hasText(apiKey)) {
            return attention(provider, model, true, false,
                    "Model provider API key is not configured.",
                    "Configure PATCHPILOT_AGENT_API_KEY and restart the backend.",
                    0,
                    checkedAt);
        }

        long startedAt = ticker.getAsLong();
        try {
            String content = probe.check(baseUrl, apiKey, model);
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            if ("ok".equalsIgnoreCase(content.trim())) {
                return new ModelProviderHealthVo(
                        provider,
                        model,
                        true,
                        true,
                        READY,
                        "Model provider responded to the health probe.",
                        latencyMs,
                        checkedAt,
                        "No action needed."
                );
            }
            return attention(provider, model, true, true,
                    "Model provider responded with unexpected health probe content.",
                    CHECK_PROVIDER_CONFIGURATION_ACTION,
                    latencyMs,
                    checkedAt);
        } catch (ModelProviderException exception) {
            long latencyMs = Math.max(0, ticker.getAsLong() - startedAt);
            return attention(provider, model, true, true,
                    exception.getMessage(),
                    CHECK_PROVIDER_CONFIGURATION_ACTION,
                    latencyMs,
                    checkedAt);
        }
    }

    private static ModelProviderHealthVo attention(
            String provider,
            String model,
            boolean baseUrlConfigured,
            boolean apiKeyConfigured,
            String message,
            String operatorAction,
            long latencyMs,
            Instant checkedAt
    ) {
        return new ModelProviderHealthVo(
                provider,
                model,
                baseUrlConfigured,
                apiKeyConfigured,
                NEEDS_ATTENTION,
                message,
                latencyMs,
                checkedAt,
                operatorAction
        );
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String textOrBlank(String value) {
        return value == null ? "" : value;
    }
}
