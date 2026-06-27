package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineSummaryVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunPreviewVo;
import io.patchpilot.backend.evaluation.service.EvaluationRunArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class EvaluationRunArchiveService {

    private static final int MAX_ARCHIVES = 20;
    private static final String READY = "READY";
    private static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String SIDE_EFFECT_CONTRACT = "Evaluation run executes local checked-in fixture verification commands and records safety coverage only; it does not create tasks, call the model, clone repositories, mutate Git, or write to GitHub.";
    private static final String READY_NEXT_ACTION = "Evaluation run passed; use the archived report as measurable demo evidence for supported adapters and safety rejections.";
    private static final String NEEDS_ATTENTION_NEXT_ACTION = "Fix failing fixture baseline cases or missing safety coverage, then rerun the evaluation.";

    private final EvaluationCaseCatalogService evaluationCaseCatalogService;
    private final Supplier<EvaluationFixtureBaselineSummaryVo> fixtureBaselineRunner;
    private final EvaluationRunArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public EvaluationRunArchiveService(
            EvaluationCaseCatalogService evaluationCaseCatalogService,
            EvaluationFixtureBaselineService evaluationFixtureBaselineService,
            EvaluationRunArchiveRepository archiveRepository
    ) {
        this(
                evaluationCaseCatalogService,
                evaluationFixtureBaselineService::runBaseline,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    public EvaluationRunArchiveService(
            EvaluationCaseCatalogService evaluationCaseCatalogService,
            Supplier<EvaluationFixtureBaselineSummaryVo> fixtureBaselineRunner,
            EvaluationRunArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.evaluationCaseCatalogService = evaluationCaseCatalogService;
        this.fixtureBaselineRunner = fixtureBaselineRunner;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public EvaluationRunArchiveVo runAndArchiveEvaluation() {
        EvaluationRunPreviewVo preview = evaluationCaseCatalogService.getEvaluationRunPreview();
        EvaluationFixtureBaselineSummaryVo baseline = fixtureBaselineRunner.get();
        Instant createdAt = Instant.now(clock);
        String id = idSupplier.get();
        boolean ready = isReady(preview, baseline);
        String status = ready ? READY : NEEDS_ATTENTION;
        String nextAction = ready ? READY_NEXT_ACTION : NEEDS_ATTENTION_NEXT_ACTION;
        EvaluationRunArchiveVo archive = new EvaluationRunArchiveVo(
                id,
                status,
                preview.caseCount(),
                preview.supportedFixCaseCount(),
                preview.safetyRejectionCaseCount(),
                baseline.executedCaseCount(),
                baseline.passedCaseCount(),
                baseline.failedCaseCount(),
                baseline.skippedCaseCount(),
                preview.coveredLanguages(),
                preview.coveredBuildSystems(),
                preview.safetyRejectionCategories(),
                createdAt,
                SIDE_EFFECT_CONTRACT,
                nextAction,
                buildReport(id, createdAt, status, nextAction, preview, baseline)
        );
        return archiveRepository.save(archive);
    }

    public List<EvaluationRunArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<EvaluationRunArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static boolean isReady(EvaluationRunPreviewVo preview, EvaluationFixtureBaselineSummaryVo baseline) {
        return READY.equals(preview.status())
                && READY.equals(baseline.status())
                && baseline.failedCaseCount() == 0
                && baseline.executedCaseCount() == preview.supportedFixCaseCount()
                && preview.safetyRejectionCaseCount() > 0
                && !preview.safetyRejectionCategories().isEmpty();
    }

    private static String buildReport(
            String id,
            Instant createdAt,
            String status,
            String nextAction,
            EvaluationRunPreviewVo preview,
            EvaluationFixtureBaselineSummaryVo baseline
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Run");
        lines.add("");
        lines.add("- Evaluation run id: `" + id + "`");
        lines.add("- Status: `" + status + "`");
        lines.add("- Archived at: `" + createdAt + "`");
        lines.add("- Total cases: " + preview.caseCount());
        lines.add("- Supported fix cases: " + preview.supportedFixCaseCount());
        lines.add("- Safety rejection cases: " + preview.safetyRejectionCaseCount());
        lines.add("- Executed fix cases: " + baseline.executedCaseCount());
        lines.add("- Passed fix cases: " + baseline.passedCaseCount());
        lines.add("- Failed fix cases: " + baseline.failedCaseCount());
        lines.add("- Skipped cases: " + baseline.skippedCaseCount());
        lines.add("- Languages: " + joinOrNone(preview.coveredLanguages()));
        lines.add("- Build systems: " + joinOrNone(preview.coveredBuildSystems()));
        lines.add("- Safety rejection categories: " + joinOrNone(preview.safetyRejectionCategories()));
        lines.add("- Side-effect contract: " + SIDE_EFFECT_CONTRACT);
        lines.add("- Next action: " + nextAction);
        lines.add("");
        lines.add("## Fixture Baseline Evidence");
        lines.add("");
        lines.add(baseline.markdownReport());
        lines.add("");
        lines.add("## Preview Evidence");
        lines.add("");
        lines.add(preview.markdownReport());
        return String.join("\n", lines);
    }

    private static String joinOrNone(List<String> values) {
        return values.isEmpty() ? "none" : String.join(", ", values);
    }
}
