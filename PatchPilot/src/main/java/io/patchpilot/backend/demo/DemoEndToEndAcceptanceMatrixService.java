package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.demo.domain.DemoEndToEndAcceptanceMatrixItemVo;
import io.patchpilot.backend.demo.domain.DemoEndToEndAcceptanceMatrixVo;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCommand;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.evaluation.EvaluationCaseCatalogService;
import io.patchpilot.backend.evaluation.EvaluationFixtureBaselineRunRegressionSummaryService;
import io.patchpilot.backend.evaluation.EvaluationRunArchiveReadinessSummaryService;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunRegressionSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveReadinessSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationSummaryVo;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class DemoEndToEndAcceptanceMatrixService {

    private static final String STATUS_READY = "READY";
    private static final String STATUS_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String STATUS_BLOCKED = "BLOCKED";
    private static final String SIDE_EFFECT_CONTRACT = """
            Read-only end-to-end acceptance matrix: this endpoint does not create tasks, does not call the model, \
            does not run tests, does not clone repositories, does not mutate Git, does not create branches, \
            does not open Pull Requests, does not write GitHub comments, does not archive records, \
            and does not mutate GitHub.\
            """;

    private final Supplier<DemoLiveLaunchGateVo> liveLaunchGateSupplier;
    private final Supplier<EvaluationSummaryVo> evaluationSummarySupplier;
    private final Supplier<EvaluationFixtureBaselineRunRegressionSummaryVo> baselineRegressionSupplier;
    private final Supplier<EvaluationRunArchiveReadinessSummaryVo> evaluationRunSummarySupplier;
    private final Function<FixTaskListQuery, List<FixTaskVo>> taskSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoEndToEndAcceptanceMatrixService(
            DemoLiveLaunchGateService liveLaunchGateService,
            DemoProperties demoProperties,
            EvaluationCaseCatalogService evaluationCaseCatalogService,
            EvaluationFixtureBaselineRunRegressionSummaryService baselineRegressionSummaryService,
            EvaluationRunArchiveReadinessSummaryService evaluationRunArchiveReadinessSummaryService,
            FixTaskService fixTaskService
    ) {
        this(
                () -> liveLaunchGateService.getGate(new DemoLiveLaunchGateCommand(
                        demoProperties.getRepositoryOwner(),
                        demoProperties.getRepositoryName(),
                        1,
                        "local-operator",
                        "/agent fix touch docs/end-to-end-acceptance.md"
                )),
                evaluationCaseCatalogService::getEvaluationSummary,
                baselineRegressionSummaryService::getRegressionSummary,
                evaluationRunArchiveReadinessSummaryService::getSummary,
                fixTaskService::listTasks,
                Instant::now
        );
    }

    DemoEndToEndAcceptanceMatrixService(
            Supplier<DemoLiveLaunchGateVo> liveLaunchGateSupplier,
            Supplier<EvaluationSummaryVo> evaluationSummarySupplier,
            Supplier<EvaluationFixtureBaselineRunRegressionSummaryVo> baselineRegressionSupplier,
            Supplier<EvaluationRunArchiveReadinessSummaryVo> evaluationRunSummarySupplier,
            Function<FixTaskListQuery, List<FixTaskVo>> taskSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.liveLaunchGateSupplier = liveLaunchGateSupplier;
        this.evaluationSummarySupplier = evaluationSummarySupplier;
        this.baselineRegressionSupplier = baselineRegressionSupplier;
        this.evaluationRunSummarySupplier = evaluationRunSummarySupplier;
        this.taskSupplier = taskSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoEndToEndAcceptanceMatrixVo getMatrix() {
        DemoLiveLaunchGateVo liveLaunchGate = liveLaunchGateSupplier.get();
        EvaluationSummaryVo evaluationSummary = evaluationSummarySupplier.get();
        EvaluationFixtureBaselineRunRegressionSummaryVo baselineRegression = baselineRegressionSupplier.get();
        EvaluationRunArchiveReadinessSummaryVo evaluationRunSummary = evaluationRunSummarySupplier.get();
        List<FixTaskVo> recentTasks = taskSupplier.apply(new FixTaskListQuery(
                null,
                null,
                null,
                null,
                null,
                null,
                50,
                0
        ));

        List<DemoEndToEndAcceptanceMatrixItemVo> items = List.of(
                launchItem(liveLaunchGate),
                languageCoverageItem(evaluationSummary),
                safetyCoverageItem(evaluationSummary),
                baselineRegressionItem(baselineRegression),
                evaluationRunItem(evaluationRunSummary),
                recentPullRequestItem(recentTasks),
                knownFailureItem(recentTasks),
                pendingReviewItem(recentTasks),
                productGapItem(liveLaunchGate, evaluationSummary, evaluationRunSummary, recentTasks)
        );

        long readyCount = count(items, STATUS_READY);
        long blockedCount = count(items, STATUS_BLOCKED);
        long needsAttentionCount = count(items, STATUS_NEEDS_ATTENTION);
        String status = aggregateStatus(blockedCount, needsAttentionCount);
        int readinessPercent = items.isEmpty() ? 0 : (int) Math.round((readyCount * 100.0) / items.size());
        List<String> nextActions = nextActions(items, status);
        String summary = summary(status);
        Instant generatedAt = nowSupplier.get();
        String markdownReport = markdownReport(status, readinessPercent, summary, items, nextActions);

        return new DemoEndToEndAcceptanceMatrixVo(
                status,
                STATUS_READY.equals(status),
                readinessPercent,
                Math.toIntExact(readyCount),
                Math.toIntExact(needsAttentionCount),
                Math.toIntExact(blockedCount),
                items.size(),
                summary,
                nextActions,
                SIDE_EFFECT_CONTRACT,
                items,
                generatedAt,
                markdownReport
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo launchItem(DemoLiveLaunchGateVo gate) {
        String status = normalizeStatus(gate.status());
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Launch",
                "Live launch gate",
                status,
                gate.summary(),
                STATUS_READY.equals(status) ? "No launch gate gap." : "Live launch gate is not ready.",
                STATUS_READY.equals(status) ? "Run the live launch gate before posting the issue comment." : "Fix the live launch gate before posting another GitHub issue comment."
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo languageCoverageItem(EvaluationSummaryVo summary) {
        List<String> requiredLanguages = List.of("go", "java", "node", "python");
        List<String> missing = requiredLanguages.stream()
                .filter(language -> !summary.coveredLanguages().contains(language))
                .toList();
        String status = missing.isEmpty() ? STATUS_READY : STATUS_NEEDS_ATTENTION;
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Language coverage",
                "Supported adapter languages",
                status,
                "Covered languages: " + join(summary.coveredLanguages()) + ".",
                missing.isEmpty() ? "No language coverage gap." : "Missing acceptance coverage for: " + join(missing) + ".",
                missing.isEmpty() ? "Keep Java, Node, Python, and Go acceptance evidence current." : "Add or refresh acceptance evidence for: " + join(missing) + "."
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo safetyCoverageItem(EvaluationSummaryVo summary) {
        List<String> requiredCategories = List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
        List<String> missing = requiredCategories.stream()
                .filter(category -> !summary.rejectionCategories().contains(category))
                .toList();
        String status = missing.isEmpty() ? STATUS_READY : STATUS_NEEDS_ATTENTION;
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Safety coverage",
                "Rejected trigger evidence",
                status,
                "Covered rejection categories: " + join(summary.rejectionCategories()) + ".",
                missing.isEmpty() ? "No safety rejection coverage gap." : "Missing rejection coverage for: " + join(missing) + ".",
                missing.isEmpty() ? "Keep unsafe and vague trigger rejection evidence current." : "Add acceptance cases for: " + join(missing) + "."
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo baselineRegressionItem(EvaluationFixtureBaselineRunRegressionSummaryVo summary) {
        String status = switch (summary.status()) {
            case "REGRESSED" -> STATUS_BLOCKED;
            case "STABLE", "IMPROVED" -> STATUS_READY;
            default -> STATUS_NEEDS_ATTENTION;
        };
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Evaluation baseline",
                "Fixture baseline regression",
                status,
                "Fixture baseline status: " + summary.status() + ".",
                STATUS_READY.equals(status) ? "No fixture baseline gap." : "Fixture baseline evidence is not stable.",
                STATUS_READY.equals(status) ? "Use fixture baseline as current adapter confidence evidence." : summary.nextAction()
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo evaluationRunItem(EvaluationRunArchiveReadinessSummaryVo summary) {
        String status = switch (summary.status()) {
            case STATUS_READY -> STATUS_READY;
            case STATUS_BLOCKED -> STATUS_BLOCKED;
            default -> STATUS_NEEDS_ATTENTION;
        };
        String latest = summary.latestRun() == null ? "none" : summary.latestRun().id();
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Evaluation run",
                "Full evaluation archive",
                status,
                "Latest archived evaluation run: " + latest + ".",
                STATUS_READY.equals(status) ? "No full evaluation archive gap." : "Full evaluation archive is missing or blocked.",
                STATUS_READY.equals(status) ? "Use latest full evaluation archive as current benchmark-shaped evidence." : summary.nextAction()
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo recentPullRequestItem(List<FixTaskVo> tasks) {
        List<FixTaskVo> completedPrTasks = tasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED)
                .filter(task -> task.pullRequestUrl() != null && !task.pullRequestUrl().isBlank())
                .toList();
        boolean ready = !completedPrTasks.isEmpty();
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Recent Pull Request evidence",
                "Completed issue-to-PR task",
                ready ? STATUS_READY : STATUS_NEEDS_ATTENTION,
                ready ? "Recent completed PR task: " + completedPrTasks.get(0).id() + "." : "No recent completed PR task found.",
                ready ? "No recent PR evidence gap." : "The final demo still needs a recent completed task with a Pull Request URL.",
                ready ? "Preserve the latest PR evidence in the demo package." : "Create at least one recent completed PatchPilot PR from a live /agent fix task."
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo knownFailureItem(List<FixTaskVo> tasks) {
        List<FixTaskVo> failedTasks = tasks.stream()
                .filter(task -> task.status() == FixTaskStatus.FAILED)
                .toList();
        boolean ready = failedTasks.stream()
                .anyMatch(task -> task.failureReason() != null && !task.failureReason().isBlank());
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Known failure handling",
                "Failed task evidence",
                ready ? STATUS_READY : STATUS_NEEDS_ATTENTION,
                ready ? "Recent failed task with reason: " + failedTasks.get(0).id() + "." : "No recent failed task with operator-facing reason found.",
                ready ? "Failure feedback path has recent evidence." : "Failure handling still needs recent evidence.",
                ready ? "Use failed-task evidence to explain safe stop behavior." : "Capture at least one failed or unsupported task with a clear failure reason."
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo pendingReviewItem(List<FixTaskVo> tasks) {
        boolean hasPendingReview = tasks.stream()
                .anyMatch(task -> task.status() == FixTaskStatus.PENDING_REVIEW);
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Pending review safety",
                "Generated diff risk review",
                hasPendingReview ? STATUS_READY : STATUS_NEEDS_ATTENTION,
                hasPendingReview ? "Recent pending-review task shows human approval boundary." : "No recent pending-review task found.",
                hasPendingReview ? "Risk review path has recent evidence." : "Human review path lacks recent evidence.",
                hasPendingReview ? "Keep pending-review evidence available for safety explanation." : "Create or preserve a generated-diff risk-review example before public demos."
        );
    }

    private static DemoEndToEndAcceptanceMatrixItemVo productGapItem(
            DemoLiveLaunchGateVo gate,
            EvaluationSummaryVo evaluationSummary,
            EvaluationRunArchiveReadinessSummaryVo evaluationRunSummary,
            List<FixTaskVo> tasks
    ) {
        boolean ready = gate.readyToPost()
                && evaluationSummary.coveredLanguages().containsAll(List.of("go", "java", "node", "python"))
                && STATUS_READY.equals(evaluationRunSummary.status())
                && tasks.stream().anyMatch(task -> task.status() == FixTaskStatus.COMPLETED && task.pullRequestUrl() != null);
        return new DemoEndToEndAcceptanceMatrixItemVo(
                "Final product gap",
                "Self-hosted issue-to-PR acceptance",
                ready ? STATUS_READY : STATUS_NEEDS_ATTENTION,
                ready ? "Core self-hosted demo acceptance signals are present." : "At least one final acceptance signal is missing.",
                ready ? "Remaining work is production hardening and hosted rollout." : "Missing launch, language, evaluation, or PR evidence before final demo acceptance.",
                ready ? "Move next toward real multi-repository smoke runs and hosted-readiness hardening." : "Close the missing matrix rows before treating the project as final-demo ready."
        );
    }

    private static long count(List<DemoEndToEndAcceptanceMatrixItemVo> items, String status) {
        return items.stream()
                .filter(item -> status.equals(item.status()))
                .count();
    }

    private static String aggregateStatus(long blockedCount, long needsAttentionCount) {
        if (blockedCount > 0) {
            return STATUS_BLOCKED;
        }
        if (needsAttentionCount > 0) {
            return STATUS_NEEDS_ATTENTION;
        }
        return STATUS_READY;
    }

    private static List<String> nextActions(List<DemoEndToEndAcceptanceMatrixItemVo> items, String status) {
        if (STATUS_READY.equals(status)) {
            return List.of("Run the live launch gate, post the tested /agent fix comment, and preserve the resulting PR evidence.");
        }
        List<String> actions = new ArrayList<>();
        items.stream()
                .filter(item -> !STATUS_READY.equals(item.status()))
                .map(DemoEndToEndAcceptanceMatrixItemVo::nextAction)
                .distinct()
                .forEach(actions::add);
        return actions;
    }

    private static String summary(String status) {
        return switch (status) {
            case STATUS_READY -> "PatchPilot has enough launch, language, safety, evaluation, and recent PR evidence for a final self-hosted demo.";
            case STATUS_BLOCKED -> "PatchPilot is blocked from final self-hosted demo acceptance.";
            default -> "PatchPilot needs more evidence before final self-hosted demo acceptance.";
        };
    }

    private static String markdownReport(
            String status,
            int readinessPercent,
            String summary,
            List<DemoEndToEndAcceptanceMatrixItemVo> items,
            List<String> nextActions
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot End-to-End Acceptance Matrix\n\n");
        report.append("- Status: ").append(status).append("\n");
        report.append("- Readiness: ").append(readinessPercent).append("%\n");
        report.append("- Summary: ").append(summary).append("\n\n");
        report.append("## Matrix\n\n");
        for (DemoEndToEndAcceptanceMatrixItemVo item : items) {
            report.append("- ").append(item.category()).append(" / ").append(item.name())
                    .append(": `").append(item.status()).append("` - ")
                    .append(item.evidence()).append(" Next: ").append(item.nextAction()).append("\n");
        }
        report.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            report.append("- ").append(action).append("\n");
        }
        return report.toString();
    }

    private static String normalizeStatus(String status) {
        if (STATUS_BLOCKED.equals(status)) {
            return STATUS_BLOCKED;
        }
        if (STATUS_READY.equals(status)) {
            return STATUS_READY;
        }
        return STATUS_NEEDS_ATTENTION;
    }

    private static String join(List<String> values) {
        return values == null || values.isEmpty() ? "none" : String.join(", ", values);
    }
}
