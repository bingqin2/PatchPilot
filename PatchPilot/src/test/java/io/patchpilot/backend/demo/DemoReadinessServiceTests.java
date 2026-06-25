package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.agent.provider.domain.ModelProviderHealthVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.language.domain.LanguageAdapterRuntimeReadinessVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.FixTaskWorkerHealthVo;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoReadinessServiceTests {

    @Test
    void should_report_ready_when_configuration_fixtures_queue_and_recent_pr_are_healthy() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS"), fixture("python-hatch", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY"), runtime("python", "hatch", "python", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> new FixTaskQueueSummaryVo(3, 0, 0, 0, 0, 3, 0, 0),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(readiness.summary()).isEqualTo("PatchPilot is ready for a controlled demo.");
        assertThat(readiness.checks())
                .extracting("name")
                .containsExactly(
                        "Backend",
                        "Credentials",
                        "GitHub credentials",
                        "GitHub repository access",
                        "Safety policy",
                        "Demo target policy",
                        "Repository preflight scope",
                        "Model provider",
                        "Adapter fixtures",
                        "Adapter runtimes",
                        "Queue",
                        "Worker heartbeat",
                        "Recent Pull Request"
                );
        assertThat(readiness.checks())
                .allSatisfy(check -> assertThat(check.status()).isEqualTo(DemoReadinessStatus.READY));
        assertThat(readiness.nextActions()).containsExactly("Open a controlled GitHub issue and comment /agent fix with a concrete change request.");
    }

    @Test
    void should_report_safety_policy_attention_when_allowlists_are_incomplete() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(
                        true,
                        true,
                        true,
                        true,
                        List.of(),
                        List.of("bingqin2/PatchPilot"),
                        List.of()
                ),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Safety policy"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("Trigger user allowlist is open");
                    assertThat(check.message()).contains("Review approval allowlist is missing");
                    assertThat(check.message()).doesNotContain("Repository allowlist is open");
                });
        assertThat(readiness.nextActions()).contains(
                "Configure PATCHPILOT_ALLOWED_TRIGGER_USERS and PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS before a live demo."
        );
    }

    @Test
    void should_report_demo_target_policy_attention_when_repository_allowlist_excludes_demo_repository() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(
                        true,
                        true,
                        true,
                        true,
                        List.of("bingqin2"),
                        List.of("bingqin2/OtherRepo"),
                        List.of("release-captain")
                ),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Demo target policy"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("Demo repository bingqin2/PatchPilot is not in PATCHPILOT_ALLOWED_REPOSITORIES");
                    assertThat(check.action()).contains("PATCHPILOT_ALLOWED_REPOSITORIES");
                    assertThat(check.action()).contains("bingqin2/PatchPilot");
                });
    }

    @Test
    void should_report_demo_target_policy_attention_when_recent_demo_trigger_user_is_not_allowed() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(
                        true,
                        true,
                        true,
                        true,
                        List.of("release-captain"),
                        List.of("bingqin2/PatchPilot"),
                        List.of("release-captain")
                ),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Demo target policy"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("Recent demo trigger user bingqin2 is not in PATCHPILOT_ALLOWED_TRIGGER_USERS");
                    assertThat(check.action()).contains("PATCHPILOT_ALLOWED_TRIGGER_USERS");
                    assertThat(check.action()).contains("bingqin2");
                });
    }

    @Test
    void should_report_blocked_when_required_credentials_or_fixtures_are_missing() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(false, false, false, false),
                () -> List.of(fixture("java-maven", "PASS"), fixture("python-hatch", "FAIL")),
                () -> List.of(runtime("java", "maven", "mvn", "READY"), runtime("python", "hatch", "python", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                List::of
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(readiness.summary()).isEqualTo("PatchPilot is blocked for demo use.");
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Credentials"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
                    assertThat(check.message()).contains("Agent API key is missing");
                    assertThat(check.message()).contains("GitHub webhook secret is missing");
                    assertThat(check.message()).contains("GitHub token is missing");
                });
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Adapter fixtures"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
                    assertThat(check.message()).contains("1 fixture verification failed");
                });
        assertThat(readiness.nextActions()).contains("Configure missing credentials in .env and restart the backend.");
        assertThat(readiness.nextActions()).contains("Run /api/language-adapters/fixtures and fix failing adapter demo fixtures.");
    }

    @Test
    void should_report_warning_when_no_recent_successful_pull_request_exists_or_queue_has_failures() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, false),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> new FixTaskQueueSummaryVo(5, 1, 1, 0, 1, 1, 2, 0),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-2", FixTaskStatus.FAILED, null))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Queue"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("2 failed queue items");
                    assertThat(check.message()).contains("1 running queue item");
                });
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Recent Pull Request"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("No completed task with a Pull Request URL");
                });
        assertThat(readiness.nextActions()).contains("Run one controlled issue-to-PR smoke task before a live demo.");
        assertThat(readiness.nextActions()).contains("Inspect failed or running queue items before starting a demo.");
    }

    @Test
    void should_report_safety_policy_attention_when_admin_token_is_missing() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true, false),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Safety policy"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("Admin API token is missing");
                    assertThat(check.action()).contains("PATCHPILOT_ADMIN_TOKEN");
                });
        assertThat(readiness.nextActions()).contains("Configure PATCHPILOT_ADMIN_TOKEN before a live demo.");
    }

    @Test
    void should_report_preflight_scope_attention_when_demo_fixtures_are_not_allowed() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(
                        true,
                        true,
                        true,
                        true,
                        List.of("bingqin2"),
                        List.of("bingqin2/PatchPilot"),
                        List.of("release-captain"),
                        List.of("/tmp/patchpilot/workspaces")
                ),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Repository preflight scope"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("do not include docs/demo-repositories");
                    assertThat(check.action()).contains("PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS");
                });
    }

    @Test
    void should_not_treat_sibling_preflight_root_prefix_as_allowed() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(
                        true,
                        true,
                        true,
                        true,
                        List.of("bingqin2"),
                        List.of("bingqin2/PatchPilot"),
                        List.of("release-captain"),
                        List.of(Path.of("..").resolve("docs/demo").toAbsolutePath().normalize().toString())
                ),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Repository preflight scope"))
                .singleElement()
                .satisfies(check -> assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION));
    }

    @Test
    void should_report_worker_attention_when_worker_heartbeat_is_not_ready() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                () -> new FixTaskWorkerHealthVo(
                        "NOT_STARTED",
                        "Worker poller has not reported a heartbeat yet.",
                        null,
                        null,
                        0,
                        0,
                        0,
                        0,
                        0,
                        null,
                        null,
                        null,
                        -1,
                        "NEEDS_ATTENTION",
                        "Wait for the queue worker poller to start or check the active Spring profile."
                ),
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Worker heartbeat"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("Worker poller has not reported a heartbeat yet.");
                    assertThat(check.action()).contains("queue worker poller");
                });
        assertThat(readiness.nextActions()).contains("Wait for the queue worker poller to start or check the active Spring profile.");
    }

    @Test
    void should_report_runtime_attention_when_adapter_executable_is_missing() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS"), fixture("python-hatch", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY"), runtime("python", "hatch", "python", "MISSING")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Adapter runtimes"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).isEqualTo("1 adapter runtime executable is missing: python-hatch requires `python`.");
                    assertThat(check.action()).isEqualTo("Install missing adapter executables on the backend PATH before demonstrating affected languages.");
                });
        assertThat(readiness.nextActions()).contains("Install missing adapter executables on the backend PATH before demonstrating affected languages.");
    }

    @Test
    void should_report_model_provider_attention_when_health_probe_is_not_ready() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                DemoReadinessServiceTests::readyRepositoryAccess,
                () -> new ModelProviderHealthVo(
                        "openai-compatible",
                        "gpt-5.5",
                        true,
                        true,
                        "NEEDS_ATTENTION",
                        "Model provider health probe failed: HTTP 401",
                        45,
                        Instant.parse("2026-06-25T02:00:00Z"),
                        "Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL."
                ),
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Model provider"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).isEqualTo("Model provider health probe failed: HTTP 401");
                    assertThat(check.action()).isEqualTo("Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.");
                });
        assertThat(readiness.nextActions()).contains("Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.");
    }

    @Test
    void should_report_blocked_when_github_credential_probe_is_not_ready() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                () -> new GitHubCredentialReadinessVo(
                        true,
                        "NEEDS_ATTENTION",
                        "GitHub credential probe failed: HTTP 401",
                        31,
                        Instant.parse("2026-06-25T03:00:00Z"),
                        "Check PATCHPILOT_GITHUB_TOKEN permissions before running a live task."
                ),
                DemoReadinessServiceTests::readyRepositoryAccess,
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("GitHub credentials"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
                    assertThat(check.message()).isEqualTo("GitHub credential probe failed: HTTP 401");
                    assertThat(check.action()).isEqualTo("Check PATCHPILOT_GITHUB_TOKEN permissions before running a live task.");
                });
        assertThat(readiness.nextActions()).contains("Check PATCHPILOT_GITHUB_TOKEN permissions before running a live task.");
    }

    @Test
    void should_report_attention_when_demo_repository_access_target_is_not_configured() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                () -> new GitHubRepositoryAccessReadinessVo(
                        true,
                        false,
                        "",
                        "NEEDS_ATTENTION",
                        "Repository owner and name are required for the access probe.",
                        null,
                        0,
                        Instant.parse("2026-06-25T04:00:00Z"),
                        "Select a repository or provide owner and repository query parameters."
                ),
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("GitHub repository access"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).isEqualTo("Demo repository access target is not configured.");
                    assertThat(check.action()).contains("PATCHPILOT_DEMO_REPOSITORY_OWNER");
                    assertThat(check.action()).contains("PATCHPILOT_DEMO_REPOSITORY_NAME");
                });
    }

    @Test
    void should_report_blocked_when_demo_repository_access_probe_is_not_ready() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS")),
                () -> List.of(runtime("java", "maven", "mvn", "READY")),
                DemoReadinessServiceTests::readyGitHubCredential,
                () -> new GitHubRepositoryAccessReadinessVo(
                        true,
                        true,
                        "bingqin2/PatchPilot",
                        "NEEDS_ATTENTION",
                        "GitHub repository access probe failed: HTTP 404",
                        null,
                        42,
                        Instant.parse("2026-06-25T04:00:00Z"),
                        "Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for bingqin2/PatchPilot; then retry the readiness check."
                ),
                DemoReadinessServiceTests::readyModelProvider,
                () -> FixTaskQueueSummaryVo.empty(),
                DemoReadinessServiceTests::readyWorker,
                () -> List.of(task("task-1", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/12"))
        );

        DemoReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("GitHub repository access"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
                    assertThat(check.message()).isEqualTo("GitHub repository access probe failed: HTTP 404");
                    assertThat(check.action()).contains("repository allowlist for bingqin2/PatchPilot");
                });
        assertThat(readiness.nextActions()).contains(
                "Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for bingqin2/PatchPilot; then retry the readiness check."
        );
    }

    private static ConfigurationSummaryVo configuration(
            boolean agentApiKeyConfigured,
            boolean githubTokenConfigured,
            boolean githubWebhookSecretConfigured,
            boolean modelCostConfigured
    ) {
        return configuration(
                agentApiKeyConfigured,
                githubTokenConfigured,
                githubWebhookSecretConfigured,
                modelCostConfigured,
                true
        );
    }

    private static ConfigurationSummaryVo configuration(
            boolean agentApiKeyConfigured,
            boolean githubTokenConfigured,
            boolean githubWebhookSecretConfigured,
            boolean modelCostConfigured,
            boolean adminTokenConfigured
    ) {
        return new ConfigurationSummaryVo(
                "openai-compatible",
                "gpt-5.5",
                "https://api.example.test/v1",
                agentApiKeyConfigured,
                githubTokenConfigured,
                githubWebhookSecretConfigured,
                adminTokenConfigured,
                true,
                "/tmp/patchpilot/workspaces",
                3,
                1000,
                30000,
                25000,
                modelCostConfigured,
                true,
                true,
                900000,
                10,
                20,
                5,
                true,
                900000,
                5,
                1800000,
                true,
                true,
                true,
                true,
                15,
                List.of("bingqin2"),
                List.of("bingqin2/PatchPilot"),
                List.of("release-captain"),
                List.of(
                        "/tmp/patchpilot/workspaces",
                        Path.of("..").resolve("docs/demo-repositories").toAbsolutePath().normalize().toString()
                )
        );
    }

    private static ConfigurationSummaryVo configuration(
            boolean agentApiKeyConfigured,
            boolean githubTokenConfigured,
            boolean githubWebhookSecretConfigured,
            boolean modelCostConfigured,
            List<String> allowedTriggerUsers,
            List<String> allowedRepositories,
            List<String> reviewApprovalAllowedOperators
    ) {
        return configuration(
                agentApiKeyConfigured,
                githubTokenConfigured,
                githubWebhookSecretConfigured,
                modelCostConfigured,
                allowedTriggerUsers,
                allowedRepositories,
                reviewApprovalAllowedOperators,
                List.of(
                        "/tmp/patchpilot/workspaces",
                        Path.of("..").resolve("docs/demo-repositories").toAbsolutePath().normalize().toString()
                )
        );
    }

    private static ConfigurationSummaryVo configuration(
            boolean agentApiKeyConfigured,
            boolean githubTokenConfigured,
            boolean githubWebhookSecretConfigured,
            boolean modelCostConfigured,
            List<String> allowedTriggerUsers,
            List<String> allowedRepositories,
            List<String> reviewApprovalAllowedOperators,
            List<String> repositoryPreflightAllowedRootDirs
    ) {
        return new ConfigurationSummaryVo(
                "openai-compatible",
                "gpt-5.5",
                "https://api.example.test/v1",
                agentApiKeyConfigured,
                githubTokenConfigured,
                githubWebhookSecretConfigured,
                true,
                true,
                "/tmp/patchpilot/workspaces",
                3,
                1000,
                30000,
                25000,
                modelCostConfigured,
                true,
                true,
                900000,
                10,
                20,
                5,
                true,
                900000,
                5,
                1800000,
                !allowedTriggerUsers.isEmpty(),
                !allowedRepositories.isEmpty(),
                !reviewApprovalAllowedOperators.isEmpty(),
                true,
                15,
                allowedTriggerUsers,
                allowedRepositories,
                reviewApprovalAllowedOperators,
                repositoryPreflightAllowedRootDirs
        );
    }

    private static LanguageAdapterFixtureVerificationVo fixture(String name, String status) {
        return new LanguageAdapterFixtureVerificationVo(
                name,
                "docs/demo-repositories/" + name,
                "java",
                "maven",
                List.of("mvn", "test"),
                "java",
                "maven",
                List.of("mvn", "test"),
                "Detected fixture",
                status
        );
    }

    private static LanguageAdapterRuntimeReadinessVo runtime(String language, String buildSystem, String executable, String status) {
        return new LanguageAdapterRuntimeReadinessVo(
                language,
                buildSystem,
                executable,
                List.of(executable, "--version"),
                status,
                "Executable `" + executable + "` readiness fixture"
        );
    }

    private static ModelProviderHealthVo readyModelProvider() {
        return new ModelProviderHealthVo(
                "openai-compatible",
                "gpt-5.5",
                true,
                true,
                "READY",
                "Model provider responded to the health probe.",
                42,
                Instant.parse("2026-06-25T02:00:00Z"),
                "No action needed."
        );
    }

    private static GitHubCredentialReadinessVo readyGitHubCredential() {
        return new GitHubCredentialReadinessVo(
                true,
                "READY",
                "GitHub API accepted the configured token.",
                31,
                Instant.parse("2026-06-25T03:00:00Z"),
                "No action needed."
        );
    }

    private static GitHubRepositoryAccessReadinessVo readyRepositoryAccess() {
        return new GitHubRepositoryAccessReadinessVo(
                true,
                true,
                "bingqin2/PatchPilot",
                "READY",
                "GitHub token can read repository bingqin2/PatchPilot.",
                "main",
                42,
                Instant.parse("2026-06-25T04:00:00Z"),
                "No action needed."
        );
    }

    private static FixTaskWorkerHealthVo readyWorker() {
        return new FixTaskWorkerHealthVo(
                "IDLE",
                "Worker poller is active but no queue item was available.",
                Instant.parse("2026-06-24T06:00:00Z"),
                Instant.parse("2026-06-24T06:00:01Z"),
                12,
                3,
                2,
                0,
                8,
                "queue-123",
                "task-123",
                null,
                1000,
                "READY",
                "No action needed."
        );
    }

    private static FixTaskVo task(String id, FixTaskStatus status, String pullRequestUrl) {
        return new FixTaskVo(
                id,
                "bingqin2",
                "PatchPilot",
                1,
                0,
                "bingqin2",
                "/agent fix touch docs/demo.md",
                "delivery-" + id,
                123,
                status,
                null,
                Instant.parse("2026-06-22T08:00:00Z"),
                pullRequestUrl,
                Instant.parse("2026-06-22T08:05:00Z"),
                Instant.parse("2026-06-22T08:05:00Z"),
                "java",
                "maven",
                "mvn test",
                456L,
                "https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456"
        );
    }
}
