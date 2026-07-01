package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureSessionEntity;
import io.patchpilot.backend.security.exposure.mapper.ExternalExposureSessionMapper;
import io.patchpilot.backend.security.exposure.service.impl.MyBatisExternalExposureSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisExternalExposureSessionRepositoryTests {

    private final ExternalExposureSessionMapper sessionMapper = mock(ExternalExposureSessionMapper.class);
    private final MyBatisExternalExposureSessionRepository repository =
            new MyBatisExternalExposureSessionRepository(sessionMapper);

    @Test
    void should_insert_or_update_external_exposure_session() {
        when(sessionMapper.insertOrUpdate(any(ExternalExposureSessionEntity.class))).thenReturn(true);
        ArgumentCaptor<ExternalExposureSessionEntity> entityCaptor =
                ArgumentCaptor.forClass(ExternalExposureSessionEntity.class);

        ExternalExposureSessionVo session = session(
                "exposure-session-1",
                "ACTIVE",
                Instant.parse("2026-07-01T15:00:00Z")
        );

        ExternalExposureSessionVo savedSession = repository.save(session);

        verify(sessionMapper).insertOrUpdate(entityCaptor.capture());
        ExternalExposureSessionEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("exposure-session-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("ACTIVE");
        assertThat(insertedEntity.getPublicUrl()).isEqualTo("https://demo.trycloudflare.com");
        assertThat(insertedEntity.getWebhookUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(insertedEntity.getLinkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(insertedEntity.getReport()).contains("# PatchPilot External Exposure Session");
        assertThat(savedSession).isEqualTo(session);
    }

    @Test
    void should_list_recent_sessions_newest_first() {
        ExternalExposureSessionEntity newer = entity(
                "exposure-session-newer",
                "ACTIVE",
                Instant.parse("2026-07-01T16:00:00Z")
        );
        ExternalExposureSessionEntity older = entity(
                "exposure-session-older",
                "CLOSED",
                Instant.parse("2026-07-01T15:00:00Z")
        );
        when(sessionMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<ExternalExposureSessionVo> sessions = repository.listRecentSessions(50);

        assertThat(sessions)
                .extracting(ExternalExposureSessionVo::id)
                .containsExactly("exposure-session-newer", "exposure-session-older");
        assertThat(sessions)
                .extracting(ExternalExposureSessionVo::status)
                .containsExactly("ACTIVE", "CLOSED");
        verify(sessionMapper).selectList(any());
    }

    @Test
    void should_find_session_by_id() {
        ExternalExposureSessionEntity entity = entity(
                "exposure-session-1",
                "ACTIVE",
                Instant.parse("2026-07-01T15:00:00Z")
        );
        when(sessionMapper.selectById("exposure-session-1")).thenReturn(entity);
        when(sessionMapper.selectById("missing-session")).thenReturn(null);

        assertThat(repository.findById("exposure-session-1"))
                .map(ExternalExposureSessionVo::publicUrl)
                .contains("https://demo.trycloudflare.com");
        assertThat(repository.findById("missing-session")).isEmpty();
    }

    private static ExternalExposureSessionVo session(String id, String status, Instant startedAt) {
        return new ExternalExposureSessionVo(
                id,
                status,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                startedAt,
                null,
                null,
                null,
                "# PatchPilot External Exposure Session"
        );
    }

    private static ExternalExposureSessionEntity entity(String id, String status, Instant startedAt) {
        ExternalExposureSessionEntity entity = new ExternalExposureSessionEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setPublicUrl("https://demo.trycloudflare.com");
        entity.setWebhookUrl("https://demo.trycloudflare.com/api/github/webhook");
        entity.setPurpose("Live GitHub webhook smoke test");
        entity.setOperator("bingqin2");
        entity.setExpectedShutdownAt(Instant.parse("2026-07-01T17:00:00Z"));
        entity.setNotes("Keep terminal visible during test.");
        entity.setLinkedHandoffStatus("READY");
        entity.setLinkedReadinessArchiveId("exposure-archive-1");
        entity.setStartedAt(startedAt);
        entity.setReport("# PatchPilot External Exposure Session");
        return entity;
    }
}
