package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositoryInspectionToolsTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_list_sorted_repository_files_and_skip_noisy_directories() throws Exception {
        Files.createDirectories(repositoryDir.resolve("src/main/java"));
        Files.createDirectories(repositoryDir.resolve(".git/objects"));
        Files.createDirectories(repositoryDir.resolve("target/classes"));
        Files.writeString(repositoryDir.resolve("README.md"), "# Demo\n");
        Files.writeString(repositoryDir.resolve("src/main/java/App.java"), "class App {}\n");
        Files.writeString(repositoryDir.resolve(".git/config"), "ignored\n");
        Files.writeString(repositoryDir.resolve("target/classes/App.class"), "ignored\n");
        RepoTreeTool tool = new RepoTreeTool(scanner());

        String tree = tool.tree(repositoryDir);

        assertThat(tree).isEqualTo("""
                README.md
                src/main/java/App.java""");
    }

    @Test
    void should_search_matching_lines_with_path_and_line_number() throws Exception {
        Files.createDirectories(repositoryDir.resolve("src/main/java"));
        Files.writeString(repositoryDir.resolve("README.md"), "PatchPilot\nNo match\n");
        Files.writeString(repositoryDir.resolve("src/main/java/App.java"), "class App {\n  String name = \"PatchPilot\";\n}\n");
        CodeSearchTool tool = new CodeSearchTool(scanner());

        String matches = tool.search(repositoryDir, "PatchPilot");

        assertThat(matches).isEqualTo("""
                README.md:1: PatchPilot
                src/main/java/App.java:2:   String name = "PatchPilot";""");
    }

    @Test
    void should_skip_generated_directories_when_searching() throws Exception {
        Files.createDirectories(repositoryDir.resolve("target/classes"));
        Files.writeString(repositoryDir.resolve("target/classes/App.java"), "PatchPilot\n");
        CodeSearchTool tool = new CodeSearchTool(scanner());

        String matches = tool.search(repositoryDir, "PatchPilot");

        assertThat(matches).isEmpty();
    }

    @Test
    void should_reject_blank_search_query() {
        CodeSearchTool tool = new CodeSearchTool(scanner());

        assertThatThrownBy(() -> tool.search(repositoryDir, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Search query must not be blank");
    }

    @Test
    void should_limit_search_matches() throws Exception {
        Files.writeString(repositoryDir.resolve("README.md"), "PatchPilot\n".repeat(60));
        CodeSearchTool tool = new CodeSearchTool(scanner());

        String matches = tool.search(repositoryDir, "PatchPilot");

        assertThat(matches.lines()).hasSize(50);
    }

    @Test
    void should_reject_repository_tree_outside_workspace_root() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        Path outsideRepositoryDir = repositoryDir.getParent().resolve("outside-repo");
        RepoTreeTool tool = new RepoTreeTool(new RepositoryFileScanner(properties));

        assertThatThrownBy(() -> tool.tree(outsideRepositoryDir))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository directory escapes workspace root: " + outsideRepositoryDir.toAbsolutePath().normalize());
    }

    @Test
    void should_reject_code_search_outside_workspace_root() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        Path outsideRepositoryDir = repositoryDir.getParent().resolve("outside-repo");
        CodeSearchTool tool = new CodeSearchTool(new RepositoryFileScanner(properties));

        assertThatThrownBy(() -> tool.search(outsideRepositoryDir, "PatchPilot"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Repository directory escapes workspace root: " + outsideRepositoryDir.toAbsolutePath().normalize());
    }

    private RepositoryFileScanner scanner() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(repositoryDir);
        return new RepositoryFileScanner(properties);
    }
}
