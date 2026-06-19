package io.patchpilot.backend.task.process;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class TaskProcessRegistry {

    private static final Duration DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT = Duration.ofSeconds(2);

    private final ConcurrentMap<String, Process> processes = new ConcurrentHashMap<>();
    private final Duration gracefulShutdownTimeout;

    public TaskProcessRegistry() {
        this(DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT);
    }

    public TaskProcessRegistry(Duration gracefulShutdownTimeout) {
        this.gracefulShutdownTimeout = gracefulShutdownTimeout;
    }

    public void register(String taskId, Process process) {
        processes.put(requireTaskId(taskId), Objects.requireNonNull(process, "process must not be null"));
    }

    public void unregister(String taskId, Process process) {
        processes.remove(requireTaskId(taskId), process);
    }

    public boolean cancel(String taskId) {
        Process process = processes.get(requireTaskId(taskId));
        if (process == null) {
            return false;
        }
        process.destroy();
        if (isStillAlive(process)) {
            process.destroyForcibly();
        }
        return true;
    }

    private boolean isStillAlive(Process process) {
        try {
            if (gracefulShutdownTimeout.isZero() || gracefulShutdownTimeout.isNegative()) {
                return true;
            }
            boolean exited = process.waitFor(gracefulShutdownTimeout.toMillis(), TimeUnit.MILLISECONDS);
            return !exited && process.isAlive();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    private static String requireTaskId(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("Task id must not be blank");
        }
        return taskId;
    }
}
