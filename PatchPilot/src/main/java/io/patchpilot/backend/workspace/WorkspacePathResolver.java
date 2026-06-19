package io.patchpilot.backend.workspace;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class WorkspacePathResolver {

    private final WorkspaceProperties workspaceProperties;

    @Autowired
    public WorkspacePathResolver(WorkspaceProperties workspaceProperties) {
        this.workspaceProperties = workspaceProperties;
    }

    public WorkspacePathResolver() {
        this.workspaceProperties = new WorkspaceProperties();
    }

    public Path resolveRepositoryPath(Path repositoryDir, String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Repository path must not be blank");
        }
        Path inputPath = Path.of(relativePath);
        if (inputPath.isAbsolute()) {
            throw new IllegalArgumentException("Repository path must be relative");
        }

        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        validateRepositoryRoot(repositoryRoot);
        Path resolvedPath = repositoryRoot.resolve(inputPath).normalize();
        if (!resolvedPath.startsWith(repositoryRoot)) {
            throw new IllegalArgumentException("Repository path escapes workspace: " + relativePath);
        }
        return resolvedPath;
    }

    private void validateRepositoryRoot(Path repositoryRoot) {
        Path workspaceRoot = workspaceProperties.getRootDir().toAbsolutePath().normalize();
        if (!repositoryRoot.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("Repository directory escapes workspace root: " + repositoryRoot);
        }
    }
}
