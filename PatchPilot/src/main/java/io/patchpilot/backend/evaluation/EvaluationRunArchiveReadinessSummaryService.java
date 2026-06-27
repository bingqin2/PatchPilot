package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveDigestVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveReadinessSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.service.EvaluationRunArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationRunArchiveReadinessSummaryService {

    private static final String NO_ARCHIVES = "NO_ARCHIVES";
    private static final String READY = "READY";
    private static final String BLOCKED = "BLOCKED";
    private static final String SIDE_EFFECT_CONTRACT = "Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.";

    private final EvaluationRunArchiveRepository archiveRepository;

    public EvaluationRunArchiveReadinessSummaryVo getSummary() {
        List<EvaluationRunArchiveVo> archives = archiveRepository.listRecentArchives(2);
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
                    "Run and archive a full evaluation before using it as demo readiness evidence."
            );
        }

        EvaluationRunArchiveVo latest = archives.get(0);
        EvaluationRunArchiveVo previous = archives.size() > 1 ? archives.get(1) : null;
        String status = latestReady(latest) ? READY : BLOCKED;
        return summary(
                status,
                digest(latest),
                digest(previous),
                previous == null ? 0 : latest.passedFixCaseCount() - previous.passedFixCaseCount(),
                previous == null ? 0 : latest.failedFixCaseCount() - previous.failedFixCaseCount(),
                previous == null ? 0 : latest.skippedCaseCount() - previous.skippedCaseCount(),
                latest.coveredLanguages(),
                latest.coveredBuildSystems(),
                latest.safetyRejectionCategories(),
                nextAction(status)
        );
    }

    private static boolean latestReady(EvaluationRunArchiveVo latest) {
        return READY.equals(latest.status())
                && latest.failedFixCaseCount() == 0
                && latest.safetyRejectionCaseCount() > 0
                && !latest.safetyRejectionCategories().isEmpty();
    }

    private static String nextAction(String status) {
        return switch (status) {
            case READY -> "Full evaluation run archive is ready; use it as current demo evidence.";
            case BLOCKED -> "Fix the latest full evaluation run failures, then rerun and archive evaluation evidence.";
            default -> "Run and archive a full evaluation before using it as demo readiness evidence.";
        };
    }

    private static EvaluationRunArchiveReadinessSummaryVo summary(
            String status,
            EvaluationRunArchiveDigestVo latestRun,
            EvaluationRunArchiveDigestVo previousRun,
            int passedDelta,
            int failedDelta,
            int skippedDelta,
            List<String> coveredLanguages,
            List<String> coveredBuildSystems,
            List<String> safetyRejectionCategories,
            String nextAction
    ) {
        return new EvaluationRunArchiveReadinessSummaryVo(
                status,
                latestRun,
                previousRun,
                passedDelta,
                failedDelta,
                skippedDelta,
                coveredLanguages,
                coveredBuildSystems,
                safetyRejectionCategories,
                SIDE_EFFECT_CONTRACT,
                nextAction,
                buildMarkdownReport(
                        status,
                        latestRun,
                        previousRun,
                        passedDelta,
                        failedDelta,
                        skippedDelta,
                        coveredLanguages,
                        coveredBuildSystems,
                        safetyRejectionCategories,
                        nextAction
                )
        );
    }

    private static EvaluationRunArchiveDigestVo digest(EvaluationRunArchiveVo archive) {
        if (archive == null) {
            return null;
        }
        return new EvaluationRunArchiveDigestVo(
                archive.id(),
                archive.status(),
                archive.totalCaseCount(),
                archive.supportedFixCaseCount(),
                archive.safetyRejectionCaseCount(),
                archive.executedFixCaseCount(),
                archive.passedFixCaseCount(),
                archive.failedFixCaseCount(),
                archive.skippedCaseCount(),
                archive.createdAt()
        );
    }

    private static String buildMarkdownReport(
            String status,
            EvaluationRunArchiveDigestVo latestRun,
            EvaluationRunArchiveDigestVo previousRun,
            int passedDelta,
            int failedDelta,
            int skippedDelta,
            List<String> coveredLanguages,
            List<String> coveredBuildSystems,
            List<String> safetyRejectionCategories,
            String nextAction
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Run Readiness Summary");
        lines.add("");
        lines.add("- Status: `" + status + "`");
        lines.add("- Latest run: " + runLabel(latestRun));
        lines.add("- Previous run: " + runLabel(previousRun));
        lines.add("- Passed delta: " + signed(passedDelta));
        lines.add("- Failed delta: " + signed(failedDelta));
        lines.add("- Skipped delta: " + signed(skippedDelta));
        lines.add("- Latest failed fix cases: " + (latestRun == null ? 0 : latestRun.failedFixCaseCount()));
        lines.add("- Languages: " + csv(coveredLanguages));
        lines.add("- Build systems: " + csv(coveredBuildSystems));
        lines.add("- Safety rejection categories: " + csv(safetyRejectionCategories));
        lines.add("- Side-effect contract: " + SIDE_EFFECT_CONTRACT);
        lines.add("- Next action: " + nextAction);
        return String.join("\n", lines);
    }

    private static String runLabel(EvaluationRunArchiveDigestVo run) {
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
