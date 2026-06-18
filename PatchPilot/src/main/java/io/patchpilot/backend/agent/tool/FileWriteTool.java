package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.WorkspacePathResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileWriteTool {

    private final WorkspacePathResolver pathResolver;

    public FileWriteTool(WorkspacePathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void write(Path repositoryDir, String relativePath, String content) {
        Path filePath = pathResolver.resolveRepositoryPath(repositoryDir, relativePath);
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(filePath, content);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write file: " + relativePath, exception);
        }
    }
}
