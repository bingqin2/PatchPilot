package io.patchpilot.backend.agent.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.FixPlanGenerationException;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextComment;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class FixPlanGenerator {

    private final ModelProviderClient modelProviderClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public FixPlanGenerator(ModelProviderClient modelProviderClient) {
        this(modelProviderClient, new ObjectMapper());
    }

    FixPlanGenerator(ModelProviderClient modelProviderClient, ObjectMapper objectMapper) {
        this.modelProviderClient = modelProviderClient;
        this.objectMapper = objectMapper;
    }

    public FixPlan generatePlan(FixTaskVo task) {
        return generatePlan(task, null);
    }

    public FixPlan generatePlan(FixTaskVo task, GitHubIssueContext issueContext) {
        ModelProviderResponse response = modelProviderClient.complete(new ModelProviderRequest(
                task.id(),
                systemPrompt(),
                userPrompt(task, issueContext)
        ));
        return parsePlan(response.content());
    }

    private String systemPrompt() {
        return """
                You are PatchPilot, an automated GitHub issue-to-PR planning agent.
                Return only JSON with fields: summary, targetFiles, steps, risk.
                Do not include markdown fences or explanatory text.
                """;
    }

    private String userPrompt(FixTaskVo task, GitHubIssueContext issueContext) {
        return """
                Repository: %s/%s
                Issue number: %d
                Trigger user: %s
                Trigger comment: %s
                %s

                Produce a focused fix plan. Do not edit files.
                """.formatted(
                task.repositoryOwner(),
                task.repositoryName(),
                task.issueNumber(),
                task.triggerUser(),
                task.triggerComment(),
                issueContextPrompt(issueContext)
        );
    }

    private String issueContextPrompt(GitHubIssueContext issueContext) {
        if (issueContext == null) {
            return "Issue context: unavailable";
        }
        return """
                Issue title: %s
                Issue URL: %s
                Issue body: %s
                Recent issue comments:
                %s
                """.formatted(
                issueContext.title(),
                issueContext.url(),
                issueContext.body(),
                issueCommentsPrompt(issueContext.comments())
        );
    }

    private String issueCommentsPrompt(List<GitHubIssueContextComment> comments) {
        if (comments.isEmpty()) {
            return "- none";
        }
        return comments.stream()
                .map(comment -> "- %s at %s: %s".formatted(
                        comment.author(),
                        comment.createdAt(),
                        comment.body()
                ))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- none");
    }

    private FixPlan parsePlan(String content) {
        JsonNode root = readJson(content);
        String summary = requiredText(root, "summary");
        List<String> targetFiles = requiredStringArray(root, "targetFiles");
        List<String> steps = requiredStringArray(root, "steps");
        String risk = requiredText(root, "risk");
        return new FixPlan(summary, targetFiles, steps, risk);
    }

    private JsonNode readJson(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException exception) {
            throw new FixPlanGenerationException("Model fix plan response was not valid JSON", exception);
        }
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new FixPlanGenerationException("Model fix plan response missing required field: " + fieldName);
        }
        return node.asText();
    }

    private List<String> requiredStringArray(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isArray() || node.isEmpty()) {
            throw new FixPlanGenerationException("Model fix plan response missing required field: " + fieldName);
        }

        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            if (!item.isTextual() || !StringUtils.hasText(item.asText())) {
                throw new FixPlanGenerationException("Model fix plan response missing required field: " + fieldName);
            }
            values.add(item.asText());
        }
        return List.copyOf(values);
    }
}
