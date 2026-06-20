package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandExecutionGuard {

    private final WorkspaceProperties workspaceProperties;

    public void validate(Path workingDir, List<String> command) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command must not be empty");
        }
        validateWorkingDir(workingDir);
        if (!isAllowlisted(command)) {
            throw new IllegalArgumentException("Command is not allowlisted: " + String.join(" ", command));
        }
    }

    private void validateWorkingDir(Path workingDir) {
        Path rootDir = workspaceProperties.getRootDir().toAbsolutePath().normalize();
        Path normalizedWorkingDir = workingDir.toAbsolutePath().normalize();
        if (!normalizedWorkingDir.startsWith(rootDir)) {
            throw new IllegalArgumentException("Command directory escapes workspace root: " + normalizedWorkingDir);
        }
    }

    private static boolean isAllowlisted(List<String> command) {
        if (command.equals(List.of("./mvnw", "test")) || command.equals(List.of("mvn", "test"))) {
            return true;
        }
        if (command.size() < 2 || !"git".equals(command.get(0))) {
            return false;
        }
        if (command.size() >= 6 && command.subList(0, 4).equals(List.of("git", "clone", "--depth", "1"))) {
            return command.size() == 6 && isGitHubHttpsRepositoryUrl(command.get(4));
        }
        if (command.size() < 4 || !"-C".equals(command.get(1))) {
            return false;
        }
        List<String> gitArgs = command.subList(3, command.size());
        return gitArgs.equals(List.of("status", "--short"))
                || gitArgs.equals(List.of("diff", "--"))
                || gitArgs.equals(List.of("add", "--all"))
                || isCreateBranch(gitArgs)
                || isCommit(gitArgs)
                || isPush(gitArgs);
    }

    private static boolean isCreateBranch(List<String> gitArgs) {
        return gitArgs.size() == 3
                && gitArgs.subList(0, 2).equals(List.of("checkout", "-b"))
                && isPatchPilotBranch(gitArgs.get(2));
    }

    private static boolean isCommit(List<String> gitArgs) {
        if (gitArgs.size() == 3
                && gitArgs.subList(0, 2).equals(List.of("commit", "-m"))
                && StringUtils.hasText(gitArgs.get(2))) {
            return true;
        }
        return gitArgs.size() == 7
                && gitArgs.subList(0, 6).equals(List.of(
                "-c",
                "user.name=PatchPilot",
                "-c",
                "user.email=patchpilot@example.com",
                "commit",
                "-m"
        ))
                && StringUtils.hasText(gitArgs.get(6));
    }

    private static boolean isPush(List<String> gitArgs) {
        return gitArgs.size() == 3
                && gitArgs.subList(0, 2).equals(List.of("push", "origin"))
                && gitArgs.get(2).startsWith("HEAD:")
                && isPatchPilotBranch(gitArgs.get(2).substring("HEAD:".length()));
    }

    private static boolean isPatchPilotBranch(String branchName) {
        return branchName != null && branchName.startsWith("patchpilot/") && branchName.length() > "patchpilot/".length();
    }

    private static boolean isGitHubHttpsRepositoryUrl(String repositoryUrl) {
        return repositoryUrl != null
                && (repositoryUrl.startsWith("https://github.com/")
                || repositoryUrl.startsWith("https://x-access-token:") && repositoryUrl.contains("@github.com/"))
                && repositoryUrl.endsWith(".git");
    }
}
