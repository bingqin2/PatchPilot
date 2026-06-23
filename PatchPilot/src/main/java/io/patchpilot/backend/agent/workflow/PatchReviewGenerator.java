package io.patchpilot.backend.agent.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.workflow.domain.FileEditContext;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchReview;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewDecision;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewGenerationException;
import io.patchpilot.backend.agent.workflow.domain.ProposedFileEdit;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class PatchReviewGenerator {

    private static final int MAX_REVIEW_CONTENT_CHARS = 4_000;

    private final ModelProviderClient modelProviderClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public PatchReviewGenerator(ModelProviderClient modelProviderClient) {
        this(modelProviderClient, new ObjectMapper());
    }

    PatchReviewGenerator(ModelProviderClient modelProviderClient, ObjectMapper objectMapper) {
        this.modelProviderClient = modelProviderClient;
        this.objectMapper = objectMapper;
    }

    public PatchReview review(
            FixTaskVo task,
            FixPlan fixPlan,
            List<FileEditContext> fileContexts,
            List<ProposedFileEdit> edits
    ) {
        ModelProviderResponse response = modelProviderClient.complete(new ModelProviderRequest(
                task.id(),
                systemPrompt(),
                userPrompt(task, fixPlan, fileContexts, edits)
        ));
        return parseReview(response.content());
    }

    private String systemPrompt() {
        return """
                You are PatchPilot's post-edit review gate.
                Decide whether generated file edits are aligned with the issue request and fix plan.
                Return only JSON with fields: decision, reason, confidence, requiredFollowUp.
                decision must be APPROVE or REJECT.
                Reject edits that are unrelated, incomplete, unsafe, suspicious, or do not address the issue.
                Do not include markdown fences or explanatory text.
                """;
    }

    private String userPrompt(
            FixTaskVo task,
            FixPlan fixPlan,
            List<FileEditContext> fileContexts,
            List<ProposedFileEdit> edits
    ) {
        return """
                Repository: %s/%s
                Issue number: %d
                Trigger user: %s
                Trigger comment: %s

                Fix plan summary: %s
                Target files: %s
                Steps:
                %s
                Risk: %s

                Proposed edits:
                %s
                """.formatted(
                task.repositoryOwner(),
                task.repositoryName(),
                task.issueNumber(),
                task.triggerUser(),
                task.triggerComment(),
                fixPlan.summary(),
                String.join(", ", fixPlan.targetFiles()),
                String.join("\n", fixPlan.steps()),
                fixPlan.risk(),
                editsPrompt(fileContexts, edits)
        );
    }

    private String editsPrompt(List<FileEditContext> fileContexts, List<ProposedFileEdit> edits) {
        return edits.stream()
                .map(edit -> """
                        File: %s
                        Rationale: %s
                        Before: %s
                        After: %s
                        """.formatted(
                        edit.path(),
                        edit.rationale(),
                        bounded(contextFor(fileContexts, edit.path())),
                        bounded(edit.content())
                ))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- none");
    }

    private String contextFor(List<FileEditContext> fileContexts, String path) {
        return fileContexts.stream()
                .filter(context -> context.path().equals(path))
                .map(FileEditContext::content)
                .findFirst()
                .orElse("");
    }

    private String bounded(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() <= MAX_REVIEW_CONTENT_CHARS) {
            return content;
        }
        return content.substring(0, MAX_REVIEW_CONTENT_CHARS) + "\n[truncated]";
    }

    private PatchReview parseReview(String content) {
        JsonNode root = readJson(content);
        return new PatchReview(
                decision(requiredText(root, "decision")),
                requiredText(root, "reason"),
                requiredText(root, "confidence"),
                requiredText(root, "requiredFollowUp")
        );
    }

    private JsonNode readJson(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException exception) {
            throw new PatchReviewGenerationException("Model patch review response was not valid JSON", exception);
        }
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new PatchReviewGenerationException("Model patch review response missing required field: " + fieldName);
        }
        return node.asText();
    }

    private PatchReviewDecision decision(String value) {
        try {
            return PatchReviewDecision.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new PatchReviewGenerationException("Model patch review response has unsupported decision: " + value, exception);
        }
    }
}
