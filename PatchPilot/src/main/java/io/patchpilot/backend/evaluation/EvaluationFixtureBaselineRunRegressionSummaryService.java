package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunDigestVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunRegressionSummaryVo;
import io.patchpilot.backend.evaluation.service.EvaluationFixtureBaselineRunArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EvaluationFixtureBaselineRunRegressionSummaryService {

    private static final Pattern CASE_STATUS_PATTERN = Pattern.compile("- `([^`]+)`: `([^`]+)`");
    private static final String FAILED = "FAILED";
    private static final String NO_ARCHIVES = "NO_ARCHIVES";
    private static final String SINGLE_ARCHIVE = "SINGLE_ARCHIVE";
    private static final String STABLE = "STABLE";
    private static final String REGRESSED = "REGRESSED";
    private static final String IMPROVED = "IMPROVED";
    private static final String SIDE_EFFECT_CONTRACT = "Fixture baseline regression summary reads archived local baseline runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.";

    private final EvaluationFixtureBaselineRunArchiveRepository archiveRepository;

    public EvaluationFixtureBaselineRunRegressionSummaryVo getRegressionSummary() {
        List<EvaluationFixtureBaselineRunArchiveVo> archives = archiveRepository.listRecentArchives(2);
        if (archives.isEmpty()) {
            return summary(
                    NO_ARCHIVES,
                    null,
                    null,
                    0,
                    0,
                    0,
                    List.of(),
                    List.of(),
                    List.of(),
                    "Run and archive at least two fixture baselines before using regression comparison."
            );
        }

        EvaluationFixtureBaselineRunArchiveVo latest = archives.get(0);
        List<String> latestFailedCaseIds = failedCaseIds(latest.report());
        if (archives.size() == 1) {
            return summary(
                    SINGLE_ARCHIVE,
                    digest(latest),
                    null,
                    0,
                    0,
                    0,
                    latestFailedCaseIds,
                    List.of(),
                    List.of(),
                    "Archive one more fixture baseline run to compare regression movement."
            );
        }

        EvaluationFixtureBaselineRunArchiveVo previous = archives.get(1);
        List<String> previousFailedCaseIds = failedCaseIds(previous.report());
        List<String> newlyFailedCaseIds = difference(latestFailedCaseIds, previousFailedCaseIds);
        List<String> recoveredCaseIds = difference(previousFailedCaseIds, latestFailedCaseIds);
        int passedDelta = latest.passedCaseCount() - previous.passedCaseCount();
        int failedDelta = latest.failedCaseCount() - previous.failedCaseCount();
        int skippedDelta = latest.skippedCaseCount() - previous.skippedCaseCount();
        String status = status(newlyFailedCaseIds, recoveredCaseIds, failedDelta);
        return summary(
                status,
                digest(latest),
                digest(previous),
                passedDelta,
                failedDelta,
                skippedDelta,
                latestFailedCaseIds,
                newlyFailedCaseIds,
                recoveredCaseIds,
                nextAction(status)
        );
    }

    private static EvaluationFixtureBaselineRunRegressionSummaryVo summary(
            String status,
            EvaluationFixtureBaselineRunDigestVo latestRun,
            EvaluationFixtureBaselineRunDigestVo previousRun,
            int passedDelta,
            int failedDelta,
            int skippedDelta,
            List<String> latestFailedCaseIds,
            List<String> newlyFailedCaseIds,
            List<String> recoveredCaseIds,
            String nextAction
    ) {
        return new EvaluationFixtureBaselineRunRegressionSummaryVo(
                status,
                latestRun,
                previousRun,
                passedDelta,
                failedDelta,
                skippedDelta,
                latestFailedCaseIds,
                newlyFailedCaseIds,
                recoveredCaseIds,
                SIDE_EFFECT_CONTRACT,
                nextAction,
                buildMarkdownReport(
                        status,
                        latestRun,
                        previousRun,
                        passedDelta,
                        failedDelta,
                        skippedDelta,
                        latestFailedCaseIds,
                        newlyFailedCaseIds,
                        recoveredCaseIds,
                        nextAction
                )
        );
    }

    private static EvaluationFixtureBaselineRunDigestVo digest(EvaluationFixtureBaselineRunArchiveVo archive) {
        return new EvaluationFixtureBaselineRunDigestVo(
                archive.id(),
                archive.status(),
                archive.totalCaseCount(),
                archive.executedCaseCount(),
                archive.passedCaseCount(),
                archive.failedCaseCount(),
                archive.skippedCaseCount(),
                archive.createdAt()
        );
    }

    private static List<String> failedCaseIds(String report) {
        if (report == null || report.isBlank()) {
            return List.of();
        }
        Matcher matcher = CASE_STATUS_PATTERN.matcher(report);
        Set<String> failedCaseIds = new LinkedHashSet<>();
        while (matcher.find()) {
            if (FAILED.equals(matcher.group(2))) {
                failedCaseIds.add(matcher.group(1));
            }
        }
        return sort(failedCaseIds);
    }

    private static List<String> difference(List<String> left, List<String> right) {
        Set<String> rightSet = Set.copyOf(right);
        return left.stream()
                .filter(item -> !rightSet.contains(item))
                .sorted()
                .toList();
    }

    private static List<String> sort(Set<String> values) {
        return values.stream()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private static String status(List<String> newlyFailedCaseIds, List<String> recoveredCaseIds, int failedDelta) {
        if (!newlyFailedCaseIds.isEmpty() || failedDelta > 0) {
            return REGRESSED;
        }
        if (!recoveredCaseIds.isEmpty() || failedDelta < 0) {
            return IMPROVED;
        }
        return STABLE;
    }

    private static String nextAction(String status) {
        return switch (status) {
            case REGRESSED -> "Investigate newly failed fixture cases before using the baseline as demo evidence.";
            case IMPROVED -> "Fixture baseline recovered; keep archiving future runs to catch regressions.";
            case STABLE -> "Fixture baseline is stable; keep the latest archive as current demo evidence.";
            default -> "Archive more fixture baseline runs to compare regression movement.";
        };
    }

    private static String buildMarkdownReport(
            String status,
            EvaluationFixtureBaselineRunDigestVo latestRun,
            EvaluationFixtureBaselineRunDigestVo previousRun,
            int passedDelta,
            int failedDelta,
            int skippedDelta,
            List<String> latestFailedCaseIds,
            List<String> newlyFailedCaseIds,
            List<String> recoveredCaseIds,
            String nextAction
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Fixture Baseline Regression Summary");
        lines.add("");
        lines.add("- Status: `" + status + "`");
        lines.add("- Latest run: " + runLabel(latestRun));
        lines.add("- Previous run: " + runLabel(previousRun));
        lines.add("- Passed delta: " + signed(passedDelta));
        lines.add("- Failed delta: " + signed(failedDelta));
        lines.add("- Skipped delta: " + signed(skippedDelta));
        lines.add("- Latest failed cases: " + csv(latestFailedCaseIds));
        lines.add("- Newly failed cases: " + csv(newlyFailedCaseIds));
        lines.add("- Recovered cases: " + csv(recoveredCaseIds));
        lines.add("- Side-effect contract: " + SIDE_EFFECT_CONTRACT);
        lines.add("- Next action: " + nextAction);
        return String.join("\n", lines);
    }

    private static String runLabel(EvaluationFixtureBaselineRunDigestVo run) {
        if (run == null) {
            return "none";
        }
        return "`" + run.id() + "` at `" + run.createdAt() + "`";
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private static String csv(List<String> values) {
        return values.isEmpty() ? "none" : String.join(", ", values);
    }
}
