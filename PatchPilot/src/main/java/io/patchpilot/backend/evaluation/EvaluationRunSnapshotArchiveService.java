package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.domain.EvaluationRunPreviewVo;
import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.service.EvaluationRunSnapshotArchiveRepository;
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
public class EvaluationRunSnapshotArchiveService {

    private static final int MAX_ARCHIVES = 20;
    private static final String ARCHIVE_SIDE_EFFECT_CONTRACT = "Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.";

    private final EvaluationCaseCatalogService evaluationCaseCatalogService;
    private final EvaluationRunSnapshotArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public EvaluationRunSnapshotArchiveService(
            EvaluationCaseCatalogService evaluationCaseCatalogService,
            EvaluationRunSnapshotArchiveRepository archiveRepository
    ) {
        this(
                evaluationCaseCatalogService,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    public EvaluationRunSnapshotArchiveService(
            EvaluationCaseCatalogService evaluationCaseCatalogService,
            EvaluationRunSnapshotArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.evaluationCaseCatalogService = evaluationCaseCatalogService;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public EvaluationRunSnapshotArchiveVo archiveCurrentPreview() {
        EvaluationRunPreviewVo preview = evaluationCaseCatalogService.getEvaluationRunPreview();
        Instant createdAt = Instant.now(clock);
        String id = idSupplier.get();
        EvaluationRunSnapshotArchiveVo archive = new EvaluationRunSnapshotArchiveVo(
                id,
                preview.previewRunId(),
                preview.title(),
                preview.status(),
                preview.caseCount(),
                preview.supportedFixCaseCount(),
                preview.safetyRejectionCaseCount(),
                preview.coveredLanguages(),
                preview.coveredBuildSystems(),
                preview.expectedVerificationCommands(),
                preview.safetyRejectionCategories(),
                createdAt,
                ARCHIVE_SIDE_EFFECT_CONTRACT,
                buildArchiveReport(id, createdAt, preview)
        );
        return archiveRepository.save(archive);
    }

    public List<EvaluationRunSnapshotArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<EvaluationRunSnapshotArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String buildArchiveReport(String id, Instant createdAt, EvaluationRunPreviewVo preview) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Evaluation Run Snapshot");
        lines.add("");
        lines.add("- Snapshot id: `" + id + "`");
        lines.add("- Preview run id: `" + preview.previewRunId() + "`");
        lines.add("- Title: " + preview.title());
        lines.add("- Status: `" + preview.status() + "`");
        lines.add("- Archived at: `" + createdAt + "`");
        lines.add("- Cases: " + preview.caseCount());
        lines.add("- Supported fix cases: " + preview.supportedFixCaseCount());
        lines.add("- Safety rejection cases: " + preview.safetyRejectionCaseCount());
        lines.add("- Languages: " + joinOrNone(preview.coveredLanguages()));
        lines.add("- Build systems: " + joinOrNone(preview.coveredBuildSystems()));
        lines.add("- Expected verification commands: " + joinOrNone(preview.expectedVerificationCommands()));
        lines.add("- Safety rejection categories: " + joinOrNone(preview.safetyRejectionCategories()));
        lines.add("- Side-effect contract: " + ARCHIVE_SIDE_EFFECT_CONTRACT);
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
