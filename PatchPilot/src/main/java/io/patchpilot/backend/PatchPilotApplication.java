package io.patchpilot.backend;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.github.config.GitHubProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({WorkspaceProperties.class, GitHubProperties.class})
@EnableScheduling
public class PatchPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatchPilotApplication.class, args);
    }

}
