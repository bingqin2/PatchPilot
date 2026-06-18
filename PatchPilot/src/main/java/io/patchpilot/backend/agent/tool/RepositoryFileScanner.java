package io.patchpilot.backend.agent.tool;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class RepositoryFileScanner {

    private static final Set<String> SKIPPED_DIRECTORIES = Set.of(
            ".git",
            ".idea",
            "target",
            "build",
            "node_modules"
    );

    public List<Path> listFiles(Path repositoryDir, int maxFiles) {
        if (maxFiles < 1) {
            throw new IllegalArgumentException("maxFiles must be positive");
        }
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        try (Stream<Path> paths = Files.walk(repositoryRoot)) {
            return paths
                    .filter(path -> !path.equals(repositoryRoot))
                    .filter(path -> shouldInclude(path, repositoryRoot))
                    .filter(Files::isRegularFile)
                    .map(repositoryRoot::relativize)
                    .sorted()
                    .limit(maxFiles)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to scan repository files", exception);
        }
    }

    private boolean shouldInclude(Path path, Path repositoryRoot) {
        Path relativePath = repositoryRoot.relativize(path);
        for (Path segment : relativePath) {
            if (SKIPPED_DIRECTORIES.contains(segment.toString())) {
                return false;
            }
        }
        return true;
    }
}
