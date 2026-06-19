package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.WorkspacePathResolver;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileToolsTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_read_utf8_file_inside_repository() throws Exception {
        Files.writeString(repositoryDir.resolve("README.md"), "hello\n");
        FileReadTool tool = new FileReadTool(resolver());

        String content = tool.read(repositoryDir, "README.md");

        assertThat(content).isEqualTo("hello\n");
    }

    @Test
    void should_write_utf8_file_inside_repository_and_create_parent_directories() throws Exception {
        FileWriteTool tool = new FileWriteTool(resolver());

        tool.write(repositoryDir, "src/main/App.java", "class App {}\n");

        assertThat(Files.readString(repositoryDir.resolve("src/main/App.java"))).isEqualTo("class App {}\n");
    }

    @Test
    void should_reject_read_path_traversal() {
        FileReadTool tool = new FileReadTool(resolver());

        assertThatThrownBy(() -> tool.read(repositoryDir, "../outside.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path escapes workspace: ../outside.txt");
    }

    @Test
    void should_reject_write_path_traversal() {
        FileWriteTool tool = new FileWriteTool(resolver());

        assertThatThrownBy(() -> tool.write(repositoryDir, "../outside.txt", "bad"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository path escapes workspace: ../outside.txt");
    }

    private WorkspacePathResolver resolver() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        return new WorkspacePathResolver(properties);
    }
}
