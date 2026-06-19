package io.patchpilot.backend.workspace.recovery;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class GitWorkspaceRecoveryInspector {

    public Optional<String> inspect(Path repositoryDir) {
        if (repositoryDir == null) {
            return Optional.empty();
        }
        Path gitDir = repositoryDir.toAbsolutePath().normalize().resolve(".git");
        if (!Files.isDirectory(gitDir)) {
            return Optional.empty();
        }
        return firstKnownState(gitDir);
    }

    public String appendGuidance(Path repositoryDir, String message) {
        String baseMessage = message == null ? "" : message;
        return inspect(repositoryDir)
                .map(guidance -> baseMessage + "\n" + guidance)
                .orElse(baseMessage);
    }

    private Optional<String> firstKnownState(Path gitDir) {
        if (Files.exists(gitDir.resolve("index.lock"))) {
            return guidance(".git/index.lock", "Git index lock detected");
        }
        if (Files.exists(gitDir.resolve("HEAD.lock"))) {
            return guidance(".git/HEAD.lock", "Git HEAD lock detected");
        }
        if (Files.exists(gitDir.resolve("MERGE_HEAD"))) {
            return guidance(".git/MERGE_HEAD", "Git merge in progress detected");
        }
        if (Files.exists(gitDir.resolve("rebase-merge"))) {
            return guidance(".git/rebase-merge", "Git rebase in progress detected");
        }
        if (Files.exists(gitDir.resolve("rebase-apply"))) {
            return guidance(".git/rebase-apply", "Git rebase in progress detected");
        }
        return Optional.empty();
    }

    private static Optional<String> guidance(String path, String state) {
        return Optional.of("%s (%s); manual cleanup required before retry.".formatted(state, path));
    }
}
