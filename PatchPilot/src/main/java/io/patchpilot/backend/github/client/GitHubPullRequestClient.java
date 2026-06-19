package io.patchpilot.backend.github.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.GitHubPullRequestException;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class GitHubPullRequestClient {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final HttpClient httpClient;
    private final GitHubProperties gitHubProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public GitHubPullRequestClient(GitHubProperties gitHubProperties) {
        this(HttpClient.newHttpClient(), gitHubProperties, new ObjectMapper());
    }

    GitHubPullRequestClient(HttpClient httpClient, GitHubProperties gitHubProperties) {
        this(httpClient, gitHubProperties, new ObjectMapper());
    }

    GitHubPullRequestClient(HttpClient httpClient, GitHubProperties gitHubProperties, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.gitHubProperties = gitHubProperties;
        this.objectMapper = objectMapper;
    }

    public PullRequestResult createPullRequest(CreatePullRequestCommand command) {
        String token = token();
        if (!StringUtils.hasText(token)) {
            throw new GitHubPullRequestException("GitHub token is required to create Pull Requests");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(pullRequestsUri(command))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody(command)))
                .build();

        HttpResponse<String> response = send(request);
        if (response.statusCode() != 201) {
            throw new GitHubPullRequestException("GitHub pull request creation failed: HTTP " + response.statusCode());
        }
        return new PullRequestResult(htmlUrl(response.body()));
    }

    private URI pullRequestsUri(CreatePullRequestCommand command) {
        return URI.create(GITHUB_API_BASE_URL + "/repos/" + command.owner() + "/" + command.repository() + "/pulls");
    }

    private String requestBody(CreatePullRequestCommand command) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "title", command.title(),
                    "head", command.head(),
                    "base", command.base(),
                    "body", command.body()
            ));
        } catch (JsonProcessingException exception) {
            throw new GitHubPullRequestException("Failed to serialize GitHub pull request request", exception);
        }
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            throw new GitHubPullRequestException("GitHub pull request creation failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubPullRequestException("GitHub pull request creation interrupted", exception);
        }
    }

    private String htmlUrl(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode htmlUrl = root.get("html_url");
            if (htmlUrl == null || !htmlUrl.isTextual() || !StringUtils.hasText(htmlUrl.asText())) {
                throw new GitHubPullRequestException("GitHub pull request response did not include html_url");
            }
            return htmlUrl.asText();
        } catch (JsonProcessingException exception) {
            throw new GitHubPullRequestException("Failed to parse GitHub pull request response", exception);
        }
    }

    private String token() {
        return StringUtils.hasText(gitHubProperties.getToken()) ? gitHubProperties.getToken().trim()
                : "";
    }
}
