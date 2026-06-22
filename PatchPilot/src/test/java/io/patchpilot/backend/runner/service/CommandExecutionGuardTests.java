package io.patchpilot.backend.runner.service;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandExecutionGuardTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_allow_mvp_git_and_maven_commands_inside_workspace_root() {
        CommandExecutionGuard guard = new CommandExecutionGuard(properties());
        Path repositoryDir = tempDir.resolve("task-123").resolve("repo");

        guard.validate(repositoryDir, List.of("./mvnw", "test"));
        guard.validate(repositoryDir, List.of("mvn", "test"));
        guard.validate(repositoryDir, List.of("./gradlew", "test"));
        guard.validate(repositoryDir, List.of("gradle", "test"));
        guard.validate(repositoryDir, List.of("npm", "test"));
        guard.validate(repositoryDir, List.of("pnpm", "test"));
        guard.validate(repositoryDir, List.of("yarn", "test"));
        guard.validate(repositoryDir, List.of("bun", "test"));
        guard.validate(repositoryDir, List.of("python3", "-m", "pytest"));
        guard.validate(repositoryDir, List.of("tox"));
        guard.validate(repositoryDir, List.of("nox"));
        guard.validate(repositoryDir, List.of("hatch", "test"));
        guard.validate(repositoryDir, List.of("poetry", "run", "pytest"));
        guard.validate(repositoryDir, List.of("uv", "run", "pytest"));
        guard.validate(repositoryDir, List.of("git", "-C", repositoryDir.toString(), "status", "--short"));
        guard.validate(repositoryDir, List.of("git", "-C", repositoryDir.toString(), "diff", "--"));
        guard.validate(repositoryDir, List.of("git", "-C", repositoryDir.toString(), "checkout", "-b", "patchpilot/task-123"));
        guard.validate(repositoryDir, List.of("git", "-C", repositoryDir.toString(), "add", "--all"));
        guard.validate(repositoryDir, List.of("git", "-C", repositoryDir.toString(), "commit", "-m", "PatchPilot task task-123"));
        guard.validate(repositoryDir, List.of(
                "git",
                "-C",
                repositoryDir.toString(),
                "-c",
                "user.name=PatchPilot",
                "-c",
                "user.email=patchpilot@example.com",
                "commit",
                "-m",
                "PatchPilot task task-123"
        ));
        guard.validate(repositoryDir, List.of("git", "-C", repositoryDir.toString(), "push", "origin", "HEAD:patchpilot/task-123"));
        guard.validate(repositoryDir, List.of("git", "clone", "--depth", "1", "https://github.com/octocat/hello-world.git", repositoryDir.toString()));
    }

    @Test
    void should_reject_disallowed_command() {
        CommandExecutionGuard guard = new CommandExecutionGuard(properties());
        Path repositoryDir = tempDir.resolve("task-123").resolve("repo");

        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("rm", "-rf", repositoryDir.toString())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: rm -rf " + repositoryDir);
    }

    @Test
    void should_reject_arbitrary_node_package_scripts() {
        CommandExecutionGuard guard = new CommandExecutionGuard(properties());
        Path repositoryDir = tempDir.resolve("task-123").resolve("repo");

        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("pnpm", "run", "build")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: pnpm run build");
        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("yarn", "install")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: yarn install");
        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("bun", "install")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: bun install");
    }

    @Test
    void should_reject_arbitrary_python_runner_commands() {
        CommandExecutionGuard guard = new CommandExecutionGuard(properties());
        Path repositoryDir = tempDir.resolve("task-123").resolve("repo");

        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("poetry", "install")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: poetry install");
        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("uv", "pip", "install", "-r", "requirements.txt")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: uv pip install -r requirements.txt");
        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("tox", "-e", "lint")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: tox -e lint");
        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("nox", "-s", "lint")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: nox -s lint");
        assertThatThrownBy(() -> guard.validate(repositoryDir, List.of("hatch", "run", "test")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command is not allowlisted: hatch run test");
    }

    @Test
    void should_reject_commands_outside_workspace_root() {
        CommandExecutionGuard guard = new CommandExecutionGuard(properties());
        Path outsideDir = tempDir.getParent().resolve("outside");

        assertThatThrownBy(() -> guard.validate(outsideDir, List.of("mvn", "test")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command directory escapes workspace root: " + outsideDir.toAbsolutePath().normalize());
    }

    @Test
    void should_reject_empty_command() {
        CommandExecutionGuard guard = new CommandExecutionGuard(properties());

        assertThatThrownBy(() -> guard.validate(tempDir, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command must not be empty");
    }

    private WorkspaceProperties properties() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(tempDir);
        return properties;
    }
}
