package io.patchpilot.backend.github.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueCommentException;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.client.domain.UpdateIssueCommentCommand;
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
public class GitHubIssueCommentClient {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private static final String ISSUE_COMMENT_FORBIDDEN_HINT = "Check PATCHPILOT_GITHUB_TOKEN permissions: "
            + "fine-grained tokens need repository `Issues: Read and write` permission. "
            + "Restart or reload the backend after changing the token.";

    private final HttpClient httpClient;
    private final GitHubProperties gitHubProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public GitHubIssueCommentClient(GitHubProperties gitHubProperties) {
        this(HttpClient.newHttpClient(), gitHubProperties, new ObjectMapper());
    }

    GitHubIssueCommentClient(HttpClient httpClient, GitHubProperties gitHubProperties) {
        this(httpClient, gitHubProperties, new ObjectMapper());
    }

    GitHubIssueCommentClient(HttpClient httpClient, GitHubProperties gitHubProperties, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.gitHubProperties = gitHubProperties;
        this.objectMapper = objectMapper;
    }

    public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
        String token = token();
        if (!StringUtils.hasText(token)) {
            throw new GitHubIssueCommentException("GitHub token is required to create Issue comments");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(issueCommentsUri(command))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody(command)))
                .build();

        HttpResponse<String> response = send(request);
        if (response.statusCode() != 201) {
            throw new GitHubIssueCommentException(httpFailureMessage("GitHub issue comment creation failed", response.statusCode()));
        }
        return issueCommentResult(response.body());
    }

    public IssueCommentResult updateIssueComment(UpdateIssueCommentCommand command) {
        String token = token();
        if (!StringUtils.hasText(token)) {
            throw new GitHubIssueCommentException("GitHub token is required to update Issue comments");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(issueCommentUri(command))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(updateRequestBody(command)))
                .build();

        HttpResponse<String> response = send(request, "GitHub issue comment update failed");
        if (response.statusCode() != 200) {
            throw new GitHubIssueCommentException(httpFailureMessage("GitHub issue comment update failed", response.statusCode()));
        }
        return issueCommentResult(response.body());
    }

    private URI issueCommentsUri(CreateIssueCommentCommand command) {
        return URI.create(GITHUB_API_BASE_URL
                + "/repos/" + command.owner()
                + "/" + command.repository()
                + "/issues/" + command.issueNumber()
                + "/comments");
    }

    private URI issueCommentUri(UpdateIssueCommentCommand command) {
        return URI.create(GITHUB_API_BASE_URL
                + "/repos/" + command.owner()
                + "/" + command.repository()
                + "/issues/comments/" + command.commentId());
    }

    private String requestBody(CreateIssueCommentCommand command) {
        try {
            return objectMapper.writeValueAsString(Map.of("body", command.body()));
        } catch (JsonProcessingException exception) {
            throw new GitHubIssueCommentException("Failed to serialize GitHub issue comment request", exception);
        }
    }

    private HttpResponse<String> send(HttpRequest request) {
        return send(request, "GitHub issue comment creation failed");
    }

    private String updateRequestBody(UpdateIssueCommentCommand command) {
        try {
            return objectMapper.writeValueAsString(Map.of("body", command.body()));
        } catch (JsonProcessingException exception) {
            throw new GitHubIssueCommentException("Failed to serialize GitHub issue comment request", exception);
        }
    }

    private HttpResponse<String> send(HttpRequest request, String failureMessage) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            throw new GitHubIssueCommentException(failureMessage, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubIssueCommentException(failureMessage + " interrupted", exception);
        }
    }

    private String httpFailureMessage(String operation, int statusCode) {
        String message = operation + ": HTTP " + statusCode;
        if (statusCode == 403) {
            return message + ". " + ISSUE_COMMENT_FORBIDDEN_HINT;
        }
        return message;
    }

    private IssueCommentResult issueCommentResult(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode id = root.get("id");
            JsonNode htmlUrl = root.get("html_url");
            if (id == null || !id.canConvertToLong()) {
                throw new GitHubIssueCommentException("GitHub issue comment response did not include id");
            }
            if (htmlUrl == null || !htmlUrl.isTextual() || !StringUtils.hasText(htmlUrl.asText())) {
                throw new GitHubIssueCommentException("GitHub issue comment response did not include html_url");
            }
            return new IssueCommentResult(id.asLong(), htmlUrl.asText());
        } catch (JsonProcessingException exception) {
            throw new GitHubIssueCommentException("Failed to parse GitHub issue comment response", exception);
        }
    }

    private String token() {
        return StringUtils.hasText(gitHubProperties.getToken()) ? gitHubProperties.getToken().trim()
                : "";
    }
}
