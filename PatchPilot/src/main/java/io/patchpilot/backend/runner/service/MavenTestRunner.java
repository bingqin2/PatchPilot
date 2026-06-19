package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MavenTestRunner {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(300);

    private final Duration timeout;
    private final CommandExecutionGuard commandExecutionGuard;
    private final TaskProcessRegistry taskProcessRegistry;

    public MavenTestRunner() {
        this(DEFAULT_TIMEOUT, new CommandExecutionGuard(new WorkspaceProperties()), new TaskProcessRegistry());
    }

    @Autowired
    public MavenTestRunner(CommandExecutionGuard commandExecutionGuard, TaskProcessRegistry taskProcessRegistry) {
        this(DEFAULT_TIMEOUT, commandExecutionGuard, taskProcessRegistry);
    }

    MavenTestRunner(Duration timeout) {
        this(timeout, new CommandExecutionGuard(new WorkspaceProperties()), new TaskProcessRegistry());
    }

    MavenTestRunner(Duration timeout, CommandExecutionGuard commandExecutionGuard) {
        this(timeout, commandExecutionGuard, new TaskProcessRegistry());
    }

    MavenTestRunner(Duration timeout, CommandExecutionGuard commandExecutionGuard, TaskProcessRegistry taskProcessRegistry) {
        this.timeout = timeout;
        this.commandExecutionGuard = commandExecutionGuard;
        this.taskProcessRegistry = taskProcessRegistry;
    }

    public TestRunResult runTests(Path repositoryDir) {
        return runTests(null, repositoryDir);
    }

    public TestRunResult runTests(String taskId, Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("mvnw"))) {
            return runCommand(taskId, repositoryRoot, List.of("./mvnw", "test"));
        }
        if (Files.isRegularFile(repositoryRoot.resolve("pom.xml"))) {
            return runCommand(taskId, repositoryRoot, List.of("mvn", "test"));
        }
        throw new IllegalStateException("Unsupported repository: no mvnw or pom.xml found");
    }

    protected TestRunResult runCommand(Path repositoryDir, List<String> command) {
        return runCommand(null, repositoryDir, command);
    }

    protected TestRunResult runCommand(String taskId, Path repositoryDir, List<String> command) {
        commandExecutionGuard.validate(repositoryDir, command);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(repositoryDir.toFile());
        processBuilder.redirectErrorStream(true);
        String commandText = String.join(" ", command);
        try {
            Process process = processBuilder.start();
            registerProcess(taskId, process);
            try {
                boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    return new TestRunResult(commandText, 124, "maven test command timed out");
                }
                String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                return new TestRunResult(commandText, process.exitValue(), output);
            } finally {
                unregisterProcess(taskId, process);
            }
        } catch (IOException exception) {
            return new TestRunResult(commandText, 1, exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new TestRunResult(commandText, 130, "maven test command interrupted");
        }
    }

    private void registerProcess(String taskId, Process process) {
        if (taskId != null && !taskId.isBlank()) {
            taskProcessRegistry.register(taskId, process);
        }
    }

    private void unregisterProcess(String taskId, Process process) {
        if (taskId != null && !taskId.isBlank()) {
            taskProcessRegistry.unregister(taskId, process);
        }
    }
}
