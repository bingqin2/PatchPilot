package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewDeliveryCertificateArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewDeliveryCertificateArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalExternalReviewDeliveryCertificateArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalExternalReviewDeliveryCertificateArchiveRepositoryTests {

    private final DemoFinalExternalReviewDeliveryCertificateArchiveMapper mapper =
            mock(DemoFinalExternalReviewDeliveryCertificateArchiveMapper.class);
    private final MyBatisDemoFinalExternalReviewDeliveryCertificateArchiveRepository repository =
            new MyBatisDemoFinalExternalReviewDeliveryCertificateArchiveRepository(mapper);

    @Test
    void inserts_final_external_review_delivery_certificate_archive() {
        when(mapper.insert(any(DemoFinalExternalReviewDeliveryCertificateArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalExternalReviewDeliveryCertificateArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalExternalReviewDeliveryCertificateArchiveEntity.class);
        DemoFinalExternalReviewDeliveryCertificateArchiveVo archive =
                archive("final-external-review-delivery-certificate-archive-1",
                        Instant.parse("2026-06-29T11:30:00Z"));

        DemoFinalExternalReviewDeliveryCertificateArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalExternalReviewDeliveryCertificateArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getCertified()).isTrue();
        assertThat(insertedEntity.getLatestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(insertedEntity.getLatestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(insertedEntity.getLatestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(insertedEntity.getChecksJson()).contains("Final external-review delivery finalization archive");
        assertThat(insertedEntity.getEvidenceNotesJson()).contains("delivery finalization archive");
        assertThat(insertedEntity.getDownloadActionsJson())
                .contains("Download final external-review delivery certificate report.");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_certificate_archives_newest_first() {
        DemoFinalExternalReviewDeliveryCertificateArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-29T11:31:00Z"));
        DemoFinalExternalReviewDeliveryCertificateArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-29T11:30:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalExternalReviewDeliveryCertificateArchiveVo> archives =
                repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalExternalReviewDeliveryCertificateArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_certificate_archive_by_id() {
        when(mapper.selectById("archive-1"))
                .thenReturn(entity("archive-1", Instant.parse("2026-06-29T11:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalExternalReviewDeliveryCertificateArchiveVo::latestDeliveryReceiptId)
                .contains("final-external-review-package-delivery-receipt-1");
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalExternalReviewDeliveryCertificateArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalExternalReviewDeliveryCertificateArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "Final external-review delivery is certified from the latest finalized archive.",
                "Share the certificate report with reviewers as the final external-review delivery proof.",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                Instant.parse("2026-06-29T10:30:00Z"),
                "FRESH",
                true,
                List.of(new DemoFinalExternalReviewDeliveryCertificateArchiveVo.Check(
                        "Final external-review delivery finalization archive",
                        DemoReadinessStatus.READY,
                        "Latest final external-review delivery finalization archive is finalized.",
                        "No action needed."
                )),
                List.of("Final external-review delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is finalized."),
                List.of("Download final external-review delivery certificate report."),
                "GET /api/demo/final-external-review-delivery-certificate is read-only.",
                "# PatchPilot Final External Review Delivery Certificate",
                Instant.parse("2026-06-29T11:00:00Z"),
                archivedAt
        );
    }

    private static DemoFinalExternalReviewDeliveryCertificateArchiveEntity entity(String id, Instant archivedAt) {
        DemoFinalExternalReviewDeliveryCertificateArchiveEntity entity =
                new DemoFinalExternalReviewDeliveryCertificateArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setCertified(true);
        entity.setSummary("Final external-review delivery is certified from the latest finalized archive.");
        entity.setNextAction("Share the certificate report with reviewers as the final external-review delivery proof.");
        entity.setLatestDeliveryFinalizationArchiveId("final-external-review-package-delivery-finalization-archive-1");
        entity.setLatestPackageArchiveId("final-external-review-package-archive-1");
        entity.setLatestDeliveryReceiptId("final-external-review-package-delivery-receipt-1");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/8");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setLatestDeliveredAt("2026-06-29T09:25:00Z");
        entity.setLatestArchivedAt(Instant.parse("2026-06-29T10:30:00Z"));
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setDeliveryReceiptFresh(true);
        entity.setChecksJson("[{\"name\":\"Final external-review delivery finalization archive\",\"status\":\"READY\",\"summary\":\"Latest final external-review delivery finalization archive is finalized.\",\"nextAction\":\"No action needed.\"}]");
        entity.setEvidenceNotesJson(
                "[\"Final external-review delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is finalized.\"]"
        );
        entity.setDownloadActionsJson("[\"Download final external-review delivery certificate report.\"]");
        entity.setSideEffectContract("GET /api/demo/final-external-review-delivery-certificate is read-only.");
        entity.setReport("# PatchPilot Final External Review Delivery Certificate");
        entity.setGeneratedAt(Instant.parse("2026-06-29T11:00:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
