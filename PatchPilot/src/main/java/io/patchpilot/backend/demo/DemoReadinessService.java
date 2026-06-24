package io.patchpilot.backend.demo;

import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.language.LanguageAdapterFixtureVerificationService;
import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoReadinessService {

    private final Supplier<ConfigurationSummaryVo> configurationSupplier;
    private final Supplier<List<LanguageAdapterFixtureVerificationVo>> fixtureSupplier;
    private final Supplier<FixTaskQueueSummaryVo> queueSummarySupplier;
    private final Supplier<List<FixTaskVo>> recentTasksSupplier;

    @Autowired
    public DemoReadinessService(
            ConfigurationSummaryService configurationSummaryService,
            LanguageAdapterFixtureVerificationService fixtureVerificationService,
            FixTaskQueueQueryService fixTaskQueueQueryService,
            FixTaskService fixTaskService
    ) {
        this(
                configurationSummaryService::getConfigurationSummary,
                fixtureVerificationService::listFixtureVerifications,
                fixTaskQueueQueryService::summary,
                () -> fixTaskService.listTasks(new FixTaskListQuery(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        20,
                        0
                ))
        );
    }

    DemoReadinessService(
            Supplier<ConfigurationSummaryVo> configurationSupplier,
            Supplier<List<LanguageAdapterFixtureVerificationVo>> fixtureSupplier,
            Supplier<FixTaskQueueSummaryVo> queueSummarySupplier,
            Supplier<List<FixTaskVo>> recentTasksSupplier
    ) {
        this.configurationSupplier = configurationSupplier;
        this.fixtureSupplier = fixtureSupplier;
        this.queueSummarySupplier = queueSummarySupplier;
        this.recentTasksSupplier = recentTasksSupplier;
    }

    public DemoReadinessVo getReadiness() {
        ConfigurationSummaryVo configuration = configurationSupplier.get();
        List<LanguageAdapterFixtureVerificationVo> fixtures = fixtureSupplier.get();
        FixTaskQueueSummaryVo queueSummary = queueSummarySupplier.get();
        List<FixTaskVo> recentTasks = recentTasksSupplier.get();

        List<DemoReadinessCheckVo> checks = List.of(
                backendCheck(),
                credentialsCheck(configuration),
                safetyPolicyCheck(configuration),
                repositoryPreflightScopeCheck(configuration),
                adapterFixtureCheck(fixtures),
                queueCheck(queueSummary),
                recentPullRequestCheck(recentTasks)
        );
        DemoReadinessStatus status = aggregateStatus(checks);
        return new DemoReadinessVo(status, summary(status), checks, nextActions(checks));
    }

    private static DemoReadinessCheckVo backendCheck() {
        return new DemoReadinessCheckVo(
                "Backend",
                DemoReadinessStatus.READY,
                "Backend readiness endpoint is reachable.",
                "No action needed."
        );
    }

    private static DemoReadinessCheckVo credentialsCheck(ConfigurationSummaryVo configuration) {
        List<String> missing = new ArrayList<>();
        if (!configuration.agentApiKeyConfigured()) {
            missing.add("Agent API key is missing");
        }
        if (!configuration.githubWebhookSecretConfigured()) {
            missing.add("GitHub webhook secret is missing");
        }
        if (!configuration.githubTokenConfigured()) {
            missing.add("GitHub token is missing");
        }
        if (!missing.isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Credentials",
                    DemoReadinessStatus.BLOCKED,
                    String.join("; ", missing) + ".",
                    "Configure missing credentials in .env and restart the backend."
            );
        }
        if (!configuration.modelCostConfigured()) {
            return new DemoReadinessCheckVo(
                    "Credentials",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "Required credentials are configured, but model cost settings are not configured.",
                    "Configure model token cost settings if you want demo cost estimates."
            );
        }
        return new DemoReadinessCheckVo(
                "Credentials",
                DemoReadinessStatus.READY,
                "Required credentials are configured.",
                "No action needed."
        );
    }

    private static DemoReadinessCheckVo safetyPolicyCheck(ConfigurationSummaryVo configuration) {
        List<String> gaps = new ArrayList<>();
        List<String> envNames = new ArrayList<>();
        if (!configuration.triggerUserAllowlistConfigured()) {
            gaps.add("Trigger user allowlist is open");
            envNames.add("PATCHPILOT_ALLOWED_TRIGGER_USERS");
        }
        if (!configuration.repositoryAllowlistConfigured()) {
            gaps.add("Repository allowlist is open");
            envNames.add("PATCHPILOT_ALLOWED_REPOSITORIES");
        }
        if (!configuration.reviewApprovalAllowlistConfigured()) {
            gaps.add("Review approval allowlist is missing");
            envNames.add("PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS");
        }
        if (!configuration.adminTokenConfigured()) {
            gaps.add("Admin API token is missing");
            envNames.add("PATCHPILOT_ADMIN_TOKEN");
        }
        if (!configuration.rejectedTriggerQuarantineEnabled()) {
            gaps.add("Rejected-trigger quarantine is disabled");
            envNames.add("PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_ENABLED");
        }
        if (!gaps.isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Safety policy",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    String.join("; ", gaps) + ".",
                    "Configure " + String.join(" and ", envNames) + " before a live demo."
            );
        }
        return new DemoReadinessCheckVo(
                "Safety policy",
                DemoReadinessStatus.READY,
                "Trigger users, repositories, review approvers, admin API token, command safety, rate limits, and rejected-trigger quarantine are configured.",
                "No action needed."
        );
    }

    private static DemoReadinessCheckVo repositoryPreflightScopeCheck(ConfigurationSummaryVo configuration) {
        if (configuration.repositoryPreflightAllowedRootDirs().isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Repository preflight scope",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "Repository preflight allowed roots are not configured.",
                    "Configure PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS before using local repository preflight."
            );
        }

        Path fixtureRoot = Path.of("..").resolve("docs/demo-repositories").toAbsolutePath().normalize();
        boolean demoFixtureRootAllowed = configuration.repositoryPreflightAllowedRootDirs().stream()
                .map(rootDir -> Path.of(rootDir).toAbsolutePath().normalize())
                .anyMatch(fixtureRoot::startsWith);
        if (!demoFixtureRootAllowed) {
            return new DemoReadinessCheckVo(
                    "Repository preflight scope",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "Repository preflight allowed roots do not include docs/demo-repositories.",
                    "Configure PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS to include docs/demo-repositories or the project root before a live demo."
            );
        }

        return new DemoReadinessCheckVo(
                "Repository preflight scope",
                DemoReadinessStatus.READY,
                "Repository preflight can inspect demo fixture paths.",
                "No action needed."
        );
    }

    private static DemoReadinessCheckVo adapterFixtureCheck(List<LanguageAdapterFixtureVerificationVo> fixtures) {
        long failedCount = fixtures.stream()
                .filter(fixture -> !"PASS".equals(fixture.status()))
                .count();
        if (failedCount > 0) {
            return new DemoReadinessCheckVo(
                    "Adapter fixtures",
                    DemoReadinessStatus.BLOCKED,
                    failedCount + " fixture verification failed.",
                    "Run /api/language-adapters/fixtures and fix failing adapter demo fixtures."
            );
        }
        return new DemoReadinessCheckVo(
                "Adapter fixtures",
                DemoReadinessStatus.READY,
                fixtures.size() + " adapter fixtures are passing.",
                "No action needed."
        );
    }

    private static DemoReadinessCheckVo queueCheck(FixTaskQueueSummaryVo queueSummary) {
        List<String> risks = new ArrayList<>();
        if (queueSummary.failedCount() > 0) {
            risks.add(queueSummary.failedCount() + " failed queue items");
        }
        if (queueSummary.runningCount() > 0) {
            risks.add(queueSummary.runningCount() + " running queue item" + plural(queueSummary.runningCount()));
        }
        if (queueSummary.delayedPendingCount() > 0) {
            risks.add(queueSummary.delayedPendingCount() + " delayed queue item" + plural(queueSummary.delayedPendingCount()));
        }
        if (!risks.isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Queue",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    String.join("; ", risks) + ".",
                    "Inspect failed or running queue items before starting a demo."
            );
        }
        return new DemoReadinessCheckVo(
                "Queue",
                DemoReadinessStatus.READY,
                "Queue has no failed, running, or delayed items.",
                "No action needed."
        );
    }

    private static DemoReadinessCheckVo recentPullRequestCheck(List<FixTaskVo> recentTasks) {
        boolean hasCompletedPullRequest = recentTasks.stream()
                .anyMatch(task -> task.status() == FixTaskStatus.COMPLETED && hasText(task.pullRequestUrl()));
        if (!hasCompletedPullRequest) {
            return new DemoReadinessCheckVo(
                    "Recent Pull Request",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No completed task with a Pull Request URL was found in recent task history.",
                    "Run one controlled issue-to-PR smoke task before a live demo."
            );
        }
        return new DemoReadinessCheckVo(
                "Recent Pull Request",
                DemoReadinessStatus.READY,
                "Recent task history includes a completed Pull Request.",
                "No action needed."
        );
    }

    private static DemoReadinessStatus aggregateStatus(List<DemoReadinessCheckVo> checks) {
        if (checks.stream().anyMatch(check -> check.status() == DemoReadinessStatus.BLOCKED)) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (checks.stream().anyMatch(check -> check.status() == DemoReadinessStatus.NEEDS_ATTENTION)) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static String summary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "PatchPilot is ready for a controlled demo.";
            case NEEDS_ATTENTION -> "PatchPilot needs attention before a live demo.";
            case BLOCKED -> "PatchPilot is blocked for demo use.";
        };
    }

    private static List<String> nextActions(List<DemoReadinessCheckVo> checks) {
        List<String> actions = checks.stream()
                .filter(check -> check.status() != DemoReadinessStatus.READY)
                .map(DemoReadinessCheckVo::action)
                .distinct()
                .toList();
        if (!actions.isEmpty()) {
            return actions;
        }
        return List.of("Open a controlled GitHub issue and comment /agent fix with a concrete change request.");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String plural(long count) {
        return count == 1 ? "" : "s";
    }
}
