package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoScriptStepVo;
import io.patchpilot.backend.demo.domain.DemoSessionSnapshotVo;
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
        appendList(report, "Operator Checklist", snapshot.operatorChecklist(), "No operator checklist items recorded.");
        appendPreparedLaunchCommands(report, request.preparedLaunchCommands());
        appendScriptSteps(report, snapshot.script().steps());
        appendList(report, "Health Contract", withReportHealthContract(snapshot.healthContract()), "No health contract recorded.");
        appendList(report, "Next Actions", snapshot.nextActions(), "No next actions recorded.");
        report.append("\n## Runbook\n\n").append(snapshot.runbook());
        return report.toString();
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
