package io.patchpilot.backend.github.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GitHubWebhookUrlHttpProbe implements GitHubWebhookUrlProbe {

    private final HttpClient httpClient;

    @Autowired
    public GitHubWebhookUrlHttpProbe() {
        this(HttpClient.newHttpClient());
    }

    GitHubWebhookUrlHttpProbe(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public WebhookUrlProbeResult check(String healthUrl) {
        long startedAt = System.currentTimeMillis();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long latencyMs = Math.max(0, System.currentTimeMillis() - startedAt);
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new WebhookUrlProbeResult(true, "Backend health endpoint returned 2xx.", latencyMs);
            }
            return new WebhookUrlProbeResult(false, "HTTP " + response.statusCode() + " from public URL.", latencyMs);
        } catch (IOException exception) {
            long latencyMs = Math.max(0, System.currentTimeMillis() - startedAt);
            return new WebhookUrlProbeResult(false, "Public webhook URL probe failed: " + exception.getMessage(), latencyMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            long latencyMs = Math.max(0, System.currentTimeMillis() - startedAt);
            return new WebhookUrlProbeResult(false, "Public webhook URL probe interrupted.", latencyMs);
        } catch (IllegalArgumentException exception) {
            long latencyMs = Math.max(0, System.currentTimeMillis() - startedAt);
            return new WebhookUrlProbeResult(false, "Public webhook URL is invalid.", latencyMs);
        }
    }
}
