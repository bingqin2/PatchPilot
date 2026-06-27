package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveReadinessSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationRunArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationRunArchiveReadinessSummaryServiceTests {

    @Test
    void should_need_attention_when_no_full_evaluation_runs_are_archived() {
        EvaluationRunArchiveReadinessSummaryService service = service(new InMemoryEvaluationRunArchiveRepository());

        EvaluationRunArchiveReadinessSummaryVo summary = service.getSummary();

        assertThat(summary.status()).isEqualTo("NO_ARCHIVES");
        assertThat(summary.latestRun()).isNull();
        assertThat(summary.previousRun()).isNull();
        assertThat(summary.nextAction()).isEqualTo("Run and archive a full evaluation before using it as demo readiness evidence.");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Evaluation Run Readiness Summary")
                .contains("- Status: `NO_ARCHIVES`");
    }

    @Test
    void should_report_ready_latest_full_evaluation_run_and_compare_previous_run() {
        InMemoryEvaluationRunArchiveRepository repository = new InMemoryEvaluationRunArchiveRepository();
        repository.save(archive("previous-run", "READY", 6, 4, 4, 0, 2, "2026-06-28T01:00:00Z"));
        repository.save(archive("latest-run", "READY", 6, 4, 4, 0, 2, "2026-06-28T02:00:00Z"));
        EvaluationRunArchiveReadinessSummaryService service = service(repository);

        EvaluationRunArchiveReadinessSummaryVo summary = service.getSummary();

        assertThat(summary.status()).isEqualTo("READY");
        assertThat(summary.latestRun().id()).isEqualTo("latest-run");
        assertThat(summary.previousRun().id()).isEqualTo("previous-run");
        assertThat(summary.passedDelta()).isZero();
        assertThat(summary.failedDelta()).isZero();
        assertThat(summary.skippedDelta()).isZero();
        assertThat(summary.coveredLanguages()).containsExactly("go", "java", "node", "python");
        assertThat(summary.coveredBuildSystems()).containsExactly("go", "maven", "npm", "pytest");
        assertThat(summary.safetyRejectionCategories()).containsExactly("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
        assertThat(summary.nextAction()).isEqualTo("Full evaluation run archive is ready; use it as current demo evidence.");
        assertThat(summary.markdownReport())
                .contains("- Latest run: `latest-run`")
                .contains("- Previous run: `previous-run`")
                .contains("- Failed delta: 0")
                .contains("- Safety rejection categories: DANGEROUS_INSTRUCTION, NOT_ACTIONABLE");
    }

    @Test
    void should_block_when_latest_full_evaluation_run_needs_attention() {
        InMemoryEvaluationRunArchiveRepository repository = new InMemoryEvaluationRunArchiveRepository();
        repository.save(archive("failed-run", "NEEDS_ATTENTION", 6, 4, 3, 1, 2, "2026-06-28T02:00:00Z"));
        EvaluationRunArchiveReadinessSummaryService service = service(repository);

        EvaluationRunArchiveReadinessSummaryVo summary = service.getSummary();

        assertThat(summary.status()).isEqualTo("BLOCKED");
        assertThat(summary.latestRun().id()).isEqualTo("failed-run");
        assertThat(summary.latestRun().failedFixCaseCount()).isEqualTo(1);
        assertThat(summary.nextAction()).isEqualTo("Fix the latest full evaluation run failures, then rerun and archive evaluation evidence.");
        assertThat(summary.markdownReport())
                .contains("- Status: `BLOCKED`")
                .contains("- Latest failed fix cases: 1");
    }

    private static EvaluationRunArchiveReadinessSummaryService service(InMemoryEvaluationRunArchiveRepository repository) {
        return new EvaluationRunArchiveReadinessSummaryService(repository);
    }

    private static EvaluationRunArchiveVo archive(
            String id,
            String status,
            int totalCaseCount,
            int executedFixCaseCount,
            int passedFixCaseCount,
            int failedFixCaseCount,
            int skippedCaseCount,
            String createdAt
    ) {
        return new EvaluationRunArchiveVo(
                id,
                status,
                totalCaseCount,
                4,
                2,
                executedFixCaseCount,
                passedFixCaseCount,
                failedFixCaseCount,
                skippedCaseCount,
                List.of("go", "java", "node", "python"),
                List.of("go", "maven", "npm", "pytest"),
                List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE"),
                Instant.parse(createdAt),
                "Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                "next action",
                "# PatchPilot Evaluation Run"
        );
    }
}
