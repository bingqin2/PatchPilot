package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoSessionReportService {

    private final Supplier<DemoSessionSnapshotVo> snapshotSupplier;

    @Autowired
    public DemoSessionReportService(DemoSessionSnapshotService demoSessionSnapshotService) {
        this(demoSessionSnapshotService::getSessionSnapshot);
    }

    DemoSessionReportService(Supplier<DemoSessionSnapshotVo> snapshotSupplier) {
        this.snapshotSupplier = snapshotSupplier;
    }

    public String getSessionReport() {
        return getSessionReport(new DemoSessionReportRequestDto(List.of()));
    }

    public String getSessionReport(DemoSessionReportRequestDto request) {
        DemoSessionSnapshotVo snapshot = snapshotSupplier.get();
        return formatSessionReport(snapshot, request);
    }

    public String getHandoffPackage(DemoSessionReportRequestDto request) {
        DemoSessionSnapshotVo snapshot = snapshotSupplier.get();
        return formatHandoffPackage(snapshot, request);
    }

    String formatSessionReport(DemoSessionSnapshotVo snapshot) {
        return formatSessionReport(snapshot, new DemoSessionReportRequestDto(List.of()));
    }

    String formatSessionReport(DemoSessionSnapshotVo snapshot, DemoSessionReportRequestDto request) {
        StringBuilder report = new StringBuilder()
                .append("# PatchPilot Demo Session Report\n\n")
                .append("- Session: `").append(snapshot.sessionId()).append("`\n")
                .append("- Status: `").append(snapshot.status()).append("`\n")
                .append("- Summary: ").append(snapshot.summary()).append("\n")
                .append("- Generated at: `").append(snapshot.generatedAt()).append("`\n")
                .append("- Share summary: ").append(snapshot.shareSummary()).append("\n")
                .append("- Recent Pull Request: ").append(valueOrNone(snapshot.evidenceBundle().recentPullRequestUrl())).append("\n");

        appendRecentTask(report, snapshot.evidenceBundle().recentTask());
        appendWebhookSetupReadiness(report, snapshot.evidenceBundle().webhookSetupReadiness());
        appendRecentWebhookDeliveries(report, snapshot.evidenceBundle().recentWebhookDeliveries());
        appendReadinessSnapshotTrend(report, snapshot.readinessSnapshotTrend());
        appendHandoffReadiness(report, snapshot, request);
        appendList(report, "Operator Checklist", snapshot.operatorChecklist(), "No operator checklist items recorded.");
        appendPreparedLaunchCommands(report, request.preparedLaunchCommands());
        appendArchivedLaunchOutcomes(report, request.archivedLaunchOutcomes());
        appendScriptSteps(report, snapshot.script().steps());
        appendList(report, "Health Contract", withReportHealthContract(snapshot.healthContract()), "No health contract recorded.");
        appendList(report, "Next Actions", snapshot.nextActions(), "No next actions recorded.");
        report.append("\n## Runbook\n\n").append(snapshot.runbook());
        return report.toString();
    }

    String formatHandoffPackage(DemoSessionSnapshotVo snapshot, DemoSessionReportRequestDto request) {
        String sessionReport = formatSessionReport(snapshot, request);
        StringBuilder report = new StringBuilder()
                .append("# PatchPilot Demo Handoff Package\n\n")
                .append("- Session: `").append(snapshot.sessionId()).append("`\n")
                .append("- Demo status: `").append(snapshot.status()).append("`\n")
                .append("- Generated at: `").append(snapshot.generatedAt()).append("`\n")
                .append("- Recent Pull Request: ").append(valueOrNone(snapshot.evidenceBundle().recentPullRequestUrl())).append("\n")
                .append("- Prepared commands: `").append(countNonBlankCommands(request.preparedLaunchCommands())).append("`\n")
                .append("- Archived launch outcomes: `").append(countNonBlankOutcomes(request.archivedLaunchOutcomes())).append("`\n");
        appendRecentTask(report, snapshot.evidenceBundle().recentTask());
        report.append("\n## Handoff Summary\n\n")
                .append("- ").append(snapshot.evidenceBundle().summary()).append("\n")
                .append("- ").append(snapshot.shareSummary()).append("\n")
                .append("- Readiness trend: `").append(snapshot.readinessSnapshotTrend().status()).append("` - ")
                .append(snapshot.readinessSnapshotTrend().summary()).append("\n")
                .append("- Health contract: GET /api/demo/handoff-package is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.\n");
        appendHandoffReadiness(report, snapshot, request);
        appendList(report, "Next Actions", snapshot.nextActions(), "No next actions recorded.");
        appendPreparedLaunchCommands(report, request.preparedLaunchCommands());
        appendArchivedLaunchOutcomeSummary(report, request.archivedLaunchOutcomes());
        report.append("\n## Embedded Session Report\n\n").append(sessionReport);
        return report.toString();
    }

    private static void appendHandoffReadiness(
            StringBuilder report,
            DemoSessionSnapshotVo snapshot,
            DemoSessionReportRequestDto request
    ) {
        HandoffReadiness readiness = handoffReadiness(snapshot, request);
        report.append("\n## Handoff Readiness\n\n")
                .append("- Overall: `")
                .append(readiness.status())
                .append("` - ")
                .append(readiness.summary())
                .append("\n");
        readiness.checks().forEach(check -> report
                .append("- ")
                .append(check.name())
                .append(": `")
                .append(check.status())
                .append("` - ")
                .append(check.summary())
                .append("\n"));
    }

    private static HandoffReadiness handoffReadiness(DemoSessionSnapshotVo snapshot, DemoSessionReportRequestDto request) {
        List<HandoffReadinessCheck> checks = List.of(
                snapshotStatusCheck(snapshot),
                recentTaskCheck(snapshot.evidenceBundle().recentTask()),
                recentPullRequestCheck(snapshot.evidenceBundle().recentPullRequestUrl()),
                preparedCommandCheck(request.preparedLaunchCommands()),
                archivedOutcomeCheck(request.archivedLaunchOutcomes()),
                readinessTrendCheck(snapshot.readinessSnapshotTrend())
        );
        DemoReadinessStatus overallStatus = overallHandoffStatus(checks);
        return new HandoffReadiness(overallStatus, handoffSummary(overallStatus), checks);
    }

    private static HandoffReadinessCheck snapshotStatusCheck(DemoSessionSnapshotVo snapshot) {
        return new HandoffReadinessCheck("Demo snapshot status", snapshot.status(), snapshot.summary());
    }

    private static HandoffReadinessCheck recentTaskCheck(FixTaskVo task) {
        if (task == null) {
            return new HandoffReadinessCheck(
                    "Recent task evidence",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No recent completed task is available in the session snapshot."
            );
        }
        if (task.status() == FixTaskStatus.COMPLETED) {
            return new HandoffReadinessCheck(
                    "Recent task evidence",
                    DemoReadinessStatus.READY,
                    task.id() + " is completed."
            );
        }
        return new HandoffReadinessCheck(
                "Recent task evidence",
                DemoReadinessStatus.NEEDS_ATTENTION,
                task.id() + " is " + task.status() + ", not completed."
        );
    }

    private static HandoffReadinessCheck recentPullRequestCheck(String pullRequestUrl) {
        if (isBlank(pullRequestUrl)) {
            return new HandoffReadinessCheck(
                    "Recent Pull Request evidence",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No recent Pull Request URL is available."
            );
        }
        return new HandoffReadinessCheck(
                "Recent Pull Request evidence",
                DemoReadinessStatus.READY,
                pullRequestUrl
        );
    }

    private static HandoffReadinessCheck preparedCommandCheck(List<DemoPreparedLaunchCommandRequestDto> commands) {
        long commandCount = countNonBlankCommands(commands);
        if (commandCount == 0) {
            return new HandoffReadinessCheck(
                    "Prepared command context",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No prepared launch command was captured in this browser session."
            );
        }
        return new HandoffReadinessCheck(
                "Prepared command context",
                DemoReadinessStatus.READY,
                commandCount + " prepared " + plural(commandCount, "command") + " recorded."
        );
    }

    private static HandoffReadinessCheck archivedOutcomeCheck(List<DemoArchivedLaunchOutcomeRequestDto> outcomes) {
        long evidenceCount = countOutcomeEvidence(outcomes);
        if (evidenceCount == 0) {
            return new HandoffReadinessCheck(
                    "Archived launch outcome context",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No archived launch outcome with completed task or Pull Request evidence was captured."
            );
        }
        return new HandoffReadinessCheck(
                "Archived launch outcome context",
                DemoReadinessStatus.READY,
                evidenceCount + " archived " + plural(evidenceCount, "outcome")
                        + " has completed task or Pull Request evidence."
        );
    }

    private static HandoffReadinessCheck readinessTrendCheck(DemoReadinessSnapshotTrendVo trend) {
        if (trend.status() == DemoReadinessSnapshotTrendStatus.REGRESSING) {
            return new HandoffReadinessCheck(
                    "Readiness trend baseline",
                    DemoReadinessStatus.BLOCKED,
                    "Readiness trend is REGRESSING; address the regression or archive a fresh passing snapshot before handoff."
            );
        }
        if (trend.status() == DemoReadinessSnapshotTrendStatus.NO_BASELINE) {
            return new HandoffReadinessCheck(
                    "Readiness trend baseline",
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    "No previous readiness snapshot is available for comparison."
            );
        }
        return new HandoffReadinessCheck(
                "Readiness trend baseline",
                DemoReadinessStatus.READY,
                trend.status() + "; latest readiness " + valueOrNone(trend.latestReadinessStatus()) + "."
        );
    }

    private static DemoReadinessStatus overallHandoffStatus(List<HandoffReadinessCheck> checks) {
        if (checks.stream().anyMatch(check -> check.status() == DemoReadinessStatus.BLOCKED)) {
            return DemoReadinessStatus.BLOCKED;
        }
        if (checks.stream().anyMatch(check -> check.status() == DemoReadinessStatus.NEEDS_ATTENTION)) {
            return DemoReadinessStatus.NEEDS_ATTENTION;
        }
        return DemoReadinessStatus.READY;
    }

    private static String handoffSummary(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> "Handoff package has current PR, command, outcome, and readiness trend evidence.";
            case NEEDS_ATTENTION -> "Handoff package is missing evidence required for a credible live-demo handoff.";
            case BLOCKED -> "Handoff package has a blocking readiness signal that should be resolved before a live-demo handoff.";
        };
    }

    private static void appendReadinessSnapshotTrend(StringBuilder report, DemoReadinessSnapshotTrendVo trend) {
        report.append("\n## Readiness Snapshot Trend\n\n")
                .append("- Trend: `").append(trend.status()).append("`\n")
                .append("- Summary: ").append(trend.summary()).append("\n")
                .append("- Latest snapshot: `").append(valueOrNone(trend.latestSnapshotId())).append("`\n")
                .append("- Previous snapshot: `").append(valueOrNone(trend.previousSnapshotId())).append("`\n")
                .append("- Latest readiness: `").append(valueOrNone(trend.latestReadinessStatus())).append("`\n")
                .append("- Previous readiness: `").append(valueOrNone(trend.previousReadinessStatus())).append("`\n")
                .append("- Delta: `")
                .append(signed(trend.readyCheckDelta())).append(" ready / ")
                .append(signed(trend.needsAttentionCheckDelta())).append(" warning / ")
                .append(signed(trend.blockedCheckDelta())).append(" blocked`\n")
                .append("- Next action: ").append(trend.nextAction()).append("\n");
    }

    private static void appendWebhookSetupReadiness(StringBuilder report, GitHubWebhookSetupReadinessVo readiness) {
        report.append("\n## Webhook Setup Readiness\n\n");
        if (readiness == null) {
            report.append("- Status: none\n");
            return;
        }
        report.append("- Status: `").append(readiness.status()).append("`\n")
                .append("- Secret configured: `").append(readiness.secretConfigured()).append("`\n")
                .append("- Public URL ready: `").append(readiness.publicUrlReady()).append("`\n")
                .append("- Payload URL: ").append(valueOrNone(readiness.payloadUrl())).append("\n")
                .append("- Latest delivery: `").append(valueOrNone(readiness.latestDeliveryStatus())).append("`");
        if (!isBlank(readiness.latestDeliveryId())) {
            report.append(" (`").append(readiness.latestDeliveryId()).append("`)");
        }
        report.append("\n")
                .append("- Redelivery recommended: `").append(readiness.redeliveryRecommended()).append("`\n")
                .append("- Next action: ").append(firstAction(readiness.nextActions())).append("\n");
    }

    private static void appendRecentWebhookDeliveries(StringBuilder report, List<WebhookDeliveryDiagnosticVo> deliveries) {
        report.append("\n## Recent Webhook Deliveries\n\n");
        if (deliveries == null || deliveries.isEmpty()) {
            report.append("- No recent webhook deliveries recorded.\n");
            return;
        }
        deliveries.stream()
                .limit(5)
                .forEach(delivery -> report
                        .append("- `").append(valueOrNone(delivery.deliveryId())).append("`: `")
                        .append(valueOrNone(delivery.status())).append("` -> ")
                        .append(valueOrNone(firstNonBlank(delivery.outcomeId(), delivery.taskId())))
                        .append("\n")
                        .append("  - Repository: `")
                        .append(valueOrNone(delivery.repositoryOwner()))
                        .append("/")
                        .append(valueOrNone(delivery.repositoryName()))
                        .append("#")
                        .append(valueOrNone(delivery.issueNumber()))
                        .append("`\n")
                        .append("  - Trigger: `").append(valueOrNone(delivery.triggerComment())).append("`\n")
                        .append("  - Outcome: `").append(valueOrNone(delivery.outcomeType())).append("`\n")
                        .append("  - Message: ").append(valueOrNone(delivery.message())).append("\n"));
    }

    private static void appendRecentTask(StringBuilder report, FixTaskVo task) {
        report.append("- Recent task: ");
        if (task == null) {
            report.append("none\n");
            return;
        }
        report.append("`")
                .append(task.id())
                .append("` (`")
                .append(task.status())
                .append("`)\n");
    }

    private static void appendPreparedLaunchCommands(
            StringBuilder report,
            List<DemoPreparedLaunchCommandRequestDto> preparedLaunchCommands
    ) {
        report.append("\n## Prepared Launch Commands\n\n");
        List<DemoPreparedLaunchCommandRequestDto> commands = preparedLaunchCommands.stream()
                .filter(command -> !isBlank(command.triggerComment()))
                .limit(5)
                .toList();
        if (commands.isEmpty()) {
            report.append("- No prepared launch commands recorded for this browser session.\n");
            return;
        }

        commands.forEach(command -> {
            report.append("- `").append(command.triggerComment().trim()).append("`\n")
                    .append("  - Target: `")
                    .append(valueOrNone(command.repositoryOwner()))
                    .append("/")
                    .append(valueOrNone(command.repositoryName()))
                    .append("#")
                    .append(command.issueNumber() == null ? "none" : command.issueNumber())
                    .append("`\n")
                    .append("  - Trigger user: `").append(valueOrNone(command.triggerUser())).append("`\n")
                    .append("  - Operation: `").append(valueOrNone(command.operation())).append("` on `")
                    .append(valueOrNone(command.targetPath()))
                    .append("`\n");
            if (!isBlank(command.replacementText())) {
                report.append("  - Replacement: `").append(command.replacementText().trim()).append("`\n");
            }
            report.append("  - Saved at: `").append(valueOrNone(command.savedAt())).append("`\n");
        });
    }

    private static void appendArchivedLaunchOutcomes(
            StringBuilder report,
            List<DemoArchivedLaunchOutcomeRequestDto> archivedLaunchOutcomes
    ) {
        report.append("\n## Archived Launch Outcomes\n\n");
        List<DemoArchivedLaunchOutcomeRequestDto> outcomes = archivedLaunchOutcomes.stream()
                .filter(outcome -> !isBlank(outcome.triggerComment()))
                .limit(5)
                .toList();
        if (outcomes.isEmpty()) {
            report.append("- No archived launch outcomes recorded for this browser session.\n");
            return;
        }

        outcomes.forEach(outcome -> {
            report.append("- `").append(outcome.triggerComment().trim()).append("`\n")
                    .append("  - Target: `")
                    .append(valueOrNone(outcome.repositoryOwner()))
                    .append("/")
                    .append(valueOrNone(outcome.repositoryName()))
                    .append("#")
                    .append(outcome.issueNumber() == null ? "none" : outcome.issueNumber())
                    .append("`\n")
                    .append("  - Trigger user: `").append(valueOrNone(outcome.triggerUser())).append("`\n")
                    .append("  - Task: `").append(valueOrNone(outcome.taskId())).append("` (`")
                    .append(valueOrNone(outcome.taskStatus()))
                    .append("`)\n")
                    .append("  - Pull Request: ").append(valueOrNone(outcome.pullRequestUrl())).append("\n")
                    .append("  - Archived at: `").append(valueOrNone(outcome.archivedAt())).append("`\n");
            String summary = summarizeOutcomeReport(outcome.report());
            if (!isBlank(summary)) {
                report.append("  - Report: `").append(summary).append("`\n");
            }
        });
    }

    private static void appendArchivedLaunchOutcomeSummary(
            StringBuilder report,
            List<DemoArchivedLaunchOutcomeRequestDto> archivedLaunchOutcomes
    ) {
        report.append("\n## Archived Launch Outcomes\n\n");
        List<DemoArchivedLaunchOutcomeRequestDto> outcomes = archivedLaunchOutcomes.stream()
                .filter(outcome -> !isBlank(outcome.triggerComment()))
                .limit(5)
                .toList();
        if (outcomes.isEmpty()) {
            report.append("- No archived launch outcomes recorded for this browser session.\n");
            return;
        }

        outcomes.forEach(outcome -> report
                .append("- `").append(valueOrNone(outcome.taskId())).append("` (`")
                .append(valueOrNone(outcome.taskStatus()))
                .append("`) -> ")
                .append(valueOrNone(outcome.pullRequestUrl()))
                .append("\n")
                .append("  - Command: `").append(outcome.triggerComment().trim()).append("`\n")
                .append("  - Archived at: `").append(valueOrNone(outcome.archivedAt())).append("`\n"));
    }

    private static void appendScriptSteps(StringBuilder report, List<DemoScriptStepVo> steps) {
        report.append("\n## Script Steps\n\n");
        if (steps.isEmpty()) {
            report.append("- No script steps recorded.\n");
            return;
        }
        steps.stream()
                .sorted((left, right) -> Integer.compare(left.order(), right.order()))
                .forEach(step -> report
                        .append("- ").append(step.order()).append(". `")
                        .append(step.name()).append("`: `")
                        .append(step.status()).append("`\n")
                        .append("  - Action: ").append(step.operatorAction()).append("\n")
                        .append("  - Verify: `").append(step.verificationCommand()).append("`\n")
                        .append("  - Success: ").append(step.successCriteria()).append("\n")
                        .append("  - Troubleshoot: ").append(step.troubleshootingPanel()).append("\n"));
    }

    private static void appendList(StringBuilder report, String heading, List<String> items, String emptyText) {
        report.append("\n## ").append(heading).append("\n\n");
        if (items.isEmpty()) {
            report.append("- ").append(emptyText).append("\n");
            return;
        }
        items.forEach(item -> report.append("- ").append(item).append("\n"));
    }

    private static List<String> withReportHealthContract(List<String> healthContract) {
        String reportContract = "GET /api/demo/session-report is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.";
        if (healthContract.contains(reportContract)) {
            return healthContract;
        }
        return java.util.stream.Stream.concat(java.util.stream.Stream.of(reportContract), healthContract.stream()).toList();
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }

    private static String valueOrNone(Object value) {
        return value == null ? "none" : value.toString();
    }

    private static String firstAction(List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            return "No action needed.";
        }
        return actions.get(0);
    }

    private static String firstNonBlank(String first, String second) {
        return isBlank(first) ? second : first;
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
    }

    private static long countNonBlankCommands(List<DemoPreparedLaunchCommandRequestDto> commands) {
        return commands.stream()
                .filter(command -> !isBlank(command.triggerComment()))
                .limit(5)
                .count();
    }

    private static long countNonBlankOutcomes(List<DemoArchivedLaunchOutcomeRequestDto> outcomes) {
        return outcomes.stream()
                .filter(outcome -> !isBlank(outcome.triggerComment()))
                .limit(5)
                .count();
    }

    private static long countOutcomeEvidence(List<DemoArchivedLaunchOutcomeRequestDto> outcomes) {
        return outcomes.stream()
                .filter(outcome -> !isBlank(outcome.triggerComment()))
                .filter(outcome -> "COMPLETED".equals(outcome.taskStatus()) || !isBlank(outcome.pullRequestUrl()))
                .limit(5)
                .count();
    }

    private static String plural(long count, String noun) {
        return count == 1 ? noun : noun + "s";
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String summarizeOutcomeReport(String report) {
        if (isBlank(report)) {
            return "";
        }
        String summary = report.replace("`", "'")
                .replace("\r", "")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (summary.length() <= 280) {
            return summary;
        }
        return summary.substring(0, 277) + "...";
    }

    private record HandoffReadiness(
            DemoReadinessStatus status,
            String summary,
            List<HandoffReadinessCheck> checks
    ) {
    }

    private record HandoffReadinessCheck(
            String name,
            DemoReadinessStatus status,
            String summary
    ) {
    }
}
