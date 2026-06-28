package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceSharePackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceSharePackageArchiveMapper;
import io.patchpilot.backend.demo.service.impl.MyBatisDemoFinalAcceptanceSharePackageArchiveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisDemoFinalAcceptanceSharePackageArchiveRepositoryTests {

    private final DemoFinalAcceptanceSharePackageArchiveMapper mapper =
            mock(DemoFinalAcceptanceSharePackageArchiveMapper.class);
    private final MyBatisDemoFinalAcceptanceSharePackageArchiveRepository repository =
            new MyBatisDemoFinalAcceptanceSharePackageArchiveRepository(mapper);

    @Test
    void inserts_final_acceptance_share_package_archive() {
        when(mapper.insert(any(DemoFinalAcceptanceSharePackageArchiveEntity.class))).thenReturn(1);
        ArgumentCaptor<DemoFinalAcceptanceSharePackageArchiveEntity> entityCaptor =
                ArgumentCaptor.forClass(DemoFinalAcceptanceSharePackageArchiveEntity.class);
        DemoFinalAcceptanceSharePackageArchiveVo archive = archive(
                "final-acceptance-share-package-archive-1",
                Instant.parse("2026-06-29T02:00:00Z")
        );

        DemoFinalAcceptanceSharePackageArchiveVo savedArchive = repository.save(archive);

        verify(mapper).insert(entityCaptor.capture());
        DemoFinalAcceptanceSharePackageArchiveEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(insertedEntity.getStatus()).isEqualTo("READY");
        assertThat(insertedEntity.getSendReady()).isTrue();
        assertThat(insertedEntity.getLaunchCertificateArchiveId()).isEqualTo("launch-certificate-archive-1");
        assertThat(insertedEntity.getTaskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(insertedEntity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(insertedEntity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(insertedEntity.getReport()).contains("# PatchPilot Final Demo Acceptance Share Package");
        assertThat(savedArchive).isEqualTo(archive);
    }

    @Test
    void lists_recent_archives_newest_first() {
        DemoFinalAcceptanceSharePackageArchiveEntity newer =
                entity("archive-newer", Instant.parse("2026-06-29T02:01:00Z"));
        DemoFinalAcceptanceSharePackageArchiveEntity older =
                entity("archive-older", Instant.parse("2026-06-29T02:00:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(newer, older));

        List<DemoFinalAcceptanceSharePackageArchiveVo> archives = repository.listRecentArchives(20);

        assertThat(archives)
                .extracting(DemoFinalAcceptanceSharePackageArchiveVo::id)
                .containsExactly("archive-newer", "archive-older");
        verify(mapper).selectList(any());
    }

    @Test
    void finds_archive_by_id() {
        when(mapper.selectById("archive-1")).thenReturn(entity("archive-1", Instant.parse("2026-06-29T02:00:00Z")));
        when(mapper.selectById("missing")).thenReturn(null);

        assertThat(repository.findById("archive-1"))
                .map(DemoFinalAcceptanceSharePackageArchiveVo::status)
                .contains(DemoReadinessStatus.READY);
        assertThat(repository.findById("missing")).isEmpty();
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance package is ready to send.",
                "Send the prepared final acceptance message with all required attachments.",
                "launch-certificate-archive-1",
                "task-evidence-certificate-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of("Final demo acceptance summary report"),
                List.of("Confirm final demo acceptance status is READY and accepted."),
                "PatchPilot final demo acceptance: task-1",
                "PatchPilot final demo acceptance is ready for external review.",
                List.of("Final acceptance status is READY."),
                "POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Share Package",
                Instant.parse("2026-06-29T01:30:00Z"),
                archivedAt
        );
    }

    private static DemoFinalAcceptanceSharePackageArchiveEntity entity(String id, Instant archivedAt) {
        DemoFinalAcceptanceSharePackageArchiveEntity entity = new DemoFinalAcceptanceSharePackageArchiveEntity();
        entity.setId(id);
        entity.setStatus(DemoReadinessStatus.READY.name());
        entity.setSendReady(true);
        entity.setSummary("PatchPilot final demo acceptance package is ready to send.");
        entity.setNextAction("Send the prepared final acceptance message with all required attachments.");
        entity.setLaunchCertificateArchiveId("launch-certificate-archive-1");
        entity.setTaskCertificateArchiveId("task-evidence-certificate-archive-1");
        entity.setLatestTaskId("task-1");
        entity.setLatestPullRequestUrl("https://github.com/bingqin2/PatchPilot/pull/42");
        entity.setRecommendedRecipientsJson("[\"Repository owner or maintainer\",\"Demo reviewer\"]");
        entity.setRequiredAttachmentsJson("[\"Final demo acceptance summary report\"]");
        entity.setPreSendChecksJson("[\"Confirm final demo acceptance status is READY and accepted.\"]");
        entity.setMessageSubject("PatchPilot final demo acceptance: task-1");
        entity.setMessageBody("PatchPilot final demo acceptance is ready for external review.");
        entity.setEvidenceNotesJson("[\"Final acceptance status is READY.\"]");
        entity.setSideEffectContract("POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.");
        entity.setReport("# PatchPilot Final Demo Acceptance Share Package");
        entity.setGeneratedAt(Instant.parse("2026-06-29T01:30:00Z"));
        entity.setArchivedAt(archivedAt);
        return entity;
    }
}
