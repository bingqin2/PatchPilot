package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewDeliveryCertificateArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewDeliveryCertificateArchiveServiceTests {

    @Test
    void archives_certified_final_external_review_delivery_certificate() {
        DemoFinalExternalReviewDeliveryCertificateArchiveService service =
                new DemoFinalExternalReviewDeliveryCertificateArchiveService(
                        DemoFinalExternalReviewDeliveryCertificateArchiveServiceTests::certifiedCertificate,
                        new InMemoryDemoFinalExternalReviewDeliveryCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T11:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-delivery-certificate-archive-1"
                );

        DemoFinalExternalReviewDeliveryCertificateArchiveVo archive = service.archiveCurrentCertificate();

        assertThat(archive.id()).isEqualTo("final-external-review-delivery-certificate-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.certified()).isTrue();
        assertThat(archive.summary())
                .isEqualTo("Final external-review delivery is certified from the latest finalized archive.");
        assertThat(archive.nextAction())
                .isEqualTo("Share the certificate report with reviewers as the final external-review delivery proof.");
        assertThat(archive.latestDeliveryFinalizationArchiveId())
                .isEqualTo("final-external-review-package-delivery-finalization-archive-1");
        assertThat(archive.latestPackageArchiveId()).isEqualTo("final-external-review-package-archive-1");
        assertThat(archive.latestDeliveryReceiptId())
                .isEqualTo("final-external-review-package-delivery-receipt-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/8");
        assertThat(archive.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.latestDeliveryChannel()).isEqualTo("email");
        assertThat(archive.latestDeliveredAt()).isEqualTo("2026-06-29T09:25:00Z");
        assertThat(archive.latestArchivedAt()).isEqualTo(Instant.parse("2026-06-29T10:30:00Z"));
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.deliveryReceiptFresh()).isTrue();
        assertThat(archive.checks())
                .extracting(DemoFinalExternalReviewDeliveryCertificateArchiveVo.Check::name)
                .containsExactly("Final external-review delivery finalization archive");
        assertThat(archive.evidenceNotes())
                .contains("Final external-review delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is finalized.");
        assertThat(archive.downloadActions())
                .contains("Download final external-review delivery certificate report.");
        assertThat(archive.sideEffectContract()).contains("read-only");
        assertThat(archive.report()).contains("# PatchPilot Final External Review Delivery Certificate");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T11:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T11:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-external-review-delivery-certificate-archive-1")).contains(archive);
    }

    @Test
    void rejects_archive_when_certificate_is_not_certified() {
        DemoFinalExternalReviewDeliveryCertificateArchiveService service =
                new DemoFinalExternalReviewDeliveryCertificateArchiveService(
                        DemoFinalExternalReviewDeliveryCertificateArchiveServiceTests::uncertifiedCertificate,
                        new InMemoryDemoFinalExternalReviewDeliveryCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T11:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-delivery-certificate-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentCertificate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review delivery certificate is not certified");
    }

    @Test
    void keeps_only_twenty_recent_certificate_archives() {
        DemoFinalExternalReviewDeliveryCertificateArchiveService service =
                new DemoFinalExternalReviewDeliveryCertificateArchiveService(
                        DemoFinalExternalReviewDeliveryCertificateArchiveServiceTests::certifiedCertificate,
                        new InMemoryDemoFinalExternalReviewDeliveryCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T11:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentCertificate();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalExternalReviewDeliveryCertificateArchiveVo::id)
                .containsExactly(
                        "final-external-review-delivery-certificate-archive-22",
                        "final-external-review-delivery-certificate-archive-21",
                        "final-external-review-delivery-certificate-archive-20",
                        "final-external-review-delivery-certificate-archive-19",
                        "final-external-review-delivery-certificate-archive-18",
                        "final-external-review-delivery-certificate-archive-17",
                        "final-external-review-delivery-certificate-archive-16",
                        "final-external-review-delivery-certificate-archive-15",
                        "final-external-review-delivery-certificate-archive-14",
                        "final-external-review-delivery-certificate-archive-13",
                        "final-external-review-delivery-certificate-archive-12",
                        "final-external-review-delivery-certificate-archive-11",
                        "final-external-review-delivery-certificate-archive-10",
                        "final-external-review-delivery-certificate-archive-9",
                        "final-external-review-delivery-certificate-archive-8",
                        "final-external-review-delivery-certificate-archive-7",
                        "final-external-review-delivery-certificate-archive-6",
                        "final-external-review-delivery-certificate-archive-5",
                        "final-external-review-delivery-certificate-archive-4",
                        "final-external-review-delivery-certificate-archive-3"
                );
        assertThat(service.findArchive("final-external-review-delivery-certificate-archive-1")).isEmpty();
    }

    private static DemoFinalExternalReviewDeliveryCertificateVo certifiedCertificate() {
        return new DemoFinalExternalReviewDeliveryCertificateVo(
                DemoReadinessStatus.READY,
                true,
                "Final external-review delivery is certified from the latest finalized archive.",
                "Share the certificate report with reviewers as the final external-review delivery proof.",
                "final-external-review-package-delivery-finalization-archive-1",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                Instant.parse("2026-06-29T10:30:00Z"),
                "FRESH",
                true,
                List.of(new DemoFinalExternalReviewDeliveryCertificateVo.Check(
                        "Final external-review delivery finalization archive",
                        DemoReadinessStatus.READY,
                        "Latest final external-review delivery finalization archive is finalized.",
                        "No action needed."
                )),
                List.of("Final external-review delivery finalization archive final-external-review-package-delivery-finalization-archive-1 is finalized."),
                List.of("Download final external-review delivery certificate report."),
                "GET /api/demo/final-external-review-delivery-certificate is read-only.",
                "# PatchPilot Final External Review Delivery Certificate",
                Instant.parse("2026-06-29T11:00:00Z")
        );
    }

    private static DemoFinalExternalReviewDeliveryCertificateVo uncertifiedCertificate() {
        DemoFinalExternalReviewDeliveryCertificateVo certified = certifiedCertificate();
        return new DemoFinalExternalReviewDeliveryCertificateVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "Latest final external-review delivery finalization archive does not have a fresh delivery receipt.",
                "Record a fresh final external-review package delivery receipt, archive the READY finalization, then download the certificate.",
                certified.latestDeliveryFinalizationArchiveId(),
                certified.latestPackageArchiveId(),
                null,
                certified.latestTaskId(),
                certified.latestPullRequestUrl(),
                certified.latestDeliveryTarget(),
                certified.latestDeliveryChannel(),
                null,
                certified.latestArchivedAt(),
                "MISSING",
                false,
                certified.checks(),
                certified.evidenceNotes(),
                certified.downloadActions(),
                certified.sideEffectContract(),
                certified.markdownReport(),
                certified.generatedAt()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-external-review-delivery-certificate-archive-" + nextId++;
        }
    }
}
