package io.patchpilot.backend.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCertificateArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageAcceptanceCertificateArchiveMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskEvidencePackageAcceptanceCertificateArchiveRepositoryTests {

    private final FixTaskEvidencePackageAcceptanceCertificateArchiveMapper mapper =
            mock(FixTaskEvidencePackageAcceptanceCertificateArchiveMapper.class);
    private final MyBatisFixTaskEvidencePackageAcceptanceCertificateArchiveRepository repository =
            new MyBatisFixTaskEvidencePackageAcceptanceCertificateArchiveRepository(mapper);

    @Test
    void should_insert_archive_entity_when_saving() {
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive =
                InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepositoryTests.archive(
                        "task-evidence-certificate-archive-1",
                        "2026-06-28T07:35:00Z"
                );
        ArgumentCaptor<FixTaskEvidencePackageAcceptanceCertificateArchiveEntity> captor =
                ArgumentCaptor.forClass(FixTaskEvidencePackageAcceptanceCertificateArchiveEntity.class);

        FixTaskEvidencePackageAcceptanceCertificateArchiveVo saved = repository.save(archive);

        verify(mapper).insert(captor.capture());
        assertThat(saved).isEqualTo(archive);
        assertThat(captor.getValue().getId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(captor.getValue().getCertified()).isTrue();
        assertThat(captor.getValue().getLatestCloseoutArchiveId()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(captor.getValue().getDownloadActionsJson()).contains("Download task evidence acceptance certificate.");
    }

    @Test
    void should_convert_recent_archives_from_mapper() {
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(entity()));

        List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> archives =
                repository.listRecentArchives(20);

        assertThat(archives).extracting(FixTaskEvidencePackageAcceptanceCertificateArchiveVo::id)
                .containsExactly("task-evidence-certificate-archive-1");
        assertThat(archives.get(0).downloadActions()).containsExactly("Download task evidence acceptance certificate.");
    }

    @Test
    void should_find_archive_by_id() {
        when(mapper.selectById("task-evidence-certificate-archive-1")).thenReturn(entity());

        assertThat(repository.findById("task-evidence-certificate-archive-1"))
                .hasValueSatisfying(archive -> assertThat(archive.latestDeliveryReceiptId())
                        .isEqualTo("task-evidence-delivery-receipt-1"));
    }

    private static FixTaskEvidencePackageAcceptanceCertificateArchiveEntity entity() {
        FixTaskEvidencePackageAcceptanceCertificateArchiveEntity entity =
                new FixTaskEvidencePackageAcceptanceCertificateArchiveEntity();
        entity.setId("task-evidence-certificate-archive-1");
        entity.setStatus("READY");
        entity.setCertified(true);
        entity.setSummary("Task evidence acceptance is certified from the latest accepted closeout archive.");
        entity.setNextAction("Share the certificate and archived closeout report with reviewers.");
        entity.setArchiveCount(1);
        entity.setLatestCloseoutArchiveId("task-evidence-closeout-archive-1");
        entity.setLatestEvidenceArchiveId("task-evidence-archive-1");
        entity.setLatestDeliveryReceiptId("task-evidence-delivery-receipt-1");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/8");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setLatestArchivedAt(Instant.parse("2026-06-28T07:00:00Z"));
        entity.setGeneratedAt(Instant.parse("2026-06-28T07:30:00Z"));
        entity.setArchivedAt(Instant.parse("2026-06-28T07:35:00Z"));
        entity.setDownloadActionsJson("[\"Download task evidence acceptance certificate.\"]");
        entity.setReport("# PatchPilot Task Evidence Acceptance Certificate");
        return entity;
    }
}
