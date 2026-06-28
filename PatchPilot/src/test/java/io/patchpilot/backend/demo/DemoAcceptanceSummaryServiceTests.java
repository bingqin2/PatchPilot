package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoAcceptanceSummaryServiceTests {

    @Test
    void should_report_ready_when_launch_and_task_certificates_are_certified() {
        DemoAcceptanceSummaryService service = new DemoAcceptanceSummaryService(
                () -> List.of(launchCertificate(DemoReadinessStatus.READY, true)),
                () -> List.of(taskCertificate("READY", true)),
                Clock.fixed(Instant.parse("2026-06-28T14:00:00Z"), ZoneOffset.UTC)
        );

        DemoAcceptanceSummaryVo summary = service.getSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(summary.accepted()).isTrue();
        assertThat(summary.summary()).isEqualTo("PatchPilot final demo acceptance is ready for external review.");
        assertThat(summary.nextAction()).isEqualTo("Share the launch and task evidence certificates with reviewers.");
        assertThat(summary.launchCertificateStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(summary.launchCertificateCertified()).isTrue();
        assertThat(summary.launchCertificateArchiveId()).isEqualTo("launch-certificate-archive-1");
        assertThat(summary.taskCertificateStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(summary.taskCertificateCertified()).isTrue();
        assertThat(summary.taskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(summary.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(summary.checks())
                .extracting(DemoAcceptanceSummaryVo.Check::name)
                .containsExactly("Launch acceptance certificate", "Task evidence acceptance certificate");
        assertThat(summary.downloadActions()).containsExactly(
                "Download launch acceptance certificate archive launch-certificate-archive-1.",
                "Download task evidence acceptance certificate archive task-evidence-certificate-archive-1."
        );
        assertThat(summary.evidenceNotes()).contains(
                "Launch certificate archive launch-certificate-archive-1 is certified.",
                "Task evidence certificate archive task-evidence-certificate-archive-1 is certified."
        );
        assertThat(summary.sideEffectContract())
                .contains("read-only")
                .contains("does not create tasks")
                .contains("write to GitHub");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Final Demo Acceptance Summary")
                .contains("- Accepted: `true`")
                .contains("- Launch certificate archive: `launch-certificate-archive-1`")
                .contains("- Task evidence certificate archive: `task-evidence-certificate-archive-1`");
    }

    @Test
    void should_need_attention_when_task_certificate_is_missing() {
        DemoAcceptanceSummaryService service = new DemoAcceptanceSummaryService(
                () -> List.of(launchCertificate(DemoReadinessStatus.READY, true)),
                List::of,
                Clock.fixed(Instant.parse("2026-06-28T14:00:00Z"), ZoneOffset.UTC)
        );

        DemoAcceptanceSummaryVo summary = service.getSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(summary.accepted()).isFalse();
        assertThat(summary.taskCertificateArchived()).isFalse();
        assertThat(summary.nextAction()).isEqualTo("Archive a certified task evidence acceptance certificate before final demo acceptance.");
        assertThat(summary.checks())
                .extracting(DemoAcceptanceSummaryVo.Check::status)
                .containsExactly(DemoReadinessStatus.READY, DemoReadinessStatus.NEEDS_ATTENTION);
    }

    @Test
    void should_block_when_any_certificate_archive_is_blocked() {
        DemoAcceptanceSummaryService service = new DemoAcceptanceSummaryService(
                () -> List.of(launchCertificate(DemoReadinessStatus.BLOCKED, false)),
                () -> List.of(taskCertificate("READY", true)),
                Clock.fixed(Instant.parse("2026-06-28T14:00:00Z"), ZoneOffset.UTC)
        );

        DemoAcceptanceSummaryVo summary = service.getSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(summary.accepted()).isFalse();
        assertThat(summary.summary()).isEqualTo("PatchPilot final demo acceptance is blocked.");
        assertThat(summary.nextAction()).isEqualTo("Resolve blocked launch acceptance certificate evidence before final demo acceptance.");
    }

    private static DemoLaunchAcceptanceCertificateArchiveVo launchCertificate(
            DemoReadinessStatus status,
            boolean certified
    ) {
        return new DemoLaunchAcceptanceCertificateArchiveVo(
                "launch-certificate-archive-1",
                status,
                certified,
                "Launch acceptance certificate summary.",
                "Launch certificate next action.",
                1,
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                "Final handoff package archive is ready.",
                "launch-delivery-receipt-1",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T12:00:00Z"),
                Instant.parse("2026-06-28T12:30:00Z"),
                Instant.parse("2026-06-28T12:35:00Z"),
                List.of("Download launch acceptance certificate."),
                "# PatchPilot Launch Acceptance Certificate"
        );
    }

    private static FixTaskEvidencePackageAcceptanceCertificateArchiveVo taskCertificate(
            String status,
            boolean certified
    ) {
        return new FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
                "task-evidence-certificate-archive-1",
                status,
                certified,
                "Task evidence acceptance certificate summary.",
                "Task certificate next action.",
                1,
                "task-evidence-closeout-archive-1",
                "task-evidence-archive-1",
                "task-evidence-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T11:00:00Z"),
                Instant.parse("2026-06-28T11:30:00Z"),
                Instant.parse("2026-06-28T11:35:00Z"),
                List.of("Download task evidence acceptance certificate."),
                "# PatchPilot Task Evidence Acceptance Certificate"
        );
    }
}
