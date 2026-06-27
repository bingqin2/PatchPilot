package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoSelfHostedLaunchReadinessArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoSelfHostedLaunchReadinessArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoSelfHostedLaunchReadinessArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoSelfHostedLaunchReadinessArchiveRepositoryTests {

    private final DemoSelfHostedLaunchReadinessArchiveMapper archiveMapper = mock(DemoSelfHostedLaunchReadinessArchiveMapper.class);
    private final MyBatisDemoSelfHostedLaunchReadinessArchiveRepository repository =
            new MyBatisDemoSelfHostedLaunchReadinessArchiveRepository(archiveMapper);

    @Test
    void should_insert_self_hosted_launch_readiness_archive() {
        when(archiveMapper.insert(any(DemoSelfHostedLaunchReadinessArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoSelfHostedLaunchReadinessArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoSelfHostedLaunchReadinessArchiveEntity.class);

        DemoSelfHostedLaunchReadinessArchiveVo archive = archive(
                "launch-archive-1",
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                Instant.parse("2026-06-28T01:30:00Z")
        );

        DemoSelfHostedLaunchReadinessArchiveVo savedArchive = repository.save(archive);

        verify(archiveMapper).insert(entityCaptor.capture());
        DemoSelfHostedLaunchReadinessArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("launch-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("NEEDS_ATTENTION");
        assertThat(insertedEntity.getReadyToLaunch()).isFalse();
        assertThat(insertedEntity.getSummary()).isEqualTo("Launch needs attention.");
        assertThat(insertedEntity.getReadyCheckCount()).isEqualTo(3);
        assertThat(insertedEntity.getNeedsAttentionCheckCount()).isEqualTo(1);
        assertThat(insertedEntity.getBlockedCheckCount()).isZero();
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-28T01:30:00Z"));
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Self-Hosted Launch Readiness");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void should_list_recent_archives_newest_first() {
        DemoSelfHostedLaunchReadinessArchiveEntity newer = entity(
                "launch-archive-newer",
                DemoReadinessStatus.READY,
                true,
                Instant.parse("2026-06-28T02:00:00Z")
        );
        DemoSelfHostedLaunchReadinessArchiveEntity older = entity(
                "launch-archive-older",
                DemoReadinessStatus.BLOCKED,
                false,
                Instant.parse("2026-06-28T01:00:00Z")
        );
        when(archiveMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoSelfHostedLaunchReadinessArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoSelfHostedLaunchReadinessArchiveVo::id)
                .containsExactly("launch-archive-newer", "launch-archive-older");
        assertThat(archives)
                .extracting(DemoSelfHostedLaunchReadinessArchiveVo::readyToLaunch)
                .containsExactly(true, false);
        verify(archiveMapper).selectList(any());
    }

    @Test
    void should_find_archive_by_id() {
        DemoSelfHostedLaunchReadinessArchiveEntity entity = entity(
                "launch-archive-1",
                DemoReadinessStatus.READY,
                true,
                Instant.parse("2026-06-28T02:00:00Z")
        );
        when(archiveMapper.selectById("launch-archive-1")).thenReturn(entity);
        when(archiveMapper.selectById("missing-archive")).thenReturn(null);

        assertThat(repository.findById("launch-archive-1"))
                .map(DemoSelfHostedLaunchReadinessArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing-archive")).isEmpty();
    }

    private static DemoSelfHostedLaunchReadinessArchiveVo archive(
            String id,
            DemoReadinessStatus status,
            boolean readyToLaunch,
            Instant createdAt
    ) {
        return new DemoSelfHostedLaunchReadinessArchiveVo(
                id,
                status,
                readyToLaunch,
                "Launch needs attention.",
                3,
                1,
                0,
                createdAt,
                "# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `" + status + "`"
        );
    }

    private static DemoSelfHostedLaunchReadinessArchiveEntity entity(
            String id,
            DemoReadinessStatus status,
            boolean readyToLaunch,
            Instant createdAt
    ) {
        DemoSelfHostedLaunchReadinessArchiveEntity entity = new DemoSelfHostedLaunchReadinessArchiveEntity();
        entity.setId(id);
        entity.setStatus(status.name());
        entity.setReadyToLaunch(readyToLaunch);
        entity.setSummary("Launch needs attention.");
        entity.setReadyCheckCount(3);
        entity.setNeedsAttentionCheckCount(1);
        entity.setBlockedCheckCount(0);
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `" + status + "`");
        return entity;
    }
}
