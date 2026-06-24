package io.patchpilot.backend.github.webhook.domain;

public enum WebhookPayloadDiagnosticStatus {
    READY_FOR_WEBHOOK,
    INVALID_SIGNATURE,
    MALFORMED_PAYLOAD,
    UNSUPPORTED_EVENT,
    UNSUPPORTED_ACTION,
    IGNORED_COMMENT
}
