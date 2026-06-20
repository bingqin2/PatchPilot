package io.patchpilot.backend.github.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.tool.IssueCommentTool;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.LogSummary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class GitHubWebhookService {

    private static final String ISSUE_COMMENT_EVENT = "issue_comment";
    private static final String CREATED_ACTION = "created";
    private static final String AGENT_FIX_COMMAND = "/agent fix";

    private final ObjectMapper objectMapper;
    private final FixTaskService fixTaskService;
    private final FixTaskDispatcher fixTaskDispatcher;
    private final IssueCommentTool issueCommentTool;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final ConcurrentMap<String, WebhookHandleResult> deliveryResults = new ConcurrentHashMap<>();

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService
    ) {
        this.objectMapper = objectMapper;
        this.fixTaskService = fixTaskService;
        this.fixTaskDispatcher = fixTaskDispatcher;
        this.issueCommentTool = issueCommentTool;
        this.fixTaskTimelineService = fixTaskTimelineService;
    }

    public WebhookHandleResult handle(String event, String deliveryId, String payload) {
        if (!StringUtils.hasText(deliveryId)) {
            throw new InvalidWebhookPayloadException("Missing X-GitHub-Delivery header");
        }
        WebhookHandleResult existingResult = deliveryResults.get(deliveryId);
        if (existingResult != null) {
            return WebhookHandleResult.duplicate(existingResult.taskId());
        }
        WebhookHandleResult result = route(event, deliveryId, payload);
        deliveryResults.put(deliveryId, result);
        return result;
    }

    private WebhookHandleResult route(String event, String deliveryId, String payload) {
        if (!ISSUE_COMMENT_EVENT.equals(event)) {
            return WebhookHandleResult.ignored();
        }
        JsonNode root = parsePayload(payload);
        if (!CREATED_ACTION.equals(requiredText(root, "action"))) {
            return WebhookHandleResult.ignored();
        }
        String commentBody = requiredText(root, "comment", "body");
        if (!isAgentFixCommand(commentBody)) {
            return WebhookHandleResult.ignored();
        }
        WebhookHandleResult duplicateDeliveryResult = fixTaskService.findTaskByDeliveryId(deliveryId)
                .map(task -> WebhookHandleResult.duplicate(task.id()))
                .orElse(null);
        if (duplicateDeliveryResult != null) {
            return duplicateDeliveryResult;
        }
        String repositoryOwner = requiredText(root, "repository", "owner", "login");
        String repositoryName = requiredText(root, "repository", "name");
        long issueNumber = requiredLong(root, "issue", "number");
        WebhookHandleResult activeTaskResult = fixTaskService.findActiveTaskForIssue(
                        repositoryOwner,
                        repositoryName,
                        issueNumber
                )
                .map(this::handleActiveTaskExists)
                .orElse(null);
        if (activeTaskResult != null) {
            return activeTaskResult;
        }
        FixTaskCreationResult creationResult = fixTaskService.createFixTaskIfAbsent(new CreateFixTaskCommand(
                repositoryOwner,
                repositoryName,
                issueNumber,
                optionalLong(root, 0, "installation", "id"),
                requiredText(root, "comment", "user", "login"),
                commentBody,
                deliveryId,
                requiredLong(root, "comment", "id")
        ));
        FixTaskVo task = creationResult.task();
        if (!creationResult.created()) {
            return WebhookHandleResult.duplicate(task.id());
        }
        recordTimelineEvent(task.id(), FixTaskTimelineEventType.TASK_CREATED, "Task accepted from /agent fix");
        createStatusComment(task);
        fixTaskDispatcher.dispatch(task.id());
        return WebhookHandleResult.taskCreated(task.id());
    }

    private WebhookHandleResult handleActiveTaskExists(FixTaskVo activeTask) {
        recordTimelineEvent(
                activeTask.id(),
                FixTaskTimelineEventType.ACTIVE_TASK_EXISTS,
                "Ignored duplicate /agent fix while task is active"
        );
        updateStatusComment(() -> issueCommentTool.updateActiveTaskExists(activeTask));
        return WebhookHandleResult.activeTaskExists(activeTask.id());
    }

    private void createStatusComment(FixTaskVo task) {
        try {
            IssueCommentResult commentResult = issueCommentTool.commentAccepted(task);
            fixTaskService.attachStatusComment(task.id(), commentResult.id(), commentResult.url());
            recordTimelineEvent(task.id(), FixTaskTimelineEventType.STATUS_COMMENT_CREATED, "Status comment created");
        } catch (RuntimeException exception) {
            recordTimelineEvent(
                    task.id(),
                    FixTaskTimelineEventType.STATUS_COMMENT_FAILED,
                    "Status comment failed: " + statusCommentFailureReason(exception)
            );
            // GitHub comment feedback must not block the durable task workflow.
        }
    }

    private static void updateStatusComment(Runnable update) {
        try {
            update.run();
        } catch (RuntimeException exception) {
            // GitHub comment feedback must not block the durable task workflow.
        }
    }

    private void recordTimelineEvent(String taskId, FixTaskTimelineEventType eventType, String message) {
        try {
            fixTaskTimelineService.recordEvent(taskId, eventType, message);
        } catch (RuntimeException exception) {
            // Timeline feedback must not block the durable task workflow.
        }
    }

    private static String statusCommentFailureReason(RuntimeException exception) {
        if (!StringUtils.hasText(exception.getMessage())) {
            return exception.getClass().getSimpleName();
        }
        return LogSummary.truncateFailureReason(exception.getMessage());
    }

    private static boolean isAgentFixCommand(String commentBody) {
        String trimmedCommentBody = commentBody.trim();
        return AGENT_FIX_COMMAND.equals(trimmedCommentBody)
                || trimmedCommentBody.startsWith(AGENT_FIX_COMMAND + " ");
    }

    private JsonNode parsePayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (IOException exception) {
            throw new InvalidWebhookPayloadException("Malformed GitHub webhook payload");
        }
    }

    private static String requiredText(JsonNode root, String... path) {
        JsonNode node = requiredNode(root, path);
        if (!node.isTextual()) {
            throw new InvalidWebhookPayloadException("Expected text field: " + String.join(".", path));
        }
        return node.asText();
    }

    private static long requiredLong(JsonNode root, String... path) {
        JsonNode node = requiredNode(root, path);
        if (!node.canConvertToLong()) {
            throw new InvalidWebhookPayloadException("Expected numeric field: " + String.join(".", path));
        }
        return node.asLong();
    }

    private static long optionalLong(JsonNode root, long defaultValue, String... path) {
        JsonNode node = nodeAt(root, path);
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        if (!node.canConvertToLong()) {
            throw new InvalidWebhookPayloadException("Expected numeric field: " + String.join(".", path));
        }
        return node.asLong();
    }

    private static JsonNode requiredNode(JsonNode root, String... path) {
        JsonNode current = nodeAt(root, path);
        if (current == null || current.isNull()) {
            throw new InvalidWebhookPayloadException("Missing field: " + String.join(".", path));
        }
        return current;
    }

    private static JsonNode nodeAt(JsonNode root, String... path) {
        JsonNode current = root;
        for (String segment : path) {
            current = current == null ? null : current.get(segment);
        }
        return current;
    }
}
