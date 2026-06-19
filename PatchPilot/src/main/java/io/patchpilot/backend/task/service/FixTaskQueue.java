package io.patchpilot.backend.task.service;

public interface FixTaskQueue {

    void enqueue(String taskId);

    default int cancelPendingForTask(String taskId) {
        return 0;
    }
}
