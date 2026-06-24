package io.patchpilot.backend.github.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.github.webhook.domain.WebhookPayloadDiagnosticDto;
import io.patchpilot.backend.github.webhook.domain.WebhookPayloadDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookPayloadDiagnosticVo;
import io.patchpilot.backend.safety.CommandSafetyGate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
public class WebhookPayloadDiagnosticService {

    private static final String ISSUE_COMMENT_EVENT = "issue_comment";
    private static final String CREATED_ACTION = "created";

    private final ObjectMapper objectMapper;
    private final GitHubWebhookSignatureVerifier signatureVerifier;
    private final CommandSafetyGate commandSafetyGate;

    public WebhookPayloadDiagnosticService(
            ObjectMapper objectMapper,
            GitHubWebhookSignatureVerifier signatureVerifier,
            CommandSafetyGate commandSafetyGate
    ) {
        this.objectMapper = objectMapper;
        this.signatureVerifier = signatureVerifier;
        this.commandSafetyGate = commandSafetyGate;
    }

    public WebhookPayloadDiagnosticVo evaluate(WebhookPayloadDiagnosticDto request) {
        String payload = request.payload() == null ? "" : request.payload();
        WebhookSignatureDiagnosticStatus signatureStatus = signatureVerifier.diagnose(payload, request.signature());
        boolean supportedEvent = ISSUE_COMMENT_EVENT.equals(request.event());
        JsonNode root;
        try {
            root = objectMapper.readTree(payload);
        } catch (IOException exception) {
            return new WebhookPayloadDiagnosticVo(
                    WebhookPayloadDiagnosticStatus.MALFORMED_PAYLOAD,
                    signatureStatus,
                    false,
                    supportedEvent,
                    false,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Malformed GitHub webhook payload",
                    "Copy the raw GitHub delivery payload again, then rerun this diagnostic."
            );
        }

        ParsedPayload parsed = parse(root);
        WebhookPayloadDiagnosticStatus status = status(signatureStatus, supportedEvent, parsed);
        return new WebhookPayloadDiagnosticVo(
                status,
                signatureStatus,
                true,
                supportedEvent,
                parsed.supportedAction(),
                parsed.agentFixCommand(),
                parsed.repositoryOwner(),
                parsed.repositoryName(),
                parsed.issueNumber(),
                parsed.triggerUser(),
                parsed.triggerComment(),
                message(status, parsed),
                nextAction(status)
        );
    }

    private ParsedPayload parse(JsonNode root) {
        String action = optionalText(root, "action");
        String triggerComment = optionalText(root, "comment", "body");
        return new ParsedPayload(
                CREATED_ACTION.equals(action),
                commandSafetyGate.isAgentFixCommand(triggerComment),
                optionalText(root, "repository", "owner", "login"),
                optionalText(root, "repository", "name"),
                optionalLong(root, "issue", "number"),
                optionalText(root, "comment", "user", "login"),
                triggerComment
        );
    }

    private static WebhookPayloadDiagnosticStatus status(
            WebhookSignatureDiagnosticStatus signatureStatus,
            boolean supportedEvent,
            ParsedPayload parsed
    ) {
        if (signatureStatus == WebhookSignatureDiagnosticStatus.INVALID) {
            return WebhookPayloadDiagnosticStatus.INVALID_SIGNATURE;
        }
        if (!supportedEvent) {
            return WebhookPayloadDiagnosticStatus.UNSUPPORTED_EVENT;
        }
        if (!parsed.supportedAction()) {
            return WebhookPayloadDiagnosticStatus.UNSUPPORTED_ACTION;
        }
        if (!parsed.agentFixCommand()) {
            return WebhookPayloadDiagnosticStatus.IGNORED_COMMENT;
        }
        return WebhookPayloadDiagnosticStatus.READY_FOR_WEBHOOK;
    }

    private static String message(WebhookPayloadDiagnosticStatus status, ParsedPayload parsed) {
        return switch (status) {
            case READY_FOR_WEBHOOK -> "Payload is an issue_comment.created /agent fix trigger.";
            case INVALID_SIGNATURE -> "Webhook signature does not match the configured secret.";
            case MALFORMED_PAYLOAD -> "Malformed GitHub webhook payload.";
            case UNSUPPORTED_EVENT -> "Unsupported GitHub webhook event.";
            case UNSUPPORTED_ACTION -> "Issue comment action is not created.";
            case IGNORED_COMMENT -> "Issue comment does not contain an /agent fix trigger.";
        };
    }

    private static String nextAction(WebhookPayloadDiagnosticStatus status) {
        return switch (status) {
            case READY_FOR_WEBHOOK ->
                    "The payload shape is ready. Use GitHub redeliver or post the live /agent fix comment when the webhook URL is current.";
            case INVALID_SIGNATURE ->
                    "Fix the GitHub webhook secret or backend PATCHPILOT_GITHUB_WEBHOOK_SECRET, then use GitHub redeliver.";
            case MALFORMED_PAYLOAD ->
                    "Copy the raw GitHub delivery payload again, then rerun this diagnostic.";
            case UNSUPPORTED_EVENT ->
                    "Configure the GitHub webhook to send issue_comment events before redelivery.";
            case UNSUPPORTED_ACTION ->
                    "Use a newly created issue comment delivery; edited or deleted comments will be ignored.";
            case IGNORED_COMMENT ->
                    "Change the comment to include a concrete /agent fix command before redelivery or reposting.";
        };
    }

    private static String optionalText(JsonNode root, String... path) {
        JsonNode node = nodeAt(root, path);
        if (node == null || node.isNull() || !node.isTextual()) {
            return null;
        }
        return node.asText();
    }

    private static Long optionalLong(JsonNode root, String... path) {
        JsonNode node = nodeAt(root, path);
        if (node == null || node.isNull() || !node.canConvertToLong()) {
            return null;
        }
        return node.asLong();
    }

    private static JsonNode nodeAt(JsonNode root, String... path) {
        JsonNode current = root;
        for (String segment : path) {
            current = current == null ? null : current.get(segment);
        }
        return current;
    }

    private record ParsedPayload(
            boolean supportedAction,
            boolean agentFixCommand,
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment
    ) {
    }
}
