package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchAcceptanceCertificateArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchAcceptanceCertificateArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoLaunchAcceptanceCertificateArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoLaunchAcceptanceCertificateArchiveRepositoryTests {

    private final DemoLaunchAcceptanceCertificateArchiveMapper mapper = mock(DemoLaunchAcceptanceCertificateArchiveMapper.class);
    private final MyBatisDemoLaunchAcceptanceCertificateArchiveRepository repository =
            new MyBatisDemoLaunchAcceptanceCertificateArchiveRepository(mapper);

    @Test
    void inserts_launch_acceptance_certificate_archive() {
        when(mapper.insert(any(DemoLaunchAcceptanceCertificateArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoLaunchAcceptanceCertificateArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoLaunchAcceptanceCertificateArchiveEntity.class);
        DemoLaunchAcceptanceCertificateArchiveVo archive = archive("archive-1", Instant.parse("2026-06-28T10:30:00Z"));

        DemoLaunchAcceptanceCertificateArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoLaunchAcceptanceCertificateArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getCertified()).isTrue();
        assertThat(insertedEntity.getLatestCloseoutArchiveId()).isEqualTo("launch-closeout-archive-1");
        assertThat(insertedEntity.getLatestLaunchEvidenceArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(insertedEntity.getFinalHandoffReportPackageArchiveStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getFinalHandoffReportPackageArchiveReady()).isTrue();
        assertThat(insertedEntity.getFinalHandoffReportPackageArchiveId()).isEqualTo("final-handoff-report-package-archive-1");
        assertThat(insertedEntity.getFinalHandoffReportPackageArchiveSummary())
                .isEqualTo("Latest final handoff report package archive is download-ready and ready.");
        assertThat(insertedEntity.getLatestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getDeliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Launch Acceptance Certificate");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_archives_newest_first() {
        DemoLaunchAcceptanceCertificateArchiveEntity newer = entity("archive-newer", Instant.parse("2026-06-28T10:31:00Z"));
        DemoLaunchAcceptanceCertificateArchiveEntity older = entity("archive-older", Instant.parse("2026-06-28T10:30:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoLaunchAcceptanceCertificateArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoLaunchAcceptanceCertificateArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-28T10:30:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoLaunchAcceptanceCertificateArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoLaunchAcceptanceCertificateArchiveVo archive(String id, Instant archivedAt) {
        return new DemoLaunchAcceptanceCertificateArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Latest final handoff report package archive is download-ready and ready.",
                "launch-delivery-receipt-1",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T08:30:00Z"),
                Instant.parse("2026-06-28T09:00:00Z"),
                archivedAt,
                List.of("Download launch acceptance certificate."),
                "# PatchPilot Launch Acceptance Certificate"
        );
    }

    private static DemoLaunchAcceptanceCertificateArchiveEntity entity(String id, Instant archivedAt) {
        DemoLaunchAcceptanceCertificateArchiveEntity entity = new DemoLaunchAcceptanceCertificateArchiveEntity();
        entity.setId(id);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setCertified(true);
        entity.setSummary("PatchPilot launch acceptance is certified from the latest accepted closeout archive.");
        entity.setNextAction("Share the certificate and archived closeout report with reviewers.");
        entity.setArchiveCount(1);
        entity.setLatestCloseoutArchiveId("launch-closeout-archive-1");
        entity.setLatestLaunchEvidenceArchiveId("launch-evidence-archive-1");
        entity.setFinalHandoffReportPackageArchiveStatus("READY");
        entity.setFinalHandoffReportPackageArchiveReady(true);
        entity.setFinalHandoffReportPackageArchiveId("final-handoff-report-package-archive-1");
        entity.setFinalHandoffReportPackageArchiveSummary("Latest final handoff report package archive is download-ready and ready.");
        entity.setLatestDeliveryReceiptId("launch-delivery-receipt-1");
        entity.setLatestSessionId("demo-session-20260624T003000Z");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setLatestWebhookDeliveryId("delivery-1");
        entity.setEvaluationRunId("evaluation-run-2");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setLatestArchivedAt(Instant.parse("2026-06-28T08:30:00Z"));
        entity.setGeneratedAt(Instant.parse("2026-06-28T09:00:00Z"));
        entity.setArchivedAt(archivedAt);
        entity.setDownloadActionsJson("[\"Download launch acceptance certificate.\"]");
        entity.setReport("# PatchPilot Launch Acceptance Certificate");
        return entity;
    }
}
