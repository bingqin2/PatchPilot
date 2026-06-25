package io.patchpilot.backend.configuration;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.dashboard.DashboardLinkService;
import io.patchpilot.backend.dashboard.config.DashboardProperties;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.language.config.RepositoryPreflightProperties;
import io.patchpilot.backend.safety.GeneratedDiffSafetyPolicy;
import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
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
        taskQueueProperties.setWorkerHeartbeatStaleMs(25_000);
        SafetyProperties safetyProperties = new SafetyProperties();
        safetyProperties.setAllowedTriggerUsers(List.of(" bingqin2 ", "BINGQIN2", "local-operator", ""));
        safetyProperties.setAllowedRepositories(List.of(" bingqin2/PatchPilot ", "BINGQIN2/PATCHPILOT", "octocat/hello-world"));
        safetyProperties.setRejectedTriggerQuarantineEnabled(true);
        safetyProperties.setRejectedTriggerQuarantineWindowMs(900_000);
        safetyProperties.setRejectedTriggerQuarantineThreshold(4);
        safetyProperties.setRejectedTriggerQuarantineCooldownMs(1_800_000);
        ReviewApprovalProperties reviewApprovalProperties = new ReviewApprovalProperties();
        reviewApprovalProperties.setAllowedOperators(List.of(" release-captain ", "RELEASE-CAPTAIN", "local-operator", ""));
        AdminApiSecurityProperties adminApiSecurityProperties = new AdminApiSecurityProperties();
        adminApiSecurityProperties.setAdminToken("test-admin-token");
        RepositoryPreflightProperties repositoryPreflightProperties = new RepositoryPreflightProperties();
        repositoryPreflightProperties.setAllowedRootDirs(List.of(
                Path.of("/tmp/patchpilot/workspaces"),
                Path.of("docs/demo-repositories"),
                Path.of("/tmp/patchpilot/workspaces")
        ));
        DashboardProperties dashboardProperties = new DashboardProperties();
        dashboardProperties.setBaseUrl("https://dashboard.example.test/");

        ConfigurationSummaryVo summary = new ConfigurationSummaryService(
                agentProperties,
                gitHubProperties,
                workspaceProperties,
                taskQueueProperties,
                safetyProperties,
                reviewApprovalProperties,
                adminApiSecurityProperties,
                new GeneratedDiffSafetyPolicy(),
                repositoryPreflightProperties,
                new DashboardLinkService(dashboardProperties)
        ).getConfigurationSummary();

        assertThat(summary.reviewApprovalAllowedOperators()).containsExactly("release-captain", "local-operator");
        assertThat(summary.allowedTriggerUsers()).containsExactly("bingqin2", "local-operator");
        assertThat(summary.allowedRepositories()).containsExactly("bingqin2/PatchPilot", "octocat/hello-world");
        assertThat(summary.triggerUserAllowlistConfigured()).isTrue();
        assertThat(summary.repositoryAllowlistConfigured()).isTrue();
        assertThat(summary.reviewApprovalAllowlistConfigured()).isTrue();
        assertThat(summary.adminTokenConfigured()).isTrue();
        assertThat(summary.generatedDiffRiskGateEnabled()).isTrue();
        assertThat(summary.generatedDiffProtectedPathCount()).isEqualTo(15);
        assertThat(summary.rejectedTriggerQuarantineEnabled()).isTrue();
        assertThat(summary.rejectedTriggerQuarantineWindowMs()).isEqualTo(900_000);
        assertThat(summary.rejectedTriggerQuarantineThreshold()).isEqualTo(4);
        assertThat(summary.rejectedTriggerQuarantineCooldownMs()).isEqualTo(1_800_000);
        assertThat(summary.queueWorkerHeartbeatStaleMs()).isEqualTo(25_000);
        assertThat(summary.repositoryPreflightAllowedRootDirs()).containsExactly(
                "/tmp/patchpilot/workspaces",
                Path.of("..").resolve("docs/demo-repositories").toAbsolutePath().normalize().toString()
        );
        assertThat(summary.dashboardBaseUrlConfigured()).isTrue();
    }
}
