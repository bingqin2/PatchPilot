package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineSummaryVo;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.impl.GoLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationFixtureBaselineServiceTests {

    @Test
    void should_execute_supported_fixture_verification_commands_and_skip_safety_rejections() {
        RecordingBaselineCommandRunner runner = new RecordingBaselineCommandRunner();
        runner.enqueue(new TestRunResult("mvn test", 0, "maven ok"));
        runner.enqueue(new TestRunResult("npm test", 0, "npm ok"));
        runner.enqueue(new TestRunResult("python3 -m pytest", 0, "pytest ok"));
        runner.enqueue(new TestRunResult("go test ./...", 0, "go ok"));
        EvaluationFixtureBaselineService service = service(runner);

        EvaluationFixtureBaselineSummaryVo summary = service.runBaseline();

        assertThat(summary.status()).isEqualTo("READY");
        assertThat(summary.totalCaseCount()).isEqualTo(6);
        assertThat(summary.executedCaseCount()).isEqualTo(4);
        assertThat(summary.passedCaseCount()).isEqualTo(4);
        assertThat(summary.failedCaseCount()).isZero();
        assertThat(summary.skippedCaseCount()).isEqualTo(2);
        assertThat(summary.sideEffectContract()).isEqualTo("Evaluation fixture baseline runs local checked-in fixture verification commands only; it does not create tasks, call the model, mutate Git, or write to GitHub.");
        assertThat(summary.nextAction()).isEqualTo("Fixture baseline is passing; use the report as demo evidence for supported language adapters.");
        assertThat(summary.cases()).hasSize(6);
        assertThat(summary.cases().get(0).caseId()).isEqualTo("java-maven-doc-fix");
        assertThat(summary.cases().get(0).status()).isEqualTo("PASSED");
        assertThat(summary.cases().get(0).executed()).isTrue();
        assertThat(summary.cases().get(0).fixturePath()).isEqualTo("docs/demo-repositories/java-maven");
        assertThat(summary.cases().get(0).verificationCommand()).containsExactly("mvn", "test");
        assertThat(summary.cases().get(0).exitCode()).isZero();
        assertThat(summary.cases().get(0).outputSnippet()).isEqualTo("maven ok");
        assertThat(summary.cases().get(4).caseId()).isEqualTo("unsafe-secret-exfiltration-rejection");
        assertThat(summary.cases().get(4).status()).isEqualTo("SKIPPED");
        assertThat(summary.cases().get(4).executed()).isFalse();
        assertThat(summary.cases().get(4).verificationCommand()).isEmpty();
        assertThat(summary.cases().get(4).reason()).isEqualTo("Safety rejection cases validate trigger gating and do not run repository verification.");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Evaluation Fixture Baseline")
                .contains("- Status: `READY`")
                .contains("- Executed cases: 4")
                .contains("- Passed cases: 4")
                .contains("- Skipped cases: 2")
                .contains("- `java-maven-doc-fix`: `PASSED`")
                .contains("  - Command: `mvn test`")
                .contains("- `unsafe-secret-exfiltration-rejection`: `SKIPPED`");
        assertThat(runner.invocations)
                .extracting(Invocation::caseId)
                .containsExactly(
                        "java-maven-doc-fix",
                        "node-npm-unit-fix",
                        "python-pytest-bug-fix",
                        "go-module-bug-fix"
                );
    }

    @Test
    void should_mark_summary_as_needing_attention_when_a_fixture_command_fails() {
        RecordingBaselineCommandRunner runner = new RecordingBaselineCommandRunner();
        runner.enqueue(new TestRunResult("mvn test", 0, "maven ok"));
        runner.enqueue(new TestRunResult("npm test", 1, "npm failed"));
        runner.enqueue(new TestRunResult("python3 -m pytest", 0, "pytest ok"));
        runner.enqueue(new TestRunResult("go test ./...", 0, "go ok"));
        EvaluationFixtureBaselineService service = service(runner);

        EvaluationFixtureBaselineSummaryVo summary = service.runBaseline();

        assertThat(summary.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(summary.executedCaseCount()).isEqualTo(4);
        assertThat(summary.passedCaseCount()).isEqualTo(3);
        assertThat(summary.failedCaseCount()).isEqualTo(1);
        assertThat(summary.skippedCaseCount()).isEqualTo(2);
        assertThat(summary.nextAction()).isEqualTo("Fix failing fixture commands before using the baseline as demo evidence.");
        assertThat(summary.cases().get(1).caseId()).isEqualTo("node-npm-unit-fix");
        assertThat(summary.cases().get(1).status()).isEqualTo("FAILED");
        assertThat(summary.cases().get(1).exitCode()).isEqualTo(1);
        assertThat(summary.cases().get(1).outputSnippet()).isEqualTo("npm failed");
        assertThat(summary.markdownReport())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Failed cases: 1")
                .contains("- `node-npm-unit-fix`: `FAILED`");
    }

    private static EvaluationFixtureBaselineService service(EvaluationFixtureBaselineCommandRunner runner) {
        EvaluationCaseCatalogService catalogService = new EvaluationCaseCatalogService();
        LanguageAdapterRegistry registry = new LanguageAdapterRegistry(List.of(
                new JavaMavenLanguageAdapter(),
                new NodeNpmLanguageAdapter(),
                new PythonPytestLanguageAdapter(),
                new GoLanguageAdapter()
        ));
        return new EvaluationFixtureBaselineService(
                catalogService,
                new EvaluationCaseFixtureReadinessService(catalogService, registry),
                registry,
                runner
        );
    }

    private static final class RecordingBaselineCommandRunner implements EvaluationFixtureBaselineCommandRunner {

        private final List<TestRunResult> results = new ArrayList<>();
        private final List<Invocation> invocations = new ArrayList<>();

        void enqueue(TestRunResult result) {
            results.add(result);
        }

        @Override
        public TestRunResult run(String caseId, Path repositoryRoot, List<String> command) {
            invocations.add(new Invocation(caseId, repositoryRoot, command));
            return results.remove(0);
        }
    }

    private record Invocation(String caseId, Path repositoryRoot, List<String> command) {
    }
}
