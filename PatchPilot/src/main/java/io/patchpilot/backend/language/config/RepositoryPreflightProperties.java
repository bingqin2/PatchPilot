package io.patchpilot.backend.language.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "patchpilot.repository-preflight")
public class RepositoryPreflightProperties {

    private List<Path> allowedRootDirs = new ArrayList<>(List.of(
            Path.of("."),
            Path.of("docs/demo-repositories")
    ));

    public List<Path> getAllowedRootDirs() {
        return allowedRootDirs;
    }

    public void setAllowedRootDirs(List<Path> allowedRootDirs) {
        this.allowedRootDirs = allowedRootDirs == null ? new ArrayList<>() : allowedRootDirs;
    }

    public List<Path> normalizedAllowedRootDirs() {
        return allowedRootDirs.stream()
                .filter(rootDir -> rootDir != null && !rootDir.toString().isBlank())
                .map(RepositoryPreflightProperties::normalizeAllowedRootDir)
                .distinct()
                .toList();
    }

    private static Path normalizeAllowedRootDir(Path rootDir) {
        if (rootDir.isAbsolute()) {
            return rootDir.toAbsolutePath().normalize();
        }

        Path currentDirectoryPath = rootDir.toAbsolutePath().normalize();
        if (Files.isDirectory(currentDirectoryPath)) {
            return currentDirectoryPath;
        }

        Path parentDirectoryPath = Path.of("..").resolve(rootDir).toAbsolutePath().normalize();
        if (Files.isDirectory(parentDirectoryPath)) {
            return parentDirectoryPath;
        }
        return currentDirectoryPath;
    }
}
