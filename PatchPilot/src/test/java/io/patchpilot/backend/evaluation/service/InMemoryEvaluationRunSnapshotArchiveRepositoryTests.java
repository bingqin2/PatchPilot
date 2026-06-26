package io.patchpilot.backend.evaluation.service;

import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationRunSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryEvaluationRunSnapshotArchiveRepositoryTests {

    private final InMemoryEvaluationRunSnapshotArchiveRepository repository = new InMemoryEvaluationRunSnapshotArchiveRepository();

    @Test
    void should_store_archives_newest_first_and_trim_history() {
        for (int index = 1; index <= 22; index++) {
            repository.save(archive("snapshot-" + index, Instant.parse("2026-06-26T04:00:00Z").plusSeconds(index)));
        }

        List<EvaluationRunSnapshotArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives).hasSize(20);
        assertThat(archives.get(0).id()).isEqualTo("snapshot-22");
        assertThat(archives.get(19).id()).isEqualTo("snapshot-3");
        assertThat(repository.findById("snapshot-1")).isEmpty();
        assertThat(repository.findById("snapshot-22")).isPresent();
    }

    private static EvaluationRunSnapshotArchiveVo archive(String id, Instant createdAt) {
        return new EvaluationRunSnapshotArchiveVo(
                id,
                "preview-current-catalog",
                "Evaluation run preview",
                "READY",
                6,
                4,
                2,
                List.of("go", "java", "node", "python"),
                List.of("go", "maven", "npm", "pytest"),
                List.of("go test ./...", "mvn test", "npm test", "python3 -m pytest"),
                List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE"),
                createdAt,
                "Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.",
                "# PatchPilot Evaluation Run Snapshot"
        );
    }
}
