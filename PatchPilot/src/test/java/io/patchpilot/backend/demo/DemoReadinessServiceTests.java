package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoReadinessServiceTests {

    @Test
    void should_report_ready_when_configuration_fixtures_queue_and_recent_pr_are_healthy() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(true, true, true, true),
                () -> List.of(fixture("java-maven", "PASS"), fixture("python-hatch", "PASS")),
                () -> new FixTaskQueueSummaryVo(3, 0, 0, 0, 0, 3, 0, 0),
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
                        "Safety policy",
                        "Adapter fixtures",
                        "Queue",
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
                () -> FixTaskQueueSummaryVo.empty(),
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
    void should_report_blocked_when_required_credentials_or_fixtures_are_missing() {
        DemoReadinessService service = new DemoReadinessService(
                () -> configuration(false, false, false, false),
                () -> List.of(fixture("java-maven", "PASS"), fixture("python-hatch", "FAIL")),
                () -> FixTaskQueueSummaryVo.empty(),
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
                () -> new FixTaskQueueSummaryVo(5, 1, 1, 0, 1, 1, 2, 0),
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
                () -> FixTaskQueueSummaryVo.empty(),
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
                "/tmp/patchpilot/workspaces",
                3,
                1000,
                30000,
                modelCostConfigured,
                true,
                true,
                900000,
                10,
                20,
                5,
                true,
                true,
                true,
                List.of("bingqin2"),
                List.of("bingqin2/PatchPilot"),
                List.of("release-captain")
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
        return new ConfigurationSummaryVo(
                "openai-compatible",
                "gpt-5.5",
                "https://api.example.test/v1",
                agentApiKeyConfigured,
                githubTokenConfigured,
                githubWebhookSecretConfigured,
                true,
                "/tmp/patchpilot/workspaces",
                3,
                1000,
                30000,
                modelCostConfigured,
                true,
                true,
                900000,
                10,
                20,
                5,
                !allowedTriggerUsers.isEmpty(),
                !allowedRepositories.isEmpty(),
                !reviewApprovalAllowedOperators.isEmpty(),
                allowedTriggerUsers,
                allowedRepositories,
                reviewApprovalAllowedOperators
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
