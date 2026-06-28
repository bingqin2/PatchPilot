package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchAcceptanceCloseoutArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchAcceptanceCloseoutArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoLaunchAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoLaunchAcceptanceCloseoutArchiveRepositoryTests {

    private final DemoLaunchAcceptanceCloseoutArchiveMapper mapper = mock(DemoLaunchAcceptanceCloseoutArchiveMapper.class);
    private final MyBatisDemoLaunchAcceptanceCloseoutArchiveRepository repository =
            new MyBatisDemoLaunchAcceptanceCloseoutArchiveRepository(mapper);

    @Test
    void inserts_launch_acceptance_closeout_archive() {
        when(mapper.insert(any(DemoLaunchAcceptanceCloseoutArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoLaunchAcceptanceCloseoutArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoLaunchAcceptanceCloseoutArchiveEntity.class);
        DemoLaunchAcceptanceCloseoutArchiveVo archive = archive("archive-1", Instant.parse("2026-06-28T08:00:00Z"));

        DemoLaunchAcceptanceCloseoutArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoLaunchAcceptanceCloseoutArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getAccepted()).isTrue();
        assertThat(insertedEntity.getSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getLatestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Launch Acceptance Closeout");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_archives_newest_first() {
        DemoLaunchAcceptanceCloseoutArchiveEntity newer = entity("archive-newer", Instant.parse("2026-06-28T08:01:00Z"));
        DemoLaunchAcceptanceCloseoutArchiveEntity older = entity("archive-older", Instant.parse("2026-06-28T08:00:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoLaunchAcceptanceCloseoutArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoLaunchAcceptanceCloseoutArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-28T08:00:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoLaunchAcceptanceCloseoutArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoLaunchAcceptanceCloseoutArchiveVo archive(String id, Instant createdAt) {
        return new DemoLaunchAcceptanceCloseoutArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance closeout is complete.",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-evidence-archive-1",
                "launch-delivery-receipt-1",
                "reviewer@example.com",
                "email",
                "FRESH",
                createdAt,
                "# PatchPilot Launch Acceptance Closeout"
        );
    }

    private static DemoLaunchAcceptanceCloseoutArchiveEntity entity(String id, Instant createdAt) {
        DemoLaunchAcceptanceCloseoutArchiveEntity entity = new DemoLaunchAcceptanceCloseoutArchiveEntity();
        entity.setId(id);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setAccepted(true);
        entity.setSummary("PatchPilot launch acceptance closeout is complete.");
        entity.setSessionId("demo-session-20260624T003000Z");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setLatestWebhookDeliveryId("delivery-1");
        entity.setEvaluationRunId("evaluation-run-2");
        entity.setLatestArchiveId("launch-evidence-archive-1");
        entity.setLatestDeliveryReceiptId("launch-delivery-receipt-1");
        entity.setLatestDeliveryTarget("reviewer@example.com");
        entity.setLatestDeliveryChannel("email");
        entity.setDeliveryReceiptFreshness("FRESH");
        entity.setCreatedAt(createdAt);
        entity.setReport("# PatchPilot Launch Acceptance Closeout");
        return entity;
    }
}
