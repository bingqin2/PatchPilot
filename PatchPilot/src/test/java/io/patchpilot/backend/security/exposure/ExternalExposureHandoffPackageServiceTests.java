package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import io.patchpilot.backend.security.exposure.service.impl.InMemoryExternalExposureReadinessArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureHandoffPackageServiceTests {

    private static final Instant ARCHIVED_AT = Instant.parse("2026-07-01T13:30:00Z");
    private static final Instant GENERATED_AT = Instant.parse("2026-07-01T14:00:00Z");

    @Test
    void should_build_ready_handoff_package_when_current_readiness_matches_latest_safe_archive() {
        InMemoryExternalExposureReadinessArchiveRepository repository =
                new InMemoryExternalExposureReadinessArchiveRepository();
        ExternalExposureReadinessArchiveService archiveService = new ExternalExposureReadinessArchiveService(
                ExternalExposureHandoffPackageServiceTests::readyReadiness,
                repository,
                Clock.fixed(ARCHIVED_AT, ZoneOffset.UTC),
                () -> "exposure-archive-1"
        );
        archiveService.archiveCurrentReadiness();
        ExternalExposureHandoffPackageService service = new ExternalExposureHandoffPackageService(
                ExternalExposureHandoffPackageServiceTests::readyReadiness,
                archiveService,
                Clock.fixed(GENERATED_AT, ZoneOffset.UTC)
        );

        ExternalExposureHandoffPackageVo handoffPackage = service.getHandoffPackage();

        assertThat(handoffPackage.status()).isEqualTo("READY");
        assertThat(handoffPackage.handoffReady()).isTrue();
        assertThat(handoffPackage.summary()).isEqualTo("External exposure handoff package is ready to share.");
        assertThat(handoffPackage.readinessStatus()).isEqualTo("READY");
        assertThat(handoffPackage.latestArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(handoffPackage.latestArchiveStatus()).isEqualTo("READY");
        assertThat(handoffPackage.latestArchiveSafeToExpose()).isTrue();
        assertThat(handoffPackage.archiveFreshness()).isEqualTo("CURRENT");
        assertThat(handoffPackage.generatedAt()).isEqualTo(GENERATED_AT);
        assertThat(handoffPackage.evidenceNotes())
                .contains("Latest archive exposure-archive-1 captures READY readiness evidence.");
        assertThat(handoffPackage.downloadActions())
                .contains("GET /api/security/external-exposure-handoff-package/report/download");
        assertThat(handoffPackage.sideEffectContract())
                .contains("does not create tasks", "does not call the model", "does not mutate GitHub");
        assertThat(handoffPackage.markdownReport())
                .contains(
                        "# PatchPilot External Exposure Handoff Package",
                        "- Status: READY",
                        "- Current readiness: READY",
                        "- Latest archive: exposure-archive-1",
                        "GET /api/security/external-exposure-handoff-package is read-only"
                );
    }

    @Test
    void should_block_handoff_when_current_readiness_has_no_archive() {
        ExternalExposureReadinessArchiveService archiveService = new ExternalExposureReadinessArchiveService(
                ExternalExposureHandoffPackageServiceTests::readyReadiness,
                new InMemoryExternalExposureReadinessArchiveRepository(),
                Clock.fixed(ARCHIVED_AT, ZoneOffset.UTC),
                () -> "exposure-archive-1"
        );
        ExternalExposureHandoffPackageService service = new ExternalExposureHandoffPackageService(
                ExternalExposureHandoffPackageServiceTests::readyReadiness,
                archiveService,
                Clock.fixed(GENERATED_AT, ZoneOffset.UTC)
        );

        ExternalExposureHandoffPackageVo handoffPackage = service.getHandoffPackage();

        assertThat(handoffPackage.status()).isEqualTo("BLOCKED");
        assertThat(handoffPackage.handoffReady()).isFalse();
        assertThat(handoffPackage.summary()).isEqualTo("External exposure handoff package is missing archived evidence.");
        assertThat(handoffPackage.latestArchiveId()).isNull();
        assertThat(handoffPackage.latestArchiveStatus()).isNull();
        assertThat(handoffPackage.latestArchiveSafeToExpose()).isNull();
        assertThat(handoffPackage.archiveFreshness()).isEqualTo("MISSING");
        assertThat(handoffPackage.nextActions())
                .contains("Archive the current external exposure readiness result before sharing the public URL.");
        assertThat(handoffPackage.markdownReport())
                .contains("- Latest archive: missing", "Archive the current external exposure readiness result");
    }

    private static ExternalExposureReadinessVo readyReadiness() {
        return new ExternalExposureReadinessVo(
                "READY",
                true,
                10,
                0,
                0,
                10,
                "PatchPilot is configured for controlled temporary public exposure.",
                List.of("Start the temporary tunnel and keep monitoring webhook deliveries, rejected triggers, and queue health."),
                "read-only external exposure readiness",
                List.of(
                        new ExternalExposureReadinessCheckVo(
                                "Admin API token",
                                "READY",
                                "Admin token is configured.",
                                "No action needed."
                        )
                ),
                ARCHIVED_AT,
                "# PatchPilot External Exposure Readiness\n\n- Status: READY\n"
        );
    }
}
