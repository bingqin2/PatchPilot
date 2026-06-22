package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MavenTestRunnerTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_run_maven_wrapper_when_present() throws Exception {
        Path repositoryDir = tempDir.resolve("wrapper-repo");
        Files.createDirectories(repositoryDir);
        createMavenWrapper(repositoryDir, "echo wrapper-ok \"$@\"\nexit 0\n");
        MavenTestRunner runner = runner();

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.command()).isEqualTo("./mvnw test");
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.output()).contains("wrapper-ok test");
    }

    @Test
    void should_prefer_maven_wrapper_over_pom_file() throws Exception {
        Path repositoryDir = tempDir.resolve("wrapper-and-pom-repo");
        Files.createDirectories(repositoryDir);
        Files.writeString(repositoryDir.resolve("pom.xml"), "<project />");
        createMavenWrapper(repositoryDir, "echo wrapper-ok \"$@\"\nexit 0\n");
        MavenTestRunner runner = runner();

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.command()).isEqualTo("./mvnw test");
        assertThat(result.output()).contains("wrapper-ok test");
    }

    @Test
    void should_run_system_maven_when_only_pom_exists() throws Exception {
        Path repositoryDir = tempDir.resolve("pom-only-repo");
        Files.createDirectories(repositoryDir);
        Files.writeString(repositoryDir.resolve("pom.xml"), "<project />");
        RecordingMavenTestRunner runner = new RecordingMavenTestRunner();

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.command()).isEqualTo("mvn test");
        assertThat(runner.repositoryDir()).isEqualTo(repositoryDir.toAbsolutePath().normalize());
        assertThat(runner.command()).containsExactly("mvn", "test");
    }

    @Test
    void should_return_non_zero_exit_and_output() throws Exception {
        Path repositoryDir = tempDir.resolve("failing-wrapper-repo");
        Files.createDirectories(repositoryDir);
        createMavenWrapper(repositoryDir, "echo wrapper-failed\nexit 7\n");
        MavenTestRunner runner = runner();

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.command()).isEqualTo("./mvnw test");
        assertThat(result.exitCode()).isEqualTo(7);
        assertThat(result.output()).contains("wrapper-failed");
    }

    @Test
    void should_return_timeout_result_when_command_exceeds_timeout() throws Exception {
        Path repositoryDir = tempDir.resolve("timeout-wrapper-repo");
        Files.createDirectories(repositoryDir);
        createMavenWrapper(repositoryDir, "sleep 5\n");
        MavenTestRunner runner = runner(Duration.ofMillis(100));

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.command()).isEqualTo("./mvnw test");
        assertThat(result.exitCode()).isEqualTo(124);
        assertThat(result.output()).isEqualTo("verification command timed out");
    }

    @Test
    void should_capture_large_command_output_without_blocking_until_timeout() throws Exception {
        Path repositoryDir = tempDir.resolve("large-output-wrapper-repo");
        Files.createDirectories(repositoryDir);
        createMavenWrapper(repositoryDir, "i=0\nwhile [ $i -lt 20000 ]; do echo \"line-$i\"; i=$((i + 1)); done\nexit 7\n");
        MavenTestRunner runner = runner(Duration.ofSeconds(5));

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.exitCode()).isEqualTo(7);
        assertThat(result.output()).contains("line-0");
        assertThat(result.output()).contains("line-19999");
    }

    @Test
    void should_register_and_unregister_task_process_when_task_id_is_provided() throws Exception {
        Path repositoryDir = tempDir.resolve("registered-wrapper-repo");
        Files.createDirectories(repositoryDir);
        createMavenWrapper(repositoryDir, "echo wrapper-ok \"$@\"\nexit 0\n");
        RecordingTaskProcessRegistry processRegistry = new RecordingTaskProcessRegistry();
        MavenTestRunner runner = runner(Duration.ofSeconds(300), processRegistry);

        TestRunResult result = runner.runTests("task-123", repositoryDir);

        assertThat(result.exitCode()).isZero();
        assertThat(processRegistry.registeredTaskIds()).containsExactly("task-123");
        assertThat(processRegistry.unregisteredTaskIds()).containsExactly("task-123");
    }

    @Test
    void should_remove_patchpilot_runtime_environment_from_maven_process() {
        Map<String, String> environment = new HashMap<>();
        environment.put("PATCHPILOT_GITHUB_TOKEN", "secret-token");
        environment.put("PATCHPILOT_AGENT_API_KEY", "secret-key");
        environment.put("SPRING_PROFILES_ACTIVE", "docker");
        environment.put("PATH", "/usr/bin");
        environment.put("JAVA_HOME", "/tmp/java");

        MavenTestRunner.sanitizeProcessEnvironment(environment);

        assertThat(environment).containsEntry("PATH", "/usr/bin");
        assertThat(environment).containsEntry("JAVA_HOME", "/tmp/java");
        assertThat(environment).doesNotContainKeys(
                "PATCHPILOT_GITHUB_TOKEN",
                "PATCHPILOT_AGENT_API_KEY",
                "SPRING_PROFILES_ACTIVE"
        );
    }

    @Test
    void should_fail_for_unsupported_repository() {
        MavenTestRunner runner = new MavenTestRunner();

        assertThatThrownBy(() -> runner.runTests(tempDir))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unsupported repository: no mvnw or pom.xml found");
    }

    @Test
    void should_reject_maven_command_outside_workspace_root() throws Exception {
        Path outsideRepositoryDir = tempDir.getParent().resolve("outside-maven-repo");
        Files.createDirectories(outsideRepositoryDir);
        Files.writeString(outsideRepositoryDir.resolve("pom.xml"), "<project />");
        MavenTestRunner runner = runner();

        assertThatThrownBy(() -> runner.runTests(outsideRepositoryDir))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command directory escapes workspace root: " + outsideRepositoryDir.toAbsolutePath().normalize());
    }

    private static void createMavenWrapper(Path repositoryDir, String body) throws Exception {
        Path wrapper = repositoryDir.resolve("mvnw");
        Files.writeString(wrapper, "#!/bin/sh\n" + body);
        assertThat(wrapper.toFile().setExecutable(true)).isTrue();
    }

    private MavenTestRunner runner() {
        return runner(Duration.ofSeconds(300));
    }

    private MavenTestRunner runner(Duration timeout) {
        return runner(timeout, new TaskProcessRegistry());
    }

    private MavenTestRunner runner(Duration timeout, TaskProcessRegistry processRegistry) {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(tempDir);
        return new MavenTestRunner(timeout, new CommandExecutionGuard(properties), processRegistry, new JavaMavenLanguageAdapter());
    }

    private static final class RecordingMavenTestRunner extends MavenTestRunner {

        private Path repositoryDir;
        private List<String> command;

        @Override
        protected TestRunResult runCommand(String taskId, Path repositoryDir, List<String> command) {
            this.repositoryDir = repositoryDir;
            this.command = List.copyOf(command);
            return new TestRunResult(String.join(" ", command), 0, "recorded");
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private List<String> command() {
            return command;
        }
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
