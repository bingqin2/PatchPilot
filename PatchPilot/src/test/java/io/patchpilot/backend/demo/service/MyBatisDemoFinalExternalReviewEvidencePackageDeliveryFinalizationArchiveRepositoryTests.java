package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepositoryTests {

    private final DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveMapper mapper =
            mock(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveMapper.class);
    private final MyBatisDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository repository =
            new MyBatisDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository(mapper);

    @Test
    void inserts_final_external_review_package_delivery_finalization_archive() {
        when(mapper.insert(any(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity.class)))
                .thenReturn(1);
        ArgumentCaptor<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity.class);
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive =
                archive("final-external-review-package-delivery-finalization-archive-1",
                        Instant.parse("2026-06-29T10:30:00Z"));

        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity insertedEntity =
                entityCaptor.getValue();
        assertThat(insertedEntity.getId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getFinalized()).isTrue();
        assertThat(insertedEntity.getLatestArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(insertedEntity.getLatestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(insertedEntity.getChecksJson()).contains("Frozen final external-review package");
        assertThat(insertedEntity.getEvidenceNotesJson()).contains("Frozen final external-review package");
        assertThat(insertedEntity.getDownloadActionsJson())
                .contains("Download final external-review package delivery finalization report.");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_delivery_finalization_archives_newest_first() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-29T10:31:00Z"));
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-29T10:30:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> archives =
                repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_delivery_finalization_archive_by_id() {
        when(mapper.selectById("archive-1"))
                .thenReturn(entity("archive-1", Instant.parse("2026-06-29T10:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo::latestDeliveryReceiptId)
                .contains("final-external-review-package-delivery-receipt-1");
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive(
            String id,
            Instant archivedAt
    ) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery is finalized with a fresh package delivery receipt.",
                "Use the finalization report as proof that the frozen external-review package was delivered.",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check(
                        "Frozen final external-review package",
                        DemoReadinessStatus.READY,
                        "Frozen final external-review package is ready.",
                        "No action needed."
                )),
                List.of("Frozen final external-review package final-external-review-package-archive-1 is ready."),
                List.of("Download final external-review package delivery finalization report."),
                "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.parse("2026-06-29T10:00:00Z"),
                archivedAt
        );
    }

    private static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity entity(
            String id,
            Instant archivedAt
    ) {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity entity =
                new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setFinalized(true);
        entity.setSummary("Final external-review package delivery is finalized with a fresh package delivery receipt.");
        entity.setNextAction("Use the finalization report as proof that the frozen external-review package was delivered.");
        entity.setLatestArchiveId("final-external-review-package-archive-1");
        entity.setLatestDeliveryReceiptId("final-external-review-package-delivery-receipt-1");
        entity.setLatestCloseoutArchiveId("final-acceptance-completion-closeout-archive-1");
        entity.setLatestCompletionArchiveId("final-acceptance-completion-archive-1");
        entity.setLatestCompletionEvidenceDeliveryReceiptId("final-acceptance-completion-evidence-delivery-receipt-1");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/8");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setLatestDeliveredAt("2026-06-29T09:25:00Z");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setDeliveryReceiptFresh(true);
        entity.setDeliveryReceiptFreshnessSummary(
                "Latest package delivery receipt matches the current frozen final external-review package."
        );
        entity.setChecksJson("[{\"name\":\"Frozen final external-review package\",\"status\":\"READY\",\"summary\":\"Frozen final external-review package is ready.\",\"nextAction\":\"No action needed.\"}]");
        entity.setEvidenceNotesJson(
                "[\"Frozen final external-review package final-external-review-package-archive-1 is ready.\"]"
        );
        entity.setDownloadActionsJson(
                "[\"Download final external-review package delivery finalization report.\"]"
        );
        entity.setSideEffectContract(
                "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only."
        );
        entity.setReport("# PatchPilot Final External Review Package Delivery Finalization");
        entity.setGeneratedAt(Instant.parse("2026-06-29T10:00:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
