package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureReadinessArchiveEntity;
import io.patchpilot.backend.security.exposure.mapper.ExternalExposureReadinessArchiveMapper;
import io.patchpilot.backend.security.exposure.service.impl.MyBatisExternalExposureReadinessArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisExternalExposureReadinessArchiveRepositoryTests {

    private final ExternalExposureReadinessArchiveMapper archiveMapper = mock(ExternalExposureReadinessArchiveMapper.class);
    private final MyBatisExternalExposureReadinessArchiveRepository repository =
            new MyBatisExternalExposureReadinessArchiveRepository(archiveMapper);

    @Test
    void should_insert_external_exposure_readiness_archive() {
        when(archiveMapper.insert(any(ExternalExposureReadinessArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<ExternalExposureReadinessArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(ExternalExposureReadinessArchiveEntity.class);

        ExternalExposureReadinessArchiveVo archive = archive(
                "exposure-archive-1",
                "NEEDS_ATTENTION",
                false,
                Instant.parse("2026-07-01T13:30:00Z")
        );

        ExternalExposureReadinessArchiveVo savedArchive = repository.save(archive);

        verify(archiveMapper).insert(entityCaptor.capture());
        ExternalExposureReadinessArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("exposure-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("NEEDS_ATTENTION");
        assertThat(insertedEntity.getSafeToExpose()).isFalse();
        assertThat(insertedEntity.getSummary()).isEqualTo("PatchPilot needs more safeguards before public exposure.");
        assertThat(insertedEntity.getReadyCount()).isEqualTo(1);
        assertThat(insertedEntity.getNeedsAttentionCount()).isEqualTo(1);
        assertThat(insertedEntity.getBlockedCount()).isZero();
        assertThat(insertedEntity.getTotalCount()).isEqualTo(2);
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-07-01T13:30:00Z"));
        assertThat(insertedEntity.getReport()).contains("# PatchPilot External Exposure Readiness");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void should_list_recent_archives_newest_first() {
        ExternalExposureReadinessArchiveEntity newer = entity(
                "exposure-archive-newer",
                "READY",
                true,
                Instant.parse("2026-07-01T14:00:00Z")
        );
        ExternalExposureReadinessArchiveEntity older = entity(
                "exposure-archive-older",
                "BLOCKED",
                false,
                Instant.parse("2026-07-01T13:00:00Z")
        );
        when(archiveMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<ExternalExposureReadinessArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(ExternalExposureReadinessArchiveVo::id)
                .containsExactly("exposure-archive-newer", "exposure-archive-older");
        assertThat(archives)
                .extracting(ExternalExposureReadinessArchiveVo::safeToExpose)
                .containsExactly(true, false);
        verify(archiveMapper).selectList(any());
    }

    @Test
    void should_find_archive_by_id() {
        ExternalExposureReadinessArchiveEntity entity = entity(
                "exposure-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T14:00:00Z")
        );
        when(archiveMapper.selectById("exposure-archive-1")).thenReturn(entity);
        when(archiveMapper.selectById("missing-archive")).thenReturn(null);

        assertThat(repository.findById("exposure-archive-1"))
                .map(ExternalExposureReadinessArchiveVo::status)
                .contains("READY");
        assertThat(repository.findById("missing-archive")).isEmpty();
    }

    private static ExternalExposureReadinessArchiveVo archive(
            String id,
            String status,
            boolean safeToExpose,
            Instant createdAt
    ) {
        return new ExternalExposureReadinessArchiveVo(
                id,
                status,
                safeToExpose,
                "PatchPilot needs more safeguards before public exposure.",
                1,
                1,
                0,
                2,
                createdAt,
                "# PatchPilot External Exposure Readiness\n\n- Status: `" + status + "`"
        );
    }

    private static ExternalExposureReadinessArchiveEntity entity(
            String id,
            String status,
            boolean safeToExpose,
            Instant createdAt
    ) {
        ExternalExposureReadinessArchiveEntity entity = new ExternalExposureReadinessArchiveEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setSafeToExpose(safeToExpose);
        entity.setSummary("PatchPilot needs more safeguards before public exposure.");
        entity.setReadyCount(1);
        entity.setNeedsAttentionCount(1);
        entity.setBlockedCount(0);
        entity.setTotalCount(2);
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot External Exposure Readiness\n\n- Status: `" + status + "`");
        return entity;
    }
}
