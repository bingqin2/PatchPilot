package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCheckVo;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCommand;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.github.credential.GitHubLivePublishPreflightService;
import io.patchpilot.backend.github.credential.GitHubWebhookSetupReadinessService;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.GitHubTriggerDryRunService;
import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunCommand;
import io.patchpilot.backend.github.webhook.domain.GitHubTriggerDryRunVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Service
public class DemoLiveLaunchGateService {

    public static final String STATUS_READY = "READY";
    public static final String STATUS_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    public static final String STATUS_BLOCKED = "BLOCKED";

    private static final String SIDE_EFFECT_CONTRACT = """
            Read-only live launch gate: this endpoint does not create tasks, does not enqueue work, \
            does not record rate-limit usage, does not run git push, does not create branches, \
            does not open Pull Requests, does not write GitHub comments, does not archive records, \
            and does not expose tokens.\
            """;

    private final Supplier<DemoSelfHostedLaunchReadinessVo> launchReadinessSupplier;
    private final Supplier<GitHubWebhookSetupReadinessVo> webhookReadinessSupplier;
    private final BiFunction<String, String, GitHubLivePublishPreflightVo> publishPreflightSupplier;
    private final GitHubTriggerDryRunService gitHubTriggerDryRunService;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveLaunchGateService(
            SelfHostedLaunchReadinessService selfHostedLaunchReadinessService,
            GitHubWebhookSetupReadinessService gitHubWebhookSetupReadinessService,
            GitHubLivePublishPreflightService gitHubLivePublishPreflightService,
            GitHubTriggerDryRunService gitHubTriggerDryRunService
    ) {
        this(
                selfHostedLaunchReadinessService::getReadinessPackage,
                gitHubWebhookSetupReadinessService::getReadiness,
                gitHubLivePublishPreflightService::getPreflight,
                gitHubTriggerDryRunService,
                Instant::now
        );
    }

