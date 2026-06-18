package io.patchpilot.backend.workspace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "patchpilot.workspace")
public class WorkspaceProperties {

    private Path rootDir = Path.of("/tmp/patchpilot/workspaces");

    public Path getRootDir() {
        return rootDir;
    }

    public void setRootDir(Path rootDir) {
        this.rootDir = rootDir;
    }
}
