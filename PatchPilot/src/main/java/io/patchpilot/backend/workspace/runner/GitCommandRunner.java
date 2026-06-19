package io.patchpilot.backend.workspace.runner;

import io.patchpilot.backend.github.config.GitHubProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GitCommandRunner {

    private static final long CLONE_TIMEOUT_SECONDS = 120;

    private final GitHubProperties gitHubProperties;

    public GitCommandRunner(GitHubProperties gitHubProperties) {
        this.gitHubProperties = gitHubProperties;
    }

    protected GitCommandRunner() {
        this.gitHubProperties = new GitHubProperties();
    }

    public GitCommandResult cloneRepository(String repositoryUrl, Path targetDir) {
        String cloneUrl = authenticatedUrl(repositoryUrl);
        return runGit(List.of(
                "git",
                "clone",
                "--depth",
                "1",
                cloneUrl,
                targetDir.toString()
        ));
    }

    public GitCommandResult createBranch(Path repositoryDir, String branchName) {
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name must not be blank");
        }
        return runGit(List.of(
                "git",
                "-C",
                repositoryDir.toString(),
                "checkout",
                "-b",
                branchName
        ));
    }

    public GitCommandResult diff(Path repositoryDir) {
        return runGit(List.of(
                "git",
                "-C",
                repositoryDir.toString(),
                "diff",
                "--"
        ));
    }

    public GitCommandResult stageAll(Path repositoryDir) {
        return runGit(List.of(
                "git",
                "-C",
                repositoryDir.toString(),
                "add",
                "--all"
        ));
    }

    public GitCommandResult commit(Path repositoryDir, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Commit message must not be blank");
        }
        return runGit(List.of(
                "git",
                "-C",
                repositoryDir.toString(),
                "commit",
                "-m",
                message
        ));
    }

    public GitCommandResult pushBranch(Path repositoryDir, String branchName) {
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name must not be blank");
        }
        return runGit(List.of(
                "git",
                "-C",
                repositoryDir.toString(),
                "push",
                "origin",
                "HEAD:" + branchName
        ));
    }

    private GitCommandResult runGit(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(CLONE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new GitCommandResult(124, "git command timed out");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return new GitCommandResult(process.exitValue(), sanitize(output));
        } catch (IOException exception) {
            return new GitCommandResult(1, sanitize(exception.getMessage()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new GitCommandResult(130, "git command interrupted");
        }
    }

    private String authenticatedUrl(String repositoryUrl) {
        String token = token();
        if (token.isBlank()) {
            return repositoryUrl;
        }
        return repositoryUrl.replace("https://", "https://x-access-token:" + token + "@");
    }

    private String sanitize(String value) {
        String token = token();
        if (value == null) {
            return "";
        }
        if (token.isBlank()) {
            return value;
        }
        return value.replace(token, "***");
    }

    private String token() {
        return gitHubProperties.getToken() == null ? "" : gitHubProperties.getToken().trim();
    }
}
