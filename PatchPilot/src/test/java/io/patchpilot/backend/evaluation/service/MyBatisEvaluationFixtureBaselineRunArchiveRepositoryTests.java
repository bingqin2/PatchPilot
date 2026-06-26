package io.patchpilot.backend.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationFixtureBaselineRunArchiveEntity;
import io.patchpilot.backend.evaluation.mapper.EvaluationFixtureBaselineRunArchiveMapper;
import io.patchpilot.backend.evaluation.service.impl.MyBatisEvaluationFixtureBaselineRunArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisEvaluationFixtureBaselineRunArchiveRepositoryTests {

    private final EvaluationFixtureBaselineRunArchiveMapper archiveMapper = mock(EvaluationFixtureBaselineRunArchiveMapper.class);
    private final MyBatisEvaluationFixtureBaselineRunArchiveRepository repository = new MyBatisEvaluationFixtureBaselineRunArchiveRepository(archiveMapper);

    @Test
    void should_insert_fixture_baseline_archive_entity() {
        when(archiveMapper.insert(any(EvaluationFixtureBaselineRunArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<EvaluationFixtureBaselineRunArchiveEntity> captor = ArgumentCaptor.forClass(EvaluationFixtureBaselineRunArchiveEntity.class);
        EvaluationFixtureBaselineRunArchiveVo archive = archive("baseline-run-1", Instant.parse("2026-06-26T06:00:00Z"));

        var saved = repository.save(archive);

        assertThat(saved).isEqualTo(archive);
        verify(archiveMapper).insert(captor.capture());
        EvaluationFixtureBaselineRunArchiveEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo("baseline-run-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getTotalCaseCount()).isEqualTo(6);
        assertThat(entity.getExecutedCaseCount()).isEqualTo(4);
        assertThat(entity.getPassedCaseCount()).isEqualTo(4);
        assertThat(entity.getFailedCaseCount()).isZero();
        assertThat(entity.getSkippedCaseCount()).isEqualTo(2);
        assertThat(entity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-26T06:00:00Z"));
        assertThat(entity.getReport()).contains("# PatchPilot Evaluation Fixture Baseline Run");
    }

    @Test
    void should_list_recent_fixture_baseline_archive_entities_as_value_objects() {
        when(archiveMapper.selectList(any(Wrapper.class))).thenReturn(List.of(
                entity("baseline-newer", Instant.parse("2026-06-26T07:00:00Z")),
                entity("baseline-older", Instant.parse("2026-06-26T06:00:00Z"))
        ));

        var archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(EvaluationFixtureBaselineRunArchiveVo::id)
                .containsExactly("baseline-newer", "baseline-older");
        assertThat(archives.get(0).passedCaseCount()).isEqualTo(4);
        assertThat(archives.get(0).sideEffectContract()).contains("does not create tasks");
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

    private static EvaluationFixtureBaselineRunArchiveEntity entity(String id, Instant createdAt) {
        EvaluationFixtureBaselineRunArchiveEntity entity = new EvaluationFixtureBaselineRunArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setTotalCaseCount(6);
        entity.setExecutedCaseCount(4);
        entity.setPassedCaseCount(4);
        entity.setFailedCaseCount(0);
        entity.setSkippedCaseCount(2);
        entity.setCreatedAt(createdAt);
        entity.setSideEffectContract("Archive stores a local fixture baseline execution report only; it does not create tasks, call the model, mutate Git, or write to GitHub.");
        entity.setNextAction("Fixture baseline is passing; use the archived report as demo evidence for supported language adapters.");
        entity.setReport("# PatchPilot Evaluation Fixture Baseline Run");
        return entity;
    }
}
