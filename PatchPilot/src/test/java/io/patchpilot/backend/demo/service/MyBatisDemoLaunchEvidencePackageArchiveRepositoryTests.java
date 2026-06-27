package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidencePackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchEvidencePackageArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoLaunchEvidencePackageArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoLaunchEvidencePackageArchiveRepositoryTests {

    private final DemoLaunchEvidencePackageArchiveMapper mapper = mock(DemoLaunchEvidencePackageArchiveMapper.class);
    private final MyBatisDemoLaunchEvidencePackageArchiveRepository repository =
            new MyBatisDemoLaunchEvidencePackageArchiveRepository(mapper);

    @Test
    void inserts_launch_evidence_package_archive() {
        when(mapper.insert(any(DemoLaunchEvidencePackageArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoLaunchEvidencePackageArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoLaunchEvidencePackageArchiveEntity.class);
        DemoLaunchEvidencePackageArchiveVo archive = archive("archive-1", Instant.parse("2026-06-28T02:00:00Z"));

        DemoLaunchEvidencePackageArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoLaunchEvidencePackageArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getReadyToShare()).isTrue();
        assertThat(insertedEntity.getSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Demo Launch Evidence Package");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_archives_newest_first() {
        DemoLaunchEvidencePackageArchiveEntity newer = entity("archive-newer", Instant.parse("2026-06-28T02:01:00Z"));
        DemoLaunchEvidencePackageArchiveEntity older = entity("archive-older", Instant.parse("2026-06-28T02:00:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoLaunchEvidencePackageArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoLaunchEvidencePackageArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-28T02:00:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoLaunchEvidencePackageArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoLaunchEvidencePackageArchiveVo archive(String id, Instant createdAt) {
        return new DemoLaunchEvidencePackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch evidence package is ready to share.",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                createdAt,
                "# PatchPilot Demo Launch Evidence Package"
        );
    }

    private static DemoLaunchEvidencePackageArchiveEntity entity(String id, Instant createdAt) {
        DemoLaunchEvidencePackageArchiveEntity entity = new DemoLaunchEvidencePackageArchiveEntity();
        entity.setId(id);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setReadyToShare(true);
        entity.setSummary("PatchPilot launch evidence package is ready to share.");
        entity.setSessionId("demo-session-20260624T003000Z");
        entity.setLaunchReadinessStatus(DemoReadinessStatus.READY.name());
        entity.setEvidenceBundleStatus(DemoReadinessStatus.READY.name());
        entity.setHandoffFinalizationStatus(DemoReadinessStatus.READY.name());
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setLatestWebhookDeliveryId("delivery-1");
        entity.setEvaluationRunId("evaluation-run-2");
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot Demo Launch Evidence Package");
        return entity;
    }
}
