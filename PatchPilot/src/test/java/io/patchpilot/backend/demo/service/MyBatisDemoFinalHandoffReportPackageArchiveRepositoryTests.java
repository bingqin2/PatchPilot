package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalHandoffReportPackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalHandoffReportPackageArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalHandoffReportPackageArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalHandoffReportPackageArchiveRepositoryTests {

    private final DemoFinalHandoffReportPackageArchiveMapper mapper =
            mock(DemoFinalHandoffReportPackageArchiveMapper.class);
    private final MyBatisDemoFinalHandoffReportPackageArchiveRepository repository =
            new MyBatisDemoFinalHandoffReportPackageArchiveRepository(mapper);

    @Test
    void inserts_final_handoff_report_package_archive() {
        when(mapper.insert(any(DemoFinalHandoffReportPackageArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalHandoffReportPackageArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalHandoffReportPackageArchiveEntity.class);
        DemoFinalHandoffReportPackageArchiveVo archive = archive(
                "final-handoff-package-archive-1",
                Instant.parse("2026-06-28T11:30:00Z")
        );

        DemoFinalHandoffReportPackageArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalHandoffReportPackageArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-handoff-package-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getDownloadReady()).isTrue();
        assertThat(insertedEntity.getLatestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(insertedEntity.getLatestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(insertedEntity.getLatestDeliveryReceiptId()).isEqualTo("delivery-receipt-1");
        assertThat(insertedEntity.getTaskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(insertedEntity.getTaskCertificateReady()).isTrue();
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Final Demo Handoff Report Package");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_archives_newest_first() {
        DemoFinalHandoffReportPackageArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-28T11:31:00Z"));
        DemoFinalHandoffReportPackageArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-28T11:30:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalHandoffReportPackageArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalHandoffReportPackageArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-28T11:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalHandoffReportPackageArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalHandoffReportPackageArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalHandoffReportPackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "delivery-receipt-1",
                "task-evidence-certificate-archive-1",
                true,
                List.of("Finalization: READY"),
                List.of("Finalization report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Latest delivery receipt delivery-receipt-1 is fresh."),
                List.of("Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package",
                Instant.parse("2026-06-28T11:00:00Z"),
                archivedAt
        );
    }

    private static DemoFinalHandoffReportPackageArchiveEntity entity(String id, Instant archivedAt) {
        DemoFinalHandoffReportPackageArchiveEntity entity = new DemoFinalHandoffReportPackageArchiveEntity();
        entity.setId(id);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setDownloadReady(true);
        entity.setSummary("Final demo handoff report package is ready to deliver.");
        entity.setNextAction("Download this final handoff report package and attach the listed evidence files.");
        entity.setLatestArchiveId("handoff-archive-1");
        entity.setLatestSessionId("demo-session-20260624T003000Z");
        entity.setLatestDeliveryReceiptId("delivery-receipt-1");
        entity.setTaskCertificateArchiveId("task-evidence-certificate-archive-1");
        entity.setTaskCertificateReady(true);
        entity.setReadinessChecksJson("[\"Finalization: READY\"]");
        entity.setRequiredAttachmentsJson("[\"Finalization report\"]");
        entity.setPreSendChecksJson("[\"Confirm no handoff share checklist warnings remain.\"]");
        entity.setEvidenceNotesJson("[\"Latest delivery receipt delivery-receipt-1 is fresh.\"]");
        entity.setSourceReportsJson("[\"Handoff finalization\"]");
        entity.setReport("# PatchPilot Final Demo Handoff Report Package");
        entity.setGeneratedAt(Instant.parse("2026-06-28T11:00:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
