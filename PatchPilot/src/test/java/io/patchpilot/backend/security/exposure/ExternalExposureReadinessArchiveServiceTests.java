package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import io.patchpilot.backend.security.exposure.service.impl.InMemoryExternalExposureReadinessArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureReadinessArchiveServiceTests {

    private static final Instant NOW = Instant.parse("2026-07-01T13:30:00Z");

    @Test
    void should_archive_current_external_exposure_readiness_snapshot() {
        ExternalExposureReadinessArchiveService service = new ExternalExposureReadinessArchiveService(
                ExternalExposureReadinessArchiveServiceTests::readiness,
                new InMemoryExternalExposureReadinessArchiveRepository(),
                Clock.fixed(NOW, ZoneOffset.UTC),
                () -> "exposure-archive-1"
        );

        ExternalExposureReadinessArchiveVo archive = service.archiveCurrentReadiness();

        assertThat(archive.id()).isEqualTo("exposure-archive-1");
        assertThat(archive.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(archive.safeToExpose()).isFalse();
        assertThat(archive.summary()).isEqualTo("PatchPilot needs more safeguards before public exposure.");
        assertThat(archive.readyCount()).isEqualTo(1);
        assertThat(archive.needsAttentionCount()).isEqualTo(1);
        assertThat(archive.blockedCount()).isZero();
        assertThat(archive.totalCount()).isEqualTo(2);
        assertThat(archive.createdAt()).isEqualTo(NOW);
        assertThat(archive.report()).contains("# PatchPilot External Exposure Readiness");
        assertThat(service.findArchive("exposure-archive-1")).contains(archive);
    }

    @Test
    void should_list_recent_external_exposure_readiness_archives_newest_first_and_trim_to_twenty() {
        AtomicInteger idSequence = new AtomicInteger();
        ExternalExposureReadinessArchiveService service = new ExternalExposureReadinessArchiveService(
                ExternalExposureReadinessArchiveServiceTests::readiness,
                new InMemoryExternalExposureReadinessArchiveRepository(),
                Clock.fixed(NOW, ZoneOffset.UTC),
                () -> "exposure-archive-" + idSequence.incrementAndGet()
        );

        for (int index = 0; index < 22; index++) {
            service.archiveCurrentReadiness();
        }

        List<ExternalExposureReadinessArchiveVo> archives = service.listRecentArchives();

        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(ExternalExposureReadinessArchiveVo::id)
                .startsWith("exposure-archive-22", "exposure-archive-21")
                .doesNotContain("exposure-archive-1", "exposure-archive-2");
    }

    private static ExternalExposureReadinessVo readiness() {
        return new ExternalExposureReadinessVo(
                "NEEDS_ATTENTION",
                false,
                1,
                1,
                0,
                2,
                "PatchPilot needs more safeguards before public exposure.",
                List.of("Configure trigger user allowlist."),
                "read-only external exposure readiness",
                List.of(
                        new ExternalExposureReadinessCheckVo(
                                "Admin API token",
                                "READY",
                                "Admin token is configured.",
                                "No action needed."
                        ),
                        new ExternalExposureReadinessCheckVo(
                                "Trigger user allowlist",
                                "NEEDS_ATTENTION",
                                "Trigger user allowlist is open.",
                                "Configure PATCHPILOT_ALLOWED_TRIGGER_USERS."
                        )
                ),
                NOW,
                "# PatchPilot External Exposure Readiness\n\n- Status: NEEDS_ATTENTION\n"
        );
    }
}
