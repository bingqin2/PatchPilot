package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
import io.patchpilot.backend.security.exposure.service.impl.InMemoryExternalExposureCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureCloseoutArchiveServiceTests {

    private static final Instant GENERATED_AT = Instant.parse("2026-07-01T18:00:00Z");
    private static final Instant ARCHIVED_AT = Instant.parse("2026-07-01T18:05:00Z");

    @Test
    void should_archive_current_external_exposure_closeout_snapshot() {
        ExternalExposureCloseoutArchiveService service = new ExternalExposureCloseoutArchiveService(
                ExternalExposureCloseoutArchiveServiceTests::readyCloseout,
                new InMemoryExternalExposureCloseoutArchiveRepository(),
                Clock.fixed(ARCHIVED_AT, ZoneOffset.UTC),
                () -> "closeout-archive-1"
        );

        ExternalExposureCloseoutArchiveVo archive = service.archiveCurrentCloseout();

        assertThat(archive.id()).isEqualTo("closeout-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.closeoutReady()).isTrue();
        assertThat(archive.latestSessionId()).isEqualTo("exposure-session-1");
        assertThat(archive.linkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(archive.handoffStatus()).isEqualTo("READY");
        assertThat(archive.archiveFreshness()).isEqualTo("CURRENT");
        assertThat(archive.readyCount()).isEqualTo(4);
        assertThat(archive.generatedAt()).isEqualTo(GENERATED_AT);
        assertThat(archive.archivedAt()).isEqualTo(ARCHIVED_AT);
        assertThat(archive.report()).contains("# PatchPilot External Exposure Closeout");
        assertThat(archive.evidenceNotes()).contains("Latest session exposure-session-1 is CLOSED.");
        assertThat(service.findArchive("closeout-archive-1")).contains(archive);
    }

    @Test
    void should_list_recent_external_exposure_closeout_archives_newest_first_and_trim_to_twenty() {
        AtomicInteger idSequence = new AtomicInteger();
        ExternalExposureCloseoutArchiveService service = new ExternalExposureCloseoutArchiveService(
                ExternalExposureCloseoutArchiveServiceTests::readyCloseout,
                new InMemoryExternalExposureCloseoutArchiveRepository(),
                Clock.fixed(ARCHIVED_AT, ZoneOffset.UTC),
                () -> "closeout-archive-" + idSequence.incrementAndGet()
        );

        for (int index = 0; index < 22; index++) {
            service.archiveCurrentCloseout();
        }

        List<ExternalExposureCloseoutArchiveVo> archives = service.listRecentArchives();

        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(ExternalExposureCloseoutArchiveVo::id)
                .startsWith("closeout-archive-22", "closeout-archive-21")
                .doesNotContain("closeout-archive-1", "closeout-archive-2");
    }

    private static ExternalExposureCloseoutVo readyCloseout() {
        return new ExternalExposureCloseoutVo(
                "READY",
                true,
                "External exposure session is closed with complete local evidence.",
                "Keep the closeout report with the demo evidence bundle.",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped and GitHub webhook URL removed.",
                "exposure-archive-1",
                "READY",
                "CURRENT",
                4,
                0,
                0,
                4,
                List.of("Keep the closeout report with the demo evidence bundle."),
                List.of("Latest session exposure-session-1 is CLOSED."),
                List.of("GET /api/security/external-exposure-closeout/report/download"),
                "GET /api/security/external-exposure-closeout is read-only.",
                GENERATED_AT,
                "# PatchPilot External Exposure Closeout"
        );
    }
}
