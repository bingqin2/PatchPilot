package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineCaseVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineSummaryVo;
import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationRunArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationRunArchiveServiceTests {

    @Test
    void should_run_archive_and_list_local_evaluation_runs() {
        InMemoryEvaluationRunArchiveRepository repository = new InMemoryEvaluationRunArchiveRepository();
        EvaluationRunArchiveService service = service(repository, "evaluation-run-1", readyBaseline(), "2026-06-28T04:00:00Z");

        var archive = service.runAndArchiveEvaluation();

        assertThat(archive.id()).isEqualTo("evaluation-run-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.totalCaseCount()).isEqualTo(6);
        assertThat(archive.supportedFixCaseCount()).isEqualTo(4);
        assertThat(archive.safetyRejectionCaseCount()).isEqualTo(2);
        assertThat(archive.executedFixCaseCount()).isEqualTo(4);
        assertThat(archive.passedFixCaseCount()).isEqualTo(4);
        assertThat(archive.failedFixCaseCount()).isZero();
        assertThat(archive.skippedCaseCount()).isEqualTo(2);
        assertThat(archive.coveredLanguages()).containsExactly("go", "java", "node", "python");
        assertThat(archive.coveredBuildSystems()).containsExactly("go", "maven", "npm", "pytest");
        assertThat(archive.safetyRejectionCategories()).containsExactly("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-28T04:00:00Z"));
        assertThat(archive.sideEffectContract()).isEqualTo("Evaluation run executes local checked-in fixture verification commands and records safety coverage only; it does not create tasks, call the model, clone repositories, mutate Git, or write to GitHub.");
        assertThat(archive.nextAction()).isEqualTo("Evaluation run passed; use the archived report as measurable demo evidence for supported adapters and safety rejections.");
        assertThat(archive.report())
                .contains("# PatchPilot Evaluation Run")
                .contains("- Evaluation run id: `evaluation-run-1`")
                .contains("- Status: `READY`")
                .contains("- Executed fix cases: 4")
                .contains("- Safety rejection cases: 2")
                .contains("## Fixture Baseline Evidence")
                .contains("# PatchPilot Evaluation Fixture Baseline")
                .contains("## Preview Evidence")
                .contains("# PatchPilot Evaluation Run Preview");

        assertThat(service.listRecentArchives())
                .extracting(item -> item.id())
                .containsExactly("evaluation-run-1");
        assertThat(service.findArchive("evaluation-run-1")).contains(archive);
    }

    @Test
    void should_mark_run_as_needs_attention_when_fixture_baseline_fails() {
        EvaluationRunArchiveService service = service(
                new InMemoryEvaluationRunArchiveRepository(),
                "evaluation-run-failed",
                failedBaseline(),
                "2026-06-28T04:10:00Z"
        );

        var archive = service.runAndArchiveEvaluation();

        assertThat(archive.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(archive.failedFixCaseCount()).isEqualTo(1);
        assertThat(archive.nextAction()).isEqualTo("Fix failing fixture baseline cases or missing safety coverage, then rerun the evaluation.");
        assertThat(archive.report())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Failed fix cases: 1");
    }

    @Test
    void should_trim_archives_to_twenty_most_recent_runs() {
        InMemoryEvaluationRunArchiveRepository repository = new InMemoryEvaluationRunArchiveRepository();
        for (int index = 0; index < 25; index++) {
            service(
                    repository,
                    "evaluation-run-" + index,
                    readyBaseline(),
                    "2026-06-28T04:" + String.format("%02d", index) + ":00Z"
            ).runAndArchiveEvaluation();
        }

        List<String> ids = repository.listRecentArchives(30).stream()
                .map(archive -> archive.id())
                .toList();

        assertThat(ids).hasSize(20);
        assertThat(ids.get(0)).isEqualTo("evaluation-run-24");
        assertThat(ids).doesNotContain("evaluation-run-0", "evaluation-run-4");
    }

    private static EvaluationRunArchiveService service(
            InMemoryEvaluationRunArchiveRepository repository,
            String id,
            EvaluationFixtureBaselineSummaryVo baseline,
            String timestamp
    ) {
        return new EvaluationRunArchiveService(
                new EvaluationCaseCatalogService(),
                () -> baseline,
                repository,
                Clock.fixed(Instant.parse(timestamp), ZoneOffset.UTC),
                () -> id
        );
    }

    private static EvaluationFixtureBaselineSummaryVo readyBaseline() {
        return baseline("READY", 4, 4, 0, 2, "Fixture baseline is passing; use the report as demo evidence for supported language adapters.");
    }

    private static EvaluationFixtureBaselineSummaryVo failedBaseline() {
        return baseline("NEEDS_ATTENTION", 4, 3, 1, 2, "Fix failing fixture commands before using the baseline as demo evidence.");
    }

    private static EvaluationFixtureBaselineSummaryVo baseline(
            String status,
            int executedCaseCount,
            int passedCaseCount,
            int failedCaseCount,
            int skippedCaseCount,
            String nextAction
    ) {
        return new EvaluationFixtureBaselineSummaryVo(
                status,
                6,
                executedCaseCount,
                passedCaseCount,
                failedCaseCount,
                skippedCaseCount,
                List.of(caseRow(failedCaseCount == 0 ? "PASSED" : "FAILED")),
                "Evaluation fixture baseline runs local checked-in fixture verification commands only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                nextAction,
                "# PatchPilot Evaluation Fixture Baseline\n\n- Status: `" + status + "`"
        );
    }

    private static EvaluationFixtureBaselineCaseVo caseRow(String status) {
        return new EvaluationFixtureBaselineCaseVo(
                "java-maven-doc-fix",
                "Java Maven documentation fix",
                "SUPPORTED_FIX",
                status,
                true,
                "docs/demo-repositories/java-maven",
                "java",
                "maven",
                List.of("mvn", "test"),
                "PASSED".equals(status) ? 0 : 1,
                "fixture output",
                "Fixture verification command exited with code " + ("PASSED".equals(status) ? 0 : 1) + ".",
                "Keep this fixture as passing demo evidence."
        );
    }
}
