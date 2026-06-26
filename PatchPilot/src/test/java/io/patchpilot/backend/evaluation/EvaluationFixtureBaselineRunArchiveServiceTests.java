package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationFixtureBaselineRunArchiveRepository;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.impl.GoLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationFixtureBaselineRunArchiveServiceTests {

    @Test
    void should_run_fixture_baseline_and_archive_local_evidence() {
        EvaluationFixtureBaselineRunArchiveService service = service("baseline-run-1", "2026-06-26T06:00:00Z");

        var archive = service.runAndArchiveBaseline();

        assertThat(archive.id()).isEqualTo("baseline-run-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.totalCaseCount()).isEqualTo(6);
        assertThat(archive.executedCaseCount()).isEqualTo(4);
        assertThat(archive.passedCaseCount()).isEqualTo(4);
        assertThat(archive.failedCaseCount()).isZero();
        assertThat(archive.skippedCaseCount()).isEqualTo(2);
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-26T06:00:00Z"));
        assertThat(archive.nextAction()).isEqualTo("Fixture baseline is passing; use the archived report as demo evidence for supported language adapters.");
        assertThat(archive.sideEffectContract()).isEqualTo("Archive stores a local fixture baseline execution report only; it does not create tasks, call the model, mutate Git, or write to GitHub.");
        assertThat(archive.report())
                .contains("# PatchPilot Evaluation Fixture Baseline Run")
                .contains("- Baseline run id: `baseline-run-1`")
                .contains("- Archived at: `2026-06-26T06:00:00Z`")
                .contains("- Status: `READY`")
                .contains("## Baseline Evidence")
                .contains("# PatchPilot Evaluation Fixture Baseline")
                .contains("- `java-maven-doc-fix`: `PASSED`");
    }

    @Test
    void should_list_recent_archived_fixture_baseline_runs_newest_first() {
        InMemoryEvaluationFixtureBaselineRunArchiveRepository repository = new InMemoryEvaluationFixtureBaselineRunArchiveRepository();
        service(repository, "baseline-older", "2026-06-26T06:00:00Z").runAndArchiveBaseline();
        service(repository, "baseline-newer", "2026-06-26T07:00:00Z").runAndArchiveBaseline();

        var archives = service(repository, "unused", "2026-06-26T08:00:00Z").listRecentArchives();

        assertThat(archives)
                .extracting(archive -> archive.id())
                .containsExactly("baseline-newer", "baseline-older");
        assertThat(service(repository, "unused", "2026-06-26T08:00:00Z").findArchive("baseline-older"))
                .map(archive -> archive.status())
                .contains("READY");
    }

    private static EvaluationFixtureBaselineRunArchiveService service(String runId, String timestamp) {
        return service(new InMemoryEvaluationFixtureBaselineRunArchiveRepository(), runId, timestamp);
    }

    private static EvaluationFixtureBaselineRunArchiveService service(
            InMemoryEvaluationFixtureBaselineRunArchiveRepository repository,
            String runId,
            String timestamp
    ) {
        EvaluationCaseCatalogService catalogService = new EvaluationCaseCatalogService();
        LanguageAdapterRegistry registry = new LanguageAdapterRegistry(List.of(
                new JavaMavenLanguageAdapter(),
                new NodeNpmLanguageAdapter(),
                new PythonPytestLanguageAdapter(),
                new GoLanguageAdapter()
        ));
        EvaluationFixtureBaselineService baselineService = new EvaluationFixtureBaselineService(
                catalogService,
                new EvaluationCaseFixtureReadinessService(catalogService, registry),
                registry,
                (caseId, repositoryRoot, command) -> new TestRunResult(String.join(" ", command), 0, caseId + " ok")
        );
        return new EvaluationFixtureBaselineRunArchiveService(
                baselineService,
                repository,
                Clock.fixed(Instant.parse(timestamp), ZoneOffset.UTC),
                () -> runId
        );
    }
}
