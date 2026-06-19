package io.patchpilot.backend.agent.workflow.domain;

public class FixPlanGenerationException extends RuntimeException {

    public FixPlanGenerationException(String message) {
        super(message);
    }

    public FixPlanGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
