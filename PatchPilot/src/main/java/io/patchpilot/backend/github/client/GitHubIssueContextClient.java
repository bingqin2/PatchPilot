package io.patchpilot.backend.github.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextComment;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextException;
import io.patchpilot.backend.github.config.GitHubProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class GitHubIssueContextClient {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final HttpClient httpClient;
    private final GitHubProperties gitHubProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public GitHubIssueContextClient(GitHubProperties gitHubProperties) {
        this(HttpClient.newHttpClient(), gitHubProperties, new ObjectMapper());
    }

    GitHubIssueContextClient(HttpClient httpClient, GitHubProperties gitHubProperties) {
        this(httpClient, gitHubProperties, new ObjectMapper());
    }

    GitHubIssueContextClient(HttpClient httpClient, GitHubProperties gitHubProperties, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.gitHubProperties = gitHubProperties;
        this.objectMapper = objectMapper;
    }

    public GitHubIssueContext getIssueContext(GetIssueContextCommand command) {
        String token = token();
        if (!StringUtils.hasText(token)) {
            throw new GitHubIssueContextException("GitHub token is required to read Issue context");
        }

        JsonNode issue = readJson(send(get(issueUri(command), token), "GitHub issue context read failed"));
        JsonNode comments = readJson(send(get(issueCommentsUri(command), token), "GitHub issue comments read failed"));
        return new GitHubIssueContext(
                requiredText(issue, "title"),
                optionalText(issue, "body"),
                requiredText(issue, "html_url"),
                comments(comments)
        );
    }

    private HttpRequest get(URI uri, String token) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();
    }

    private URI issueUri(GetIssueContextCommand command) {
        return URI.create(GITHUB_API_BASE_URL
                + "/repos/" + command.owner()
                + "/" + command.repository()
                + "/issues/" + command.issueNumber());
    }

    private URI issueCommentsUri(GetIssueContextCommand command) {
        int commentLimit = Math.max(1, command.commentLimit());
        return URI.create(GITHUB_API_BASE_URL
                + "/repos/" + command.owner()
                + "/" + command.repository()
                + "/issues/" + command.issueNumber()
                + "/comments?per_page=" + commentLimit);
    }

    private String send(HttpRequest request, String failureMessage) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new GitHubIssueContextException(failureMessage + ": HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException exception) {
            throw new GitHubIssueContextException(failureMessage, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubIssueContextException(failureMessage + " interrupted", exception);
        }
    }

    private JsonNode readJson(String responseBody) {
        try {
            return objectMapper.readTree(responseBody);
        } catch (JsonProcessingException exception) {
            throw new GitHubIssueContextException("Failed to parse GitHub issue context response", exception);
        }
    }

    private List<GitHubIssueContextComment> comments(JsonNode comments) {
        if (!comments.isArray()) {
            throw new GitHubIssueContextException("GitHub issue comments response was not an array");
        }
        List<GitHubIssueContextComment> parsed = new ArrayList<>();
        for (JsonNode comment : comments) {
            parsed.add(new GitHubIssueContextComment(
                    requiredLong(comment, "id"),
                    requiredText(comment.path("user"), "login"),
                    optionalText(comment, "body"),
                    requiredText(comment, "created_at"),
                    requiredText(comment, "html_url")
            ));
        }
        return List.copyOf(parsed);
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new GitHubIssueContextException("GitHub issue context response missing required field: " + fieldName);
        }
        return node.asText();
    }

    private long requiredLong(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.canConvertToLong()) {
            throw new GitHubIssueContextException("GitHub issue context response missing required field: " + fieldName);
        }
        return node.asLong();
    }

    private String optionalText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || node.isNull()) {
            return "";
        }
        return node.isTextual() ? node.asText() : "";
    }

    private String token() {
        return StringUtils.hasText(gitHubProperties.getToken()) ? gitHubProperties.getToken().trim() : "";
    }
}
