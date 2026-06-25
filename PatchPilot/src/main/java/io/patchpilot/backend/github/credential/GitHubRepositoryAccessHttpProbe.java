package io.patchpilot.backend.github.credential;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class GitHubRepositoryAccessHttpProbe implements GitHubRepositoryAccessProbe {

    private static final String REPOSITORY_API_PREFIX = "https://api.github.com/repos/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public GitHubRepositoryAccessHttpProbe() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    GitHubRepositoryAccessHttpProbe(HttpClient httpClient) {
        this(httpClient, new ObjectMapper());
    }

    GitHubRepositoryAccessHttpProbe(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String check(String token, String owner, String repository) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(repositoryUri(owner, repository))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new GitHubCredentialReadinessException("GitHub repository access probe failed: HTTP " + response.statusCode());
            }
            return defaultBranch(response.body());
        } catch (IOException exception) {
            throw new GitHubCredentialReadinessException("GitHub repository access probe failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubCredentialReadinessException("GitHub repository access probe interrupted", exception);
        }
    }

    private String defaultBranch(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String defaultBranch = root.path("default_branch").asText("");
            if (!StringUtils.hasText(defaultBranch)) {
                throw new GitHubCredentialReadinessException("GitHub repository access probe failed: default branch missing");
            }
            return defaultBranch.trim();
        } catch (IOException exception) {
            throw new GitHubCredentialReadinessException("GitHub repository access probe failed: invalid response", exception);
        }
    }

    private static URI repositoryUri(String owner, String repository) {
        return URI.create(REPOSITORY_API_PREFIX + encode(owner) + "/" + encode(repository));
    }

    private static String encode(String pathSegment) {
        return URLEncoder.encode(pathSegment, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
