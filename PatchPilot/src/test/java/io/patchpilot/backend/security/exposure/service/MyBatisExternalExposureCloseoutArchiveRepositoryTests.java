package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureCloseoutArchiveEntity;
import io.patchpilot.backend.security.exposure.mapper.ExternalExposureCloseoutArchiveMapper;
import io.patchpilot.backend.security.exposure.service.impl.MyBatisExternalExposureCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisExternalExposureCloseoutArchiveRepositoryTests {

    private final ExternalExposureCloseoutArchiveMapper archiveMapper = mock(ExternalExposureCloseoutArchiveMapper.class);
    private final MyBatisExternalExposureCloseoutArchiveRepository repository =
            new MyBatisExternalExposureCloseoutArchiveRepository(archiveMapper);

    @Test
    void should_insert_external_exposure_closeout_archive() {
        when(archiveMapper.insert(any(ExternalExposureCloseoutArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<ExternalExposureCloseoutArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(ExternalExposureCloseoutArchiveEntity.class);

        ExternalExposureCloseoutArchiveVo archive = archive(
                "closeout-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T18:05:00Z")
        );

        ExternalExposureCloseoutArchiveVo savedArchive = repository.save(archive);

        verify(archiveMapper).insert(entityCaptor.capture());
        ExternalExposureCloseoutArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("closeout-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getCloseoutReady()).isTrue();
        assertThat(insertedEntity.getLatestSessionId()).isEqualTo("exposure-session-1");
        assertThat(insertedEntity.getLinkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(insertedEntity.getHandoffStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getArchiveFreshness()).isEqualTo("CURRENT");
        assertThat(insertedEntity.getReadyCount()).isEqualTo(4);
        assertThat(insertedEntity.getGeneratedAt()).isEqualTo(Instant.parse("2026-07-01T18:00:00Z"));
        assertThat(insertedEntity.getArchivedAt()).isEqualTo(Instant.parse("2026-07-01T18:05:00Z"));
        assertThat(insertedEntity.getReport()).contains("# PatchPilot External Exposure Closeout");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void should_list_recent_archives_newest_first() {
        ExternalExposureCloseoutArchiveEntity newer = entity(
                "closeout-archive-newer",
                "READY",
                true,
                Instant.parse("2026-07-01T18:10:00Z")
        );
        ExternalExposureCloseoutArchiveEntity older = entity(
                "closeout-archive-older",
                "BLOCKED",
                false,
                Instant.parse("2026-07-01T18:00:00Z")
        );
        when(archiveMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<ExternalExposureCloseoutArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(ExternalExposureCloseoutArchiveVo::id)
                .containsExactly("closeout-archive-newer", "closeout-archive-older");
        assertThat(archives)
                .extracting(ExternalExposureCloseoutArchiveVo::closeoutReady)
                .containsExactly(true, false);
        verify(archiveMapper).selectList(any());
    }

    @Test
    void should_find_archive_by_id() {
        ExternalExposureCloseoutArchiveEntity entity = entity(
                "closeout-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T18:05:00Z")
        );
        when(archiveMapper.selectById("closeout-archive-1")).thenReturn(entity);
        when(archiveMapper.selectById("missing-archive")).thenReturn(null);

        assertThat(repository.findById("closeout-archive-1"))
                .map(ExternalExposureCloseoutArchiveVo::status)
                .contains("READY");
        assertThat(repository.findById("missing-archive")).isEmpty();
    }

    private static ExternalExposureCloseoutArchiveVo archive(
            String id,
            String status,
            boolean closeoutReady,
            Instant archivedAt
    ) {
        return new ExternalExposureCloseoutArchiveVo(
                id,
                status,
                closeoutReady,
                "External exposure session is closed with complete local evidence.",
                "Keep the closeout report with the demo evidence bundle.",
                "exposure-session-1",
                closeoutReady ? "CLOSED" : "ACTIVE",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T15:00:00Z"),
                closeoutReady ? "bingqin2" : null,
                closeoutReady ? Instant.parse("2026-07-01T16:30:00Z") : null,
                closeoutReady ? "Tunnel process stopped and GitHub webhook URL removed." : null,
                "exposure-archive-1",
                "READY",
                "CURRENT",
                4,
                closeoutReady ? 0 : 1,
                closeoutReady ? 0 : 1,
                4,
                List.of("Keep the closeout report with the demo evidence bundle."),
                List.of("Latest session exposure-session-1 is " + (closeoutReady ? "CLOSED." : "ACTIVE.")),
                List.of("GET /api/security/external-exposure-closeout/report/download"),
                "GET /api/security/external-exposure-closeout is read-only.",
                Instant.parse("2026-07-01T18:00:00Z"),
                archivedAt,
                "# PatchPilot External Exposure Closeout\n\n- Status: `" + status + "`"
        );
    }

    private static ExternalExposureCloseoutArchiveEntity entity(
            String id,
            String status,
            boolean closeoutReady,
            Instant archivedAt
    ) {
        ExternalExposureCloseoutArchiveEntity entity = new ExternalExposureCloseoutArchiveEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setCloseoutReady(closeoutReady);
        entity.setSummary("External exposure session is closed with complete local evidence.");
        entity.setNextAction("Keep the closeout report with the demo evidence bundle.");
        entity.setLatestSessionId("exposure-session-1");
        entity.setLatestSessionStatus(closeoutReady ? "CLOSED" : "ACTIVE");
        entity.setPublicUrl("https://demo.trycloudflare.com");
        entity.setWebhookUrl("https://demo.trycloudflare.com/api/github/webhook");
        entity.setPurpose("Live GitHub webhook smoke test");
        entity.setOperator("bingqin2");
        entity.setStartedAt(Instant.parse("2026-07-01T15:00:00Z"));
        entity.setClosedBy(closeoutReady ? "bingqin2" : null);
        entity.setClosedAt(closeoutReady ? Instant.parse("2026-07-01T16:30:00Z") : null);
        entity.setCloseNotes(closeoutReady ? "Tunnel process stopped and GitHub webhook URL removed." : null);
        entity.setLinkedReadinessArchiveId("exposure-archive-1");
        entity.setHandoffStatus("READY");
        entity.setArchiveFreshness("CURRENT");
        entity.setReadyCount(4);
        entity.setNeedsAttentionCount(closeoutReady ? 0 : 1);
        entity.setBlockedCount(closeoutReady ? 0 : 1);
        entity.setTotalCount(4);
        entity.setEvidenceNotes("Latest session exposure-session-1 is " + (closeoutReady ? "CLOSED." : "ACTIVE."));
        entity.setNextActions("Keep the closeout report with the demo evidence bundle.");
        entity.setDownloadActions("GET /api/security/external-exposure-closeout/report/download");
        entity.setSideEffectContract("GET /api/security/external-exposure-closeout is read-only.");
        entity.setGeneratedAt(Instant.parse("2026-07-01T18:00:00Z"));
        entity.setArchivedAt(archivedAt);
        entity.setReport("# PatchPilot External Exposure Closeout\n\n- Status: `" + status + "`");
        return entity;
    }
}
