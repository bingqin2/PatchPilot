package io.patchpilot.backend.agent.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.provider.domain.ModelProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
class OpenAiCompatibleModelProviderHealthProbe implements ModelProviderHealthProbe {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    OpenAiCompatibleModelProviderHealthProbe(ObjectMapper objectMapper) {
        this(HttpClient.newHttpClient(), objectMapper);
    }

    OpenAiCompatibleModelProviderHealthProbe(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String check(String baseUrl, String apiKey, String model) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are PatchPilot's model provider health probe."),
                            Map.of("role", "user", "content", "Reply with exactly: ok")
                    ),
                    "max_tokens", 4,
                    "temperature", 0
            ));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizedBaseUrl(baseUrl) + "/chat/completions"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ModelProviderException("Model provider health probe failed: HTTP " + response.statusCode());
            }
            return assistantContent(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ModelProviderException("Model provider health probe was interrupted");
        } catch (IOException exception) {
            throw new ModelProviderException("Model provider health probe failed: " + exception.getMessage());
        }
    }

    private String assistantContent(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.at("/choices/0/message/content");
        if (content.isMissingNode() || content.asText().isBlank()) {
            throw new ModelProviderException("Model provider health probe returned an empty response");
        }
        return content.asText();
    }

    private static String normalizedBaseUrl(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }
}
