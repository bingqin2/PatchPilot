package io.patchpilot.backend.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunSnapshotArchiveEntity;
import io.patchpilot.backend.evaluation.mapper.EvaluationRunSnapshotArchiveMapper;
import io.patchpilot.backend.evaluation.service.impl.MyBatisEvaluationRunSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisEvaluationRunSnapshotArchiveRepositoryTests {

    private final EvaluationRunSnapshotArchiveMapper archiveMapper = mock(EvaluationRunSnapshotArchiveMapper.class);
    private final MyBatisEvaluationRunSnapshotArchiveRepository repository = new MyBatisEvaluationRunSnapshotArchiveRepository(archiveMapper);

    @Test
    void should_insert_archive_entity_with_joined_list_fields() {
        when(archiveMapper.insert(any(EvaluationRunSnapshotArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<EvaluationRunSnapshotArchiveEntity> captor = ArgumentCaptor.forClass(EvaluationRunSnapshotArchiveEntity.class);
        EvaluationRunSnapshotArchiveVo archive = archive("snapshot-1", Instant.parse("2026-06-26T04:00:00Z"));

        EvaluationRunSnapshotArchiveVo saved = repository.save(archive);

        assertThat(saved).isEqualTo(archive);
        verify(archiveMapper).insert(captor.capture());
        EvaluationRunSnapshotArchiveEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo("snapshot-1");
        assertThat(entity.getPreviewRunId()).isEqualTo("preview-current-catalog");
        assertThat(entity.getCoveredLanguages()).isEqualTo("go,java,node,python");
        assertThat(entity.getCoveredBuildSystems()).isEqualTo("go,maven,npm,pytest");
        assertThat(entity.getExpectedVerificationCommands()).isEqualTo("go test ./...\nmvn test\nnpm test\npython3 -m pytest");
        assertThat(entity.getSafetyRejectionCategories()).isEqualTo("DANGEROUS_INSTRUCTION,NOT_ACTIONABLE");
        assertThat(entity.getReport()).contains("# PatchPilot Evaluation Run Snapshot");
    }

    @Test
    void should_list_recent_archive_entities_as_value_objects() {
        EvaluationRunSnapshotArchiveEntity newer = entity("snapshot-newer", Instant.parse("2026-06-26T05:00:00Z"));
        EvaluationRunSnapshotArchiveEntity older = entity("snapshot-older", Instant.parse("2026-06-26T04:00:00Z"));
        when(archiveMapper.selectList(any(Wrapper.class))).thenReturn(List.of(newer, older));

        List<EvaluationRunSnapshotArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(EvaluationRunSnapshotArchiveVo::id)
                .containsExactly("snapshot-newer", "snapshot-older");
        assertThat(archives.get(0).expectedVerificationCommands()).containsExactly("go test ./...", "mvn test");
        assertThat(archives.get(0).coveredLanguages()).containsExactly("go", "java");
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

    private static EvaluationRunSnapshotArchiveEntity entity(String id, Instant createdAt) {
        EvaluationRunSnapshotArchiveEntity entity = new EvaluationRunSnapshotArchiveEntity();
        entity.setId(id);
        entity.setPreviewRunId("preview-current-catalog");
        entity.setTitle("Evaluation run preview");
        entity.setStatus("READY");
        entity.setCaseCount(6);
        entity.setSupportedFixCaseCount(4);
        entity.setSafetyRejectionCaseCount(2);
        entity.setCoveredLanguages("go,java");
        entity.setCoveredBuildSystems("go,maven");
        entity.setExpectedVerificationCommands("go test ./...\nmvn test");
        entity.setSafetyRejectionCategories("DANGEROUS_INSTRUCTION,NOT_ACTIONABLE");
        entity.setCreatedAt(createdAt);
        entity.setSideEffectContract("Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.");
        entity.setReport("# PatchPilot Evaluation Run Snapshot");
        return entity;
    }
}
