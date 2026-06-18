package io.patchpilot.backend.agent.tool;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class RepoTreeTool {

    private static final int MAX_TREE_FILES = 500;

    private final RepositoryFileScanner fileScanner;

    public RepoTreeTool(RepositoryFileScanner fileScanner) {
        this.fileScanner = fileScanner;
    }

    public String tree(Path repositoryDir) {
        return String.join("\n", fileScanner.listFiles(repositoryDir, MAX_TREE_FILES).stream()
                .map(Path::toString)
                .toList());
    }
}
