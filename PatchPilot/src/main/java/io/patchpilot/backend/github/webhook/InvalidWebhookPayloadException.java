package io.patchpilot.backend.github.webhook;

public class InvalidWebhookPayloadException extends RuntimeException {

    public InvalidWebhookPayloadException(String message) {
        super(message);
    }
}
