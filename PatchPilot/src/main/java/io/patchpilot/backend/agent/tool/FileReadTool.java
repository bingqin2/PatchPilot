package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.WorkspacePathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FileReadTool {

    private final WorkspacePathResolver pathResolver;

    public String read(Path repositoryDir, String relativePath) {
        Path filePath = pathResolver.resolveRepositoryPath(repositoryDir, relativePath);
        try {
            return Files.readString(filePath);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read file: " + relativePath, exception);
        }
    }
}
