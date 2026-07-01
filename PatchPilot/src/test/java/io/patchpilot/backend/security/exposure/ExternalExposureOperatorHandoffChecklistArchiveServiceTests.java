package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import io.patchpilot.backend.security.exposure.service.impl.InMemoryExternalExposureOperatorHandoffChecklistArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureOperatorHandoffChecklistArchiveServiceTests {

    private static final Instant GENERATED_AT = Instant.parse("2026-07-01T19:00:00Z");
    private static final Instant ARCHIVED_AT = Instant.parse("2026-07-01T19:05:00Z");

    @Test
    void should_archive_current_operator_handoff_checklist_snapshot() {
        ExternalExposureOperatorHandoffChecklistArchiveService service =
                new ExternalExposureOperatorHandoffChecklistArchiveService(
                        ExternalExposureOperatorHandoffChecklistArchiveServiceTests::readyChecklist,
                        new InMemoryExternalExposureOperatorHandoffChecklistArchiveRepository(),
                        Clock.fixed(ARCHIVED_AT, ZoneOffset.UTC),
                        () -> "operator-handoff-archive-1"
                );

        ExternalExposureOperatorHandoffChecklistArchiveVo archive = service.archiveCurrentChecklist();

        assertThat(archive.id()).isEqualTo("operator-handoff-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.readyForNextLiveStep()).isTrue();
        assertThat(archive.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(archive.latestCloseoutArchiveId()).isEqualTo("closeout-archive-1");
        assertThat(archive.latestSessionId()).isEqualTo("exposure-session-1");
        assertThat(archive.handoffStatus()).isEqualTo("READY");
        assertThat(archive.livePublishStatus()).isEqualTo("READY");
        assertThat(archive.activeSessionCount()).isZero();
        assertThat(archive.readyCount()).isEqualTo(4);
        assertThat(archive.generatedAt()).isEqualTo(GENERATED_AT);
        assertThat(archive.archivedAt()).isEqualTo(ARCHIVED_AT);
        assertThat(archive.report()).contains("# PatchPilot External Exposure Operator Handoff Checklist");
        assertThat(archive.evidenceNotes()).contains("Latest closeout archive closeout-archive-1 is READY.");
        assertThat(service.findArchive("operator-handoff-archive-1")).contains(archive);
    }

    @Test
    void should_list_recent_operator_handoff_archives_newest_first_and_trim_to_twenty() {
        AtomicInteger idSequence = new AtomicInteger();
        ExternalExposureOperatorHandoffChecklistArchiveService service =
                new ExternalExposureOperatorHandoffChecklistArchiveService(
                        ExternalExposureOperatorHandoffChecklistArchiveServiceTests::readyChecklist,
                        new InMemoryExternalExposureOperatorHandoffChecklistArchiveRepository(),
                        Clock.fixed(ARCHIVED_AT, ZoneOffset.UTC),
                        () -> "operator-handoff-archive-" + idSequence.incrementAndGet()
                );

        for (int index = 0; index < 22; index++) {
            service.archiveCurrentChecklist();
        }

        List<ExternalExposureOperatorHandoffChecklistArchiveVo> archives = service.listRecentArchives();

        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(ExternalExposureOperatorHandoffChecklistArchiveVo::id)
                .startsWith("operator-handoff-archive-22", "operator-handoff-archive-21")
                .doesNotContain("operator-handoff-archive-1", "operator-handoff-archive-2");
    }

    private static ExternalExposureOperatorHandoffChecklistVo readyChecklist() {
        return new ExternalExposureOperatorHandoffChecklistVo(
                "READY",
                true,
                "External exposure evidence is closed and ready for the next live step.",
                "Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.",
                "bingqin2/PatchPilot",
                "closeout-archive-1",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "READY",
                "CURRENT",
                "READY",
                true,
                0,
                4,
                0,
                0,
                4,
                List.of("Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend."),
                List.of("Latest closeout archive closeout-archive-1 is READY."),
                List.of("GET /api/security/external-exposure-operator-handoff-checklist/report/download"),
                "GET /api/security/external-exposure-operator-handoff-checklist is read-only.",
                List.of(new ExternalExposureOperatorHandoffChecklistCheckVo(
                        "Closeout archive",
                        "READY",
                        "Latest closeout archive closeout-archive-1 is READY.",
                        "Ready."
                )),
                GENERATED_AT,
                "# PatchPilot External Exposure Operator Handoff Checklist"
        );
    }
}
