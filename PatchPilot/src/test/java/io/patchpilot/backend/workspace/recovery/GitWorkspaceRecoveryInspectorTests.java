package io.patchpilot.backend.workspace.recovery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GitWorkspaceRecoveryInspectorTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_return_empty_when_repository_has_no_known_git_recovery_state() throws Exception {
        Path repositoryDir = gitRepository();
        GitWorkspaceRecoveryInspector inspector = new GitWorkspaceRecoveryInspector();

        Optional<String> guidance = inspector.inspect(repositoryDir);

        assertThat(guidance).isEmpty();
    }

    @Test
    void should_detect_index_lock_file() throws Exception {
        Path repositoryDir = gitRepository();
        Files.writeString(repositoryDir.resolve(".git/index.lock"), "");
        GitWorkspaceRecoveryInspector inspector = new GitWorkspaceRecoveryInspector();

        Optional<String> guidance = inspector.inspect(repositoryDir);

        assertThat(guidance).hasValueSatisfying(value -> assertThat(value)
                .contains(".git/index.lock")
                .contains("manual cleanup required"));
    }

    @Test
    void should_detect_head_lock_file() throws Exception {
        Path repositoryDir = gitRepository();
        Files.writeString(repositoryDir.resolve(".git/HEAD.lock"), "");
        GitWorkspaceRecoveryInspector inspector = new GitWorkspaceRecoveryInspector();

        Optional<String> guidance = inspector.inspect(repositoryDir);

        assertThat(guidance).hasValueSatisfying(value -> assertThat(value)
                .contains(".git/HEAD.lock")
                .contains("manual cleanup required"));
    }

    @Test
    void should_detect_merge_in_progress() throws Exception {
        Path repositoryDir = gitRepository();
        Files.writeString(repositoryDir.resolve(".git/MERGE_HEAD"), "abc123\n");
        GitWorkspaceRecoveryInspector inspector = new GitWorkspaceRecoveryInspector();

        Optional<String> guidance = inspector.inspect(repositoryDir);

        assertThat(guidance).hasValueSatisfying(value -> assertThat(value)
                .contains("merge in progress")
                .contains(".git/MERGE_HEAD")
                .contains("manual cleanup required"));
    }

    @Test
    void should_detect_rebase_merge_in_progress() throws Exception {
        Path repositoryDir = gitRepository();
        Files.createDirectories(repositoryDir.resolve(".git/rebase-merge"));
        GitWorkspaceRecoveryInspector inspector = new GitWorkspaceRecoveryInspector();

        Optional<String> guidance = inspector.inspect(repositoryDir);

        assertThat(guidance).hasValueSatisfying(value -> assertThat(value)
                .contains("rebase in progress")
                .contains(".git/rebase-merge")
                .contains("manual cleanup required"));
    }

    @Test
    void should_detect_rebase_apply_in_progress() throws Exception {
        Path repositoryDir = gitRepository();
        Files.createDirectories(repositoryDir.resolve(".git/rebase-apply"));
        GitWorkspaceRecoveryInspector inspector = new GitWorkspaceRecoveryInspector();

        Optional<String> guidance = inspector.inspect(repositoryDir);

        assertThat(guidance).hasValueSatisfying(value -> assertThat(value)
                .contains("rebase in progress")
                .contains(".git/rebase-apply")
                .contains("manual cleanup required"));
    }

    private Path gitRepository() throws Exception {
        Path repositoryDir = tempDir.resolve("repo");
        Files.createDirectories(repositoryDir.resolve(".git"));
        return repositoryDir;
    }
}
