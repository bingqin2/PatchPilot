package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskQueue;
import org.springframework.stereotype.Service;

@Service
public class AsyncFixTaskDispatcher implements FixTaskDispatcher {

    private final FixTaskQueue fixTaskQueue;

    public AsyncFixTaskDispatcher(FixTaskQueue fixTaskQueue) {
        this.fixTaskQueue = fixTaskQueue;
    }

    @Override
    public void dispatch(String taskId) {
        fixTaskQueue.enqueue(taskId);
    }
}
