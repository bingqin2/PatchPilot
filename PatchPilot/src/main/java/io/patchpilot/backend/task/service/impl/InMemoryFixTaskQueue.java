package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.service.FixTaskQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Profile("default")
@RequiredArgsConstructor
public class InMemoryFixTaskQueue implements FixTaskQueue {

    private final FixTaskWorker fixTaskWorker;

    @Override
    public void enqueue(String taskId) {
        CompletableFuture.runAsync(() -> fixTaskWorker.execute(taskId));
    }
}
