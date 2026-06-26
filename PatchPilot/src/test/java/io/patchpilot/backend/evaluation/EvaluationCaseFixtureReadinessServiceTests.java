package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationCaseFixtureReadinessVo;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.impl.GoLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationCaseFixtureReadinessServiceTests {

    @Test
    void should_verify_supported_case_fixtures_against_adapter_detection_and_expected_files() {
        EvaluationCaseFixtureReadinessService service = service();

        List<EvaluationCaseFixtureReadinessVo> readiness = service.listCaseReadiness();

        assertThat(readiness).hasSize(6);
        assertThat(readiness)
                .extracting(EvaluationCaseFixtureReadinessVo::caseId)
                .containsExactly(
                        "java-maven-doc-fix",
                        "node-npm-unit-fix",
                        "python-pytest-bug-fix",
                        "go-module-bug-fix",
                        "unsafe-secret-exfiltration-rejection",
                        "vague-trigger-rejection"
                );
        EvaluationCaseFixtureReadinessVo javaReadiness = readiness.get(0);
        assertThat(javaReadiness.status()).isEqualTo("PASS");
        assertThat(javaReadiness.fixtureRequired()).isTrue();
        assertThat(javaReadiness.fixtureExists()).isTrue();
        assertThat(javaReadiness.adapterMatches()).isTrue();
        assertThat(javaReadiness.expectedFilesExist()).isTrue();
        assertThat(javaReadiness.expectedLanguage()).isEqualTo("java");
        assertThat(javaReadiness.actualLanguage()).isEqualTo("java");
        assertThat(javaReadiness.expectedBuildSystem()).isEqualTo("maven");
        assertThat(javaReadiness.actualBuildSystem()).isEqualTo("maven");
        assertThat(javaReadiness.expectedVerificationCommand()).containsExactly("mvn", "test");
        assertThat(javaReadiness.actualVerificationCommand()).containsExactly("mvn", "test");
        assertThat(javaReadiness.expectedChangedFiles()).containsExactly("src/main/java/demo/Calculator.java");
        assertThat(javaReadiness.missingExpectedFiles()).isEmpty();
        assertThat(javaReadiness.reason()).contains("Detected Maven project");
        assertThat(javaReadiness.nextAction()).isEqualTo("Fixture readiness is verified for this supported evaluation case.");
    }

    @Test
    void should_mark_safety_rejection_cases_as_not_requiring_fixtures() {
        EvaluationCaseFixtureReadinessService service = service();

        List<EvaluationCaseFixtureReadinessVo> readiness = service.listCaseReadiness();

        EvaluationCaseFixtureReadinessVo unsafeRejection = readiness.get(4);
        assertThat(unsafeRejection.caseId()).isEqualTo("unsafe-secret-exfiltration-rejection");
        assertThat(unsafeRejection.status()).isEqualTo("NO_FIXTURE_REQUIRED");
        assertThat(unsafeRejection.fixtureRequired()).isFalse();
        assertThat(unsafeRejection.fixtureExists()).isFalse();
        assertThat(unsafeRejection.adapterMatches()).isFalse();
        assertThat(unsafeRejection.expectedFilesExist()).isFalse();
        assertThat(unsafeRejection.actualLanguage()).isEqualTo("none");
        assertThat(unsafeRejection.actualBuildSystem()).isEqualTo("none");
        assertThat(unsafeRejection.actualVerificationCommand()).isEmpty();
        assertThat(unsafeRejection.reason()).isEqualTo("Safety rejection cases validate trigger gating and do not require repository fixtures.");
        assertThat(unsafeRejection.nextAction()).isEqualTo("Keep this case in the safety rejection catalog; no fixture verification is required.");
    }

    @Test
    void should_summarize_case_readiness_as_copyable_markdown() {
        EvaluationCaseFixtureReadinessService service = service();

        var summary = service.getReadinessSummary();

        assertThat(summary.status()).isEqualTo("READY");
        assertThat(summary.totalCaseCount()).isEqualTo(6);
        assertThat(summary.passingCaseCount()).isEqualTo(4);
        assertThat(summary.noFixtureRequiredCaseCount()).isEqualTo(2);
        assertThat(summary.failingCaseCount()).isZero();
        assertThat(summary.sideEffectContract()).isEqualTo("Evaluation case fixture readiness checks local checked-in fixtures and adapter metadata only; it does not create tasks, call the model, run verification commands, mutate Git, or write to GitHub.");
        assertThat(summary.nextAction()).isEqualTo("Evaluation case fixtures are ready for demo evidence; automated evaluation execution remains future work.");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Evaluation Case Fixture Readiness")
                .contains("- Status: `READY`")
                .contains("- Passing cases: 4")
                .contains("- No-fixture-required cases: 2")
                .contains("- Failing cases: 0")
                .contains("- `java-maven-doc-fix`: `PASS`")
                .contains("  - Expected files: `src/main/java/demo/Calculator.java`")
                .contains("- `unsafe-secret-exfiltration-rejection`: `NO_FIXTURE_REQUIRED`");
    }

    private static EvaluationCaseFixtureReadinessService service() {
        return new EvaluationCaseFixtureReadinessService(
                new EvaluationCaseCatalogService(),
                new LanguageAdapterRegistry(List.of(
                        new JavaMavenLanguageAdapter(),
                        new NodeNpmLanguageAdapter(),
                        new PythonPytestLanguageAdapter(),
                        new GoLanguageAdapter()
                ))
        );
    }
}
