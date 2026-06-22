package io.patchpilot.backend.configuration;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.task.config.ReviewApprovalProperties;
import io.patchpilot.backend.task.config.TaskQueueProperties;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationSummaryServiceTests {

    @Test
    void should_expose_configured_review_approval_operators() {
        AgentProperties agentProperties = new AgentProperties();
        agentProperties.setProvider("openai-compatible");
        agentProperties.setModel("gpt-5.5");
        agentProperties.setBaseUrl("https://api.example.test/v1");
        GitHubProperties gitHubProperties = new GitHubProperties();
        WorkspaceProperties workspaceProperties = new WorkspaceProperties();
        workspaceProperties.setRootDir(Path.of("/tmp/patchpilot/workspaces"));
        TaskQueueProperties taskQueueProperties = new TaskQueueProperties();
        SafetyProperties safetyProperties = new SafetyProperties();
        safetyProperties.setAllowedTriggerUsers(List.of(" bingqin2 ", "BINGQIN2", "local-operator", ""));
        safetyProperties.setAllowedRepositories(List.of(" bingqin2/PatchPilot ", "BINGQIN2/PATCHPILOT", "octocat/hello-world"));
        ReviewApprovalProperties reviewApprovalProperties = new ReviewApprovalProperties();
        reviewApprovalProperties.setAllowedOperators(List.of(" release-captain ", "RELEASE-CAPTAIN", "local-operator", ""));

        ConfigurationSummaryVo summary = new ConfigurationSummaryService(
                agentProperties,
                gitHubProperties,
                workspaceProperties,
                taskQueueProperties,
                safetyProperties,
                reviewApprovalProperties
        ).getConfigurationSummary();

        assertThat(summary.reviewApprovalAllowedOperators()).containsExactly("release-captain", "local-operator");
        assertThat(summary.allowedTriggerUsers()).containsExactly("bingqin2", "local-operator");
        assertThat(summary.allowedRepositories()).containsExactly("bingqin2/PatchPilot", "octocat/hello-world");
        assertThat(summary.triggerUserAllowlistConfigured()).isTrue();
        assertThat(summary.repositoryAllowlistConfigured()).isTrue();
        assertThat(summary.reviewApprovalAllowlistConfigured()).isTrue();
    }
}
