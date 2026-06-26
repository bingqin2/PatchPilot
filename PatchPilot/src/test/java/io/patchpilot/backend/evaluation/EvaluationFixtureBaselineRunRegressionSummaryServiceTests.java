package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationFixtureBaselineRunArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationFixtureBaselineRunRegressionSummaryServiceTests {

    @Test
    void should_return_no_archives_when_no_fixture_baseline_runs_exist() {
        EvaluationFixtureBaselineRunRegressionSummaryService service = service();

        var summary = service.getRegressionSummary();

        assertThat(summary.status()).isEqualTo("NO_ARCHIVES");
        assertThat(summary.latestRun()).isNull();
        assertThat(summary.previousRun()).isNull();
        assertThat(summary.newlyFailedCaseIds()).isEmpty();
        assertThat(summary.recoveredCaseIds()).isEmpty();
        assertThat(summary.nextAction()).isEqualTo("Run and archive at least two fixture baselines before using regression comparison.");
        assertThat(summary.sideEffectContract()).isEqualTo("Fixture baseline regression summary reads archived local baseline runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.");
        assertThat(summary.markdownReport()).contains("# PatchPilot Evaluation Fixture Baseline Regression Summary")
                .contains("- Status: `NO_ARCHIVES`");
    }

    @Test
    void should_return_single_archive_when_only_one_baseline_run_exists() {
        InMemoryEvaluationFixtureBaselineRunArchiveRepository repository = new InMemoryEvaluationFixtureBaselineRunArchiveRepository();
        repository.save(archive("baseline-1", "READY", "2026-06-26T06:00:00Z", 4, 0, 2, """
                - `java-maven-doc-fix`: `PASSED`
                - `node-npm-unit-fix`: `PASSED`
                """));
        EvaluationFixtureBaselineRunRegressionSummaryService service = new EvaluationFixtureBaselineRunRegressionSummaryService(repository);

        var summary = service.getRegressionSummary();

        assertThat(summary.status()).isEqualTo("SINGLE_ARCHIVE");
        assertThat(summary.latestRun().id()).isEqualTo("baseline-1");
        assertThat(summary.previousRun()).isNull();
        assertThat(summary.failedDelta()).isZero();
        assertThat(summary.latestFailedCaseIds()).isEmpty();
        assertThat(summary.nextAction()).isEqualTo("Archive one more fixture baseline run to compare regression movement.");
    }

    @Test
    void should_report_regressed_when_latest_baseline_has_new_failed_cases() {
        InMemoryEvaluationFixtureBaselineRunArchiveRepository repository = new InMemoryEvaluationFixtureBaselineRunArchiveRepository();
        repository.save(archive("baseline-old", "READY", "2026-06-26T06:00:00Z", 4, 0, 2, """
                - `java-maven-doc-fix`: `PASSED`
                - `node-npm-unit-fix`: `PASSED`
                """));
        repository.save(archive("baseline-new", "NEEDS_ATTENTION", "2026-06-26T07:00:00Z", 3, 1, 2, """
                - `java-maven-doc-fix`: `FAILED`
                - `node-npm-unit-fix`: `PASSED`
                """));
        EvaluationFixtureBaselineRunRegressionSummaryService service = new EvaluationFixtureBaselineRunRegressionSummaryService(repository);

        var summary = service.getRegressionSummary();

        assertThat(summary.status()).isEqualTo("REGRESSED");
        assertThat(summary.latestRun().id()).isEqualTo("baseline-new");
        assertThat(summary.previousRun().id()).isEqualTo("baseline-old");
        assertThat(summary.passedDelta()).isEqualTo(-1);
        assertThat(summary.failedDelta()).isEqualTo(1);
        assertThat(summary.skippedDelta()).isZero();
        assertThat(summary.latestFailedCaseIds()).containsExactly("java-maven-doc-fix");
        assertThat(summary.newlyFailedCaseIds()).containsExactly("java-maven-doc-fix");
        assertThat(summary.recoveredCaseIds()).isEmpty();
        assertThat(summary.nextAction()).isEqualTo("Investigate newly failed fixture cases before using the baseline as demo evidence.");
        assertThat(summary.markdownReport()).contains("- Newly failed cases: java-maven-doc-fix")
                .contains("- Recovered cases: none");
    }

    @Test
    void should_report_improved_when_latest_baseline_recovers_failed_cases() {
        InMemoryEvaluationFixtureBaselineRunArchiveRepository repository = new InMemoryEvaluationFixtureBaselineRunArchiveRepository();
        repository.save(archive("baseline-old", "NEEDS_ATTENTION", "2026-06-26T06:00:00Z", 3, 1, 2, """
                - `java-maven-doc-fix`: `FAILED`
                - `node-npm-unit-fix`: `PASSED`
                """));
        repository.save(archive("baseline-new", "READY", "2026-06-26T07:00:00Z", 4, 0, 2, """
                - `java-maven-doc-fix`: `PASSED`
                - `node-npm-unit-fix`: `PASSED`
                """));
        EvaluationFixtureBaselineRunRegressionSummaryService service = new EvaluationFixtureBaselineRunRegressionSummaryService(repository);

        var summary = service.getRegressionSummary();

        assertThat(summary.status()).isEqualTo("IMPROVED");
        assertThat(summary.passedDelta()).isEqualTo(1);
        assertThat(summary.failedDelta()).isEqualTo(-1);
        assertThat(summary.newlyFailedCaseIds()).isEmpty();
        assertThat(summary.recoveredCaseIds()).containsExactly("java-maven-doc-fix");
        assertThat(summary.nextAction()).isEqualTo("Fixture baseline recovered; keep archiving future runs to catch regressions.");
    }

    private static EvaluationFixtureBaselineRunRegressionSummaryService service() {
        return new EvaluationFixtureBaselineRunRegressionSummaryService(new InMemoryEvaluationFixtureBaselineRunArchiveRepository());
    }

    private static EvaluationFixtureBaselineRunArchiveVo archive(
            String id,
            String status,
            String createdAt,
            int passedCaseCount,
            int failedCaseCount,
            int skippedCaseCount,
            String caseReport
    ) {
        return new EvaluationFixtureBaselineRunArchiveVo(
                id,
                status,
                6,
                passedCaseCount + failedCaseCount,
                passedCaseCount,
                failedCaseCount,
                skippedCaseCount,
                Instant.parse(createdAt),
                "Archive stores a local fixture baseline execution report only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                "archive next action",
                "# PatchPilot Evaluation Fixture Baseline Run\n\n## Baseline Evidence\n\n## Cases\n\n" + caseReport
        );
    }
}
