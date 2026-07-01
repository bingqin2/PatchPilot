package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.demo.config.DemoProperties;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureOperatorHandoffChecklistServiceTests {

    @Test
    void should_report_ready_when_closeout_archive_handoff_sessions_and_publish_preflight_are_ready() {
        DemoProperties demoProperties = demoProperties();
        ExternalExposureOperatorHandoffChecklistService service = new ExternalExposureOperatorHandoffChecklistService(
                () -> List.of(readyCloseoutArchive()),
                ExternalExposureOperatorHandoffChecklistServiceTests::readyHandoffPackage,
                () -> List.of(closedSession()),
                ExternalExposureOperatorHandoffChecklistServiceTests::readyPublishPreflight,
                demoProperties,
                () -> Instant.parse("2026-07-01T19:00:00Z")
        );

        ExternalExposureOperatorHandoffChecklistVo checklist = service.getChecklist();

        assertThat(checklist.status()).isEqualTo("READY");
        assertThat(checklist.readyForNextLiveStep()).isTrue();
        assertThat(checklist.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(checklist.latestCloseoutArchiveId()).isEqualTo("closeout-archive-1");
        assertThat(checklist.latestSessionId()).isEqualTo("exposure-session-1");
        assertThat(checklist.publicUrl()).isEqualTo("https://demo.trycloudflare.com");
        assertThat(checklist.webhookUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(checklist.handoffStatus()).isEqualTo("READY");
        assertThat(checklist.livePublishStatus()).isEqualTo("READY");
        assertThat(checklist.activeSessionCount()).isZero();
        assertThat(checklist.readyCount()).isEqualTo(4);
        assertThat(checklist.blockedCount()).isZero();
        assertThat(checklist.nextActions()).containsExactly(
                "Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend."
        );
        assertThat(checklist.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Closeout archive:READY",
                        "Exposure handoff package:READY",
                        "Active exposure sessions:READY",
                        "Live GitHub publish preflight:READY"
                );
        assertThat(checklist.markdownReport())
                .contains("# PatchPilot External Exposure Operator Handoff Checklist")
                .contains("- Status: READY")
                .contains("- Closeout archive: closeout-archive-1")
                .contains("- Repository: bingqin2/PatchPilot");
    }

    @Test
    void should_block_when_latest_session_is_active_even_if_closeout_archive_exists() {
        DemoProperties demoProperties = demoProperties();
        ExternalExposureOperatorHandoffChecklistService service = new ExternalExposureOperatorHandoffChecklistService(
                () -> List.of(readyCloseoutArchive()),
                ExternalExposureOperatorHandoffChecklistServiceTests::readyHandoffPackage,
                () -> List.of(activeSession(), closedSession()),
                ExternalExposureOperatorHandoffChecklistServiceTests::readyPublishPreflight,
                demoProperties,
                () -> Instant.parse("2026-07-01T19:00:00Z")
        );

        ExternalExposureOperatorHandoffChecklistVo checklist = service.getChecklist();

        assertThat(checklist.status()).isEqualTo("BLOCKED");
        assertThat(checklist.readyForNextLiveStep()).isFalse();
        assertThat(checklist.activeSessionCount()).isEqualTo(1);
        assertThat(checklist.blockedCount()).isEqualTo(1);
        assertThat(checklist.nextActions()).contains("Close active external exposure sessions before posting another live /agent fix.");
        assertThat(checklist.checks())
                .filteredOn(check -> check.name().equals("Active exposure sessions"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo("BLOCKED");
                    assertThat(check.summary()).contains("1 active external exposure session");
                });
    }

    @Test
    void should_need_attention_when_closeout_archive_is_missing() {
        DemoProperties demoProperties = demoProperties();
        ExternalExposureOperatorHandoffChecklistService service = new ExternalExposureOperatorHandoffChecklistService(
                List::of,
                ExternalExposureOperatorHandoffChecklistServiceTests::readyHandoffPackage,
                () -> List.of(closedSession()),
                ExternalExposureOperatorHandoffChecklistServiceTests::readyPublishPreflight,
                demoProperties,
                () -> Instant.parse("2026-07-01T19:00:00Z")
        );

        ExternalExposureOperatorHandoffChecklistVo checklist = service.getChecklist();

        assertThat(checklist.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(checklist.latestCloseoutArchiveId()).isNull();
        assertThat(checklist.nextActions()).contains("Archive the current external exposure closeout before relying on handoff evidence.");
        assertThat(checklist.markdownReport()).contains("- Closeout archive: none");
    }

    @Test
    void should_need_attention_when_ready_status_has_false_ready_flags() {
        DemoProperties demoProperties = demoProperties();
        ExternalExposureOperatorHandoffChecklistService service = new ExternalExposureOperatorHandoffChecklistService(
                () -> List.of(notReadyCloseoutArchiveWithReadyStatus()),
                ExternalExposureOperatorHandoffChecklistServiceTests::notReadyHandoffPackageWithReadyStatus,
                () -> List.of(closedSession()),
                ExternalExposureOperatorHandoffChecklistServiceTests::notReadyPublishPreflightWithReadyStatus,
                demoProperties,
                () -> Instant.parse("2026-07-01T19:00:00Z")
        );

        ExternalExposureOperatorHandoffChecklistVo checklist = service.getChecklist();

        assertThat(checklist.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(checklist.readyForNextLiveStep()).isFalse();
        assertThat(checklist.readyCount()).isEqualTo(1);
        assertThat(checklist.needsAttentionCount()).isEqualTo(3);
        assertThat(checklist.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Closeout archive:NEEDS_ATTENTION",
                        "Exposure handoff package:NEEDS_ATTENTION",
                        "Active exposure sessions:READY",
                        "Live GitHub publish preflight:NEEDS_ATTENTION"
                );
        assertThat(checklist.nextActions())
                .contains(
                        "Create a READY closeout archive after closing the temporary public URL.",
                        "Refresh external exposure readiness archive.",
                        "Clear stale PatchPilot publish artifacts."
                );
    }

    private static DemoProperties demoProperties() {
        DemoProperties properties = new DemoProperties();
        properties.setRepositoryOwner("bingqin2");
        properties.setRepositoryName("PatchPilot");
        return properties;
    }

    private static ExternalExposureCloseoutArchiveVo readyCloseoutArchive() {
        return new ExternalExposureCloseoutArchiveVo(
                "closeout-archive-1",
                "READY",
                true,
                "External exposure closeout archive is ready.",
                "Keep archived closeout evidence with the demo bundle.",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped.",
                "exposure-archive-1",
                "READY",
                "CURRENT",
                4,
                0,
                0,
                4,
                List.of("Keep archived closeout evidence with the demo bundle."),
                List.of("Latest session exposure-session-1 is CLOSED."),
                List.of("GET /api/security/external-exposure-closeout/archives/closeout-archive-1/report/download"),
                "Closeout archive is local evidence only.",
                Instant.parse("2026-07-01T18:00:00Z"),
                Instant.parse("2026-07-01T18:05:00Z"),
                "# PatchPilot External Exposure Closeout"
        );
    }

    private static ExternalExposureCloseoutArchiveVo notReadyCloseoutArchiveWithReadyStatus() {
        return new ExternalExposureCloseoutArchiveVo(
                "closeout-archive-not-ready",
                "READY",
                false,
                "External exposure closeout archive is incomplete.",
                "Create a READY closeout archive after closing the temporary public URL.",
                "exposure-session-1",
                "CLOSED",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped.",
                "exposure-archive-1",
                "READY",
                "CURRENT",
                3,
                1,
                0,
                4,
                List.of("Create a READY closeout archive after closing the temporary public URL."),
                List.of("Closeout archive is incomplete."),
                List.of("GET /api/security/external-exposure-closeout/archives/closeout-archive-not-ready/report/download"),
                "Closeout archive is local evidence only.",
                Instant.parse("2026-07-01T18:00:00Z"),
                Instant.parse("2026-07-01T18:05:00Z"),
                "# PatchPilot External Exposure Closeout"
        );
    }

    private static ExternalExposureHandoffPackageVo readyHandoffPackage() {
        return new ExternalExposureHandoffPackageVo(
                "READY",
                true,
                "External exposure handoff package is ready.",
                "Start the temporary tunnel only when needed.",
                "READY",
                true,
                10,
                0,
                0,
                10,
                "exposure-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T13:30:00Z"),
                "CURRENT",
                List.of("Start the temporary tunnel only when needed."),
                List.of("Latest readiness archive is current."),
                List.of("GET /api/security/external-exposure-handoff-package/report/download"),
                "Handoff package is read-only.",
                Instant.parse("2026-07-01T14:00:00Z"),
                "# PatchPilot External Exposure Handoff Package"
        );
    }

    private static ExternalExposureHandoffPackageVo notReadyHandoffPackageWithReadyStatus() {
        return new ExternalExposureHandoffPackageVo(
                "READY",
                false,
                "External exposure handoff package is incomplete.",
                "Refresh external exposure readiness archive.",
                "READY",
                false,
                9,
                1,
                0,
                10,
                "exposure-archive-1",
                "READY",
                true,
                Instant.parse("2026-07-01T13:30:00Z"),
                "CURRENT",
                List.of("Refresh external exposure readiness archive."),
                List.of("Latest readiness archive needs attention."),
                List.of("GET /api/security/external-exposure-handoff-package/report/download"),
                "Handoff package is read-only.",
                Instant.parse("2026-07-01T14:00:00Z"),
                "# PatchPilot External Exposure Handoff Package"
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
                Instant.parse("2026-07-01T15:00:00Z"),
                "bingqin2",
                Instant.parse("2026-07-01T16:30:00Z"),
                "Tunnel process stopped.",
                "# PatchPilot External Exposure Session"
        );
    }

    private static ExternalExposureSessionVo activeSession() {
        return new ExternalExposureSessionVo(
                "exposure-session-active",
                "ACTIVE",
                "https://active.trycloudflare.com",
                "https://active.trycloudflare.com/api/github/webhook",
                "Reviewer smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T20:00:00Z"),
                "Still active.",
                "READY",
                "exposure-archive-1",
                Instant.parse("2026-07-01T18:30:00Z"),
                null,
                null,
                null,
                "# PatchPilot External Exposure Session"
        );
    }

    private static GitHubLivePublishPreflightVo readyPublishPreflight() {
        return new GitHubLivePublishPreflightVo(
                "READY",
                true,
                true,
                true,
                "bingqin2/PatchPilot",
                "main",
                List.of(),
                List.of(),
                "Live GitHub publish preflight is ready.",
                "Post the prepared /agent fix comment.",
                "Read-only live publish preflight.",
                List.of(new GitHubLivePublishPreflightCheckVo("Publish permission readiness", "READY", "Token can create PRs.", "Ready.")),
                List.of("Repository: bingqin2/PatchPilot"),
                15,
                Instant.parse("2026-07-01T18:50:00Z")
        );
    }

    private static GitHubLivePublishPreflightVo notReadyPublishPreflightWithReadyStatus() {
        return new GitHubLivePublishPreflightVo(
                "READY",
                false,
                true,
                false,
                "bingqin2/PatchPilot",
                "main",
                List.of("patchpilot/stale"),
                List.of(),
                "Live GitHub publish preflight has stale artifacts.",
                "Clear stale PatchPilot publish artifacts.",
                "Read-only live publish preflight.",
                List.of(new GitHubLivePublishPreflightCheckVo(
                        "PatchPilot publish artifacts",
                        "NEEDS_ATTENTION",
                        "Stale branch exists.",
                        "Clear stale PatchPilot publish artifacts."
                )),
                List.of("Repository: bingqin2/PatchPilot"),
                15,
                Instant.parse("2026-07-01T18:50:00Z")
        );
    }
}
