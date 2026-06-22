package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationRunnerTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_run_adapter_supplied_verification_command() throws Exception {
        Path repositoryDir = tempDir.resolve("repo");
        Files.createDirectories(repositoryDir);
        createExecutable(repositoryDir.resolve("mvnw"), "echo adapter-command \"$@\"\nexit 0\n");
        VerificationRunner runner = runner();

        TestRunResult result = runner.runVerification("task-123", repositoryDir, List.of("./mvnw", "test"));

        assertThat(result.command()).isEqualTo("./mvnw test");
        assertThat(result.exitCode()).isZero();
        assertThat(result.output()).contains("adapter-command test");
    }

    @Test
    void should_register_and_unregister_process_for_task() throws Exception {
        Path repositoryDir = tempDir.resolve("registered-repo");
        Files.createDirectories(repositoryDir);
        createExecutable(repositoryDir.resolve("mvnw"), "echo ok\nexit 0\n");
        RecordingTaskProcessRegistry processRegistry = new RecordingTaskProcessRegistry();
        VerificationRunner runner = runner(processRegistry);

        TestRunResult result = runner.runVerification("task-123", repositoryDir, List.of("./mvnw", "test"));

        assertThat(result.exitCode()).isZero();
        assertThat(processRegistry.registeredTaskIds()).containsExactly("task-123");
        assertThat(processRegistry.unregisteredTaskIds()).containsExactly("task-123");
    }

    private static void createExecutable(Path script, String body) throws Exception {
        Files.writeString(script, "#!/bin/sh\n" + body);
        assertThat(script.toFile().setExecutable(true)).isTrue();
    }

    private VerificationRunner runner() {
        return runner(new TaskProcessRegistry());
    }

    private VerificationRunner runner(TaskProcessRegistry processRegistry) {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(tempDir);
        return new VerificationRunner(
                Duration.ofSeconds(5),
                new CommandExecutionGuard(properties),
                processRegistry
        );
    }

    private static final class RecordingTaskProcessRegistry extends TaskProcessRegistry {

        private final List<String> registeredTaskIds = new CopyOnWriteArrayList<>();
        private final List<String> unregisteredTaskIds = new CopyOnWriteArrayList<>();

        @Override
        public void register(String taskId, Process process) {
            registeredTaskIds.add(taskId);
        }

        @Override
        public void unregister(String taskId, Process process) {
            unregisteredTaskIds.add(taskId);
        }

        private List<String> registeredTaskIds() {
            return registeredTaskIds;
        }

        private List<String> unregisteredTaskIds() {
            return unregisteredTaskIds;
        }
    }
}
