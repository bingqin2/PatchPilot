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

    @Test
    void should_summarize_evaluation_case_readiness() {
        EvaluationCaseCatalogService service = new EvaluationCaseCatalogService();

        var summary = service.getEvaluationSummary();

        assertThat(summary.status()).isEqualTo("READY");
        assertThat(summary.totalCaseCount()).isEqualTo(6);
        assertThat(summary.supportedFixCaseCount()).isEqualTo(4);
        assertThat(summary.safetyRejectionCaseCount()).isEqualTo(2);
        assertThat(summary.coveredLanguages()).containsExactly("go", "java", "node", "python");
        assertThat(summary.coveredBuildSystems()).containsExactly("go", "maven", "npm", "pytest");
        assertThat(summary.rejectionCategories()).containsExactly("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
        assertThat(summary.nextAction()).isEqualTo("Evaluation catalog is ready for demo evidence; automated evaluation runs are still future work.");
        assertThat(summary.readOnly()).isTrue();
        assertThat(summary.healthContract()).isEqualTo("Summary is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.");
    }

    @Test
    void should_build_evaluation_run_preview_report_without_side_effects() {
        EvaluationCaseCatalogService service = new EvaluationCaseCatalogService();

        var preview = service.getEvaluationRunPreview();

        assertThat(preview.status()).isEqualTo("READY");
        assertThat(preview.title()).isEqualTo("Evaluation run preview");
        assertThat(preview.caseCount()).isEqualTo(6);
        assertThat(preview.supportedFixCaseCount()).isEqualTo(4);
        assertThat(preview.safetyRejectionCaseCount()).isEqualTo(2);
        assertThat(preview.previewRunId()).isEqualTo("preview-current-catalog");
        assertThat(preview.coveredLanguages()).containsExactly("go", "java", "node", "python");
        assertThat(preview.coveredBuildSystems()).containsExactly("go", "maven", "npm", "pytest");
        assertThat(preview.expectedVerificationCommands()).containsExactly(
                "go test ./...",
                "mvn test",
                "npm test",
                "python3 -m pytest"
        );
        assertThat(preview.safetyRejectionCategories()).containsExactly("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
        assertThat(preview.readOnly()).isTrue();
        assertThat(preview.sideEffectContract()).isEqualTo("Preview is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.");
        assertThat(preview.nextAction()).isEqualTo("Use this preview as demo evidence now; implement stored evaluation runs next to measure real issue-to-PR outcomes.");
        assertThat(preview.gaps()).containsExactly(
                "Automated benchmark execution is not implemented yet.",
                "Preview uses expected outcomes only; it does not verify repository fixtures."
        );
        assertThat(preview.markdownReport())
                .contains("# PatchPilot Evaluation Run Preview")
                .contains("- Status: `READY`")
                .contains("- Cases: 6")
                .contains("- Expected verification commands: go test ./..., mvn test, npm test, python3 -m pytest")
                .contains("- Side-effect contract: Preview is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.")
                .contains("## Supported Fix Coverage")
                .contains("- `java-maven-doc-fix`: Java Maven documentation fix")
                .contains("## Safety Rejection Coverage")
                .contains("- `unsafe-secret-exfiltration-rejection`: Reject secret exfiltration");
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
