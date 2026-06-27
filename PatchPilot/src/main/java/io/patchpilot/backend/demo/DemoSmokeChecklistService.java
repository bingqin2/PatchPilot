package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.github.credential.GitHubWebhookSetupReadinessService;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DemoSmokeChecklistService {

    private final Supplier<DemoReadinessVo> readinessSupplier;
    private final Supplier<List<WebhookDeliveryDiagnosticVo>> webhookDeliveriesSupplier;
    private final Supplier<GitHubWebhookSetupReadinessVo> webhookSetupReadinessSupplier;
    private final Supplier<List<FixTaskVo>> recentTasksSupplier;

    @Autowired
    public DemoSmokeChecklistService(
            DemoReadinessService demoReadinessService,
            GitHubWebhookSetupReadinessService gitHubWebhookSetupReadinessService,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService,
            FixTaskService fixTaskService
    ) {
        this(
                demoReadinessService::getReadiness,
                () -> webhookDeliveryDiagnosticService.listRecent(10),
                gitHubWebhookSetupReadinessService::getReadiness,
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

    DemoSmokeChecklistService(
            Supplier<DemoReadinessVo> readinessSupplier,
            Supplier<List<WebhookDeliveryDiagnosticVo>> webhookDeliveriesSupplier,
            Supplier<List<FixTaskVo>> recentTasksSupplier
    ) {
        this(
                readinessSupplier,
                webhookDeliveriesSupplier,
                DemoSmokeChecklistService::defaultReadyWebhookSetupReadiness,
                recentTasksSupplier
        );
    }

    DemoSmokeChecklistService(
            Supplier<DemoReadinessVo> readinessSupplier,
            Supplier<List<WebhookDeliveryDiagnosticVo>> webhookDeliveriesSupplier,
            Supplier<GitHubWebhookSetupReadinessVo> webhookSetupReadinessSupplier,
            Supplier<List<FixTaskVo>> recentTasksSupplier
    ) {
        this.readinessSupplier = readinessSupplier;
        this.webhookDeliveriesSupplier = webhookDeliveriesSupplier;
        this.webhookSetupReadinessSupplier = webhookSetupReadinessSupplier;
        this.recentTasksSupplier = recentTasksSupplier;
    }

    public DemoSmokeChecklistVo getSmokeChecklist() {
        DemoReadinessVo readiness = readinessSupplier.get();
        List<WebhookDeliveryDiagnosticVo> deliveries = webhookDeliveriesSupplier.get();
        GitHubWebhookSetupReadinessVo webhookSetupReadiness = webhookSetupReadinessSupplier.get();
        List<FixTaskVo> recentTasks = recentTasksSupplier.get();

        List<DemoSmokeChecklistStepVo> steps = List.of(
                readinessStep(readiness),
                adapterRuntimeStep(readiness),
                webhookStep(webhookSetupReadiness, deliveries),
                taskExecutionStep(recentTasks),
                pullRequestStep(recentTasks)
        );
        DemoSmokeChecklistStatus status = aggregateStatus(steps);
        return new DemoSmokeChecklistVo(status, summary(status), steps, nextActions(readiness, steps));
    }

    private static DemoSmokeChecklistStepVo readinessStep(DemoReadinessVo readiness) {
        DemoSmokeChecklistStatus status = toSmokeStatus(readiness.status());
        return new DemoSmokeChecklistStepVo(
                1,
                "Readiness gate",
                status,
                readiness.summary(),
                readiness.checks().size() + " readiness checks evaluated",
                firstAction(readiness.nextActions())
        );
    }

    private static DemoSmokeChecklistStepVo adapterRuntimeStep(DemoReadinessVo readiness) {
        Optional<DemoReadinessCheckVo> runtimeCheck = readiness.checks().stream()
                .filter(check -> check.name().equals("Adapter runtimes"))
                .findFirst();
        if (runtimeCheck.isEmpty()) {
            return new DemoSmokeChecklistStepVo(
                    2,
                    "Adapter runtime gate",
                    DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                    "Adapter runtime readiness has not been evaluated.",
                    "No adapter runtime evidence",
                    "Open /api/language-adapters/runtime-readiness before a live demo."
            );
        }

        DemoReadinessCheckVo check = runtimeCheck.get();
        return new DemoSmokeChecklistStepVo(
                2,
                "Adapter runtime gate",
                toSmokeStatus(check.status()),
                check.message(),
                check.name(),
                check.action()
        );
    }

    private static DemoSmokeChecklistStepVo webhookStep(
            GitHubWebhookSetupReadinessVo setupReadiness,
            List<WebhookDeliveryDiagnosticVo> deliveries
    ) {
        if (setupReadiness == null) {
            return new DemoSmokeChecklistStepVo(
                    3,
                    "Webhook delivery",
                    DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                    "Webhook setup readiness has not been evaluated.",
                    latestDeliveryEvidence(deliveries),
                    "Open /api/github/webhook-setup-readiness before a live demo."
            );
        }
        if (!GitHubWebhookSetupReadinessService.READY.equals(setupReadiness.status())) {
            return new DemoSmokeChecklistStepVo(
                    3,
                    "Webhook delivery",
                    toSmokeStatus(setupReadiness.status()),
                    setupReadiness.summary(),
                    firstNonBlank(setupReadiness.latestDeliveryId(), latestDeliveryEvidence(deliveries), setupReadiness.payloadUrl()),
                    firstAction(setupReadiness.nextActions())
            );
        }
        if (deliveries.isEmpty()) {
            return new DemoSmokeChecklistStepVo(
                    3,
                    "Webhook delivery",
                    DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                    "No recent webhook delivery has been recorded.",
                    "No delivery evidence",
                    "Update the GitHub webhook URL, post a controlled /agent fix comment, and confirm a delivery appears."
            );
        }
        WebhookDeliveryDiagnosticVo latest = deliveries.get(0);
        if (latest.redeliveryRecommended()) {
            return new DemoSmokeChecklistStepVo(
                    3,
                    "Webhook delivery",
                    DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                    latest.message(),
                    latest.deliveryId(),
                    "Fix the webhook secret or URL, then use GitHub Redeliver before the live demo."
            );
        }
        if (latest.status() != WebhookDeliveryDiagnosticStatus.TASK_CREATED || latest.taskId() == null) {
            return new DemoSmokeChecklistStepVo(
                    3,
                    "Webhook delivery",
                    DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                    latest.message(),
                    latest.deliveryId(),
                    latest.operatorAction()
            );
        }
        return new DemoSmokeChecklistStepVo(
                3,
                "Webhook delivery",
                DemoSmokeChecklistStatus.READY,
                "Latest webhook delivery reached PatchPilot and produced task " + latest.taskId() + ".",
                latest.deliveryId(),
                "Post the live /agent fix comment only after confirming the webhook URL is current."
        );
    }

    private static DemoSmokeChecklistStatus toSmokeStatus(String readinessStatus) {
        return switch (readinessStatus) {
            case GitHubWebhookSetupReadinessService.READY -> DemoSmokeChecklistStatus.READY;
            case GitHubWebhookSetupReadinessService.BLOCKED -> DemoSmokeChecklistStatus.BLOCKED;
            default -> DemoSmokeChecklistStatus.NEEDS_ATTENTION;
        };
    }

    private static DemoSmokeChecklistStepVo taskExecutionStep(List<FixTaskVo> recentTasks) {
        Optional<FixTaskVo> completedTask = recentTasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED)
                .findFirst();
        if (completedTask.isPresent()) {
            FixTaskVo task = completedTask.get();
            return new DemoSmokeChecklistStepVo(
                    4,
                    "Task execution",
                    DemoSmokeChecklistStatus.READY,
                    "Recent task completed with verification command " + nullToUnknown(task.verificationCommand()) + ".",
                    task.id(),
                    "Use the same repository shape for the live demo."
            );
        }
        Optional<FixTaskVo> activeTask = recentTasks.stream()
                .filter(task -> task.status() == FixTaskStatus.PENDING
                        || task.status() == FixTaskStatus.RUNNING
                        || task.status() == FixTaskStatus.RUNNING_TESTS
                        || task.status() == FixTaskStatus.PENDING_REVIEW)
                .findFirst();
        if (activeTask.isPresent()) {
            return new DemoSmokeChecklistStepVo(
                    4,
                    "Task execution",
                    DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                    "A task is still active: " + activeTask.get().status() + ".",
                    activeTask.get().id(),
                    "Wait for the active task to finish, or cancel it before starting the live demo."
            );
        }
        return new DemoSmokeChecklistStepVo(
                4,
                "Task execution",
                DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                "No recent completed task is available as execution evidence.",
                "No completed task evidence",
                "Run one controlled issue-to-PR smoke task before a live demo."
        );
    }

    private static DemoSmokeChecklistStepVo pullRequestStep(List<FixTaskVo> recentTasks) {
        Optional<FixTaskVo> taskWithPullRequest = recentTasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED && hasText(task.pullRequestUrl()))
                .findFirst();
        if (taskWithPullRequest.isPresent()) {
            FixTaskVo task = taskWithPullRequest.get();
            return new DemoSmokeChecklistStepVo(
                    5,
                    "Pull Request evidence",
                    DemoSmokeChecklistStatus.READY,
                    "Recent completed task opened a Pull Request.",
                    task.pullRequestUrl(),
                    "Use this as the baseline proof that branch push and PR creation work."
            );
        }
        return new DemoSmokeChecklistStepVo(
                5,
                "Pull Request evidence",
                DemoSmokeChecklistStatus.NEEDS_ATTENTION,
                "No recent completed task has a Pull Request URL.",
                "No Pull Request evidence",
                "Run one controlled issue-to-PR smoke task before a live demo."
        );
    }

    private static DemoSmokeChecklistStatus toSmokeStatus(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> DemoSmokeChecklistStatus.READY;
            case NEEDS_ATTENTION -> DemoSmokeChecklistStatus.NEEDS_ATTENTION;
            case BLOCKED -> DemoSmokeChecklistStatus.BLOCKED;
        };
    }

    private static DemoSmokeChecklistStatus aggregateStatus(List<DemoSmokeChecklistStepVo> steps) {
        if (steps.stream().anyMatch(step -> step.status() == DemoSmokeChecklistStatus.BLOCKED)) {
            return DemoSmokeChecklistStatus.BLOCKED;
        }
        if (steps.stream().anyMatch(step -> step.status() == DemoSmokeChecklistStatus.NEEDS_ATTENTION)) {
            return DemoSmokeChecklistStatus.NEEDS_ATTENTION;
        }
        return DemoSmokeChecklistStatus.READY;
    }

    private static String summary(DemoSmokeChecklistStatus status) {
        return switch (status) {
            case READY -> "Live demo smoke checklist is ready.";
            case NEEDS_ATTENTION -> "Live demo smoke checklist needs attention.";
            case BLOCKED -> "Live demo smoke checklist is blocked.";
        };
    }

    private static List<String> nextActions(DemoReadinessVo readiness, List<DemoSmokeChecklistStepVo> steps) {
        if (readiness.status() == DemoReadinessStatus.BLOCKED) {
            return readiness.nextActions();
        }
        List<String> actions = steps.stream()
                .filter(step -> step.status() != DemoSmokeChecklistStatus.READY)
                .map(DemoSmokeChecklistStepVo::action)
                .distinct()
                .toList();
        if (!actions.isEmpty()) {
            return actions;
        }
        return List.of("Post a concrete /agent fix comment on the controlled GitHub issue.");
    }

    private static String firstAction(List<String> actions) {
        return actions.isEmpty() ? "No action needed." : actions.get(0);
    }

    private static String latestDeliveryEvidence(List<WebhookDeliveryDiagnosticVo> deliveries) {
        if (deliveries == null || deliveries.isEmpty()) {
            return "No delivery evidence";
        }
        return deliveries.get(0).deliveryId();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return "No delivery evidence";
    }

    private static GitHubWebhookSetupReadinessVo defaultReadyWebhookSetupReadiness() {
        return new GitHubWebhookSetupReadinessVo(
                GitHubWebhookSetupReadinessService.READY,
                true,
                true,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "https://demo.trycloudflare.com/health",
                "TASK_CREATED",
                "delivery-ready",
                false,
                "Webhook setup is ready for GitHub deliveries.",
                List.of("Use the payload URL in GitHub Webhooks and continue the live demo."),
                java.time.Instant.EPOCH,
                "# PatchPilot Webhook Setup Readiness"
        );
    }

    private static String nullToUnknown(String value) {
        return hasText(value) ? value : "unknown";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
