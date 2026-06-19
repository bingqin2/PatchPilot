package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
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

    public MavenTestRunner() {
        this(DEFAULT_TIMEOUT);
    }

    MavenTestRunner(Duration timeout) {
        this.timeout = timeout;
    }

    public TestRunResult runTests(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("mvnw"))) {
            return runCommand(repositoryRoot, List.of("./mvnw", "test"));
        }
        if (Files.isRegularFile(repositoryRoot.resolve("pom.xml"))) {
            return runCommand(repositoryRoot, List.of("mvn", "test"));
        }
        throw new IllegalStateException("Unsupported repository: no mvnw or pom.xml found");
    }

    protected TestRunResult runCommand(Path repositoryDir, List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(repositoryDir.toFile());
        processBuilder.redirectErrorStream(true);
        String commandText = String.join(" ", command);
        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new TestRunResult(commandText, 124, "maven test command timed out");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return new TestRunResult(commandText, process.exitValue(), output);
        } catch (IOException exception) {
            return new TestRunResult(commandText, 1, exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new TestRunResult(commandText, 130, "maven test command interrupted");
        }
    }
}
