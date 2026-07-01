package io.patchpilot.backend.security.exposure.convert;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureCloseoutArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureCloseoutArchiveConvertTests {

    @Test
    void should_convert_closeout_archive_to_entity_and_back() {
        ExternalExposureCloseoutArchiveVo archive = archive();

        ExternalExposureCloseoutArchiveEntity entity = ExternalExposureCloseoutArchiveConvert.toEntity(archive);
        ExternalExposureCloseoutArchiveVo converted = ExternalExposureCloseoutArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("closeout-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getCloseoutReady()).isTrue();
        assertThat(entity.getLatestSessionId()).isEqualTo("exposure-session-1");
        assertThat(entity.getLinkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(entity.getHandoffStatus()).isEqualTo("READY");
        assertThat(entity.getArchiveFreshness()).isEqualTo("CURRENT");
        assertThat(entity.getEvidenceNotes()).contains("Latest session exposure-session-1 is CLOSED.");
        assertThat(entity.getNextActions()).contains("Keep the closeout report with the demo evidence bundle.");
        assertThat(entity.getDownloadActions()).contains("GET /api/security/external-exposure-closeout/report/download");
        assertThat(entity.getReport()).contains("# PatchPilot External Exposure Closeout");
        assertThat(converted).isEqualTo(archive);
    }

    private static ExternalExposureCloseoutArchiveVo archive() {
        return new ExternalExposureCloseoutArchiveVo(
                "closeout-archive-1",
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
                Instant.parse("2026-07-01T18:00:00Z"),
                Instant.parse("2026-07-01T18:05:00Z"),
                "# PatchPilot External Exposure Closeout"
        );
    }
}
