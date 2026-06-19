package io.patchpilot.backend.task.executor;

public class TaskCancellationException extends RuntimeException {

    private final String taskId;

    public TaskCancellationException(String taskId) {
        super("Task cancelled: " + taskId);
        this.taskId = taskId;
    }

    public String taskId() {
        return taskId;
    }
}
