package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
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
    private final WebhookDeliveryDiagnosticService diagnosticService;

    public GitHubWebhookController(
            GitHubWebhookSignatureVerifier signatureVerifier,
            GitHubWebhookService webhookService,
            WebhookDeliveryDiagnosticService diagnosticService
    ) {
        this.signatureVerifier = signatureVerifier;
        this.webhookService = webhookService;
        this.diagnosticService = diagnosticService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WebhookHandleResult>> receive(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String payload
    ) {
        if (!signatureVerifier.isValid(payload, signature)) {
            recordControllerDiagnostic(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.INVALID_SIGNATURE,
                    "Invalid GitHub webhook signature"
            );
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("Invalid GitHub webhook signature"));
        }
        try {
            return ResponseEntity.ok(ApiResponse.ok(webhookService.handle(event, deliveryId, payload)));
        } catch (InvalidWebhookPayloadException exception) {
            recordControllerDiagnostic(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.BAD_REQUEST,
                    exception.getMessage()
            );
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.fail(exception.getMessage()));
        }
    }

    private void recordControllerDiagnostic(
            String deliveryId,
            String event,
            WebhookDeliveryDiagnosticStatus status,
            String message
    ) {
        try {
            diagnosticService.record(new RecordWebhookDeliveryDiagnosticCommand(
                    deliveryId,
                    event,
                    status,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    message
            ));
        } catch (RuntimeException exception) {
            // Diagnostics must not block webhook responses.
        }
    }
}
