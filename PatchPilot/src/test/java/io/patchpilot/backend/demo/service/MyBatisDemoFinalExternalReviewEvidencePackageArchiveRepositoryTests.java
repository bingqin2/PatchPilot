package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewEvidencePackageArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests {

    private final DemoFinalExternalReviewEvidencePackageArchiveMapper mapper =
            mock(DemoFinalExternalReviewEvidencePackageArchiveMapper.class);
    private final MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepository repository =
            new MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepository(mapper);

    @Test
    void inserts_final_external_review_package_archive() {
        when(mapper.insert(any(DemoFinalExternalReviewEvidencePackageArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalExternalReviewEvidencePackageArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalExternalReviewEvidencePackageArchiveEntity.class);
        DemoFinalExternalReviewEvidencePackageArchiveVo archive = archive(
                "final-external-review-package-archive-1",
                Instant.parse("2026-06-29T08:30:00Z")
        );

        DemoFinalExternalReviewEvidencePackageArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalExternalReviewEvidencePackageArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getReadyForExternalReview()).isTrue();
        assertThat(insertedEntity.getLatestTaskId()).isEqualTo("task-2");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getCompletionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(insertedEntity.getEvidenceNotesJson()).contains("Frozen closeout archive");
        assertThat(insertedEntity.getDownloadActionsJson()).contains("Download final external-review evidence package.");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_package_archives_newest_first() {
        DemoFinalExternalReviewEvidencePackageArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-29T08:31:00Z"));
        DemoFinalExternalReviewEvidencePackageArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-29T08:30:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalExternalReviewEvidencePackageArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalExternalReviewEvidencePackageArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_package_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-29T08:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalExternalReviewEvidencePackageArchiveVo::closeoutArchiveId)
                .contains("final-acceptance-completion-closeout-archive-1");
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T07:45:00Z",
                "FRESH",
                Instant.parse("2026-06-29T07:50:00Z"),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of("Download final external-review evidence package."),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package",
                Instant.parse("2026-06-29T08:00:00Z"),
                archivedAt
        );
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveEntity entity(String id, Instant archivedAt) {
        DemoFinalExternalReviewEvidencePackageArchiveEntity entity =
                new DemoFinalExternalReviewEvidencePackageArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setReadyForExternalReview(true);
        entity.setSummary("PatchPilot final external-review evidence package is ready.");
        entity.setNextAction("Share this package with reviewers as the frozen external-review record.");
        entity.setLatestTaskId("task-2");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setFinalAcceptanceSharePackageArchiveId("final-acceptance-share-package-archive-1");
        entity.setCompletionArchiveId("final-acceptance-completion-archive-1");
        entity.setCompletionEvidenceDeliveryReceiptId("final-acceptance-completion-evidence-delivery-receipt-1");
        entity.setCloseoutArchiveId("final-acceptance-completion-closeout-archive-1");
        entity.setDeliveryTarget("reviewer@example.com");
        entity.setDeliveryChannel("email");
        entity.setDeliveredAt("2026-06-29T07:45:00Z");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setCloseoutArchivedAt(Instant.parse("2026-06-29T07:50:00Z"));
        entity.setEvidenceNotesJson("[\"Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed.\"]");
        entity.setDownloadActionsJson("[\"Download final external-review evidence package.\"]");
        entity.setSideEffectContract("GET /api/demo/final-external-review-evidence-package is read-only.");
        entity.setReport("# PatchPilot Final External Review Evidence Package");
        entity.setGeneratedAt(Instant.parse("2026-06-29T08:00:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
