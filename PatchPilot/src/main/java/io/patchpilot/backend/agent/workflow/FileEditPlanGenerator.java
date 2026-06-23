package io.patchpilot.backend.agent.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.provider.ModelProviderClient;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.workflow.domain.FileEditContext;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlan;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlanGenerationException;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.ProposedFileEdit;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class FileEditPlanGenerator {

    private final ModelProviderClient modelProviderClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public FileEditPlanGenerator(ModelProviderClient modelProviderClient) {
        this(modelProviderClient, new ObjectMapper());
    }

    FileEditPlanGenerator(ModelProviderClient modelProviderClient, ObjectMapper objectMapper) {
        this.modelProviderClient = modelProviderClient;
        this.objectMapper = objectMapper;
    }

    public FileEditPlan generateEdits(FixTaskVo task, FixPlan fixPlan, List<FileEditContext> fileContexts) {
        ModelProviderResponse response = modelProviderClient.complete(new ModelProviderRequest(
                task.id(),
                systemPrompt(),
                userPrompt(task, fixPlan, fileContexts)
        ));
        return parsePlan(response.content());
    }

    private String systemPrompt() {
        return """
                You are PatchPilot, an automated GitHub issue-to-PR editing agent.
                Return only JSON with field: edits.
                edits must be an array of objects with fields: path, content, rationale.
                content must be the complete UTF-8 file content to write.
                Only edit files listed in the provided target files.
                Do not include markdown fences or explanatory text.
                """;
    }

    private String userPrompt(FixTaskVo task, FixPlan fixPlan, List<FileEditContext> fileContexts) {
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

                Current file contents:
                %s

                Produce the smallest safe full-file edits needed for the fix.
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
                fileContextsPrompt(fileContexts)
        );
    }

    private String fileContextsPrompt(List<FileEditContext> fileContexts) {
        if (fileContexts.isEmpty()) {
            return "- none";
        }
        return fileContexts.stream()
                .map(context -> """
                        File: %s
                        ```text
                        %s
                        ```
                        """.formatted(context.path(), context.content()))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- none");
    }

    private FileEditPlan parsePlan(String content) {
        JsonNode root = readJson(content);
        JsonNode editsNode = root.get("edits");
        if (editsNode == null || !editsNode.isArray()) {
            throw new FileEditPlanGenerationException("Model file edit response missing required field: edits");
        }

        List<ProposedFileEdit> edits = new ArrayList<>();
        for (JsonNode editNode : editsNode) {
            edits.add(new ProposedFileEdit(
                    requiredText(editNode, "path"),
                    requiredText(editNode, "content"),
                    requiredText(editNode, "rationale")
            ));
        }
        return new FileEditPlan(edits);
    }

    private JsonNode readJson(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException exception) {
            throw new FileEditPlanGenerationException("Model file edit response was not valid JSON", exception);
        }
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new FileEditPlanGenerationException("Model file edit response missing required field: " + fieldName);
        }
        return node.asText();
    }
}
