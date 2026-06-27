package io.patchpilot.backend.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunArchiveEntity;
import io.patchpilot.backend.evaluation.mapper.EvaluationRunArchiveMapper;
import io.patchpilot.backend.evaluation.service.impl.MyBatisEvaluationRunArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisEvaluationRunArchiveRepositoryTests {

    private final EvaluationRunArchiveMapper archiveMapper = mock(EvaluationRunArchiveMapper.class);
    private final MyBatisEvaluationRunArchiveRepository repository = new MyBatisEvaluationRunArchiveRepository(archiveMapper);

    @Test
    void should_insert_archive_entity_with_joined_coverage_fields() {
        when(archiveMapper.insert(any(EvaluationRunArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<EvaluationRunArchiveEntity> captor = ArgumentCaptor.forClass(EvaluationRunArchiveEntity.class);
        EvaluationRunArchiveVo archive = archive("evaluation-run-1", Instant.parse("2026-06-28T04:00:00Z"));

        EvaluationRunArchiveVo saved = repository.save(archive);

        assertThat(saved).isEqualTo(archive);
        verify(archiveMapper).insert(captor.capture());
        EvaluationRunArchiveEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo("evaluation-run-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getTotalCaseCount()).isEqualTo(6);
        assertThat(entity.getSupportedFixCaseCount()).isEqualTo(4);
        assertThat(entity.getSafetyRejectionCaseCount()).isEqualTo(2);
        assertThat(entity.getExecutedFixCaseCount()).isEqualTo(4);
        assertThat(entity.getPassedFixCaseCount()).isEqualTo(4);
        assertThat(entity.getFailedFixCaseCount()).isZero();
        assertThat(entity.getSkippedCaseCount()).isEqualTo(2);
        assertThat(entity.getCoveredLanguages()).isEqualTo("go,java,node,python");
        assertThat(entity.getCoveredBuildSystems()).isEqualTo("go,maven,npm,pytest");
        assertThat(entity.getSafetyRejectionCategories()).isEqualTo("DANGEROUS_INSTRUCTION,NOT_ACTIONABLE");
        assertThat(entity.getReport()).contains("# PatchPilot Evaluation Run");
    }

    @Test
    void should_list_recent_archive_entities_as_value_objects() {
        EvaluationRunArchiveEntity newer = entity("evaluation-run-newer", Instant.parse("2026-06-28T05:00:00Z"));
        EvaluationRunArchiveEntity older = entity("evaluation-run-older", Instant.parse("2026-06-28T04:00:00Z"));
        when(archiveMapper.selectList(any(Wrapper.class))).thenReturn(List.of(newer, older));

        List<EvaluationRunArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(EvaluationRunArchiveVo::id)
                .containsExactly("evaluation-run-newer", "evaluation-run-older");
        assertThat(archives.get(0).coveredLanguages()).containsExactly("go", "java");
        assertThat(archives.get(0).coveredBuildSystems()).containsExactly("go", "maven");
        assertThat(archives.get(0).safetyRejectionCategories()).containsExactly("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE");
    }

    private static EvaluationRunArchiveVo archive(String id, Instant createdAt) {
        return new EvaluationRunArchiveVo(
                id,
                "READY",
                6,
                4,
                2,
                4,
                4,
                0,
                2,
                List.of("go", "java", "node", "python"),
                List.of("go", "maven", "npm", "pytest"),
                List.of("DANGEROUS_INSTRUCTION", "NOT_ACTIONABLE"),
                createdAt,
                "Evaluation run executes local checked-in fixture verification commands and records safety coverage only; it does not create tasks, call the model, clone repositories, mutate Git, or write to GitHub.",
                "Evaluation run passed; use the archived report as measurable demo evidence for supported adapters and safety rejections.",
                "# PatchPilot Evaluation Run"
        );
    }

    private static EvaluationRunArchiveEntity entity(String id, Instant createdAt) {
        EvaluationRunArchiveEntity entity = new EvaluationRunArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setTotalCaseCount(6);
        entity.setSupportedFixCaseCount(4);
        entity.setSafetyRejectionCaseCount(2);
        entity.setExecutedFixCaseCount(4);
        entity.setPassedFixCaseCount(4);
        entity.setFailedFixCaseCount(0);
        entity.setSkippedCaseCount(2);
        entity.setCoveredLanguages("go,java");
        entity.setCoveredBuildSystems("go,maven");
        entity.setSafetyRejectionCategories("DANGEROUS_INSTRUCTION,NOT_ACTIONABLE");
        entity.setCreatedAt(createdAt);
        entity.setSideEffectContract("Evaluation run executes local checked-in fixture verification commands and records safety coverage only; it does not create tasks, call the model, clone repositories, mutate Git, or write to GitHub.");
        entity.setNextAction("Evaluation run passed; use the archived report as measurable demo evidence for supported adapters and safety rejections.");
        entity.setReport("# PatchPilot Evaluation Run");
        return entity;
    }
}
