package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class MavenTestRunner {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(300);

    private final VerificationRunner verificationRunner;
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
        this.verificationRunner = new VerificationRunner(timeout, commandExecutionGuard, taskProcessRegistry);
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

    public TestRunResult runVerification(String taskId, Path repositoryDir, List<String> command) {
        return verificationRunner.runVerification(taskId, repositoryDir, command);
    }

    protected TestRunResult runCommand(Path repositoryDir, List<String> command) {
        return runCommand(null, repositoryDir, command);
    }

    protected TestRunResult runCommand(String taskId, Path repositoryDir, List<String> command) {
        return verificationRunner.runVerification(taskId, repositoryDir, command);
    }

    static void sanitizeProcessEnvironment(Map<String, String> environment) {
        VerificationRunner.sanitizeProcessEnvironment(environment);
    }
}
