package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class MavenTestRunner {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(300);

    private final Duration timeout;
    private final CommandExecutionGuard commandExecutionGuard;
    private final TaskProcessRegistry taskProcessRegistry;
    private final JavaMavenLanguageAdapter languageAdapter;

    public MavenTestRunner() {
        this(DEFAULT_TIMEOUT, new CommandExecutionGuard(new WorkspaceProperties()), new TaskProcessRegistry(), new JavaMavenLanguageAdapter());
    }

    @Autowired
    public MavenTestRunner(
            CommandExecutionGuard commandExecutionGuard,
            TaskProcessRegistry taskProcessRegistry,
            JavaMavenLanguageAdapter languageAdapter
    ) {
        this(DEFAULT_TIMEOUT, commandExecutionGuard, taskProcessRegistry, languageAdapter);
    }

    MavenTestRunner(Duration timeout) {
        this(timeout, new CommandExecutionGuard(new WorkspaceProperties()), new TaskProcessRegistry(), new JavaMavenLanguageAdapter());
    }

    MavenTestRunner(Duration timeout, CommandExecutionGuard commandExecutionGuard) {
        this(timeout, commandExecutionGuard, new TaskProcessRegistry(), new JavaMavenLanguageAdapter());
    }

    MavenTestRunner(Duration timeout, CommandExecutionGuard commandExecutionGuard, TaskProcessRegistry taskProcessRegistry) {
        this(timeout, commandExecutionGuard, taskProcessRegistry, new JavaMavenLanguageAdapter());
    }

    MavenTestRunner(
            Duration timeout,
            CommandExecutionGuard commandExecutionGuard,
            TaskProcessRegistry taskProcessRegistry,
            JavaMavenLanguageAdapter languageAdapter
    ) {
        this.timeout = timeout;
        this.commandExecutionGuard = commandExecutionGuard;
        this.taskProcessRegistry = taskProcessRegistry;
        this.languageAdapter = languageAdapter;
    }

    public TestRunResult runTests(Path repositoryDir) {
        return runTests(null, repositoryDir);
    }

    public TestRunResult runTests(String taskId, Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        LanguageDetectionResult detectionResult = languageAdapter.detect(repositoryRoot);
        if (detectionResult.supported()) {
            return runCommand(taskId, repositoryRoot, detectionResult.verificationCommand());
        }
        throw new IllegalStateException(detectionResult.reason());
    }

    protected TestRunResult runCommand(Path repositoryDir, List<String> command) {
        return runCommand(null, repositoryDir, command);
    }

    protected TestRunResult runCommand(String taskId, Path repositoryDir, List<String> command) {
        commandExecutionGuard.validate(repositoryDir, command);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(repositoryDir.toFile());
        processBuilder.redirectErrorStream(true);
        sanitizeProcessEnvironment(processBuilder.environment());
        String commandText = String.join(" ", command);
        try {
            Process process = processBuilder.start();
            registerProcess(taskId, process);
            CompletableFuture<String> outputFuture = readOutputAsync(process);
            try {
                boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    String output = outputFuture.get(5, TimeUnit.SECONDS);
                    String timeoutOutput = output.isBlank()
                            ? "maven test command timed out"
                            : output + System.lineSeparator() + "maven test command timed out";
                    return new TestRunResult(commandText, 124, timeoutOutput);
                }
                String output = outputFuture.get(5, TimeUnit.SECONDS);
                return new TestRunResult(commandText, process.exitValue(), output);
            } finally {
                unregisterProcess(taskId, process);
            }
        } catch (IOException exception) {
            return new TestRunResult(commandText, 1, exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new TestRunResult(commandText, 130, "maven test command interrupted");
        } catch (Exception exception) {
            return new TestRunResult(commandText, 1, exception.getMessage());
        }
    }

    private CompletableFuture<String> readOutputAsync(Process process) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                return exception.getMessage();
            }
        });
    }

    private void registerProcess(String taskId, Process process) {
        if (StringUtils.hasText(taskId)) {
            taskProcessRegistry.register(taskId, process);
        }
    }

    private void unregisterProcess(String taskId, Process process) {
        if (StringUtils.hasText(taskId)) {
            taskProcessRegistry.unregister(taskId, process);
        }
    }

    static void sanitizeProcessEnvironment(Map<String, String> environment) {
        environment.keySet().removeIf(key -> key.startsWith("PATCHPILOT_"));
        environment.remove("SPRING_PROFILES_ACTIVE");
    }
}
