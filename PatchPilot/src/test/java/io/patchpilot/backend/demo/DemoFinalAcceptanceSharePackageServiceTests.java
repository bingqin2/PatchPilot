package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAcceptanceSummaryVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceSharePackageServiceTests {

    @Test
    void should_build_send_ready_package_when_final_acceptance_is_accepted() {
        DemoFinalAcceptanceSharePackageService service = new DemoFinalAcceptanceSharePackageService(
                () -> acceptanceSummary(DemoReadinessStatus.READY, true),
                Clock.fixed(Instant.parse("2026-06-28T15:00:00Z"), ZoneOffset.UTC)
        );

        DemoFinalAcceptanceSharePackageVo packageVo = service.getSharePackage();

        assertThat(packageVo.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(packageVo.sendReady()).isTrue();
        assertThat(packageVo.summary()).isEqualTo("PatchPilot final demo acceptance package is ready to send.");
        assertThat(packageVo.nextAction()).isEqualTo("Send the prepared final acceptance message with all required attachments.");
        assertThat(packageVo.launchCertificateArchiveId()).isEqualTo("launch-certificate-archive-1");
        assertThat(packageVo.taskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(packageVo.latestTaskId()).isEqualTo("task-1");
        assertThat(packageVo.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(packageVo.recommendedRecipients()).containsExactly(
                "Repository owner or maintainer",
                "Demo reviewer"
        );
        assertThat(packageVo.requiredAttachments()).containsExactly(
                "Final demo acceptance summary report",
                "Launch acceptance certificate archive launch-certificate-archive-1",
                "Task evidence acceptance certificate archive task-evidence-certificate-archive-1",
                "Pull Request https://github.com/bingqin2/PatchPilot/pull/42"
        );
        assertThat(packageVo.preSendChecks()).contains(
                "Confirm final demo acceptance status is READY and accepted.",
                "Confirm launch acceptance certificate archive launch-certificate-archive-1 is attached.",
                "Confirm task evidence acceptance certificate archive task-evidence-certificate-archive-1 is attached.",
                "Confirm Pull Request https://github.com/bingqin2/PatchPilot/pull/42 opens correctly."
        );
        assertThat(packageVo.messageSubject()).isEqualTo("PatchPilot final demo acceptance: task-1");
        assertThat(packageVo.messageBody())
                .contains("PatchPilot final demo acceptance is ready for external review.")
                .contains("Launch certificate archive: launch-certificate-archive-1")
                .contains("Task evidence certificate archive: task-evidence-certificate-archive-1");
        assertThat(packageVo.sideEffectContract())
                .contains("read-only")
                .contains("does not create tasks")
                .contains("write to GitHub");
        assertThat(packageVo.markdownReport())
                .contains("# PatchPilot Final Demo Acceptance Share Package")
                .contains("Subject: PatchPilot final demo acceptance: task-1")
                .contains("## Embedded Final Acceptance Summary");
    }

    @Test
    void should_block_sending_when_final_acceptance_is_not_accepted() {
        DemoFinalAcceptanceSharePackageService service = new DemoFinalAcceptanceSharePackageService(
                () -> acceptanceSummary(DemoReadinessStatus.NEEDS_ATTENTION, false),
                Clock.fixed(Instant.parse("2026-06-28T15:00:00Z"), ZoneOffset.UTC)
        );

        DemoFinalAcceptanceSharePackageVo packageVo = service.getSharePackage();

        assertThat(packageVo.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(packageVo.sendReady()).isFalse();
        assertThat(packageVo.messageSubject()).isEqualTo("PatchPilot final demo acceptance: not ready");
        assertThat(packageVo.requiredAttachments()).containsExactly(
                "Resolve final demo acceptance blockers before attaching final evidence."
        );
        assertThat(packageVo.preSendChecks()).containsExactly(
                "Resolve final demo acceptance before sending: Archive a certified task evidence acceptance certificate before final demo acceptance.",
                "Do not send the final acceptance package until the summary reports READY and accepted."
        );
        assertThat(packageVo.messageBody()).contains("not ready to send yet");
    }

    private static DemoAcceptanceSummaryVo acceptanceSummary(DemoReadinessStatus status, boolean accepted) {
        return new DemoAcceptanceSummaryVo(
                status,
                accepted,
                accepted
                        ? "PatchPilot final demo acceptance is ready for external review."
                        : "PatchPilot final demo acceptance needs attention.",
                accepted
                        ? "Share the launch and task evidence certificates with reviewers."
                        : "Archive a certified task evidence acceptance certificate before final demo acceptance.",
                DemoReadinessStatus.READY,
                true,
                true,
                "launch-certificate-archive-1",
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                "launch-delivery-receipt-1",
                accepted ? DemoReadinessStatus.READY : DemoReadinessStatus.NEEDS_ATTENTION,
                accepted,
                accepted,
                accepted ? "task-evidence-certificate-archive-1" : null,
                accepted ? "task-evidence-closeout-archive-1" : null,
                accepted ? "task-evidence-archive-1" : null,
                accepted ? "task-evidence-receipt-1" : null,
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                Instant.parse("2026-06-28T14:00:00Z"),
                List.of(new DemoAcceptanceSummaryVo.Check(
                        "Launch acceptance certificate",
                        DemoReadinessStatus.READY,
                        "Latest launch acceptance certificate archive is certified.",
                        "Use the archived launch acceptance certificate for launch-level review proof."
                )),
                List.of("Launch certificate archive launch-certificate-archive-1 is certified."),
                List.of("Download launch acceptance certificate archive launch-certificate-archive-1."),
                "GET /api/demo/acceptance-summary is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Summary"
        );
    }
}
