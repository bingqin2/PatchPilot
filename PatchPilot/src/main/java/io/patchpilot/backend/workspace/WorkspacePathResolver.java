package io.patchpilot.backend.workspace;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class WorkspacePathResolver {

    public Path resolveRepositoryPath(Path repositoryDir, String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Repository path must not be blank");
        }
        Path inputPath = Path.of(relativePath);
        if (inputPath.isAbsolute()) {
            throw new IllegalArgumentException("Repository path must be relative");
        }

        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        Path resolvedPath = repositoryRoot.resolve(inputPath).normalize();
        if (!resolvedPath.startsWith(repositoryRoot)) {
            throw new IllegalArgumentException("Repository path escapes workspace: " + relativePath);
        }
        return resolvedPath;
    }
}
