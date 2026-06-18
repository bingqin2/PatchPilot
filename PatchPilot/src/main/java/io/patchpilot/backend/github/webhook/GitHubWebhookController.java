package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github/webhook")
public class GitHubWebhookController {

    private final GitHubWebhookSignatureVerifier signatureVerifier;
    private final GitHubWebhookService webhookService;

    public GitHubWebhookController(
            GitHubWebhookSignatureVerifier signatureVerifier,
            GitHubWebhookService webhookService
    ) {
        this.signatureVerifier = signatureVerifier;
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WebhookHandleResult>> receive(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String payload
    ) {
        if (!signatureVerifier.isValid(payload, signature)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("Invalid GitHub webhook signature"));
        }
        try {
            return ResponseEntity.ok(ApiResponse.ok(webhookService.handle(event, deliveryId, payload)));
        } catch (InvalidWebhookPayloadException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.fail(exception.getMessage()));
        }
    }
}
