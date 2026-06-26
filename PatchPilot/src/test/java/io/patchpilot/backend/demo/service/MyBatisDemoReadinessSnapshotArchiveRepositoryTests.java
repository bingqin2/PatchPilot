package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoReadinessSnapshotArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoReadinessSnapshotArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoReadinessSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoReadinessSnapshotArchiveRepositoryTests {

    private final DemoReadinessSnapshotArchiveMapper archiveMapper = mock(DemoReadinessSnapshotArchiveMapper.class);
    private final MyBatisDemoReadinessSnapshotArchiveRepository repository = new MyBatisDemoReadinessSnapshotArchiveRepository(archiveMapper);

    @Test
    void should_insert_readiness_snapshot_archive() {
        when(archiveMapper.insert(any(DemoReadinessSnapshotArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoReadinessSnapshotArchiveEntity> entityCaptor = ArgumentCaptor.forClass(DemoReadinessSnapshotArchiveEntity.class);

        DemoReadinessSnapshotArchiveVo archive = archive("snapshot-1", DemoReadinessStatus.BLOCKED, Instant.parse("2026-06-27T04:00:00Z"));

        DemoReadinessSnapshotArchiveVo savedArchive = repository.save(archive);

        verify(archiveMapper).insert(entityCaptor.capture());
        DemoReadinessSnapshotArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("snapshot-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("BLOCKED");
        assertThat(insertedEntity.getSummary()).isEqualTo("Readiness is blocked.");
        assertThat(insertedEntity.getReadyCheckCount()).isEqualTo(2);
        assertThat(insertedEntity.getNeedsAttentionCheckCount()).isEqualTo(1);
        assertThat(insertedEntity.getBlockedCheckCount()).isEqualTo(1);
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-27T04:00:00Z"));
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Demo Readiness Snapshot");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void should_list_recent_readiness_snapshot_archives_newest_first() {
        DemoReadinessSnapshotArchiveEntity newer = entity("snapshot-newer", DemoReadinessStatus.READY, Instant.parse("2026-06-27T05:00:00Z"));
        DemoReadinessSnapshotArchiveEntity older = entity("snapshot-older", DemoReadinessStatus.BLOCKED, Instant.parse("2026-06-27T04:00:00Z"));
        when(archiveMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoReadinessSnapshotArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoReadinessSnapshotArchiveVo::id)
                .containsExactly("snapshot-newer", "snapshot-older");
        verify(archiveMapper).selectList(any());
    }

    @Test
    void should_find_readiness_snapshot_archive_by_id() {
        DemoReadinessSnapshotArchiveEntity entity = entity("snapshot-1", DemoReadinessStatus.READY, Instant.parse("2026-06-27T04:00:00Z"));
        when(archiveMapper.selectById("snapshot-1")).thenReturn(entity);
        when(archiveMapper.selectById("missing-snapshot")).thenReturn(null);

        assertThat(repository.findById("snapshot-1"))
                .map(DemoReadinessSnapshotArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing-snapshot")).isEmpty();
    }

    private static DemoReadinessSnapshotArchiveVo archive(String id, DemoReadinessStatus status, Instant createdAt) {
        return new DemoReadinessSnapshotArchiveVo(
                id,
                status,
                status == DemoReadinessStatus.READY ? "Readiness is ready." : "Readiness is blocked.",
                2,
                1,
                status == DemoReadinessStatus.BLOCKED ? 1 : 0,
                createdAt,
                "# PatchPilot Demo Readiness Snapshot\n\n- Status: `" + status + "`"
        );
    }

    private static DemoReadinessSnapshotArchiveEntity entity(String id, DemoReadinessStatus status, Instant createdAt) {
        DemoReadinessSnapshotArchiveEntity entity = new DemoReadinessSnapshotArchiveEntity();
        entity.setId(id);
        entity.setStatus(status.name());
        entity.setSummary(status == DemoReadinessStatus.READY ? "Readiness is ready." : "Readiness is blocked.");
        entity.setReadyCheckCount(2);
        entity.setNeedsAttentionCheckCount(1);
        entity.setBlockedCheckCount(status == DemoReadinessStatus.BLOCKED ? 1 : 0);
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot Demo Readiness Snapshot\n\n- Status: `" + status + "`");
        return entity;
    }
}
