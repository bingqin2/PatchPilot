package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLiveDemoCompletionCertificateServiceTests {

    @Test
    void should_certify_ready_live_demo_completion_from_latest_finalization_archive() {
        DemoLiveDemoCompletionCertificateService certificateService = certificateService();
        DemoLiveDemoCompletionCertificateVo certificate = certificateService.getCertificate();

        assertThat(certificate.status()).isEqualTo("READY");
        assertThat(certificate.certified()).isTrue();
        assertThat(certificate.summary())
                .isEqualTo("PatchPilot live demo is certified from the latest handoff finalization archive.");
        assertThat(certificate.nextAction())
                .isEqualTo("Share the live demo completion certificate with reviewers.");
        assertThat(certificate.latestFinalizationArchiveId())
                .isEqualTo("live-demo-handoff-delivery-finalization-archive-1");
        assertThat(certificate.latestDeliveryReceiptId())
                .isEqualTo("live-demo-handoff-delivery-receipt-1");
        assertThat(certificate.evidenceBundleArchiveId()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(certificate.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(certificate.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(certificate.taskId()).isEqualTo("task-1");
        assertThat(certificate.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(certificate.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(certificate.latestFinalizationArchivedAt()).isEqualTo(Instant.parse("2026-07-02T07:00:00Z"));
        assertThat(certificate.generatedAt()).isEqualTo(Instant.parse("2026-07-02T08:00:00Z"));
        assertThat(certificate.downloadActions()).containsExactly(
                "Download live demo completion certificate.",
                "Download live demo handoff delivery finalization archive live-demo-handoff-delivery-finalization-archive-1.",
                "Download live demo handoff delivery receipt live-demo-handoff-delivery-receipt-1.",
                "Download live demo evidence bundle archive live-demo-evidence-bundle-archive-1.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review."
        );
        assertThat(certificate.markdownReport())
                .contains("# PatchPilot Live Demo Completion Certificate")
                .contains("- Certified: `true`")
                .contains("live-demo-handoff-delivery-finalization-archive-1")
                .contains("live-demo-handoff-delivery-receipt-1")
                .contains("live-demo-evidence-bundle-archive-1")
                .contains("GET /api/demo/live-demo-handoff-package/completion-certificate is read-only");
    }

    @Test
    void should_need_attention_without_finalization_archive() {
        DemoLiveDemoCompletionCertificateService certificateService = new DemoLiveDemoCompletionCertificateService(
                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                Clock.fixed(Instant.parse("2026-07-02T08:00:00Z"), ZoneOffset.UTC)
        );

        DemoLiveDemoCompletionCertificateVo certificate = certificateService.getCertificate();

        assertThat(certificate.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary())
                .isEqualTo("No live demo handoff delivery finalization archive is available for certification.");
        assertThat(certificate.nextAction())
                .isEqualTo("Archive a READY live demo handoff delivery finalization before downloading the completion certificate.");
        assertThat(certificate.latestFinalizationArchiveId()).isNull();
        assertThat(certificate.downloadActions()).containsExactly(
                "Archive a READY live demo handoff delivery finalization before sharing a completion certificate."
        );
    }

    @Test
    void should_archive_certified_live_demo_completion_certificate() {
        DemoLiveDemoCompletionCertificateArchiveService archiveService = archiveService();

        DemoLiveDemoCompletionCertificateArchiveVo archive = archiveService.archiveCurrentCertificate();

        assertThat(archive.id()).isEqualTo("live-demo-completion-certificate-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.certified()).isTrue();
        assertThat(archive.latestFinalizationArchiveId())
                .isEqualTo("live-demo-handoff-delivery-finalization-archive-1");
        assertThat(archive.latestDeliveryReceiptId())
                .isEqualTo("live-demo-handoff-delivery-receipt-1");
        assertThat(archive.evidenceBundleArchiveId()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-07-02T08:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-07-02T09:00:00Z"));
        assertThat(archive.report()).contains("# PatchPilot Live Demo Completion Certificate");
        assertThat(archiveService.listRecentArchives()).containsExactly(archive);
        assertThat(archiveService.findArchive("live-demo-completion-certificate-archive-1")).contains(archive);
    }

    @Test
    void should_reject_completion_certificate_archive_when_certificate_is_not_certified() {
        DemoLiveDemoCompletionCertificateArchiveService archiveService =
                new DemoLiveDemoCompletionCertificateArchiveService(
                        () -> new DemoLiveDemoCompletionCertificateService(
                                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                                Clock.fixed(Instant.parse("2026-07-02T08:00:00Z"), ZoneOffset.UTC)
                        ).getCertificate(),
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository(),
                        () -> "live-demo-completion-certificate-archive-1",
                        () -> Instant.parse("2026-07-02T09:00:00Z")
                );

        assertThatThrownBy(archiveService::archiveCurrentCertificate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Certified live demo completion certificate is required");
    }

    @Test
    void should_keep_latest_twenty_completion_certificate_archives_first() {
        java.util.concurrent.atomic.AtomicInteger nextId = new java.util.concurrent.atomic.AtomicInteger();
        DemoLiveDemoCompletionCertificateArchiveService archiveService =
                new DemoLiveDemoCompletionCertificateArchiveService(
                        certificateService()::getCertificate,
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository(),
                        () -> "archive-" + nextId.incrementAndGet(),
                        () -> Instant.parse("2026-07-02T09:00:00Z")
                );

        for (int index = 0; index < 21; index++) {
            archiveService.archiveCurrentCertificate();
        }

        assertThat(archiveService.listRecentArchives()).hasSize(20);
        assertThat(archiveService.listRecentArchives().get(0).id()).isEqualTo("archive-21");
        assertThat(archiveService.listRecentArchives())
                .extracting(DemoLiveDemoCompletionCertificateArchiveVo::id)
                .doesNotContain("archive-1");
    }

    private static DemoLiveDemoCompletionCertificateArchiveService archiveService() {
        return new DemoLiveDemoCompletionCertificateArchiveService(
                certificateService()::getCertificate,
                new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository(),
                () -> "live-demo-completion-certificate-archive-1",
                () -> Instant.parse("2026-07-02T09:00:00Z")
        );
    }

    private static DemoLiveDemoCompletionCertificateService certificateService() {
        InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository repository =
                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository();
        repository.save(finalizationArchiveService().archiveFinalization());
        return new DemoLiveDemoCompletionCertificateService(
                repository,
                Clock.fixed(Instant.parse("2026-07-02T08:00:00Z"), ZoneOffset.UTC)
        );
    }

    private static DemoLiveDemoHandoffDeliveryFinalizationArchiveService finalizationArchiveService() {
        return new DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                        () -> java.util.List.of(freshReceipt()),
                        Clock.fixed(Instant.parse("2026-07-02T06:00:00Z"), ZoneOffset.UTC)
                ),
                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                () -> "live-demo-handoff-delivery-finalization-archive-1",
                () -> Instant.parse("2026-07-02T07:00:00Z")
        );
    }

    private static io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo freshReceipt() {
        return new io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo(
                "live-demo-handoff-delivery-receipt-1",
                "READY",
                "READY",
                "live-demo-evidence-bundle-archive-1",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "Live demo handoff package delivery receipt is recorded.",
                "github-comment",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "local-operator",
                "Sent live demo handoff package.",
                Instant.parse("2026-07-02T05:30:00Z"),
                Instant.parse("2026-07-02T05:35:00Z"),
                "# PatchPilot Live Demo Handoff Delivery Receipt"
        );
    }
}
