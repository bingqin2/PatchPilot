package io.patchpilot.backend.agent.tool;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class CodeSearchTool {

    private static final int MAX_SEARCH_FILES = 1_000;
    private static final int MAX_MATCHES = 50;

    private final RepositoryFileScanner fileScanner;

    public CodeSearchTool(RepositoryFileScanner fileScanner) {
        this.fileScanner = fileScanner;
    }

    public String search(Path repositoryDir, String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query must not be blank");
        }

        List<String> matches = new ArrayList<>();
        for (Path relativePath : fileScanner.listFiles(repositoryDir, MAX_SEARCH_FILES)) {
            collectMatches(repositoryDir, relativePath, query, matches);
            if (matches.size() >= MAX_MATCHES) {
                break;
            }
        }
        return String.join("\n", matches);
    }

    private void collectMatches(Path repositoryDir, Path relativePath, String query, List<String> matches) {
        Path filePath = repositoryDir.toAbsolutePath().normalize().resolve(relativePath).normalize();
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                if (line.contains(query)) {
                    matches.add(relativePath + ":" + (index + 1) + ": " + line);
                }
                if (matches.size() >= MAX_MATCHES) {
                    return;
                }
            }
        } catch (MalformedInputException ignored) {
            // Binary or non-UTF-8 files are not useful for text search.
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to search file: " + relativePath, exception);
        }
    }
}