    DemoLiveLaunchGateService(
            Supplier<DemoSelfHostedLaunchReadinessVo> launchReadinessSupplier,
            Supplier<GitHubWebhookSetupReadinessVo> webhookReadinessSupplier,
            BiFunction<String, String, GitHubLivePublishPreflightVo> publishPreflightSupplier,
            GitHubTriggerDryRunService gitHubTriggerDryRunService,
            Supplier<Instant> nowSupplier
    ) {
        this.launchReadinessSupplier = launchReadinessSupplier;
        this.webhookReadinessSupplier = webhookReadinessSupplier;
        this.publishPreflightSupplier = publishPreflightSupplier;
        this.gitHubTriggerDryRunService = gitHubTriggerDryRunService;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveLaunchGateVo getGate(DemoLiveLaunchGateCommand command) {
        DemoSelfHostedLaunchReadinessVo launchReadiness = launchReadiness();
        GitHubWebhookSetupReadinessVo webhookSetup = webhookReadiness();
        GitHubLivePublishPreflightVo livePublishPreflight = livePublishPreflight(
                command.repositoryOwner(),
                command.repositoryName()
        );
        GitHubTriggerDryRunVo triggerDryRun = gitHubTriggerDryRunService.dryRun(new GitHubTriggerDryRunCommand(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser(),
                command.triggerComment()
        ));

        List<DemoLiveLaunchGateCheckVo> checks = checks(launchReadiness, webhookSetup, livePublishPreflight, triggerDryRun);
        String status = aggregateStatus(checks);
        List<String> nextActions = nextActions(status, launchReadiness, webhookSetup, livePublishPreflight, triggerDryRun);
        String summary = summary(status);
        Instant generatedAt = nowSupplier.get();
        String repository = command.repositoryOwner() + "/" + command.repositoryName();
        String issueUrl = "https://github.com/" + repository + "/issues/" + command.issueNumber();
        String markdownReport = markdownReport(status, summary, repository, issueUrl, command, checks, nextActions);

        return new DemoLiveLaunchGateVo(
                status,
                STATUS_READY.equals(status),
                repository,
                command.issueNumber(),
                issueUrl,
                command.triggerUser(),
                command.triggerComment(),
                summary,
                nextActions,
                SIDE_EFFECT_CONTRACT,
                launchReadiness,
                webhookSetup,
                livePublishPreflight,
                triggerDryRun,
                checks,
                generatedAt,
                markdownReport
        );
    }

    private DemoSelfHostedLaunchReadinessVo launchReadiness() {
        return launchReadinessSupplier.get();
    }

    private GitHubWebhookSetupReadinessVo webhookReadiness() {
        return webhookReadinessSupplier.get();
    }

    private GitHubLivePublishPreflightVo livePublishPreflight(String owner, String repository) {
        return publishPreflightSupplier.apply(owner, repository);
    }

    private static List<DemoLiveLaunchGateCheckVo> checks(
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            GitHubWebhookSetupReadinessVo webhookSetup,
            GitHubLivePublishPreflightVo livePublishPreflight,
            GitHubTriggerDryRunVo triggerDryRun
    ) {
        return List.of(
                new DemoLiveLaunchGateCheckVo(
                        "Self-hosted launch readiness",
                        launchReadiness.status().name(),
                        launchReadiness.summary(),
                        launchReadiness.readyToLaunch() ? "Ready." : "Complete launch readiness actions."
                ),
                new DemoLiveLaunchGateCheckVo(
                        "Webhook setup",
                        webhookSetup.status(),
                        webhookSetup.summary(),
                        webhookSetup.nextActions().isEmpty() ? "Ready." : webhookSetup.nextActions().get(0)
                ),
                new DemoLiveLaunchGateCheckVo(
                        "Live GitHub publish preflight",
                        livePublishPreflight.status(),
                        livePublishPreflight.summary(),
                        livePublishPreflight.nextAction()
                ),
                new DemoLiveLaunchGateCheckVo(
                        "Live trigger dry run",
                        triggerDryRun.wouldCreateTask() ? STATUS_READY : STATUS_BLOCKED,
                        triggerDryRun.summary(),
                        triggerDryRun.nextAction()
                )
        );
    }

    private static String aggregateStatus(List<DemoLiveLaunchGateCheckVo> checks) {
        if (checks.stream().anyMatch(check -> STATUS_BLOCKED.equals(check.status()))) {
            return STATUS_BLOCKED;
        }
        if (checks.stream().anyMatch(check -> STATUS_NEEDS_ATTENTION.equals(check.status()))) {
            return STATUS_NEEDS_ATTENTION;
        }
        return STATUS_READY;
    }

    private static List<String> nextActions(
            String status,
            DemoSelfHostedLaunchReadinessVo launchReadiness,
            GitHubWebhookSetupReadinessVo webhookSetup,
            GitHubLivePublishPreflightVo livePublishPreflight,
            GitHubTriggerDryRunVo triggerDryRun
    ) {
        if (STATUS_READY.equals(status)) {
            return List.of("Post the exact /agent fix comment on the GitHub issue and watch webhook delivery, task execution, and Pull Request creation.");
        }
        List<String> actions = new ArrayList<>();
        actions.addAll(launchReadiness.nextActions());
        actions.addAll(webhookSetup.nextActions());
        if (!livePublishPreflight.livePublishReady() && livePublishPreflight.nextAction() != null) {
            actions.add(livePublishPreflight.nextAction());
        }
        if (!triggerDryRun.wouldCreateTask()) {
            actions.add(triggerDryRun.nextAction());
        }
        actions.add("Rerun this live launch gate before posting the GitHub issue comment.");
        return actions.stream().distinct().toList();
    }

    private static String summary(String status) {
        return switch (status) {
            case STATUS_READY -> "PatchPilot is ready for a live /agent fix launch.";
            case STATUS_BLOCKED -> "PatchPilot is blocked before live launch.";
            default -> "PatchPilot needs attention before live launch.";
        };
    }

    private static String markdownReport(
            String status,
            String summary,
            String repository,
            String issueUrl,
            DemoLiveLaunchGateCommand command,
            List<DemoLiveLaunchGateCheckVo> checks,
            List<String> nextActions
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Launch Gate\n\n");
        report.append("- Status: ").append(status).append("\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Repository: ").append(repository).append("\n");
        report.append("- Issue: ").append(issueUrl).append("\n");
        report.append("- Trigger user: ").append(command.triggerUser()).append("\n");
        report.append("- Trigger: `").append(command.triggerComment()).append("`\n\n");
        report.append("## Checks\n\n");
        for (DemoLiveLaunchGateCheckVo check : checks) {
            report.append("- ").append(check.name()).append(": ").append(check.status())
                    .append(" - ").append(check.message()).append("\n");
        }
        report.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            report.append("- ").append(action).append("\n");
        }
        return report.toString();
    }
}
