package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.service.FixTaskQueue;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Profile("default")
public class InMemoryFixTaskQueue implements FixTaskQueue {

    private final FixTaskWorker fixTaskWorker;

    public InMemoryFixTaskQueue(FixTaskWorker fixTaskWorker) {
        this.fixTaskWorker = fixTaskWorker;
    }

    @Override
    public void enqueue(String taskId) {
        CompletableFuture.runAsync(() -> fixTaskWorker.execute(taskId));
    }
}
