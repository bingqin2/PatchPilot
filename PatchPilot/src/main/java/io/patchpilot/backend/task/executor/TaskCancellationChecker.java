package io.patchpilot.backend.task.executor;

public interface TaskCancellationChecker {

    void throwIfCancelled(String taskId);
}
