package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GitHubCredentialHttpProbe implements GitHubCredentialProbe {

    private static final URI USER_URI = URI.create("https://api.github.com/user");

    private final HttpClient httpClient;

    @Autowired
    public GitHubCredentialHttpProbe() {
        this(HttpClient.newHttpClient());
    }

    GitHubCredentialHttpProbe(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void check(String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(USER_URI)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new GitHubCredentialReadinessException("GitHub credential probe failed: HTTP " + response.statusCode());
            }
        } catch (IOException exception) {
            throw new GitHubCredentialReadinessException("GitHub credential probe failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubCredentialReadinessException("GitHub credential probe interrupted", exception);
        }
    }
}
