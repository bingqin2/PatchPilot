package io.patchpilot.backend.task.process;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class TaskProcessRegistryTests {

    @Test
    void should_destroy_registered_process_for_task() {
        TaskProcessRegistry registry = new TaskProcessRegistry(Duration.ZERO);
        RecordingProcess process = new RecordingProcess(false);

        registry.register("task-123", process);

        assertThat(registry.cancel("task-123")).isTrue();
        assertThat(process.destroyed()).isTrue();
        assertThat(process.destroyedForcibly()).isTrue();
    }

    @Test
    void should_not_cancel_when_registered_process_was_unregistered() {
        TaskProcessRegistry registry = new TaskProcessRegistry(Duration.ZERO);
        RecordingProcess process = new RecordingProcess(false);

        registry.register("task-123", process);
        registry.unregister("task-123", process);

        assertThat(registry.cancel("task-123")).isFalse();
        assertThat(process.destroyed()).isFalse();
    }

    @Test
    void should_not_unregister_newer_process_with_stale_process_reference() {
        TaskProcessRegistry registry = new TaskProcessRegistry(Duration.ZERO);
        RecordingProcess oldProcess = new RecordingProcess(false);
        RecordingProcess newProcess = new RecordingProcess(false);

        registry.register("task-123", oldProcess);
        registry.register("task-123", newProcess);
        registry.unregister("task-123", oldProcess);

        assertThat(registry.cancel("task-123")).isTrue();
        assertThat(oldProcess.destroyed()).isFalse();
        assertThat(newProcess.destroyed()).isTrue();
    }

    private static final class RecordingProcess extends Process {

        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final AtomicBoolean destroyedForcibly = new AtomicBoolean(false);
        private final boolean aliveAfterDestroy;

        private RecordingProcess(boolean aliveAfterDestroy) {
            this.aliveAfterDestroy = aliveAfterDestroy;
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return java.io.OutputStream.nullOutputStream();
        }

        @Override
        public java.io.InputStream getInputStream() {
            return java.io.InputStream.nullInputStream();
        }

        @Override
        public java.io.InputStream getErrorStream() {
            return java.io.InputStream.nullInputStream();
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
            destroyed.set(true);
        }

        @Override
        public Process destroyForcibly() {
            destroyedForcibly.set(true);
            return this;
        }

        @Override
        public boolean isAlive() {
            return destroyed.get() && aliveAfterDestroy && !destroyedForcibly.get();
        }

        private boolean destroyed() {
            return destroyed.get();
        }

        private boolean destroyedForcibly() {
            return destroyedForcibly.get();
        }
    }
}
