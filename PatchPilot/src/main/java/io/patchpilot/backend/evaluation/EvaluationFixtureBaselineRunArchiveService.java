package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineSummaryVo;
import io.patchpilot.backend.evaluation.service.EvaluationFixtureBaselineRunArchiveRepository;
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
public class EvaluationFixtureBaselineRunArchiveService {

    private static final int MAX_ARCHIVES = 20;
    private static final String ARCHIVE_SIDE_EFFECT_CONTRACT = "Archive stores a local fixture baseline execution report only; it does not create tasks, call the model, mutate Git, or write to GitHub.";
    private static final String READY_ARCHIVE_NEXT_ACTION = "Fixture baseline is passing; use the archived report as demo evidence for supported language adapters.";
    private static final String NEEDS_ATTENTION_ARCHIVE_NEXT_ACTION = "Fix failing fixture commands, then rerun and archive the fixture baseline.";

    private final EvaluationFixtureBaselineService fixtureBaselineService;
    private final EvaluationFixtureBaselineRunArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public EvaluationFixtureBaselineRunArchiveService(
            EvaluationFixtureBaselineService fixtureBaselineService,
            EvaluationFixtureBaselineRunArchiveRepository archiveRepository
    ) {
        this(
                fixtureBaselineService,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    public EvaluationFixtureBaselineRunArchiveService(
            EvaluationFixtureBaselineService fixtureBaselineService,
            EvaluationFixtureBaselineRunArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.fixtureBaselineService = fixtureBaselineService;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public EvaluationFixtureBaselineRunArchiveVo runAndArchiveBaseline() {
        EvaluationFixtureBaselineSummaryVo baseline = fixtureBaselineService.runBaseline();
        Instant createdAt = Instant.now(clock);
        String id = idSupplier.get();
        String nextAction = "READY".equals(baseline.status())
                ? READY_ARCHIVE_NEXT_ACTION
                : NEEDS_ATTENTION_ARCHIVE_NEXT_ACTION;
        EvaluationFixtureBaselineRunArchiveVo archive = new EvaluationFixtureBaselineRunArchiveVo(
                id,
                baseline.status(),
                baseline.totalCaseCount(),
                baseline.executedCaseCount(),
                baseline.passedCaseCount(),
                baseline.failedCaseCount(),
                baseline.skippedCaseCount(),
                createdAt,
                ARCHIVE_SIDE_EFFECT_CONTRACT,
                nextAction,
                buildArchiveReport(id, createdAt, baseline, nextAction)
        );
        return archiveRepository.save(archive);
    }

    public List<EvaluationFixtureBaselineRunArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<EvaluationFixtureBaselineRunArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String buildArchiveReport(
            String id,
            Instant createdAt,
            EvaluationFixtureBaselineSummaryVo baseline,
            String nextAction
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Fixture Baseline Run");
        lines.add("");
        lines.add("- Baseline run id: `" + id + "`");
        lines.add("- Status: `" + baseline.status() + "`");
        lines.add("- Archived at: `" + createdAt + "`");
        lines.add("- Cases: " + baseline.totalCaseCount());
        lines.add("- Executed cases: " + baseline.executedCaseCount());
        lines.add("- Passed cases: " + baseline.passedCaseCount());
        lines.add("- Failed cases: " + baseline.failedCaseCount());
        lines.add("- Skipped cases: " + baseline.skippedCaseCount());
        lines.add("- Side-effect contract: " + ARCHIVE_SIDE_EFFECT_CONTRACT);
        lines.add("- Next action: " + nextAction);
        lines.add("");
        lines.add("## Baseline Evidence");
        lines.add("");
        lines.add(baseline.markdownReport());
        return String.join("\n", lines);
    }
}
