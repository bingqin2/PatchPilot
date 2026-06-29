package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionCloseoutArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceCompletionCloseoutArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalAcceptanceCompletionCloseoutArchiveRepositoryTests {

    private final DemoFinalAcceptanceCompletionCloseoutArchiveMapper mapper =
            mock(DemoFinalAcceptanceCompletionCloseoutArchiveMapper.class);
    private final MyBatisDemoFinalAcceptanceCompletionCloseoutArchiveRepository repository =
            new MyBatisDemoFinalAcceptanceCompletionCloseoutArchiveRepository(mapper);

    @Test
    void inserts_final_acceptance_completion_closeout_archive() {
        when(mapper.insert(any(DemoFinalAcceptanceCompletionCloseoutArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalAcceptanceCompletionCloseoutArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalAcceptanceCompletionCloseoutArchiveEntity.class);
        DemoFinalAcceptanceCompletionCloseoutArchiveVo archive = archive(
                "final-acceptance-completion-closeout-archive-1",
                Instant.parse("2026-06-29T06:30:00Z")
        );

        DemoFinalAcceptanceCompletionCloseoutArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalAcceptanceCompletionCloseoutArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getClosed()).isTrue();
        assertThat(insertedEntity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(insertedEntity.getLatestSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(insertedEntity.getLatestCompletionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(insertedEntity.getLatestCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(insertedEntity.getEvidenceNotesJson()).contains("Final demo acceptance summary is accepted.");
        assertThat(insertedEntity.getDownloadActionsJson()).contains("Download final acceptance completion closeout report.");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_closeout_archives_newest_first() {
        DemoFinalAcceptanceCompletionCloseoutArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-29T06:31:00Z"));
        DemoFinalAcceptanceCompletionCloseoutArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-29T06:30:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalAcceptanceCompletionCloseoutArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalAcceptanceCompletionCloseoutArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_closeout_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-29T06:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalAcceptanceCompletionCloseoutArchiveVo::latestCompletionEvidenceDeliveryReceiptId)
                .contains("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.",
                "Use this closeout report as the final external-review completion record.",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T04:25:00Z",
                "FRESH",
                List.of("Final demo acceptance summary is accepted."),
                List.of("Download final acceptance completion closeout report."),
                "GET /api/demo/final-acceptance-completion-closeout is read-only.",
                "# PatchPilot Final Acceptance Completion Closeout",
                Instant.parse("2026-06-29T06:00:00Z"),
                archivedAt
        );
    }

    private static DemoFinalAcceptanceCompletionCloseoutArchiveEntity entity(String id, Instant archivedAt) {
        DemoFinalAcceptanceCompletionCloseoutArchiveEntity entity =
                new DemoFinalAcceptanceCompletionCloseoutArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setClosed(true);
        entity.setSummary("PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.");
        entity.setNextAction("Use this closeout report as the final external-review completion record.");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/8");
        entity.setLatestSharePackageArchiveId("final-acceptance-share-package-archive-1");
        entity.setLatestCompletionArchiveId("final-acceptance-completion-archive-1");
        entity.setLatestCompletionEvidenceDeliveryReceiptId("final-acceptance-completion-evidence-delivery-receipt-1");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setLatestDeliveredAt("2026-06-29T04:25:00Z");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setEvidenceNotesJson("[\"Final demo acceptance summary is accepted.\"]");
        entity.setDownloadActionsJson("[\"Download final acceptance completion closeout report.\"]");
        entity.setSideEffectContract("GET /api/demo/final-acceptance-completion-closeout is read-only.");
        entity.setReport("# PatchPilot Final Acceptance Completion Closeout");
        entity.setGeneratedAt(Instant.parse("2026-06-29T06:00:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
