package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubPublishReadinessServiceTests {

    @Test
    void should_report_ready_when_token_and_repository_access_are_ready() {
        GitHubPublishReadinessService service = new GitHubPublishReadinessService(
                demoProperties("bingqin2", "PatchPilot"),
                new GitHubCredentialReadinessService(() -> credential("READY", true, "GitHub API accepted the configured token.")),
                new GitHubRepositoryAccessReadinessService((owner, repository) ->
                        repositoryAccess("READY", true, true, owner + "/" + repository, "main", "GitHub token can read repository " + owner + "/" + repository + "."))
        );

        GitHubPublishReadinessVo readiness = service.getReadiness("", "");

        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.publishReady()).isTrue();
        assertThat(readiness.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(readiness.summary()).isEqualTo("GitHub publish path is ready for PatchPilot push and Pull Request creation.");
        assertThat(readiness.nextAction()).isEqualTo("Continue with the live /agent fix demo.");
        assertThat(readiness.safePublishCommand()).isEqualTo("git push origin HEAD:<patchpilot-branch>");
        assertThat(readiness.sideEffectContract()).contains("does not run git push");
        assertThat(readiness.sideEffectContract()).contains("does not create Pull Requests");
        assertThat(readiness.checks()).extracting("name")
                .containsExactly("GitHub token", "Repository access", "Publish command safety");
        assertThat(readiness.checks()).extracting("status")
                .containsExactly("READY", "READY", "READY");
        assertThat(readiness.evidenceNotes()).contains("Default branch: main");
    }

    @Test
    void should_block_publish_when_github_token_is_missing() {
        GitHubPublishReadinessService service = new GitHubPublishReadinessService(
                demoProperties("bingqin2", "PatchPilot"),
                new GitHubCredentialReadinessService(() -> credential("NEEDS_ATTENTION", false, "GitHub token is not configured.")),
                new GitHubRepositoryAccessReadinessService((owner, repository) ->
                        repositoryAccess("NEEDS_ATTENTION", false, true, owner + "/" + repository, null, "GitHub token is not configured."))
        );

        GitHubPublishReadinessVo readiness = service.getReadiness(null, null);

        assertThat(readiness.status()).isEqualTo("BLOCKED");
        assertThat(readiness.publishReady()).isFalse();
        assertThat(readiness.summary()).isEqualTo("GitHub publish path is blocked before PatchPilot can push branches or create Pull Requests.");
        assertThat(readiness.nextAction()).isEqualTo("Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.");
        assertThat(readiness.checks()).extracting("status").containsExactly("BLOCKED", "BLOCKED", "READY");
        assertThat(readiness.evidenceNotes()).contains("Token configured: false");
    }

    @Test
    void should_require_repository_target_before_publish_readiness_can_pass() {
        GitHubPublishReadinessService service = new GitHubPublishReadinessService(
                demoProperties("", ""),
                new GitHubCredentialReadinessService(() -> credential("READY", true, "GitHub API accepted the configured token.")),
                new GitHubRepositoryAccessReadinessService((owner, repository) ->
                        repositoryAccess("NEEDS_ATTENTION", true, false, "", null, "Repository owner and name are required for the access probe."))
        );

        GitHubPublishReadinessVo readiness = service.getReadiness(" ", " ");

        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.publishReady()).isFalse();
        assertThat(readiness.repositoryConfigured()).isFalse();
        assertThat(readiness.nextAction()).isEqualTo("Set PATCHPILOT_DEMO_REPOSITORY_OWNER and PATCHPILOT_DEMO_REPOSITORY_NAME or pass owner and repository query parameters.");
        assertThat(readiness.checks()).extracting("status").containsExactly("READY", "NEEDS_ATTENTION", "READY");
    }

    @Test
    void should_report_attention_when_repository_access_probe_fails() {
        GitHubPublishReadinessService service = new GitHubPublishReadinessService(
                demoProperties("bingqin2", "PatchPilot"),
                new GitHubCredentialReadinessService(() -> credential("READY", true, "GitHub API accepted the configured token.")),
                new GitHubRepositoryAccessReadinessService((owner, repository) ->
                        repositoryAccess("NEEDS_ATTENTION", true, true, owner + "/" + repository, null, "GitHub repository access probe failed: HTTP 404"))
        );

        GitHubPublishReadinessVo readiness = service.getReadiness(null, null);

        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.publishReady()).isFalse();
        assertThat(readiness.nextAction()).contains("repository access");
        assertThat(readiness.checks()).extracting("status").containsExactly("READY", "NEEDS_ATTENTION", "READY");
        assertThat(readiness.checks().get(1).nextAction()).contains("PATCHPILOT_GITHUB_TOKEN");
    }

    private static DemoProperties demoProperties(String owner, String repository) {
        DemoProperties properties = new DemoProperties();
        properties.setRepositoryOwner(owner);
        properties.setRepositoryName(repository);
        return properties;
    }

    private static GitHubCredentialReadinessVo credential(String status, boolean tokenConfigured, String message) {
        return new GitHubCredentialReadinessVo(
                tokenConfigured,
                status,
                message,
                12,
                Instant.parse("2026-06-30T01:00:00Z"),
                tokenConfigured ? "No action needed." : "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend."
        );
    }

    private static GitHubRepositoryAccessReadinessVo repositoryAccess(
            String status,
            boolean tokenConfigured,
            boolean repositoryConfigured,
            String repository,
            String defaultBranch,
            String message
    ) {
        return new GitHubRepositoryAccessReadinessVo(
                tokenConfigured,
                repositoryConfigured,
                repository,
                status,
                message,
                defaultBranch,
                18,
                Instant.parse("2026-06-30T01:01:00Z"),
                status.equals("READY")
                        ? "No action needed."
                        : "Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for " + repository + "; then retry the readiness check."
        );
    }
}
