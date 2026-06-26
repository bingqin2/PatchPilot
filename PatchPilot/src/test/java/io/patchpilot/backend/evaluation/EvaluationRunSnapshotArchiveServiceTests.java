package io.patchpilot.backend.evaluation;

import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationRunSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationRunSnapshotArchiveServiceTests {

    @Test
    void should_archive_current_evaluation_run_preview_as_local_evidence() {
        EvaluationRunSnapshotArchiveService service = new EvaluationRunSnapshotArchiveService(
                new EvaluationCaseCatalogService(),
                new InMemoryEvaluationRunSnapshotArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-26T04:00:00Z"), ZoneOffset.UTC),
                () -> "snapshot-1"
        );

        var archive = service.archiveCurrentPreview();

        assertThat(archive.id()).isEqualTo("snapshot-1");
        assertThat(archive.previewRunId()).isEqualTo("preview-current-catalog");
        assertThat(archive.title()).isEqualTo("Evaluation run preview");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.caseCount()).isEqualTo(6);
        assertThat(archive.supportedFixCaseCount()).isEqualTo(4);
        assertThat(archive.safetyRejectionCaseCount()).isEqualTo(2);
        assertThat(archive.coveredLanguages()).containsExactly("go", "java", "node", "python");
        assertThat(archive.coveredBuildSystems()).containsExactly("go", "maven", "npm", "pytest");
        assertThat(archive.expectedVerificationCommands()).containsExactly(
                "go test ./...",
                "mvn test",
                "npm test",
                "python3 -m pytest"
        );
        assertThat(archive.safetyRejectionCategories()).containsExactly("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-26T04:00:00Z"));
        assertThat(archive.sideEffectContract()).isEqualTo("Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.");
        assertThat(archive.report())
                .contains("# PatchPilot Evaluation Run Snapshot")
                .contains("- Snapshot id: `snapshot-1`")
                .contains("- Preview run id: `preview-current-catalog`")
                .contains("- Archived at: `2026-06-26T04:00:00Z`")
                .contains("- Side-effect contract: Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.")
                .contains("## Preview Evidence")
                .contains("# PatchPilot Evaluation Run Preview");
    }

    @Test
    void should_list_recent_archived_snapshots_newest_first() {
        InMemoryEvaluationRunSnapshotArchiveRepository repository = new InMemoryEvaluationRunSnapshotArchiveRepository();
        EvaluationRunSnapshotArchiveService firstService = service(repository, "snapshot-older", "2026-06-26T04:00:00Z");
        EvaluationRunSnapshotArchiveService secondService = service(repository, "snapshot-newer", "2026-06-26T05:00:00Z");

        firstService.archiveCurrentPreview();
        secondService.archiveCurrentPreview();

        List<String> archiveIds = secondService.listRecentArchives().stream()
                .map(archive -> archive.id())
                .toList();

        assertThat(archiveIds).containsExactly("snapshot-newer", "snapshot-older");
        assertThat(secondService.findArchive("snapshot-older"))
                .map(archive -> archive.previewRunId())
                .contains("preview-current-catalog");
    }

    private static EvaluationRunSnapshotArchiveService service(
            InMemoryEvaluationRunSnapshotArchiveRepository repository,
            String snapshotId,
            String timestamp
    ) {
        return new EvaluationRunSnapshotArchiveService(
                new EvaluationCaseCatalogService(),
                repository,
                Clock.fixed(Instant.parse(timestamp), ZoneOffset.UTC),
                () -> snapshotId
        );
    }
}
