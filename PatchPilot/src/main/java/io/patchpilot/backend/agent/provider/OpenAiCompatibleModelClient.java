package io.patchpilot.backend.agent.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.agent.provider.domain.ModelProviderException;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiCompatibleModelClient implements ModelProviderClient {

    private static final int SUMMARY_LIMIT = 512;

    private final HttpClient httpClient;
    private final AgentProperties agentProperties;
    private final FixTaskModelCallService modelCallService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenAiCompatibleModelClient(AgentProperties agentProperties, FixTaskModelCallService modelCallService) {
        this(HttpClient.newHttpClient(), agentProperties, modelCallService, new ObjectMapper());
    }

    OpenAiCompatibleModelClient(
            HttpClient httpClient,
            AgentProperties agentProperties,
            FixTaskModelCallService modelCallService
    ) {
        this(httpClient, agentProperties, modelCallService, new ObjectMapper());
    }

    OpenAiCompatibleModelClient(
            HttpClient httpClient,
            AgentProperties agentProperties,
            FixTaskModelCallService modelCallService,
            ObjectMapper objectMapper
    ) {
        this.httpClient = httpClient;
        this.agentProperties = agentProperties;
        this.modelCallService = modelCallService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ModelProviderResponse complete(ModelProviderRequest request) {
        String apiKey = apiKey();
        if (!StringUtils.hasText(apiKey)) {
            throw new ModelProviderException("Model provider API key is required");
        }

        Instant startedAt = Instant.now();
        try {
            HttpResponse<String> response = send(httpRequest(apiKey, request));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ModelProviderException("Model provider request failed: HTTP " + response.statusCode());
            }
            ModelProviderResponse providerResponse = parseResponse(response.body());
            recordSuccess(request, providerResponse, startedAt, Instant.now());
            return providerResponse;
        } catch (ModelProviderException exception) {
            recordFailure(request, exception.getMessage(), startedAt, Instant.now());
            throw exception;
        } catch (RuntimeException exception) {
            recordFailure(request, exception.getMessage(), startedAt, Instant.now());
            throw exception;
        }
    }

    private HttpRequest httpRequest(String apiKey, ModelProviderRequest request) {
        return HttpRequest.newBuilder()
                .uri(chatCompletionsUri())
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody(request)))
                .build();
    }

    private URI chatCompletionsUri() {
        return URI.create(baseUrl() + "/chat/completions");
    }

    private String requestBody(ModelProviderRequest request) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "model", model(),
                    "messages", List.of(
                            Map.of("role", "system", "content", request.systemPrompt()),
                            Map.of("role", "user", "content", request.userPrompt())
                    )
            ));
        } catch (JsonProcessingException exception) {
            throw new ModelProviderException("Failed to serialize model provider request", exception);
        }
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            throw new ModelProviderException("Model provider request failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ModelProviderException("Model provider request interrupted", exception);
        }
    }

    private ModelProviderResponse parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = assistantContent(root);
            int promptTokens = intValue(root.at("/usage/prompt_tokens"));
            int completionTokens = intValue(root.at("/usage/completion_tokens"));
            int totalTokens = intValue(root.at("/usage/total_tokens"));
            return new ModelProviderResponse(content, promptTokens, completionTokens, totalTokens);
        } catch (JsonProcessingException exception) {
            throw new ModelProviderException("Failed to parse model provider response", exception);
        }
    }

    private String assistantContent(JsonNode root) {
        JsonNode content = root.at("/choices/0/message/content");
        if (!content.isTextual()) {
            throw new ModelProviderException("Model provider response did not include assistant content");
        }
        return content.asText();
    }

    private int intValue(JsonNode node) {
        return node.canConvertToInt() ? node.asInt() : 0;
    }

    private void recordSuccess(
            ModelProviderRequest request,
            ModelProviderResponse response,
            Instant startedAt,
            Instant finishedAt
    ) {
        modelCallService.recordModelCall(
                request.taskId(),
                provider(),
                model(),
                summary(request.userPrompt()),
                summary(response.content()),
                response.promptTokens(),
                response.completionTokens(),
                true,
                null,
                startedAt,
                finishedAt
        );
    }

    private void recordFailure(ModelProviderRequest request, String errorMessage, Instant startedAt, Instant finishedAt) {
        modelCallService.recordModelCall(
                request.taskId(),
                provider(),
                model(),
                summary(request.userPrompt()),
                null,
                0,
                0,
                false,
                errorMessage,
                startedAt,
                finishedAt
        );
    }

    private String summary(String value) {
        if (value == null || value.length() <= SUMMARY_LIMIT) {
            return value;
        }
        return value.substring(0, SUMMARY_LIMIT);
    }

    private String provider() {
        return agentProperties.getProvider() == null ? "openai-compatible" : agentProperties.getProvider().trim();
    }

    private String model() {
        return trimmedOrEmpty(agentProperties.getModel());
    }

    private String baseUrl() {
        String baseUrl = trimmedOrEmpty(agentProperties.getBaseUrl());
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private String apiKey() {
        return trimmedOrEmpty(agentProperties.getApiKey());
    }

    private static String trimmedOrEmpty(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }
}
