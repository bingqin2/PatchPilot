package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationCaseVo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationCaseCatalogServiceTests {

    @Test
    void should_list_multilanguage_and_safety_evaluation_cases() {
        EvaluationCaseCatalogService service = new EvaluationCaseCatalogService();

        List<EvaluationCaseVo> cases = service.listEvaluationCases();

        assertThat(cases).hasSizeGreaterThanOrEqualTo(6);
        assertThat(cases)
                .extracting(EvaluationCaseVo::id)
                .containsExactly(
                        "java-maven-doc-fix",
                        "node-npm-unit-fix",
                        "python-pytest-bug-fix",
                        "go-module-bug-fix",
                        "unsafe-secret-exfiltration-rejection",
                        "vague-trigger-rejection"
                );
        assertSupportedCase(
                cases.get(0),
                "java",
                "maven",
                List.of("mvn", "test"),
                "docs/demo-repositories/java-maven",
                List.of("src/main/java/io/patchpilot/demo/GreetingService.java")
        );
        assertSupportedCase(
                cases.get(1),
                "node",
                "npm",
                List.of("npm", "test"),
                "docs/demo-repositories/node-npm",
                List.of("src/sum.js")
        );
        assertSupportedCase(
                cases.get(2),
                "python",
                "pytest",
                List.of("python3", "-m", "pytest"),
                "docs/demo-repositories/python-pytest",
                List.of("src/calculator.py")
        );
        assertSupportedCase(
                cases.get(3),
                "go",
                "go",
                List.of("go", "test", "./..."),
                "docs/demo-repositories/go-module",
                List.of("greeting.go")
        );
        assertRejectedCase(cases.get(4), "DANGEROUS_INSTRUCTION");
        assertRejectedCase(cases.get(5), "NOT_ACTIONABLE");
        assertThat(cases).allSatisfy(evaluationCase -> {
            assertThat(evaluationCase.id()).isNotBlank();
            assertThat(evaluationCase.title()).isNotBlank();
            assertThat(evaluationCase.issueText()).startsWith("/agent fix");
            assertThat(evaluationCase.successCriteria()).isNotEmpty();
            assertThat(evaluationCase.safetyExpectation()).isNotBlank();
        });
    }

    private static void assertSupportedCase(
            EvaluationCaseVo evaluationCase,
            String language,
            String buildSystem,
            List<String> command,
            String fixturePath,
            List<String> expectedChangedFiles
    ) {
        assertThat(evaluationCase.category()).isEqualTo("SUPPORTED_FIX");
        assertThat(evaluationCase.expectedDecision()).isEqualTo("ACCEPT_AND_CREATE_PR");
        assertThat(evaluationCase.language()).isEqualTo(language);
        assertThat(evaluationCase.buildSystem()).isEqualTo(buildSystem);
        assertThat(evaluationCase.expectedVerificationCommand()).containsExactlyElementsOf(command);
        assertThat(evaluationCase.repositoryFixturePath()).isEqualTo(fixturePath);
        assertThat(evaluationCase.expectedChangedFiles()).containsExactlyElementsOf(expectedChangedFiles);
        assertThat(evaluationCase.expectedRejectionCategory()).isNull();
    }

    private static void assertRejectedCase(EvaluationCaseVo evaluationCase, String category) {
        assertThat(evaluationCase.category()).isEqualTo("SAFETY_REJECTION");
        assertThat(evaluationCase.expectedDecision()).isEqualTo("REJECT_BEFORE_TASK");
        assertThat(evaluationCase.expectedRejectionCategory()).isEqualTo(category);
        assertThat(evaluationCase.repositoryFixturePath()).isNull();
        assertThat(evaluationCase.expectedVerificationCommand()).isEmpty();
        assertThat(evaluationCase.expectedChangedFiles()).isEmpty();
    }
}
