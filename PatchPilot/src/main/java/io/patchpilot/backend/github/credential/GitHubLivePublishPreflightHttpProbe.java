package io.patchpilot.backend.github.credential;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightProbeResult;
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
import java.util.ArrayList;
import java.util.List;

@Component
public class GitHubLivePublishPreflightHttpProbe implements GitHubLivePublishPreflightProbe {

    private static final String REPOSITORY_API_PREFIX = "https://api.github.com/repos/";
    private static final String PATCHPILOT_BRANCH_PREFIX = "patchpilot/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public GitHubLivePublishPreflightHttpProbe() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    GitHubLivePublishPreflightHttpProbe(HttpClient httpClient) {
        this(httpClient, new ObjectMapper());
    }

    GitHubLivePublishPreflightHttpProbe(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public GitHubLivePublishPreflightProbeResult check(String token, String owner, String repository) {
        String branchesBody = send(token, branchesUri(owner, repository), "branches");
        String pullsBody = send(token, pullsUri(owner, repository), "pulls");
        List<String> branchNames = branchNames(branchesBody);
        return new GitHubLivePublishPreflightProbeResult(
                defaultBranch(branchNames),
                branchNames,
                openPatchpilotPullRequestUrls(pullsBody)
        );
    }

    private String send(String token, URI uri, String resourceName) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new GitHubCredentialReadinessException(
                        "GitHub live publish preflight failed: " + resourceName + " HTTP " + response.statusCode()
                );
            }
            return response.body();
        } catch (IOException exception) {
            throw new GitHubCredentialReadinessException("GitHub live publish preflight failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubCredentialReadinessException("GitHub live publish preflight interrupted", exception);
        }
    }

    private List<String> branchNames(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (!root.isArray()) {
                throw new GitHubCredentialReadinessException("GitHub live publish preflight failed: invalid branches response");
            }
            List<String> branchNames = new ArrayList<>();
            for (JsonNode branch : root) {
                String branchName = branch.path("name").asText("");
                if (StringUtils.hasText(branchName)) {
                    branchNames.add(branchName.trim());
                }
            }
            if (branchNames.isEmpty()) {
                throw new GitHubCredentialReadinessException("GitHub live publish preflight failed: no branches returned");
            }
            return branchNames;
        } catch (IOException exception) {
            throw new GitHubCredentialReadinessException("GitHub live publish preflight failed: invalid branches response", exception);
        }
    }

    private static String defaultBranch(List<String> branchNames) {
        return branchNames.stream()
                .filter("main"::equals)
                .findFirst()
                .orElse(branchNames.get(0));
    }

    private List<String> openPatchpilotPullRequestUrls(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (!root.isArray()) {
                throw new GitHubCredentialReadinessException("GitHub live publish preflight failed: invalid pulls response");
            }
            List<String> urls = new ArrayList<>();
            for (JsonNode pullRequest : root) {
                String headRef = pullRequest.path("head").path("ref").asText("");
                String url = pullRequest.path("html_url").asText("");
                if (headRef.startsWith(PATCHPILOT_BRANCH_PREFIX) && StringUtils.hasText(url)) {
                    urls.add(url.trim());
                }
            }
            return urls;
        } catch (IOException exception) {
            throw new GitHubCredentialReadinessException("GitHub live publish preflight failed: invalid pulls response", exception);
        }
    }

    private static URI branchesUri(String owner, String repository) {
        return URI.create(REPOSITORY_API_PREFIX + encode(owner) + "/" + encode(repository) + "/branches?per_page=100");
    }

    private static URI pullsUri(String owner, String repository) {
        return URI.create(REPOSITORY_API_PREFIX + encode(owner) + "/" + encode(repository) + "/pulls?state=open&per_page=100");
    }

    private static String encode(String pathSegment) {
        return URLEncoder.encode(pathSegment, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
