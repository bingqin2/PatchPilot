package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationCaseFixtureReadinessVo;
import io.patchpilot.backend.evaluation.domain.EvaluationCaseVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineCaseVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineSummaryVo;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationFixtureBaselineService {

    private static final String SUPPORTED_FIX = "SUPPORTED_FIX";
    private static final String PASSED = "PASSED";
    private static final String FAILED = "FAILED";
    private static final String SKIPPED = "SKIPPED";
    private static final String READY = "READY";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String SIDE_EFFECT_CONTRACT = "Evaluation fixture baseline runs local checked-in fixture verification commands only; it does not create tasks, call the model, mutate Git, or write to GitHub.";
    private static final String READY_NEXT_ACTION = "Fixture baseline is passing; use the report as demo evidence for supported language adapters.";
    private static final String NEEDS_ATTENTION_NEXT_ACTION = "Fix failing fixture commands before using the baseline as demo evidence.";
    private static final int OUTPUT_SNIPPET_LIMIT = 2_000;

    private final EvaluationCaseCatalogService evaluationCaseCatalogService;
    private final EvaluationCaseFixtureReadinessService evaluationCaseFixtureReadinessService;
    private final LanguageAdapterRegistry languageAdapterRegistry;
    private final EvaluationFixtureBaselineCommandRunner commandRunner;

    public EvaluationFixtureBaselineSummaryVo runBaseline() {
        List<EvaluationCaseVo> evaluationCases = evaluationCaseCatalogService.listEvaluationCases();
        List<EvaluationCaseFixtureReadinessVo> readinessRows = evaluationCaseFixtureReadinessService.listCaseReadiness();
        List<EvaluationFixtureBaselineCaseVo> cases = evaluationCases.stream()
                .map(evaluationCase -> toBaselineCase(evaluationCase, readinessFor(evaluationCase, readinessRows)))
                .toList();
        int executedCaseCount = countExecuted(cases);
        int passedCaseCount = countStatus(cases, PASSED);
        int failedCaseCount = countStatus(cases, FAILED);
        int skippedCaseCount = countStatus(cases, SKIPPED);
        boolean ready = failedCaseCount == 0 && executedCaseCount > 0;
        String status = ready ? READY : NEEDS_ATTENTION;
        String nextAction = ready ? READY_NEXT_ACTION : NEEDS_ATTENTION_NEXT_ACTION;
        return new EvaluationFixtureBaselineSummaryVo(
                status,
                cases.size(),
                executedCaseCount,
                passedCaseCount,
                failedCaseCount,
                skippedCaseCount,
                cases,
                SIDE_EFFECT_CONTRACT,
                nextAction,
                buildMarkdownReport(status, executedCaseCount, passedCaseCount, failedCaseCount, skippedCaseCount, cases, nextAction)
        );
    }

    private EvaluationFixtureBaselineCaseVo toBaselineCase(
            EvaluationCaseVo evaluationCase,
            EvaluationCaseFixtureReadinessVo readiness
    ) {
        if (!SUPPORTED_FIX.equals(evaluationCase.category())) {
            return skippedCase(evaluationCase);
        }
        if (readiness == null || !"PASS".equals(readiness.status())) {
            return failedReadinessCase(evaluationCase, readiness);
        }

        Path fixturePath = resolveFixturePath(evaluationCase.repositoryFixturePath());
        LanguageDetectionResult detection = languageAdapterRegistry.detect(fixturePath);
        if (!detection.supported()) {
            return failedCase(
                    evaluationCase,
                    evaluationCase.repositoryFixturePath(),
                    detection.language(),
                    detection.buildSystem(),
                    List.of(),
                    null,
                    "",
                    detection.reason(),
                    "Fix adapter detection before running fixture verification."
            );
        }

        TestRunResult result = commandRunner.run(evaluationCase.id(), fixturePath, detection.verificationCommand());
        boolean passed = result.exitCode() == 0;
        return new EvaluationFixtureBaselineCaseVo(
                evaluationCase.id(),
                evaluationCase.title(),
                evaluationCase.category(),
                passed ? PASSED : FAILED,
                true,
                evaluationCase.repositoryFixturePath(),
                detection.language(),
                detection.buildSystem(),
                detection.verificationCommand(),
                result.exitCode(),
                outputSnippet(result.output()),
                "Fixture verification command exited with code " + result.exitCode() + ".",
                passed ? "Keep this fixture as passing demo evidence." : "Inspect the output and fix the fixture or adapter command."
        );
    }

    private static EvaluationCaseFixtureReadinessVo readinessFor(
            EvaluationCaseVo evaluationCase,
            List<EvaluationCaseFixtureReadinessVo> readinessRows
    ) {
        return readinessRows.stream()
                .filter(readiness -> evaluationCase.id().equals(readiness.caseId()))
                .findFirst()
                .orElse(null);
    }

    private static EvaluationFixtureBaselineCaseVo skippedCase(EvaluationCaseVo evaluationCase) {
        return new EvaluationFixtureBaselineCaseVo(
                evaluationCase.id(),
                evaluationCase.title(),
                evaluationCase.category(),
                SKIPPED,
                false,
                "none",
                "none",
                "none",
                List.of(),
                null,
                "",
                "Safety rejection cases validate trigger gating and do not run repository verification.",
                "Validate this case through trigger rejection tests instead."
        );
    }

    private static EvaluationFixtureBaselineCaseVo failedReadinessCase(
            EvaluationCaseVo evaluationCase,
            EvaluationCaseFixtureReadinessVo readiness
    ) {
        String fixturePath = evaluationCase.repositoryFixturePath() == null ? "none" : evaluationCase.repositoryFixturePath();
        String language = readiness == null ? "unknown" : readiness.actualLanguage();
        String buildSystem = readiness == null ? "unknown" : readiness.actualBuildSystem();
        String reason = readiness == null ? "Fixture readiness row is missing." : readiness.nextAction();
        return failedCase(
                evaluationCase,
                fixturePath,
                language,
                buildSystem,
                readiness == null ? List.of() : readiness.actualVerificationCommand(),
                null,
                "",
                reason,
                "Fix fixture readiness before running verification."
        );
    }

    private static EvaluationFixtureBaselineCaseVo failedCase(
            EvaluationCaseVo evaluationCase,
            String fixturePath,
            String language,
            String buildSystem,
            List<String> verificationCommand,
            Integer exitCode,
            String outputSnippet,
            String reason,
            String nextAction
    ) {
        return new EvaluationFixtureBaselineCaseVo(
                evaluationCase.id(),
                evaluationCase.title(),
                evaluationCase.category(),
                FAILED,
                false,
                fixturePath,
                language,
                buildSystem,
                verificationCommand,
                exitCode,
                outputSnippet,
                reason,
                nextAction
        );
    }

    private static Path resolveFixturePath(String fixturePath) {
        Path rootRelativePath = Path.of(fixturePath);
        if (Files.exists(rootRelativePath)) {
            return rootRelativePath.toAbsolutePath().normalize();
        }
        return Path.of("..").resolve(fixturePath).toAbsolutePath().normalize();
    }

    private static String outputSnippet(String output) {
        if (output == null || output.isBlank()) {
            return "";
        }
        String normalized = output.strip();
        if (normalized.length() <= OUTPUT_SNIPPET_LIMIT) {
            return normalized;
        }
        return normalized.substring(0, OUTPUT_SNIPPET_LIMIT) + "\n... output truncated";
    }

    private static int countExecuted(List<EvaluationFixtureBaselineCaseVo> cases) {
        return (int) cases.stream()
                .filter(EvaluationFixtureBaselineCaseVo::executed)
                .count();
    }

    private static int countStatus(List<EvaluationFixtureBaselineCaseVo> cases, String status) {
        return (int) cases.stream()
                .filter(evaluationCase -> status.equals(evaluationCase.status()))
                .count();
    }

    private static String buildMarkdownReport(
            String status,
            int executedCaseCount,
            int passedCaseCount,
            int failedCaseCount,
            int skippedCaseCount,
            List<EvaluationFixtureBaselineCaseVo> cases,
            String nextAction
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Fixture Baseline");
        lines.add("");
        lines.add("- Status: `" + status + "`");
        lines.add("- Cases: " + cases.size());
        lines.add("- Executed cases: " + executedCaseCount);
        lines.add("- Passed cases: " + passedCaseCount);
        lines.add("- Failed cases: " + failedCaseCount);
        lines.add("- Skipped cases: " + skippedCaseCount);
        lines.add("- Side-effect contract: " + SIDE_EFFECT_CONTRACT);
        lines.add("- Next action: " + nextAction);
        lines.add("");
        lines.add("## Cases");
        lines.add("");
        cases.forEach(evaluationCase -> {
            lines.add("- `" + evaluationCase.caseId() + "`: `" + evaluationCase.status() + "`");
            lines.add("  - Fixture: `" + evaluationCase.fixturePath() + "`");
            lines.add("  - Adapter: `" + evaluationCase.language() + "/" + evaluationCase.buildSystem() + "`");
            lines.add("  - Command: `" + commandLabel(evaluationCase.verificationCommand()) + "`");
            lines.add("  - Exit code: `" + (evaluationCase.exitCode() == null ? "none" : evaluationCase.exitCode()) + "`");
            lines.add("  - Reason: " + evaluationCase.reason());
            lines.add("  - Next action: " + evaluationCase.nextAction());
            if (!evaluationCase.outputSnippet().isBlank()) {
                lines.add("  - Output: `" + inlineSnippet(evaluationCase.outputSnippet()) + "`");
            }
        });
        return String.join("\n", lines);
    }

    private static String commandLabel(List<String> command) {
        return command.isEmpty() ? "none" : String.join(" ", command);
    }

    private static String inlineSnippet(String outputSnippet) {
        return outputSnippet.replace("`", "'").replace("\n", " ");
    }
}
