package io.patchpilot.backend.workspace;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspacePathResolverTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_resolve_nested_relative_path_inside_repository() {
        WorkspacePathResolver resolver = new WorkspacePathResolver();

        Path resolved = resolver.resolveRepositoryPath(repositoryDir, "src/main/App.java");

        assertThat(resolved).isEqualTo(repositoryDir.resolve("src/main/App.java").toAbsolutePath().normalize());
    }

    @Test
    void should_reject_blank_path() {
        WorkspacePathResolver resolver = new WorkspacePathResolver();

        assertThatThrownBy(() -> resolver.resolveRepositoryPath(repositoryDir, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path must not be blank");
    }

    @Test
    void should_reject_absolute_path() {
        WorkspacePathResolver resolver = new WorkspacePathResolver();

        assertThatThrownBy(() -> resolver.resolveRepositoryPath(repositoryDir, repositoryDir.resolve("README.md").toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path must be relative");
    }

    @Test
    void should_reject_path_traversal() {
        WorkspacePathResolver resolver = new WorkspacePathResolver();

        assertThatThrownBy(() -> resolver.resolveRepositoryPath(repositoryDir, "../outside.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path escapes workspace: ../outside.txt");
    }
}
