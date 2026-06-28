package io.patchpilot.backend.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageAcceptanceCloseoutArchiveMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskEvidencePackageAcceptanceCloseoutArchiveRepositoryTests {

    private final FixTaskEvidencePackageAcceptanceCloseoutArchiveMapper mapper =
            mock(FixTaskEvidencePackageAcceptanceCloseoutArchiveMapper.class);
    private final MyBatisFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository repository =
            new MyBatisFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository(mapper);

    @Test
    void should_insert_archive_entity_when_saving() {
        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive =
                archive("task-evidence-closeout-archive-1");
        ArgumentCaptor<FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity> captor =
                ArgumentCaptor.forClass(FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity.class);

        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo saved = repository.save(archive);

        verify(mapper).insert(captor.capture());
        assertThat(saved).isEqualTo(archive);
        assertThat(captor.getValue().getId()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(captor.getValue().getAccepted()).isTrue();
        assertThat(captor.getValue().getLatestDeliveryTarget()).isEqualTo("reviewer@example.com");
    }

    @Test
    void should_convert_recent_archives_from_mapper() {
        FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity entity =
                entity("task-evidence-closeout-archive-1");
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(entity));

        List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> archives =
                repository.listRecentArchives(20);

        assertThat(archives).extracting(FixTaskEvidencePackageAcceptanceCloseoutArchiveVo::id)
                .containsExactly("task-evidence-closeout-archive-1");
        assertThat(archives.get(0).report())
                .contains("# PatchPilot Task Evidence Acceptance Closeout Archive");
    }

    @Test
    void should_find_archive_by_id() {
        when(mapper.selectById("task-evidence-closeout-archive-1"))
                .thenReturn(entity("task-evidence-closeout-archive-1"));

        assertThat(repository.findById("task-evidence-closeout-archive-1"))
                .hasValueSatisfying(archive -> assertThat(archive.latestDeliveryReceiptId())
                        .isEqualTo("task-evidence-delivery-receipt-1"));
    }

    private static FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive(String id) {
        return new FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
                id,
                "READY",
                true,
                "Task evidence is finalized with a fresh delivery receipt for the current shareable archive.",
                "task-evidence-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "task-evidence-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T07:00:00Z"),
                "# PatchPilot Task Evidence Acceptance Closeout Archive"
        );
    }

    private static FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity entity(String id) {
        FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity entity =
                new FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity();
        entity.setId(id);
        entity.setStatus("READY");
        entity.setAccepted(true);
        entity.setSummary("Task evidence is finalized with a fresh delivery receipt for the current shareable archive.");
        entity.setLatestArchiveId("task-evidence-archive-1");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/8");
        entity.setLatestDeliveryReceiptId("task-evidence-delivery-receipt-1");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setCreatedAt(Instant.parse("2026-06-28T07:00:00Z"));
        entity.setReport("# PatchPilot Task Evidence Acceptance Closeout Archive");
        return entity;
    }
}
