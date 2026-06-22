package io.patchpilot.backend.runner.service;

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
public class VerificationRunner {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(300);

    private final Duration timeout;
    private final CommandExecutionGuard commandExecutionGuard;
    private final TaskProcessRegistry taskProcessRegistry;

    @Autowired
    public VerificationRunner(CommandExecutionGuard commandExecutionGuard, TaskProcessRegistry taskProcessRegistry) {
        this(DEFAULT_TIMEOUT, commandExecutionGuard, taskProcessRegistry);
    }

    VerificationRunner(
            Duration timeout,
            CommandExecutionGuard commandExecutionGuard,
            TaskProcessRegistry taskProcessRegistry
    ) {
        this.timeout = timeout;
        this.commandExecutionGuard = commandExecutionGuard;
        this.taskProcessRegistry = taskProcessRegistry;
    }

    public TestRunResult runVerification(String taskId, Path repositoryDir, List<String> command) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        commandExecutionGuard.validate(repositoryRoot, command);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(repositoryRoot.toFile());
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
                            ? "verification command timed out"
                            : output + System.lineSeparator() + "verification command timed out";
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
            return new TestRunResult(commandText, 130, "verification command interrupted");
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
