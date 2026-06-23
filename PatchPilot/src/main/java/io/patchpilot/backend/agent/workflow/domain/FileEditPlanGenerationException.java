package io.patchpilot.backend.agent.workflow.domain;

public class FileEditPlanGenerationException extends RuntimeException {

    public FileEditPlanGenerationException(String message) {
        super(message);
    }

    public FileEditPlanGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
