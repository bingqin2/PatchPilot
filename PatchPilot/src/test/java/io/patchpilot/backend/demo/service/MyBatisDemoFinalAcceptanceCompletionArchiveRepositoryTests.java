package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceCompletionArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalAcceptanceCompletionArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalAcceptanceCompletionArchiveRepositoryTests {

    private final DemoFinalAcceptanceCompletionArchiveMapper mapper =
            mock(DemoFinalAcceptanceCompletionArchiveMapper.class);
    private final MyBatisDemoFinalAcceptanceCompletionArchiveRepository repository =
            new MyBatisDemoFinalAcceptanceCompletionArchiveRepository(mapper);

    @Test
    void inserts_final_acceptance_completion_archive() {
        when(mapper.insert(any(DemoFinalAcceptanceCompletionArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalAcceptanceCompletionArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalAcceptanceCompletionArchiveEntity.class);
        DemoFinalAcceptanceCompletionArchiveVo archive = archive(
                "final-acceptance-completion-archive-1",
                Instant.parse("2026-06-29T04:00:00Z")
        );

        DemoFinalAcceptanceCompletionArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalAcceptanceCompletionArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getFinalized()).isTrue();
        assertThat(insertedEntity.getLatestArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(insertedEntity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(insertedEntity.getLatestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(insertedEntity.getLatestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(insertedEntity.getLatestDeliveryChannel()).isEqualTo("email");
        assertThat(insertedEntity.getDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(insertedEntity.getEvidenceNotesJson()).contains("send-ready");
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Final Demo Acceptance Share Finalization Gate");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_archives_newest_first() {
        DemoFinalAcceptanceCompletionArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-29T04:01:00Z"));
        DemoFinalAcceptanceCompletionArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-29T04:00:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalAcceptanceCompletionArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalAcceptanceCompletionArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-29T04:00:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalAcceptanceCompletionArchiveVo::latestDeliveryReceiptId)
                .contains("final-acceptance-delivery-receipt-1");
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalAcceptanceCompletionArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "Final demo acceptance share package is finalized with a fresh delivery receipt.",
                "Use the finalization report as the external-review acceptance delivery record.",
                "final-acceptance-share-package-archive-1",
                "task-1",
                "final-acceptance-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T03:05:00Z",
                "FRESH",
                true,
                "Latest delivery receipt matches the current final acceptance share package archive.",
                List.of("Latest final acceptance share package archive is send-ready."),
                "# PatchPilot Final Demo Acceptance Share Finalization Gate",
                Instant.parse("2026-06-29T03:30:00Z"),
                archivedAt
        );
    }

    private static DemoFinalAcceptanceCompletionArchiveEntity entity(String id, Instant archivedAt) {
        DemoFinalAcceptanceCompletionArchiveEntity entity = new DemoFinalAcceptanceCompletionArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setFinalized(true);
        entity.setSummary("Final demo acceptance share package is finalized with a fresh delivery receipt.");
        entity.setNextAction("Use the finalization report as the external-review acceptance delivery record.");
        entity.setLatestArchiveId("final-acceptance-share-package-archive-1");
        entity.setLatestTaskId("task-1");
        entity.setLatestDeliveryReceiptId("final-acceptance-delivery-receipt-1");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setLatestDeliveredAt("2026-06-29T03:05:00Z");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setDeliveryReceiptFresh(true);
        entity.setDeliveryReceiptFreshnessSummary("Latest delivery receipt matches the current final acceptance share package archive.");
        entity.setEvidenceNotesJson("[\"Latest final acceptance share package archive is send-ready.\"]");
        entity.setReport("# PatchPilot Final Demo Acceptance Share Finalization Gate");
        entity.setGeneratedAt(Instant.parse("2026-06-29T03:30:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
