package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

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
        MavenTestRunner runner = new MavenTestRunner();

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
        MavenTestRunner runner = new MavenTestRunner();

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
        MavenTestRunner runner = new MavenTestRunner();

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
        MavenTestRunner runner = new MavenTestRunner(Duration.ofMillis(100));

        TestRunResult result = runner.runTests(repositoryDir);

        assertThat(result.command()).isEqualTo("./mvnw test");
        assertThat(result.exitCode()).isEqualTo(124);
        assertThat(result.output()).isEqualTo("maven test command timed out");
    }

    @Test
    void should_fail_for_unsupported_repository() {
        MavenTestRunner runner = new MavenTestRunner();

        assertThatThrownBy(() -> runner.runTests(tempDir))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unsupported repository: no mvnw or pom.xml found");
    }

    private static void createMavenWrapper(Path repositoryDir, String body) throws Exception {
        Path wrapper = repositoryDir.resolve("mvnw");
        Files.writeString(wrapper, "#!/bin/sh\n" + body);
        assertThat(wrapper.toFile().setExecutable(true)).isTrue();
    }

    private static final class RecordingMavenTestRunner extends MavenTestRunner {

        private Path repositoryDir;
        private List<String> command;

        @Override
        protected TestRunResult runCommand(Path repositoryDir, List<String> command) {
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
}
