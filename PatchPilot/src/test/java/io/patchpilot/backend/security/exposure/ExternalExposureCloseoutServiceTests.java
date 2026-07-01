package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureCloseoutServiceTests {

    private static final Instant GENERATED_AT = Instant.parse("2026-07-01T18:00:00Z");
    private static final Instant STARTED_AT = Instant.parse("2026-07-01T15:00:00Z");
    private static final Instant CLOSED_AT = Instant.parse("2026-07-01T16:30:00Z");

    @Test
    void should_report_ready_when_latest_session_is_closed_with_complete_evidence_and_handoff_ready() {
        ExternalExposureCloseoutService service = serviceWith(List.of(closedSession()), readyHandoffPackage());

        ExternalExposureCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo("READY");
        assertThat(closeout.closeoutReady()).isTrue();
        assertThat(closeout.summary()).isEqualTo("External exposure session is closed with complete local evidence.");
        assertThat(closeout.latestSessionId()).isEqualTo("exposure-session-1");
        assertThat(closeout.latestSessionStatus()).isEqualTo("CLOSED");
        assertThat(closeout.linkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(closeout.handoffStatus()).isEqualTo("READY");
        assertThat(closeout.readyCount()).isEqualTo(4);
        assertThat(closeout.needsAttentionCount()).isZero();
        assertThat(closeout.blockedCount()).isZero();
        assertThat(closeout.nextActions())
                .containsExactly("Keep the closeout report with the demo evidence bundle and rotate or remove the temporary webhook URL if it was configured in GitHub.");
        assertThat(closeout.evidenceNotes())
                .contains(
                        "Latest session exposure-session-1 is CLOSED.",
                        "Session close evidence includes closedBy, closedAt, and closeNotes.",
                        "Session is linked to readiness archive exposure-archive-1.",
                        "Current external exposure handoff package is READY."
                );
        assertThat(closeout.downloadActions())
                .containsExactly("GET /api/security/external-exposure-closeout/report/download");
        assertThat(closeout.markdownReport())
                .contains(
                        "# PatchPilot External Exposure Closeout",
                        "- Status: READY",
                        "- Latest session: exposure-session-1",
                        "- Linked readiness archive: exposure-archive-1",
                        "does not probe public URLs"
                );
    }

    @Test
    void should_block_when_latest_session_is_active() {
        ExternalExposureCloseoutService service = serviceWith(List.of(activeSession()), readyHandoffPackage());

        ExternalExposureCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo("BLOCKED");
        assertThat(closeout.closeoutReady()).isFalse();
        assertThat(closeout.summary()).isEqualTo("External exposure is still active.");
        assertThat(closeout.blockedCount()).isEqualTo(1);
        assertThat(closeout.nextActions())
                .contains("Stop the temporary tunnel, remove or rotate the GitHub webhook payload URL if needed, and close the active exposure session.");
        assertThat(closeout.markdownReport()).contains("- Latest session status: ACTIVE");
    }

    @Test
    void should_need_attention_when_no_session_exists() {
        ExternalExposureCloseoutService service = serviceWith(List.of(), readyHandoffPackage());

        ExternalExposureCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(closeout.closeoutReady()).isFalse();
        assertThat(closeout.summary()).isEqualTo("No external exposure session has been recorded.");
        assertThat(closeout.latestSessionId()).isNull();
        assertThat(closeout.needsAttentionCount()).isEqualTo(1);
        assertThat(closeout.nextActions())
                .contains("Record an exposure session when a temporary public URL is shared, then close it after the tunnel is stopped.");
    }

    @Test
    void should_need_attention_when_closed_session_is_missing_close_notes() {
        ExternalExposureCloseoutService service = serviceWith(List.of(closedSessionWithoutNotes()), readyHandoffPackage());

        ExternalExposureCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(closeout.closeoutReady()).isFalse();
        assertThat(closeout.needsAttentionCount()).isEqualTo(1);
        assertThat(closeout.nextActions())
                .contains("Add close notes that explain how the tunnel and webhook exposure were shut down.");
        assertThat(closeout.evidenceNotes()).contains("Session close notes are missing.");
    }

    @Test
    void should_need_attention_when_handoff_package_is_no_longer_ready() {
        ExternalExposureCloseoutService service = serviceWith(List.of(closedSession()), blockedHandoffPackage());

        ExternalExposureCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(closeout.closeoutReady()).isFalse();
        assertThat(closeout.handoffStatus()).isEqualTo("BLOCKED");
        assertThat(closeout.nextActions())
                .contains("Refresh external exposure readiness and handoff evidence so closeout is tied to current safety state.");
        assertThat(closeout.evidenceNotes()).contains("Current external exposure handoff package is BLOCKED.");
    }

    private static ExternalExposureCloseoutService serviceWith(
            List<ExternalExposureSessionVo> sessions,
            ExternalExposureHandoffPackageVo handoffPackage
    ) {
        return new ExternalExposureCloseoutService(
                () -> sessions,
                () -> handoffPackage,
                Clock.fixed(GENERATED_AT, ZoneOffset.UTC)
        );
    }

    private static ExternalExposureSessionVo activeSession() {
        return new ExternalExposureSessionVo(
                "exposure-session-1",
                "ACTIVE",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                STARTED_AT,
                null,
                null,
                null,
                "# PatchPilot External Exposure Session"
        );
    }

    private static ExternalExposureSessionVo closedSession() {
        return new ExternalExposureSessionVo(
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                STARTED_AT,
                "bingqin2",
                CLOSED_AT,
                "Tunnel process stopped and GitHub webhook URL removed.",
                "# PatchPilot External Exposure Session"
        );
    }

    private static ExternalExposureSessionVo closedSessionWithoutNotes() {
        return new ExternalExposureSessionVo(
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                STARTED_AT,
                "bingqin2",
                CLOSED_AT,
                "",
                "# PatchPilot External Exposure Session"
        );
    }

    private static ExternalExposureHandoffPackageVo readyHandoffPackage() {
        return new ExternalExposureHandoffPackageVo(
                "READY",
                true,
                "External exposure handoff package is ready to share.",
                "Start the temporary tunnel and monitor deliveries.",
                "READY",
                true,
                10,
                0,
                0,
                10,
                "exposure-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T14:00:00Z"),
                "CURRENT",
                List.of("Start the temporary tunnel and keep monitoring."),
                List.of("Latest archive exposure-archive-1 captures READY readiness evidence."),
                List.of("GET /api/security/external-exposure-handoff-package/report/download"),
                "GET /api/security/external-exposure-handoff-package is read-only.",
                Instant.parse("2026-07-01T14:05:00Z"),
                "# PatchPilot External Exposure Handoff Package"
        );
    }

    private static ExternalExposureHandoffPackageVo blockedHandoffPackage() {
        return new ExternalExposureHandoffPackageVo(
                "BLOCKED",
                false,
                "External exposure handoff package is blocked by readiness safeguards.",
                "Fix external exposure readiness first.",
                "BLOCKED",
                false,
                8,
                1,
                1,
                10,
                "exposure-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T14:00:00Z"),
                "STALE",
                List.of("Fix external exposure readiness first."),
                List.of("Archive freshness is STALE."),
                List.of("GET /api/security/external-exposure-handoff-package/report/download"),
                "GET /api/security/external-exposure-handoff-package is read-only.",
                Instant.parse("2026-07-01T14:05:00Z"),
                "# PatchPilot External Exposure Handoff Package"
        );
    }
}
