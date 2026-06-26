package io.patchpilot.backend.evaluation.service;

import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.service.impl.InMemoryEvaluationFixtureBaselineRunArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryEvaluationFixtureBaselineRunArchiveRepositoryTests {

    private final InMemoryEvaluationFixtureBaselineRunArchiveRepository repository = new InMemoryEvaluationFixtureBaselineRunArchiveRepository();

    @Test
    void should_store_archives_newest_first_and_trim_history() {
        for (int index = 1; index <= 22; index++) {
            repository.save(archive("baseline-" + index, Instant.parse("2026-06-26T06:00:00Z").plusSeconds(index)));
        }

        var archives = repository.listRecentArchives(20);

        assertThat(archives).hasSize(20);
        assertThat(archives.get(0).id()).isEqualTo("baseline-22");
        assertThat(archives.get(19).id()).isEqualTo("baseline-3");
        assertThat(repository.findById("baseline-1")).isEmpty();
        assertThat(repository.findById("baseline-22")).isPresent();
    }

    private static EvaluationFixtureBaselineRunArchiveVo archive(String id, Instant createdAt) {
        return new EvaluationFixtureBaselineRunArchiveVo(
                id,
                "READY",
                6,
                4,
                4,
                0,
                2,
                createdAt,
                "Archive stores a local fixture baseline execution report only; it does not create tasks, call the model, mutate Git, or write to GitHub.",
                "Fixture baseline is passing; use the archived report as demo evidence for supported language adapters.",
                "# PatchPilot Evaluation Fixture Baseline Run"
        );
    }
}
