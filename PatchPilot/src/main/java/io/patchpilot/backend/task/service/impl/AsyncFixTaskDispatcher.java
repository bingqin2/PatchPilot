package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncFixTaskDispatcher implements FixTaskDispatcher {

    private final FixTaskQueue fixTaskQueue;

    @Override
    public void dispatch(String taskId) {
        fixTaskQueue.enqueue(taskId);
    }
}
