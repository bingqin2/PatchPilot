package io.patchpilot.backend.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "patchpilot.task.queue")
public class TaskQueueProperties {

    private int maxAttempts = 3;

    private long retryDelayMs = 30000;

    private long visibilityTimeoutMs = 300000;

    private long workerHeartbeatStaleMs = 10000;

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }

    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }

    public long getVisibilityTimeoutMs() {
        return visibilityTimeoutMs;
    }

    public void setVisibilityTimeoutMs(long visibilityTimeoutMs) {
        this.visibilityTimeoutMs = visibilityTimeoutMs;
    }

    public long getWorkerHeartbeatStaleMs() {
        return workerHeartbeatStaleMs;
    }

    public void setWorkerHeartbeatStaleMs(long workerHeartbeatStaleMs) {
        this.workerHeartbeatStaleMs = workerHeartbeatStaleMs;
    }
}
