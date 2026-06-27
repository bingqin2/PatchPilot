package io.patchpilot.backend.demo;

import io.patchpilot.backend.agent.provider.ModelProviderHealthService;
import io.patchpilot.backend.agent.provider.domain.ModelProviderHealthVo;
import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.evaluation.EvaluationFixtureBaselineRunRegressionSummaryService;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunRegressionSummaryVo;
import io.patchpilot.backend.github.credential.GitHubCredentialReadinessService;
import io.patchpilot.backend.github.credential.GitHubRepositoryAccessReadinessService;
import io.patchpilot.backend.github.credential.GitHubWebhookUrlReadinessService;
import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubRepositoryAccessReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import io.patchpilot.backend.language.LanguageAdapterFixtureVerificationService;
import io.patchpilot.backend.language.LanguageAdapterRuntimeReadinessService;
import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.language.domain.LanguageAdapterRuntimeReadinessVo;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.FixTaskWorkerHealthVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskWorkerHealthService;
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
    private final Supplier<List<LanguageAdapterRuntimeReadinessVo>> runtimeReadinessSupplier;
    private final Supplier<GitHubCredentialReadinessVo> gitHubCredentialReadinessSupplier;
    private final Supplier<GitHubRepositoryAccessReadinessVo> gitHubRepositoryAccessReadinessSupplier;
    private final Supplier<GitHubWebhookUrlReadinessVo> gitHubWebhookUrlReadinessSupplier;
    private final Supplier<ModelProviderHealthVo> modelProviderHealthSupplier;
    private final Supplier<FixTaskQueueSummaryVo> queueSummarySupplier;
    private final Supplier<FixTaskWorkerHealthVo> workerHealthSupplier;
    private final Supplier<EvaluationFixtureBaselineRunRegressionSummaryVo> evaluationBaselineRegressionSupplier;
    private final Supplier<List<FixTaskVo>> recentTasksSupplier;

    @Autowired
    public DemoReadinessService(
            ConfigurationSummaryService configurationSummaryService,
            LanguageAdapterFixtureVerificationService fixtureVerificationService,
            LanguageAdapterRuntimeReadinessService runtimeReadinessService,
            GitHubCredentialReadinessService gitHubCredentialReadinessService,
            GitHubRepositoryAccessReadinessService gitHubRepositoryAccessReadinessService,
            GitHubWebhookUrlReadinessService gitHubWebhookUrlReadinessService,
            DemoProperties demoProperties,
            ModelProviderHealthService modelProviderHealthService,
            FixTaskQueueQueryService fixTaskQueueQueryService,
            FixTaskWorkerHealthService fixTaskWorkerHealthService,
            EvaluationFixtureBaselineRunRegressionSummaryService evaluationBaselineRegressionSummaryService,
            FixTaskService fixTaskService
    ) {
        this(
                configurationSummaryService::getConfigurationSummary,
                fixtureVerificationService::listFixtureVerifications,
                runtimeReadinessService::listRuntimeReadiness,
                gitHubCredentialReadinessService::getReadiness,
                () -> gitHubRepositoryAccessReadinessService.getReadiness(
                        demoProperties.getRepositoryOwner(),
                        demoProperties.getRepositoryName()
                ),
                gitHubWebhookUrlReadinessService::getReadiness,
                modelProviderHealthService::getHealth,
                fixTaskQueueQueryService::summary,
                fixTaskWorkerHealthService::getHealth,
                evaluationBaselineRegressionSummaryService::getRegressionSummary,
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
            Supplier<List<LanguageAdapterRuntimeReadinessVo>> runtimeReadinessSupplier,
            Supplier<GitHubCredentialReadinessVo> gitHubCredentialReadinessSupplier,
            Supplier<GitHubRepositoryAccessReadinessVo> gitHubRepositoryAccessReadinessSupplier,
            Supplier<ModelProviderHealthVo> modelProviderHealthSupplier,
            Supplier<FixTaskQueueSummaryVo> queueSummarySupplier,
            Supplier<FixTaskWorkerHealthVo> workerHealthSupplier,
            Supplier<List<FixTaskVo>> recentTasksSupplier
    ) {
        this(
                configurationSupplier,
                fixtureSupplier,
                runtimeReadinessSupplier,
                gitHubCredentialReadinessSupplier,
                gitHubRepositoryAccessReadinessSupplier,
                DemoReadinessService::defaultReadyWebhookUrlReadiness,
                modelProviderHealthSupplier,
                queueSummarySupplier,
                workerHealthSupplier,
                DemoReadinessService::defaultStableEvaluationBaselineRegression,
                recentTasksSupplier
        );
    }

    DemoReadinessService(
            Supplier<ConfigurationSummaryVo> configurationSupplier,
            Supplier<List<LanguageAdapterFixtureVerificationVo>> fixtureSupplier,
            Supplier<List<LanguageAdapterRuntimeReadinessVo>> runtimeReadinessSupplier,
            Supplier<GitHubCredentialReadinessVo> gitHubCredentialReadinessSupplier,
            Supplier<GitHubRepositoryAccessReadinessVo> gitHubRepositoryAccessReadinessSupplier,
            Supplier<GitHubWebhookUrlReadinessVo> gitHubWebhookUrlReadinessSupplier,
            Supplier<ModelProviderHealthVo> modelProviderHealthSupplier,
            Supplier<FixTaskQueueSummaryVo> queueSummarySupplier,
            Supplier<FixTaskWorkerHealthVo> workerHealthSupplier,
            Supplier<EvaluationFixtureBaselineRunRegressionSummaryVo> evaluationBaselineRegressionSupplier,
            Supplier<List<FixTaskVo>> recentTasksSupplier
    ) {
        this.configurationSupplier = configurationSupplier;
        this.fixtureSupplier = fixtureSupplier;
        this.runtimeReadinessSupplier = runtimeReadinessSupplier;
        this.gitHubCredentialReadinessSupplier = gitHubCredentialReadinessSupplier;
        this.gitHubRepositoryAccessReadinessSupplier = gitHubRepositoryAccessReadinessSupplier;
        this.gitHubWebhookUrlReadinessSupplier = gitHubWebhookUrlReadinessSupplier;
        this.modelProviderHealthSupplier = modelProviderHealthSupplier;
        this.queueSummarySupplier = queueSummarySupplier;
        this.workerHealthSupplier = workerHealthSupplier;
        this.evaluationBaselineRegressionSupplier = evaluationBaselineRegressionSupplier;
        this.recentTasksSupplier = recentTasksSupplier;
    }

    public DemoReadinessVo getReadiness() {
        ConfigurationSummaryVo configuration = configurationSupplier.get();
        List<LanguageAdapterFixtureVerificationVo> fixtures = fixtureSupplier.get();
        List<LanguageAdapterRuntimeReadinessVo> runtimes = runtimeReadinessSupplier.get();
        GitHubCredentialReadinessVo gitHubCredentialReadiness = gitHubCredentialReadinessSupplier.get();
        GitHubRepositoryAccessReadinessVo gitHubRepositoryAccessReadiness = gitHubRepositoryAccessReadinessSupplier.get();
        GitHubWebhookUrlReadinessVo gitHubWebhookUrlReadiness = gitHubWebhookUrlReadinessSupplier.get();
        ModelProviderHealthVo modelProviderHealth = modelProviderHealthSupplier.get();
        FixTaskQueueSummaryVo queueSummary = queueSummarySupplier.get();
        FixTaskWorkerHealthVo workerHealth = workerHealthSupplier.get();
        EvaluationFixtureBaselineRunRegressionSummaryVo evaluationBaselineRegression = evaluationBaselineRegressionSupplier.get();
        List<FixTaskVo> recentTasks = recentTasksSupplier.get();

        List<DemoReadinessCheckVo> checks = List.of(
                backendCheck(),
                credentialsCheck(configuration),
                gitHubCredentialCheck(gitHubCredentialReadiness),
                gitHubWebhookUrlCheck(gitHubWebhookUrlReadiness),
                gitHubRepositoryAccessCheck(gitHubRepositoryAccessReadiness),
                safetyPolicyCheck(configuration),
                demoTargetPolicyCheck(configuration, gitHubRepositoryAccessReadiness, recentTasks),
                repositoryPreflightScopeCheck(configuration),
                modelProviderCheck(modelProviderHealth),
                adapterFixtureCheck(fixtures),
                adapterRuntimeCheck(runtimes),
                queueCheck(queueSummary),
                workerHeartbeatCheck(workerHealth),
                evaluationBaselineCheck(evaluationBaselineRegression),
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

    private static DemoReadinessCheckVo adapterRuntimeCheck(List<LanguageAdapterRuntimeReadinessVo> runtimes) {
        List<LanguageAdapterRuntimeReadinessVo> missing = runtimes.stream()
                .filter(runtime -> !"READY".equals(runtime.status()))
                .toList();
        if (!missing.isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Adapter runtimes",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    missing.size() + " adapter runtime executable" + plural(missing.size())
                            + " " + (missing.size() == 1 ? "is" : "are")
                            + " missing: " + missingRuntimeSummary(missing) + ".",
                    "Install missing adapter executables on the backend PATH before demonstrating affected languages."
            );
        }
        return new DemoReadinessCheckVo(
                "Adapter runtimes",
                DemoReadinessStatus.READY,
                runtimes.size() + " adapter runtime executable" + plural(runtimes.size()) + " are available on PATH.",
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

    private static DemoReadinessCheckVo gitHubCredentialCheck(GitHubCredentialReadinessVo readiness) {
        if (GitHubCredentialReadinessService.READY.equals(readiness.status())) {
            return new DemoReadinessCheckVo(
                    "GitHub credentials",
                    DemoReadinessStatus.READY,
                    readiness.message(),
                    "No action needed."
            );
        }
        return new DemoReadinessCheckVo(
                "GitHub credentials",
                DemoReadinessStatus.BLOCKED,
                readiness.message(),
                readiness.operatorAction()
        );
    }

    private static DemoReadinessCheckVo gitHubRepositoryAccessCheck(GitHubRepositoryAccessReadinessVo readiness) {
        if (!readiness.repositoryConfigured()) {
            return new DemoReadinessCheckVo(
                    "GitHub repository access",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "Demo repository access target is not configured.",
                    "Configure PATCHPILOT_DEMO_REPOSITORY_OWNER and PATCHPILOT_DEMO_REPOSITORY_NAME before a live demo."
            );
        }
        if (GitHubRepositoryAccessReadinessService.READY.equals(readiness.status())) {
            return new DemoReadinessCheckVo(
                    "GitHub repository access",
                    DemoReadinessStatus.READY,
                    readiness.message(),
                    "No action needed."
            );
        }
        return new DemoReadinessCheckVo(
                "GitHub repository access",
                DemoReadinessStatus.BLOCKED,
                readiness.message(),
                readiness.operatorAction()
        );
    }

    private static DemoReadinessCheckVo gitHubWebhookUrlCheck(GitHubWebhookUrlReadinessVo readiness) {
        if (GitHubWebhookUrlReadinessService.READY.equals(readiness.status())) {
            return new DemoReadinessCheckVo(
                    "GitHub webhook URL",
                    DemoReadinessStatus.READY,
                    readiness.message() + " Payload URL: " + readiness.payloadUrl() + ".",
                    "No action needed."
            );
        }
        return new DemoReadinessCheckVo(
                "GitHub webhook URL",
                DemoReadinessStatus.NEEDS_ATTENTION,
                readiness.message(),
                readiness.operatorAction()
        );
    }

    private static DemoReadinessCheckVo demoTargetPolicyCheck(
            ConfigurationSummaryVo configuration,
            GitHubRepositoryAccessReadinessVo repositoryAccessReadiness,
            List<FixTaskVo> recentTasks
    ) {
        List<String> gaps = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        String demoRepository = repositoryAccessReadiness.repository();
        if (hasText(demoRepository)
                && configuration.repositoryAllowlistConfigured()
                && !configuration.allowedRepositories().contains(demoRepository)) {
            gaps.add("Demo repository " + demoRepository + " is not in PATCHPILOT_ALLOWED_REPOSITORIES");
            actions.add("add " + demoRepository + " to PATCHPILOT_ALLOWED_REPOSITORIES");
        }

        String recentTriggerUser = recentTasks.stream()
                .map(FixTaskVo::triggerUser)
                .filter(DemoReadinessService::hasText)
                .findFirst()
                .orElse("");
        if (hasText(recentTriggerUser)
                && configuration.triggerUserAllowlistConfigured()
                && !configuration.allowedTriggerUsers().contains(recentTriggerUser)) {
            gaps.add("Recent demo trigger user " + recentTriggerUser + " is not in PATCHPILOT_ALLOWED_TRIGGER_USERS");
            actions.add("add " + recentTriggerUser + " to PATCHPILOT_ALLOWED_TRIGGER_USERS");
        }

        if (!gaps.isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Demo target policy",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    String.join("; ", gaps) + ".",
                    "Update demo safety allowlists before a live demo: " + String.join("; ", actions) + "."
            );
        }
        return new DemoReadinessCheckVo(
                "Demo target policy",
                DemoReadinessStatus.READY,
                "Demo repository and recent trigger user align with configured safety allowlists.",
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

    private static DemoReadinessCheckVo modelProviderCheck(ModelProviderHealthVo modelProviderHealth) {
        if (ModelProviderHealthService.READY.equals(modelProviderHealth.status())) {
            return new DemoReadinessCheckVo(
                    "Model provider",
                    DemoReadinessStatus.READY,
                    modelProviderHealth.message(),
                    "No action needed."
            );
        }
        return new DemoReadinessCheckVo(
                "Model provider",
                DemoReadinessStatus.NEEDS_ATTENTION,
                modelProviderHealth.message(),
                modelProviderHealth.operatorAction()
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

    private static DemoReadinessCheckVo workerHeartbeatCheck(FixTaskWorkerHealthVo workerHealth) {
        DemoReadinessStatus status = "READY".equals(workerHealth.readinessStatus())
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
        if (status == DemoReadinessStatus.READY) {
            return new DemoReadinessCheckVo(
                    "Worker heartbeat",
                    DemoReadinessStatus.READY,
                    workerHealth.message(),
                    "No action needed."
            );
        }
        return new DemoReadinessCheckVo(
                "Worker heartbeat",
                DemoReadinessStatus.NEEDS_ATTENTION,
                workerHealth.message(),
                workerHealth.operatorAction()
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

    private static DemoReadinessCheckVo evaluationBaselineCheck(EvaluationFixtureBaselineRunRegressionSummaryVo summary) {
        if (summary == null) {
            return new DemoReadinessCheckVo(
                    "Evaluation baseline",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "Evaluation fixture baseline regression evidence is not available.",
                    "Run and archive at least two fixture baselines before using regression comparison."
            );
        }
        if ("NO_ARCHIVES".equals(summary.status())) {
            return new DemoReadinessCheckVo(
                    "Evaluation baseline",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No archived fixture baseline runs are available for demo readiness.",
                    summary.nextAction()
            );
        }
        if ("SINGLE_ARCHIVE".equals(summary.status())) {
            return new DemoReadinessCheckVo(
                    "Evaluation baseline",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "Only one archived fixture baseline run is available; regression movement is not comparable yet.",
                    summary.nextAction()
            );
        }
        if ("REGRESSED".equals(summary.status())) {
            return new DemoReadinessCheckVo(
                    "Evaluation baseline",
                    DemoReadinessStatus.BLOCKED,
                    "Latest fixture baseline regressed. Newly failed cases: " + csv(summary.newlyFailedCaseIds()) + ".",
                    summary.nextAction()
            );
        }
        if (!summary.latestFailedCaseIds().isEmpty()) {
            return new DemoReadinessCheckVo(
                    "Evaluation baseline",
                    DemoReadinessStatus.BLOCKED,
                    "Latest fixture baseline has failed cases: " + csv(summary.latestFailedCaseIds()) + ".",
                    summary.nextAction()
            );
        }
        return new DemoReadinessCheckVo(
                "Evaluation baseline",
                DemoReadinessStatus.READY,
                "Fixture baseline regression status is " + summary.status() + " with no latest failed cases.",
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

    private static String missingRuntimeSummary(List<LanguageAdapterRuntimeReadinessVo> missing) {
        return missing.stream()
                .map(runtime -> runtime.language() + "-" + runtime.buildSystem() + " requires `" + runtime.executable() + "`")
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private static EvaluationFixtureBaselineRunRegressionSummaryVo defaultStableEvaluationBaselineRegression() {
        return new EvaluationFixtureBaselineRunRegressionSummaryVo(
                "STABLE",
                null,
                null,
                0,
                0,
                0,
                List.of(),
                List.of(),
                List.of(),
                "Fixture baseline regression summary reads archived local baseline runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                "Fixture baseline is stable; keep the latest archive as current demo evidence.",
                "# PatchPilot Evaluation Fixture Baseline Regression Summary"
        );
    }

    private static GitHubWebhookUrlReadinessVo defaultReadyWebhookUrlReadiness() {
        return new GitHubWebhookUrlReadinessVo(
                true,
                GitHubWebhookUrlReadinessService.READY,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "https://demo.trycloudflare.com/health",
                "Configured public webhook URL reaches PatchPilot health.",
                0,
                java.time.Instant.EPOCH,
                "No action needed."
        );
    }

    private static String csv(List<String> values) {
        return values.isEmpty() ? "none" : String.join(", ", values);
    }
}
