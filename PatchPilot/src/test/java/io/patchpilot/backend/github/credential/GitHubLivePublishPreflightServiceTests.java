package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightProbeResult;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubLivePublishPreflightServiceTests {

    @Test
    void should_report_ready_when_publish_path_permissions_and_live_artifacts_are_clean() {
        GitHubLivePublishPreflightService service = service(
                properties("github-token"),
                demoProperties("bingqin2", "PatchPilot"),
                readyPublishReadiness(),
                readyPermissionReadiness(),
                (token, owner, repository) -> new GitHubLivePublishPreflightProbeResult("main", List.of("main"), List.of()),
                () -> Instant.parse("2026-06-30T09:00:00Z"),
                new FixedTicker(100, 169)
        );

        GitHubLivePublishPreflightVo preflight = service.getPreflight("", "");

        assertThat(preflight.status()).isEqualTo("READY");
        assertThat(preflight.livePublishReady()).isTrue();
        assertThat(preflight.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(preflight.defaultBranch()).isEqualTo("main");
        assertThat(preflight.patchpilotBranches()).isEmpty();
        assertThat(preflight.openPatchpilotPullRequests()).isEmpty();
        assertThat(preflight.summary()).isEqualTo("Live GitHub publish preflight is ready for a clean PatchPilot branch and Pull Request.");
        assertThat(preflight.nextAction()).isEqualTo("Post the live /agent fix comment when the rest of launch readiness is READY.");
        assertThat(preflight.sideEffectContract()).contains("does not create branches");
        assertThat(preflight.sideEffectContract()).contains("does not open Pull Requests");
        assertThat(preflight.checks()).extracting("name")
                .containsExactly("Publish path readiness", "Publish permission readiness", "PatchPilot branch inventory", "Open PatchPilot Pull Requests");
        assertThat(preflight.checks()).extracting("status")
                .containsExactly("READY", "READY", "READY", "READY");
        assertThat(preflight.evidenceNotes()).contains("Repository: bingqin2/PatchPilot", "PatchPilot branch count: 0");
        assertThat(preflight.latencyMs()).isEqualTo(69);
        assertThat(preflight.checkedAt()).isEqualTo(Instant.parse("2026-06-30T09:00:00Z"));
        assertThat(preflight.evidenceNotes()).doesNotContain("github-token");
    }

    @Test
    void should_report_attention_when_old_patchpilot_branches_and_open_prs_exist() {
        GitHubLivePublishPreflightService service = service(
                properties("github-token"),
                demoProperties("bingqin2", "PatchPilot"),
                readyPublishReadiness(),
                readyPermissionReadiness(),
                (token, owner, repository) -> new GitHubLivePublishPreflightProbeResult(
                        "main",
                        List.of("main", "patchpilot/old-task", "patchpilot/retry-task"),
                        List.of("https://github.com/bingqin2/PatchPilot/pull/12")
                ),
                () -> Instant.parse("2026-06-30T09:00:00Z"),
                new FixedTicker(100, 144)
        );

        GitHubLivePublishPreflightVo preflight = service.getPreflight("bingqin2", "PatchPilot");

        assertThat(preflight.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(preflight.livePublishReady()).isFalse();
        assertThat(preflight.patchpilotBranches()).containsExactly("patchpilot/old-task", "patchpilot/retry-task");
        assertThat(preflight.openPatchpilotPullRequests()).containsExactly("https://github.com/bingqin2/PatchPilot/pull/12");
        assertThat(preflight.summary()).isEqualTo("Live GitHub publish preflight found existing PatchPilot publish artifacts.");
        assertThat(preflight.nextAction()).contains("close or merge stale PatchPilot Pull Requests");
        assertThat(preflight.checks()).extracting("status")
                .containsExactly("READY", "READY", "NEEDS_ATTENTION", "NEEDS_ATTENTION");
    }

    @Test
    void should_block_without_token_before_calling_live_probe() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubLivePublishPreflightService service = service(
                properties(" "),
                demoProperties("bingqin2", "PatchPilot"),
                publishReadiness("BLOCKED"),
                permissionReadiness("BLOCKED"),
                (token, owner, repository) -> {
                    called.set(true);
                    return new GitHubLivePublishPreflightProbeResult("main", List.of(), List.of());
                },
                () -> Instant.parse("2026-06-30T09:00:00Z"),
                new FixedTicker(100, 144)
        );

        GitHubLivePublishPreflightVo preflight = service.getPreflight(null, null);

        assertThat(called).isFalse();
        assertThat(preflight.status()).isEqualTo("BLOCKED");
        assertThat(preflight.livePublishReady()).isFalse();
        assertThat(preflight.tokenConfigured()).isFalse();
        assertThat(preflight.repositoryConfigured()).isTrue();
        assertThat(preflight.nextAction()).isEqualTo("Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.");
    }

    @Test
    void should_require_repository_before_calling_live_probe() {
        AtomicBoolean called = new AtomicBoolean(false);
        GitHubLivePublishPreflightService service = service(
                properties("github-token"),
                demoProperties("", ""),
                readyPublishReadiness(),
                readyPermissionReadiness(),
                (token, owner, repository) -> {
                    called.set(true);
                    return new GitHubLivePublishPreflightProbeResult("main", List.of(), List.of());
                },
                () -> Instant.parse("2026-06-30T09:00:00Z"),
                new FixedTicker(100, 144)
        );

        GitHubLivePublishPreflightVo preflight = service.getPreflight(" ", " ");

        assertThat(called).isFalse();
        assertThat(preflight.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(preflight.repositoryConfigured()).isFalse();
        assertThat(preflight.nextAction()).isEqualTo("Set PATCHPILOT_DEMO_REPOSITORY_OWNER and PATCHPILOT_DEMO_REPOSITORY_NAME or pass owner and repository query parameters.");
    }

    @Test
    void should_report_attention_when_live_probe_fails() {
        GitHubLivePublishPreflightService service = service(
                properties("github-token"),
                demoProperties("bingqin2", "PatchPilot"),
                readyPublishReadiness(),
                readyPermissionReadiness(),
                (token, owner, repository) -> {
                    throw new GitHubCredentialReadinessException("GitHub live publish preflight failed: HTTP 403");
                },
                () -> Instant.parse("2026-06-30T09:00:00Z"),
                new FixedTicker(100, 111)
        );

        GitHubLivePublishPreflightVo preflight = service.getPreflight(null, null);

        assertThat(preflight.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(preflight.summary()).isEqualTo("Live GitHub publish preflight could not confirm branch and Pull Request state.");
        assertThat(preflight.nextAction()).contains("repository access");
        assertThat(preflight.evidenceNotes()).contains("Probe message: GitHub live publish preflight failed: HTTP 403");
    }

    private static GitHubLivePublishPreflightService service(
            GitHubProperties gitHubProperties,
            DemoProperties demoProperties,
            GitHubPublishReadinessVo publishReadiness,
            GitHubPublishPermissionReadinessVo permissionReadiness,
            GitHubLivePublishPreflightProbe probe,
            java.util.function.Supplier<Instant> clock,
            java.util.function.LongSupplier ticker
    ) {
        return new GitHubLivePublishPreflightService(
                gitHubProperties,
                demoProperties,
                new GitHubPublishReadinessService((owner, repository) -> publishReadiness),
                new GitHubPublishPermissionReadinessService((owner, repository) -> permissionReadiness),
                probe,
                clock,
                ticker
        );
    }

    private static GitHubProperties properties(String token) {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken(token);
        return properties;
    }

    private static DemoProperties demoProperties(String owner, String repository) {
        DemoProperties properties = new DemoProperties();
        properties.setRepositoryOwner(owner);
        properties.setRepositoryName(repository);
        return properties;
    }

    private static GitHubPublishReadinessVo readyPublishReadiness() {
        return publishReadiness("READY");
    }

    private static GitHubPublishReadinessVo publishReadiness(String status) {
        return new GitHubPublishReadinessVo(
                status,
                "READY".equals(status),
                "READY".equals(status),
                true,
                "bingqin2/PatchPilot",
                "main",
                status,
                "READY".equals(status) ? "Continue with the live /agent fix demo." : "Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.",
                "git push origin HEAD:<patchpilot-branch>",
                "Read-only readiness probe.",
                List.of(),
                List.of(),
                Instant.parse("2026-06-30T08:59:00Z")
        );
    }

    private static GitHubPublishPermissionReadinessVo readyPermissionReadiness() {
        return permissionReadiness("READY");
    }

    private static GitHubPublishPermissionReadinessVo permissionReadiness(String status) {
        return new GitHubPublishPermissionReadinessVo(
                status,
                "READY".equals(status),
                "READY".equals(status),
                true,
                "bingqin2/PatchPilot",
                "main",
                "READY".equals(status),
                "READY".equals(status),
                "READY".equals(status),
                "READY".equals(status),
                status,
                "READY".equals(status) ? "Continue with the live /agent fix demo." : "Grant Contents: Read and write.",
                "Read-only permission probe.",
                List.of(),
                List.of(),
                1,
                Instant.parse("2026-06-30T08:59:01Z")
        );
    }

    private static final class FixedTicker implements java.util.function.LongSupplier {

        private final long[] values;
        private int index;

        private FixedTicker(long... values) {
            this.values = values;
        }

        @Override
        public long getAsLong() {
            return values[Math.min(index++, values.length - 1)];
        }
    }
}
