package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.github.credential.GitHubPublishPermissionReadinessService;
import io.patchpilot.backend.github.credential.GitHubPublishReadinessService;
import io.patchpilot.backend.github.credential.domain.GitHubPublishPermissionReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubPublishReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class SelfHostedLaunchReadinessService {

    private final Supplier<DemoReadinessVo> readinessSupplier;
    private final Supplier<DemoEvidenceBundleVo> evidenceBundleSupplier;
    private final Supplier<GitHubPublishReadinessVo> publishReadinessSupplier;
    private final Supplier<GitHubPublishPermissionReadinessVo> publishPermissionReadinessSupplier;

    @Autowired
    public SelfHostedLaunchReadinessService(
            DemoReadinessService demoReadinessService,
            DemoEvidenceBundleService demoEvidenceBundleService,
            GitHubPublishReadinessService gitHubPublishReadinessService,
            GitHubPublishPermissionReadinessService gitHubPublishPermissionReadinessService
    ) {
        this(
                demoReadinessService::getReadiness,
                demoEvidenceBundleService::getEvidenceBundle,
                () -> gitHubPublishReadinessService.getReadiness(null, null),
                () -> gitHubPublishPermissionReadinessService.getReadiness(null, null)
        );
    }

    SelfHostedLaunchReadinessService(
            Supplier<DemoReadinessVo> readinessSupplier,
            Supplier<DemoEvidenceBundleVo> evidenceBundleSupplier,
            Supplier<GitHubPublishReadinessVo> publishReadinessSupplier,
            Supplier<GitHubPublishPermissionReadinessVo> publishPermissionReadinessSupplier
    ) {
        this.readinessSupplier = readinessSupplier;
        this.evidenceBundleSupplier = evidenceBundleSupplier;
        this.publishReadinessSupplier = publishReadinessSupplier;
        this.publishPermissionReadinessSupplier = publishPermissionReadinessSupplier;
    }

    public DemoSelfHostedLaunchReadinessVo getReadinessPackage() {
        DemoReadinessVo readiness = readinessSupplier.get();
        DemoEvidenceBundleVo evidenceBundle = evidenceBundleSupplier.get();
        GitHubPublishReadinessVo publishReadiness = publishReadinessSupplier.get();
        GitHubPublishPermissionReadinessVo publishPermissionReadiness = publishPermissionReadinessSupplier.get();
        List<DemoSelfHostedLaunchCheckVo> checks = checks(readiness, evidenceBundle, publishReadiness, publishPermissionReadiness);
        DemoReadinessStatus status = aggregateStatus(checks);
        List<String> nextActions = nextActions(status, readiness, evidenceBundle, publishReadiness, publishPermissionReadiness);
        Instant generatedAt = Instant.now();
        return new DemoSelfHostedLaunchReadinessVo(
                status,
                status == DemoReadinessStatus.READY,
                summary(status),
                checks,
                nextActions,
                generatedAt,
                markdownReport(status, checks, nextActions, generatedAt)
        );
    }

    private static List<DemoSelfHostedLaunchCheckVo> checks(
            DemoReadinessVo readiness,
            DemoEvidenceBundleVo evidenceBundle,
            GitHubPublishReadinessVo publishReadiness,
            GitHubPublishPermissionReadinessVo publishPermissionReadiness
    ) {
        return List.of(
                new DemoSelfHostedLaunchCheckVo(
                        "Demo readiness",
                        readiness.status(),
                        readiness.summary(),
                        firstAction(readiness.nextActions())
                ),
                new DemoSelfHostedLaunchCheckVo(
                        "Evidence bundle",
                        evidenceBundle.status(),
                        evidenceBundle.summary(),
                        firstAction(evidenceBundle.nextActions())
                ),
                new DemoSelfHostedLaunchCheckVo(
                        "Handoff finalization",
                        evidenceBundle.handoffFinalizationStatus(),
                        evidenceBundle.handoffFinalizationSummary(),
                        evidenceBundle.handoffFinalizationNextAction()
                ),
                new DemoSelfHostedLaunchCheckVo(
                        "GitHub publish path",
                        githubStatus(publishReadiness.status()),
                        publishReadiness.summary(),
                        publishReadiness.nextAction()
                ),
                new DemoSelfHostedLaunchCheckVo(
                        "GitHub publish permissions",
                        githubStatus(publishPermissionReadiness.status()),
                        publishPermissionReadiness.summary(),
                        publishPermissionReadiness.nextAction()
                ),
                projectedCheck("Credentials and secrets", readiness, "Credentials"),
                projectedCheck("Webhook setup", readiness, "Webhook setup"),
                projectedCheck("Queue and worker", readiness, "Queue")
        );
    }

    private static DemoSelfHostedLaunchCheckVo projectedCheck(
            String launchCheckName,
            DemoReadinessVo readiness,
            String readinessCheckName
    ) {
        DemoReadinessCheckVo check = readiness.checks().stream()
                .filter(candidate -> candidate.name().equals(readinessCheckName))
                .findFirst()
                .orElse(null);
        if (check == null) {
            return new DemoSelfHostedLaunchCheckVo(
                    launchCheckName,
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    readinessCheckName + " readiness check is missing from demo readiness.",
                    "Refresh demo readiness and verify " + readinessCheckName.toLowerCase() + " configuration."
            );
        }
        return new DemoSelfHostedLaunchCheckVo(
                launchCheckName,
                check.status(),
                check.message(),
                check.action()
        );
    }

    private static DemoReadinessStatus aggregateStatus(List<DemoSelfHostedLaunchCheckVo> checks) {
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
            case READY -> "Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.";
            case NEEDS_ATTENTION -> "Self-hosted PatchPilot needs attention before launch.";
            case BLOCKED -> "Self-hosted PatchPilot is blocked before launch.";
        };
    }

    private static List<String> nextActions(
            DemoReadinessStatus status,
            DemoReadinessVo readiness,
            DemoEvidenceBundleVo evidenceBundle,
            GitHubPublishReadinessVo publishReadiness,
            GitHubPublishPermissionReadinessVo publishPermissionReadiness
    ) {
        if (status == DemoReadinessStatus.READY) {
            return List.of("Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review.");
        }
        Set<String> actions = new LinkedHashSet<>();
        actions.addAll(readiness.nextActions());
        actions.addAll(evidenceBundle.nextActions());
        if (!GitHubPublishReadinessService.READY.equals(publishReadiness.status())) {
            actions.add(publishReadiness.nextAction());
        }
        if (!GitHubPublishPermissionReadinessService.READY.equals(publishPermissionReadiness.status())) {
            actions.add(publishPermissionReadiness.nextAction());
        }
        if (status == DemoReadinessStatus.BLOCKED) {
            actions.add("Resolve blocked launch checks before posting a live /agent fix trigger.");
        } else {
            actions.add("Resolve launch package warnings, then rerun this readiness package.");
        }
        return List.copyOf(actions);
    }

    private static String markdownReport(
            DemoReadinessStatus status,
            List<DemoSelfHostedLaunchCheckVo> checks,
            List<String> nextActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder()
                .append("# PatchPilot Self-Hosted Launch Readiness\n\n")
                .append("- Status: `").append(status).append("`\n")
                .append("- Ready to launch: `").append(status == DemoReadinessStatus.READY).append("`\n")
                .append("- Generated at: `").append(generatedAt).append("`\n\n")
                .append("## Checks\n\n");
        for (DemoSelfHostedLaunchCheckVo check : checks) {
            report.append("- `").append(check.status()).append("` ")
                    .append(check.name()).append(": ")
                    .append(check.message()).append(" Action: ")
                    .append(check.action()).append("\n");
        }
        report.append("\n## Next Actions\n\n");
        for (String nextAction : nextActions) {
            report.append("- ").append(nextAction).append("\n");
        }
        report.append("\n## Side Effect Contract\n\n")
                .append("This report is read-only. It does not create tasks, call the model, redeliver webhooks, run tests, mutate Git, or write to GitHub.\n");
        return report.toString();
    }

    private static String firstAction(List<String> nextActions) {
        return nextActions.isEmpty() ? "No action needed." : nextActions.get(0);
    }

    private static DemoReadinessStatus githubStatus(String status) {
        if (GitHubPublishReadinessService.READY.equals(status)
                || GitHubPublishPermissionReadinessService.READY.equals(status)) {
            return DemoReadinessStatus.READY;
        }
        if (GitHubPublishReadinessService.BLOCKED.equals(status)
                || GitHubPublishPermissionReadinessService.BLOCKED.equals(status)) {
            return DemoReadinessStatus.BLOCKED;
        }
        return DemoReadinessStatus.NEEDS_ATTENTION;
    }
}
