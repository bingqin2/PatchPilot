package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchAcceptanceCertificateServiceTests {

    @Test
    void certifies_latest_ready_accepted_closeout_archive() {
        InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository repository =
                new InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository();
        repository.save(archive("launch-closeout-archive-1", DemoReadinessStatus.READY, true));
        DemoLaunchAcceptanceCertificateService service = new DemoLaunchAcceptanceCertificateService(
                repository,
                Clock.fixed(Instant.parse("2026-06-28T09:00:00Z"), ZoneOffset.UTC)
        );

        DemoLaunchAcceptanceCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(certificate.certified()).isTrue();
        assertThat(certificate.summary()).isEqualTo("PatchPilot launch acceptance is certified from the latest accepted closeout archive.");
        assertThat(certificate.nextAction()).isEqualTo("Share the certificate and archived closeout report with reviewers.");
        assertThat(certificate.archiveCount()).isEqualTo(1);
        assertThat(certificate.latestCloseoutArchiveId()).isEqualTo("launch-closeout-archive-1");
        assertThat(certificate.latestLaunchEvidenceArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(certificate.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(certificate.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(certificate.generatedAt()).isEqualTo(Instant.parse("2026-06-28T09:00:00Z"));
        assertThat(certificate.downloadActions())
                .containsExactly(
                        "Download launch acceptance certificate.",
                        "Download launch acceptance closeout archive launch-closeout-archive-1.",
                        "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review."
                );
        assertThat(certificate.markdownReport())
                .contains("# PatchPilot Launch Acceptance Certificate")
                .contains("- Certified: `true`")
                .contains("- Closeout archive: `launch-closeout-archive-1`")
                .contains("GET /api/demo/launch-acceptance-certificate is read-only");
    }

    @Test
    void needs_attention_without_accepted_ready_archive() {
        InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository repository =
                new InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository();
        repository.save(archive("launch-closeout-archive-1", DemoReadinessStatus.NEEDS_ATTENTION, false));
        DemoLaunchAcceptanceCertificateService service = new DemoLaunchAcceptanceCertificateService(
                repository,
                Clock.fixed(Instant.parse("2026-06-28T09:00:00Z"), ZoneOffset.UTC)
        );

        DemoLaunchAcceptanceCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary()).isEqualTo("Latest launch acceptance closeout archive is not accepted.");
        assertThat(certificate.nextAction()).isEqualTo("Resolve closeout blockers, archive a READY accepted closeout, then download the certificate.");
        assertThat(certificate.latestCloseoutArchiveId()).isEqualTo("launch-closeout-archive-1");
        assertThat(certificate.downloadActions()).containsExactly(
                "Archive a READY launch acceptance closeout before sharing a certificate."
        );
    }

    @Test
    void needs_attention_when_no_closeout_archive_exists() {
        DemoLaunchAcceptanceCertificateService service = new DemoLaunchAcceptanceCertificateService(
                new InMemoryDemoLaunchAcceptanceCloseoutArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T09:00:00Z"), ZoneOffset.UTC)
        );

        DemoLaunchAcceptanceCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary()).isEqualTo("No launch acceptance closeout archive is available for certification.");
        assertThat(certificate.nextAction()).isEqualTo("Archive an accepted launch acceptance closeout before downloading the certificate.");
        assertThat(certificate.archiveCount()).isZero();
        assertThat(certificate.latestCloseoutArchiveId()).isNull();
        assertThat(certificate.downloadActions()).containsExactly(
                "Archive a READY launch acceptance closeout before sharing a certificate."
        );
    }

    private static DemoLaunchAcceptanceCloseoutArchiveVo archive(
            String id,
            DemoReadinessStatus status,
            boolean accepted
    ) {
        return new DemoLaunchAcceptanceCloseoutArchiveVo(
                id,
                status,
                accepted,
                accepted
                        ? "PatchPilot launch acceptance closeout is complete."
                        : "PatchPilot launch acceptance closeout needs attention.",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "launch-evidence-archive-1",
                accepted ? "launch-delivery-receipt-1" : null,
                accepted ? "reviewer@example.com" : null,
                accepted ? "email" : null,
                accepted ? "FRESH" : "MISSING",
                Instant.parse("2026-06-28T08:30:00Z"),
                "# PatchPilot Launch Acceptance Closeout"
        );
    }
}
