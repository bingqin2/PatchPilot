package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoSessionArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoSessionArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoSessionArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoSessionArchiveRepositoryTests {

    private final DemoSessionArchiveMapper archiveMapper = mock(DemoSessionArchiveMapper.class);
    private final MyBatisDemoSessionArchiveRepository repository = new MyBatisDemoSessionArchiveRepository(archiveMapper);

    @Test
    void should_insert_archive() {
        when(archiveMapper.insert(any(DemoSessionArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoSessionArchiveEntity> entityCaptor = ArgumentCaptor.forClass(DemoSessionArchiveEntity.class);

        DemoSessionArchiveVo archive = archive("archive-1", "demo-session-1", Instant.parse("2026-06-24T04:00:00Z"));

        DemoSessionArchiveVo savedArchive = repository.save(archive);

        verify(archiveMapper).insert(entityCaptor.capture());
        DemoSessionArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("archive-1");
        assertThat(insertedEntity.getSessionId()).isEqualTo("demo-session-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getSummary()).isEqualTo("Demo session demo-session-1 is ready.");
        assertThat(insertedEntity.getShareSummary()).isEqualTo("Status READY; session demo-session-1.");
        assertThat(insertedEntity.getRecentPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(Instant.parse("2026-06-24T04:00:00Z"));
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Demo Session Report");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void should_list_recent_archives_newest_first() {
        DemoSessionArchiveEntity newer = entity("archive-newer", "demo-session-newer", Instant.parse("2026-06-24T05:00:00Z"));
        DemoSessionArchiveEntity older = entity("archive-older", "demo-session-older", Instant.parse("2026-06-24T04:00:00Z"));
        when(archiveMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoSessionArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoSessionArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(archiveMapper).selectList(any());
    }

    @Test
    void should_find_archive_by_id() {
        DemoSessionArchiveEntity entity = entity("archive-1", "demo-session-1", Instant.parse("2026-06-24T04:00:00Z"));
        when(archiveMapper.selectById("archive-1")).thenReturn(entity);
        when(archiveMapper.selectById("missing-archive")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoSessionArchiveVo::sessionId)
                .contains("demo-session-1");
        assertThat(repository.findById("missing-archive")).isEmpty();
    }

    private static DemoSessionArchiveVo archive(String id, String sessionId, Instant createdAt) {
        return new DemoSessionArchiveVo(
                id,
                sessionId,
                DemoReadinessStatus.READY,
                "Demo session " + sessionId + " is ready.",
                "Status READY; session " + sessionId + ".",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                createdAt,
                "# PatchPilot Demo Session Report\n\n- Session: `" + sessionId + "`"
        );
    }

    private static DemoSessionArchiveEntity entity(String id, String sessionId, Instant createdAt) {
        DemoSessionArchiveEntity entity = new DemoSessionArchiveEntity();
        entity.setId(id);
        entity.setSessionId(sessionId);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setSummary("Demo session " + sessionId + " is ready.");
        entity.setShareSummary("Status READY; session " + sessionId + ".");
        entity.setRecentPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot Demo Session Report\n\n- Session: `" + sessionId + "`");
        return entity;
    }
}
