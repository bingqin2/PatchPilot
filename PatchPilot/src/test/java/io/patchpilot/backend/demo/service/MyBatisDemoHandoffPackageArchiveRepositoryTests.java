package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoHandoffPackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoHandoffPackageArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoHandoffPackageArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoHandoffPackageArchiveRepositoryTests {

    private final DemoHandoffPackageArchiveMapper archiveMapper = mock(DemoHandoffPackageArchiveMapper.class);
    private final MyBatisDemoHandoffPackageArchiveRepository repository = new MyBatisDemoHandoffPackageArchiveRepository(archiveMapper);

    @Test
    void should_insert_handoff_package_archive() {
        when(archiveMapper.insert(any(DemoHandoffPackageArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoHandoffPackageArchiveEntity> entityCaptor = ArgumentCaptor.forClass(DemoHandoffPackageArchiveEntity.class);

        DemoHandoffPackageArchiveVo archive = archive("handoff-archive-1", "demo-session-1", Instant.parse("2026-06-24T04:00:00Z"));

        DemoHandoffPackageArchiveVo savedArchive = repository.save(archive);

        verify(archiveMapper).insert(entityCaptor.capture());
        DemoHandoffPackageArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("handoff-archive-1");
        assertThat(insertedEntity.getSessionId()).isEqualTo("demo-session-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getSummary()).isEqualTo("Demo session demo-session-1 is ready.");
        assertThat(insertedEntity.getHandoffReadinessStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getHandoffReadinessSummary()).isEqualTo("Handoff package is ready.");
        assertThat(insertedEntity.getHandoffReadinessNextAction()).isEqualTo("No missing handoff evidence.");
        assertThat(insertedEntity.getHandoffReadyCheckCount()).isEqualTo(7);
        assertThat(insertedEntity.getHandoffNeedsAttentionCheckCount()).isZero();
        assertThat(insertedEntity.getHandoffBlockedCheckCount()).isZero();
        assertThat(insertedEntity.getShareSummary()).isEqualTo("Status READY; session demo-session-1.");
        assertThat(insertedEntity.getRecentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-24T04:00:00Z"));
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Demo Handoff Package");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void should_list_recent_handoff_package_archives_newest_first() {
        DemoHandoffPackageArchiveEntity newer = entity("handoff-archive-newer", "demo-session-newer", Instant.parse("2026-06-24T05:00:00Z"));
        DemoHandoffPackageArchiveEntity older = entity("handoff-archive-older", "demo-session-older", Instant.parse("2026-06-24T04:00:00Z"));
        when(archiveMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoHandoffPackageArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoHandoffPackageArchiveVo::id)
                .containsExactly("handoff-archive-newer", "handoff-archive-older");
        verify(archiveMapper).selectList(any());
    }

    @Test
    void should_find_handoff_package_archive_by_id() {
        DemoHandoffPackageArchiveEntity entity = entity("handoff-archive-1", "demo-session-1", Instant.parse("2026-06-24T04:00:00Z"));
        when(archiveMapper.selectById("handoff-archive-1")).thenReturn(entity);
        when(archiveMapper.selectById("missing-archive")).thenReturn(null);

        assertThat(repository.findById("handoff-archive-1"))
                .map(DemoHandoffPackageArchiveVo::sessionId)
                .contains("demo-session-1");
        assertThat(repository.findById("missing-archive")).isEmpty();
    }

    private static DemoHandoffPackageArchiveVo archive(String id, String sessionId, Instant createdAt) {
        return new DemoHandoffPackageArchiveVo(
                id,
                sessionId,
                DemoReadinessStatus.READY,
                "Demo session " + sessionId + " is ready.",
                DemoReadinessStatus.READY,
                "Handoff package is ready.",
                "No missing handoff evidence.",
                7,
                0,
                0,
                "Status READY; session " + sessionId + ".",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                createdAt,
                "# PatchPilot Demo Handoff Package\n\n- Session: `" + sessionId + "`"
        );
    }

    private static DemoHandoffPackageArchiveEntity entity(String id, String sessionId, Instant createdAt) {
        DemoHandoffPackageArchiveEntity entity = new DemoHandoffPackageArchiveEntity();
        entity.setId(id);
        entity.setSessionId(sessionId);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setSummary("Demo session " + sessionId + " is ready.");
        entity.setHandoffReadinessStatus(DemoReadinessStatus.READY.name());
        entity.setHandoffReadinessSummary("Handoff package is ready.");
        entity.setHandoffReadinessNextAction("No missing handoff evidence.");
        entity.setHandoffReadyCheckCount(7);
        entity.setHandoffNeedsAttentionCheckCount(0);
        entity.setHandoffBlockedCheckCount(0);
        entity.setShareSummary("Status READY; session " + sessionId + ".");
        entity.setRecentPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot Demo Handoff Package\n\n- Session: `" + sessionId + "`");
        return entity;
    }
}
