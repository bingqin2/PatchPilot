package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchAcceptanceCertificateArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchAcceptanceCertificateArchiveServiceTests {

    @Test
    void archives_current_launch_acceptance_certificate() {
        DemoLaunchAcceptanceCertificateArchiveService service = new DemoLaunchAcceptanceCertificateArchiveService(
                DemoLaunchAcceptanceCertificateArchiveServiceTests::certificate,
                new InMemoryDemoLaunchAcceptanceCertificateArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T10:30:00Z"), ZoneOffset.UTC),
                () -> "launch-certificate-archive-1"
        );

        DemoLaunchAcceptanceCertificateArchiveVo archive = service.archiveCurrentCertificate();

        assertThat(archive.id()).isEqualTo("launch-certificate-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.certified()).isTrue();
        assertThat(archive.summary()).isEqualTo("PatchPilot launch acceptance is certified from the latest accepted closeout archive.");
        assertThat(archive.nextAction()).isEqualTo("Share the certificate and archived closeout report with reviewers.");
        assertThat(archive.latestCloseoutArchiveId()).isEqualTo("launch-closeout-archive-1");
        assertThat(archive.latestLaunchEvidenceArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-28T09:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-28T10:30:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Launch Acceptance Certificate")
                .contains("launch-closeout-archive-1");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("launch-certificate-archive-1")).contains(archive);
    }

    @Test
    void keeps_only_twenty_recent_certificate_archives() {
        DemoLaunchAcceptanceCertificateArchiveService service = new DemoLaunchAcceptanceCertificateArchiveService(
                DemoLaunchAcceptanceCertificateArchiveServiceTests::certificate,
                new InMemoryDemoLaunchAcceptanceCertificateArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T10:30:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentCertificate();
        }

        List<DemoLaunchAcceptanceCertificateArchiveVo> archives = service.listRecentArchives();
        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(DemoLaunchAcceptanceCertificateArchiveVo::id)
                .containsExactly(
                        "launch-certificate-archive-22",
                        "launch-certificate-archive-21",
                        "launch-certificate-archive-20",
                        "launch-certificate-archive-19",
                        "launch-certificate-archive-18",
                        "launch-certificate-archive-17",
                        "launch-certificate-archive-16",
                        "launch-certificate-archive-15",
                        "launch-certificate-archive-14",
                        "launch-certificate-archive-13",
                        "launch-certificate-archive-12",
                        "launch-certificate-archive-11",
                        "launch-certificate-archive-10",
                        "launch-certificate-archive-9",
                        "launch-certificate-archive-8",
                        "launch-certificate-archive-7",
                        "launch-certificate-archive-6",
                        "launch-certificate-archive-5",
                        "launch-certificate-archive-4",
                        "launch-certificate-archive-3"
                );
        assertThat(service.findArchive("launch-certificate-archive-1")).isEmpty();
    }

    private static DemoLaunchAcceptanceCertificateVo certificate() {
        return new DemoLaunchAcceptanceCertificateVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot launch acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "launch-closeout-archive-1",
                "launch-evidence-archive-1",
                "launch-delivery-receipt-1",
                "demo-session-20260624T003000Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T08:30:00Z"),
                Instant.parse("2026-06-28T09:00:00Z"),
                List.of("Download launch acceptance certificate."),
                "# PatchPilot Launch Acceptance Certificate\n\n- Closeout archive: `launch-closeout-archive-1`\n"
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "launch-certificate-archive-" + nextId++;
        }
    }
}
