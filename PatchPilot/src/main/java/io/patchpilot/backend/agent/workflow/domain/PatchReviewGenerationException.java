package io.patchpilot.backend.agent.workflow.domain;

public class PatchReviewGenerationException extends RuntimeException {

    public PatchReviewGenerationException(String message) {
        super(message);
    }

    public PatchReviewGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
