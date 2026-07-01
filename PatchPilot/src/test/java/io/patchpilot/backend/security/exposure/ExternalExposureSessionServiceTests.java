package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCloseRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCreateRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.service.impl.InMemoryExternalExposureSessionRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalExposureSessionServiceTests {

    private static final Instant CREATED_AT = Instant.parse("2026-07-01T15:00:00Z");
    private static final Instant EXPECTED_SHUTDOWN_AT = Instant.parse("2026-07-01T17:00:00Z");
    private static final Instant CLOSED_AT = Instant.parse("2026-07-01T16:30:00Z");

    @Test
    void should_start_session_when_handoff_package_is_ready() {
        ExternalExposureSessionService service = readyService();

        ExternalExposureSessionVo session = service.startSession(new ExternalExposureSessionCreateRequestDto(
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                EXPECTED_SHUTDOWN_AT,
                "Keep terminal visible during test."
        ));

        assertThat(session.id()).isEqualTo("exposure-session-1");
        assertThat(session.status()).isEqualTo("ACTIVE");
        assertThat(session.publicUrl()).isEqualTo("https://demo.trycloudflare.com");
        assertThat(session.webhookUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(session.purpose()).isEqualTo("Live GitHub webhook smoke test");
        assertThat(session.operator()).isEqualTo("bingqin2");
        assertThat(session.expectedShutdownAt()).isEqualTo(EXPECTED_SHUTDOWN_AT);
        assertThat(session.startedAt()).isEqualTo(CREATED_AT);
        assertThat(session.linkedHandoffStatus()).isEqualTo("READY");
        assertThat(session.linkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(session.closeNotes()).isNull();
        assertThat(session.closedAt()).isNull();
        assertThat(session.markdownReport())
                .contains(
                        "# PatchPilot External Exposure Session",
                        "- Status: `ACTIVE`",
                        "- Public URL: https://demo.trycloudflare.com",
                        "- GitHub webhook URL: https://demo.trycloudflare.com/api/github/webhook",
                        "- Linked readiness archive: `exposure-archive-1`",
                        "records local exposure-session evidence only"
                );
    }

    @Test
    void should_reject_session_when_handoff_package_is_not_ready() {
        ExternalExposureSessionService service = serviceWith(blockedHandoffPackage());

        assertThatThrownBy(() -> service.startSession(new ExternalExposureSessionCreateRequestDto(
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                EXPECTED_SHUTDOWN_AT,
                ""
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("external exposure handoff package is not ready");
    }

    @Test
    void should_validate_required_start_fields() {
        ExternalExposureSessionService service = readyService();

        assertThatThrownBy(() -> service.startSession(new ExternalExposureSessionCreateRequestDto(
                " ",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                EXPECTED_SHUTDOWN_AT,
                ""
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("publicUrl is required");
    }

    @Test
    void should_close_active_session() {
        ExternalExposureSessionService service = readyService();
        ExternalExposureSessionVo session = service.startSession(new ExternalExposureSessionCreateRequestDto(
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                EXPECTED_SHUTDOWN_AT,
                "Started from dashboard."
        ));

        ExternalExposureSessionVo closed = service.closeSession(
                session.id(),
                new ExternalExposureSessionCloseRequestDto("bingqin2", CLOSED_AT, "Tunnel process stopped.")
        );

        assertThat(closed.status()).isEqualTo("CLOSED");
        assertThat(closed.closedBy()).isEqualTo("bingqin2");
        assertThat(closed.closedAt()).isEqualTo(CLOSED_AT);
        assertThat(closed.closeNotes()).isEqualTo("Tunnel process stopped.");
        assertThat(closed.markdownReport())
                .contains(
                        "- Status: `CLOSED`",
                        "- Closed by: `bingqin2`",
                        "- Closed at: `2026-07-01T16:30:00Z`",
                        "Tunnel process stopped."
                );
    }

    @Test
    void should_list_recent_sessions_newest_first_and_find_by_id() {
        InMemoryExternalExposureSessionRepository repository = new InMemoryExternalExposureSessionRepository();
        ExternalExposureSessionService firstService = serviceWith(repository, "exposure-session-1", CREATED_AT);
        ExternalExposureSessionService secondService = serviceWith(
                repository,
                "exposure-session-2",
                CREATED_AT.plusSeconds(60)
        );
        firstService.startSession(requestFor("https://first.trycloudflare.com"));
        secondService.startSession(requestFor("https://second.trycloudflare.com"));

        List<ExternalExposureSessionVo> sessions = secondService.listRecentSessions();

        assertThat(sessions)
                .extracting(ExternalExposureSessionVo::id)
                .containsExactly("exposure-session-2", "exposure-session-1");
        assertThat(secondService.findSession("exposure-session-1"))
                .map(ExternalExposureSessionVo::publicUrl)
                .contains("https://first.trycloudflare.com");
        assertThat(secondService.findSession("missing")).isEmpty();
    }

    private static ExternalExposureSessionCreateRequestDto requestFor(String publicUrl) {
        return new ExternalExposureSessionCreateRequestDto(
                publicUrl,
                publicUrl + "/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                EXPECTED_SHUTDOWN_AT,
                ""
        );
    }

    private static ExternalExposureSessionService readyService() {
        return serviceWith(new InMemoryExternalExposureSessionRepository(), "exposure-session-1", CREATED_AT);
    }

    private static ExternalExposureSessionService serviceWith(ExternalExposureHandoffPackageVo handoffPackage) {
        return new ExternalExposureSessionService(
                () -> handoffPackage,
                new InMemoryExternalExposureSessionRepository(),
                Clock.fixed(CREATED_AT, ZoneOffset.UTC),
                () -> "exposure-session-1"
        );
    }

    private static ExternalExposureSessionService serviceWith(
            InMemoryExternalExposureSessionRepository repository,
            String id,
            Instant now
    ) {
        return new ExternalExposureSessionService(
                ExternalExposureSessionServiceTests::readyHandoffPackage,
                repository,
                Clock.fixed(now, ZoneOffset.UTC),
                () -> id
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
                "External exposure handoff package is missing archived evidence.",
                "Archive readiness first.",
                "READY",
                true,
                10,
                0,
                0,
                10,
                null,
                null,
                null,
                null,
                "MISSING",
                List.of("Archive readiness first."),
                List.of("No archive is available."),
                List.of("GET /api/security/external-exposure-handoff-package/report/download"),
                "GET /api/security/external-exposure-handoff-package is read-only.",
                Instant.parse("2026-07-01T14:05:00Z"),
                "# PatchPilot External Exposure Handoff Package"
        );
    }
}
