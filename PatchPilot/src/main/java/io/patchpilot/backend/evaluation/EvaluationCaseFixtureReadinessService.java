package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationCaseFixtureReadinessSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseFixtureReadinessVo;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseVo;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationCaseFixtureReadinessService {

    private static final String SUPPORTED_FIX = "SUPPORTED_FIX";
    private static final String PASS = "PASS";
    private static final String FAIL = "FAIL";
    private static final String NO_FIXTURE_REQUIRED = "NO_FIXTURE_REQUIRED";
    private static final String READY = "READY";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String SIDE_EFFECT_CONTRACT = "Evaluation case fixture readiness checks local checked-in fixtures and adapter metadata only; it does not create tasks, call the model, run verification commands, mutate Git, or write to GitHub.";
    private static final String READY_NEXT_ACTION = "Evaluation case fixtures are ready for demo evidence; automated evaluation execution remains future work.";
    private static final String NEEDS_ATTENTION_NEXT_ACTION = "Fix failing evaluation fixture readiness rows before using the catalog as demo evidence.";

    private final EvaluationCaseCatalogService evaluationCaseCatalogService;
    private final LanguageAdapterRegistry languageAdapterRegistry;

    public List<EvaluationCaseFixtureReadinessVo> listCaseReadiness() {
        return evaluationCaseCatalogService.listEvaluationCases().stream()
                .map(this::toReadiness)
                .toList();
    }

    public EvaluationCaseFixtureReadinessSummaryVo getReadinessSummary() {
        List<EvaluationCaseFixtureReadinessVo> cases = listCaseReadiness();
        int passingCaseCount = countByStatus(cases, PASS);
        int noFixtureRequiredCaseCount = countByStatus(cases, NO_FIXTURE_REQUIRED);
        int failingCaseCount = countByStatus(cases, FAIL);
        boolean ready = failingCaseCount == 0 && !cases.isEmpty();
        return new EvaluationCaseFixtureReadinessSummaryVo(
                ready ? READY : NEEDS_ATTENTION,
                cases.size(),
                passingCaseCount,
                noFixtureRequiredCaseCount,
                failingCaseCount,
                cases,
                SIDE_EFFECT_CONTRACT,
                ready ? READY_NEXT_ACTION : NEEDS_ATTENTION_NEXT_ACTION,
                buildMarkdownReport(
                        ready ? READY : NEEDS_ATTENTION,
                        passingCaseCount,
                        noFixtureRequiredCaseCount,
                        failingCaseCount,
                        cases,
                        ready ? READY_NEXT_ACTION : NEEDS_ATTENTION_NEXT_ACTION
                )
        );
    }

    private EvaluationCaseFixtureReadinessVo toReadiness(EvaluationCaseVo evaluationCase) {
        if (!SUPPORTED_FIX.equals(evaluationCase.category())) {
            return noFixtureRequired(evaluationCase);
        }

        Path fixturePath = resolveFixturePath(evaluationCase.repositoryFixturePath());
        boolean fixtureExists = Files.isDirectory(fixturePath);
        LanguageDetectionResult detection = fixtureExists
                ? languageAdapterRegistry.detect(fixturePath)
                : LanguageDetectionResult.unsupported("unknown", "unknown", "Missing evaluation fixture path");
        boolean adapterMatches = detection.supported()
                && evaluationCase.language().equals(detection.language())
                && evaluationCase.buildSystem().equals(detection.buildSystem())
                && evaluationCase.expectedVerificationCommand().equals(detection.verificationCommand());
        List<String> missingExpectedFiles = expectedMissingFiles(fixturePath, evaluationCase.expectedChangedFiles());
        boolean expectedFilesExist = fixtureExists && missingExpectedFiles.isEmpty();
        boolean passing = fixtureExists && adapterMatches && expectedFilesExist;
        return new EvaluationCaseFixtureReadinessVo(
                evaluationCase.id(),
                evaluationCase.title(),
                evaluationCase.category(),
                passing ? PASS : FAIL,
                true,
                evaluationCase.repositoryFixturePath(),
                fixtureExists,
                evaluationCase.language(),
                detection.language(),
                evaluationCase.buildSystem(),
                detection.buildSystem(),
                evaluationCase.expectedVerificationCommand(),
                detection.verificationCommand(),
                adapterMatches,
                evaluationCase.expectedChangedFiles(),
                missingExpectedFiles,
                expectedFilesExist,
                detection.reason(),
                passing
                        ? "Fixture readiness is verified for this supported evaluation case."
                        : "Fix the fixture path, adapter expectation, or expected changed files for this evaluation case."
        );
    }

    private static EvaluationCaseFixtureReadinessVo noFixtureRequired(EvaluationCaseVo evaluationCase) {
        return new EvaluationCaseFixtureReadinessVo(
                evaluationCase.id(),
                evaluationCase.title(),
                evaluationCase.category(),
                NO_FIXTURE_REQUIRED,
                false,
                "none",
                false,
                "none",
                "none",
                "none",
                "none",
                List.of(),
                List.of(),
                false,
                List.of(),
                List.of(),
                false,
                "Safety rejection cases validate trigger gating and do not require repository fixtures.",
                "Keep this case in the safety rejection catalog; no fixture verification is required."
        );
    }

    private static Path resolveFixturePath(String fixturePath) {
        Path rootRelativePath = Path.of(fixturePath);
        if (Files.exists(rootRelativePath)) {
            return rootRelativePath.toAbsolutePath().normalize();
        }
        return Path.of("..").resolve(fixturePath).toAbsolutePath().normalize();
    }

    private static List<String> expectedMissingFiles(Path fixturePath, List<String> expectedChangedFiles) {
        if (!Files.isDirectory(fixturePath)) {
            return List.copyOf(expectedChangedFiles);
        }
        return expectedChangedFiles.stream()
                .filter(expectedFile -> !Files.isRegularFile(fixturePath.resolve(expectedFile)))
                .toList();
    }

    private static int countByStatus(List<EvaluationCaseFixtureReadinessVo> cases, String status) {
        return (int) cases.stream()
                .filter(evaluationCase -> status.equals(evaluationCase.status()))
                .count();
    }

    private static String buildMarkdownReport(
            String status,
            int passingCaseCount,
            int noFixtureRequiredCaseCount,
            int failingCaseCount,
            List<EvaluationCaseFixtureReadinessVo> cases,
            String nextAction
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Case Fixture Readiness");
        lines.add("");
        lines.add("- Status: `" + status + "`");
        lines.add("- Cases: " + cases.size());
        lines.add("- Passing cases: " + passingCaseCount);
        lines.add("- No-fixture-required cases: " + noFixtureRequiredCaseCount);
        lines.add("- Failing cases: " + failingCaseCount);
        lines.add("- Side-effect contract: " + SIDE_EFFECT_CONTRACT);
        lines.add("- Next action: " + nextAction);
        lines.add("");
        lines.add("## Cases");
        lines.add("");
        cases.forEach(evaluationCase -> {
            lines.add("- `" + evaluationCase.caseId() + "`: `" + evaluationCase.status() + "`");
            lines.add("  - Fixture: `" + evaluationCase.fixturePath() + "`");
            lines.add("  - Adapter: `" + evaluationCase.expectedLanguage() + "/" + evaluationCase.expectedBuildSystem() + "` -> `" + evaluationCase.actualLanguage() + "/" + evaluationCase.actualBuildSystem() + "`");
            lines.add("  - Expected command: `" + commandLabel(evaluationCase.expectedVerificationCommand()) + "`");
            lines.add("  - Actual command: `" + commandLabel(evaluationCase.actualVerificationCommand()) + "`");
            lines.add("  - Expected files: " + joinCodeLabels(evaluationCase.expectedChangedFiles()));
            lines.add("  - Missing files: " + joinCodeLabels(evaluationCase.missingExpectedFiles()));
            lines.add("  - Next action: " + evaluationCase.nextAction());
        });
        return String.join("\n", lines);
    }

    private static String commandLabel(List<String> command) {
        return command.isEmpty() ? "none" : String.join(" ", command);
    }

    private static String joinCodeLabels(List<String> values) {
        if (values.isEmpty()) {
            return "none";
        }
        return values.stream()
                .map(value -> "`" + value + "`")
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
    }
}
