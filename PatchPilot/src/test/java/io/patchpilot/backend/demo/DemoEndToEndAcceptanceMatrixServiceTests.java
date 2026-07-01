package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoEndToEndAcceptanceMatrixVo;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunRegressionSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveDigestVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveReadinessSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationSummaryVo;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DemoEndToEndAcceptanceMatrixServiceTests {

    private static final Instant NOW = Instant.parse("2026-07-01T12:00:00Z");

    @Test
    void should_report_ready_when_launch_evaluation_safety_and_recent_pr_evidence_are_ready() {
        DemoEndToEndAcceptanceMatrixService service = service(
                () -> liveLaunchGate("READY", true),
                () -> evaluationSummary(List.of("go", "java", "node", "python"), List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE")),
                () -> baselineRegression("STABLE"),
                () -> evaluationRunSummary("READY"),
                List.of(
                        task("task-completed", FixTaskStatus.COMPLETED, "https://github.com/bingqin2/PatchPilot/pull/9", null, "java", "maven"),
                        task("task-failed", FixTaskStatus.FAILED, null, "Unsupported repository: no adapter", null, null),
                        task("task-review", FixTaskStatus.PENDING_REVIEW, null, "Risk review required", "python", "pytest")
                )
        );

        DemoEndToEndAcceptanceMatrixVo matrix = service.getMatrix();

        assertThat(matrix.status()).isEqualTo("READY");
        assertThat(matrix.readyForFinalDemo()).isTrue();
        assertThat(matrix.readinessPercent()).isEqualTo(100);
        assertThat(matrix.readyCount()).isEqualTo(matrix.items().size());
        assertThat(matrix.blockedCount()).isZero();
        assertThat(matrix.summary()).isEqualTo("PatchPilot has enough launch, language, safety, evaluation, and recent PR evidence for a final self-hosted demo.");
        assertThat(matrix.items())
                .extracting(item -> item.category() + ":" + item.status())
                .contains(
                        "Launch:READY",
                        "Language coverage:READY",
                        "Safety coverage:READY",
                        "Recent Pull Request evidence:READY",
                        "Known failure handling:READY",
                        "Pending review safety:READY"
                );
        assertThat(matrix.nextActions()).containsExactly("Run the live launch gate, post the tested /agent fix comment, and preserve the resulting PR evidence.");
        assertThat(matrix.sideEffectContract())
                .contains("does not create tasks")
                .contains("does not mutate GitHub");
        assertThat(matrix.markdownReport())
                .contains("# PatchPilot End-to-End Acceptance Matrix")
                .contains("- Status: READY")
                .contains("- Readiness: 100%");
    }

    @Test
    void should_need_attention_when_no_recent_pr_evidence_exists() {
        DemoEndToEndAcceptanceMatrixService service = service(
                () -> liveLaunchGate("READY", true),
                () -> evaluationSummary(List.of("go", "java", "node", "python"), List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE")),
                () -> baselineRegression("STABLE"),
                () -> evaluationRunSummary("READY"),
                List.of(task("task-failed", FixTaskStatus.FAILED, null, "maven tests failed", "java", "maven"))
        );

        DemoEndToEndAcceptanceMatrixVo matrix = service.getMatrix();

        assertThat(matrix.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(matrix.readyForFinalDemo()).isFalse();
        assertThat(matrix.items())
                .anySatisfy(item -> {
                    assertThat(item.category()).isEqualTo("Recent Pull Request evidence");
                    assertThat(item.status()).isEqualTo("NEEDS_ATTENTION");
                    assertThat(item.nextAction()).contains("Create at least one recent completed PatchPilot PR");
                });
        assertThat(matrix.nextActions()).contains("Create at least one recent completed PatchPilot PR from a live /agent fix task.");
    }

    @Test
    void should_block_when_live_launch_gate_is_blocked() {
        DemoEndToEndAcceptanceMatrixService service = service(
                () -> liveLaunchGate("BLOCKED", false),
                () -> evaluationSummary(List.of("java"), List.of("NOT_ACTIONABLE")),
                () -> baselineRegression("REGRESSED"),
                () -> evaluationRunSummary("BLOCKED"),
                List.of()
        );

        DemoEndToEndAcceptanceMatrixVo matrix = service.getMatrix();

        assertThat(matrix.status()).isEqualTo("BLOCKED");
        assertThat(matrix.readyForFinalDemo()).isFalse();
        assertThat(matrix.blockedCount()).isGreaterThan(0);
        assertThat(matrix.summary()).isEqualTo("PatchPilot is blocked from final self-hosted demo acceptance.");
        assertThat(matrix.nextActions()).contains("Fix the live launch gate before posting another GitHub issue comment.");
    }

    private static DemoEndToEndAcceptanceMatrixService service(
            Supplier<DemoLiveLaunchGateVo> liveLaunchGateSupplier,
            Supplier<EvaluationSummaryVo> evaluationSummarySupplier,
            Supplier<EvaluationFixtureBaselineRunRegressionSummaryVo> baselineRegressionSupplier,
            Supplier<EvaluationRunArchiveReadinessSummaryVo> evaluationRunSummarySupplier,
            List<FixTaskVo> tasks
    ) {
        return new DemoEndToEndAcceptanceMatrixService(
                liveLaunchGateSupplier,
                evaluationSummarySupplier,
                baselineRegressionSupplier,
                evaluationRunSummarySupplier,
                query -> tasks,
                () -> NOW
        );
    }

    private static DemoLiveLaunchGateVo liveLaunchGate(String status, boolean readyToPost) {
        return new DemoLiveLaunchGateVo(
                status,
                readyToPost,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/e2e.md",
                "Launch gate " + status,
                readyToPost ? List.of() : List.of("Fix launch gate."),
                "live launch gate contract",
                null,
                null,
                null,
                null,
                List.of(),
                NOW,
                "# Launch Gate"
        );
    }

    private static EvaluationSummaryVo evaluationSummary(List<String> languages, List<String> rejectionCategories) {
        return new EvaluationSummaryVo(
                "READY",
                languages.size() + rejectionCategories.size(),
                languages.size(),
                rejectionCategories.size(),
                languages,
                List.of("go", "maven", "npm", "pytest"),
                rejectionCategories,
                "Evaluation catalog ready.",
                true,
                "read-only"
        );
    }

    private static EvaluationFixtureBaselineRunRegressionSummaryVo baselineRegression(String status) {
        return new EvaluationFixtureBaselineRunRegressionSummaryVo(
                status,
                null,
                null,
                0,
                0,
                0,
                List.of(),
                List.of(),
                List.of(),
                "read-only",
                status.equals("REGRESSED") ? "Fix fixture baseline regressions." : "Fixture baseline stable.",
                "# Fixture Baseline"
        );
    }

    private static EvaluationRunArchiveReadinessSummaryVo evaluationRunSummary(String status) {
        EvaluationRunArchiveDigestVo latestRun = status.equals("READY")
                ? new EvaluationRunArchiveDigestVo("run-1", "READY", 6, 4, 2, 4, 4, 0, 0, NOW)
                : null;
        return new EvaluationRunArchiveReadinessSummaryVo(
                status,
                latestRun,
                null,
                0,
                0,
                0,
                List.of("go", "java", "node", "python"),
                List.of("go", "maven", "npm", "pytest"),
                List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE"),
                "read-only",
                status.equals("READY") ? "Evaluation run archive ready." : "Run and archive full evaluation.",
                "# Evaluation Run"
        );
    }

    private static FixTaskVo task(
            String id,
            FixTaskStatus status,
            String pullRequestUrl,
            String failureReason,
            String language,
            String buildSystem
    ) {
        return new FixTaskVo(
                id,
                "bingqin2",
                "PatchPilot",
                1,
                0,
                "bingqin2",
                "/agent fix touch docs/e2e.md",
                "delivery-" + id,
                1,
                status,
                failureReason,
                NOW,
                pullRequestUrl,
                status == FixTaskStatus.COMPLETED ? NOW : null,
                NOW,
                language,
                buildSystem,
                language == null ? null : "test command",
                language == null ? null : "detected " + language,
                null,
                null
        );
    }
}
