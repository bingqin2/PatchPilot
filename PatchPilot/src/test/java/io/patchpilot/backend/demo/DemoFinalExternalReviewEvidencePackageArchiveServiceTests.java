package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalExternalReviewEvidencePackageArchiveServiceTests {

    @Test
    void archives_ready_final_external_review_evidence_package() {
        DemoFinalExternalReviewEvidencePackageArchiveService service =
                new DemoFinalExternalReviewEvidencePackageArchiveService(
                        DemoFinalExternalReviewEvidencePackageArchiveServiceTests::readyPackage,
                        new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T08:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-archive-1"
                );

        DemoFinalExternalReviewEvidencePackageArchiveVo archive = service.archiveCurrentPackage();

        assertThat(archive.id()).isEqualTo("final-external-review-package-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.readyForExternalReview()).isTrue();
        assertThat(archive.summary()).isEqualTo("PatchPilot final external-review evidence package is ready.");
        assertThat(archive.nextAction()).isEqualTo("Share this package with reviewers as the frozen external-review record.");
        assertThat(archive.latestTaskId()).isEqualTo("task-2");
        assertThat(archive.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.finalAcceptanceSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(archive.completionArchiveId()).isEqualTo("final-acceptance-completion-archive-1");
        assertThat(archive.completionEvidenceDeliveryReceiptId())
                .isEqualTo("final-acceptance-completion-evidence-delivery-receipt-1");
        assertThat(archive.closeoutArchiveId()).isEqualTo("final-acceptance-completion-closeout-archive-1");
        assertThat(archive.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(archive.deliveryChannel()).isEqualTo("email");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.evidenceNotes())
                .contains("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed.");
        assertThat(archive.downloadActions()).contains("Download final external-review evidence package.");
        assertThat(archive.sideEffectContract()).contains("read-only");
        assertThat(archive.report()).contains("# PatchPilot Final External Review Evidence Package");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-29T08:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-29T08:30:00Z"));
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-external-review-package-archive-1")).contains(archive);
    }

    @Test
    void rejects_archive_when_package_is_not_ready_for_external_review() {
        DemoFinalExternalReviewEvidencePackageArchiveService service =
                new DemoFinalExternalReviewEvidencePackageArchiveService(
                        DemoFinalExternalReviewEvidencePackageArchiveServiceTests::waitingPackage,
                        new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T08:30:00Z"), ZoneOffset.UTC),
                        () -> "final-external-review-package-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentPackage)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final external-review evidence package is not ready");
    }

    @Test
    void keeps_only_twenty_recent_package_archives() {
        DemoFinalExternalReviewEvidencePackageArchiveService service =
                new DemoFinalExternalReviewEvidencePackageArchiveService(
                        DemoFinalExternalReviewEvidencePackageArchiveServiceTests::readyPackage,
                        new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-29T08:30:00Z"), ZoneOffset.UTC),
                        new IncrementingIdSupplier()
                );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentPackage();
        }

        assertThat(service.listRecentArchives())
                .hasSize(20)
                .extracting(DemoFinalExternalReviewEvidencePackageArchiveVo::id)
                .containsExactly(
                        "final-external-review-package-archive-22",
                        "final-external-review-package-archive-21",
                        "final-external-review-package-archive-20",
                        "final-external-review-package-archive-19",
                        "final-external-review-package-archive-18",
                        "final-external-review-package-archive-17",
                        "final-external-review-package-archive-16",
                        "final-external-review-package-archive-15",
                        "final-external-review-package-archive-14",
                        "final-external-review-package-archive-13",
                        "final-external-review-package-archive-12",
                        "final-external-review-package-archive-11",
                        "final-external-review-package-archive-10",
                        "final-external-review-package-archive-9",
                        "final-external-review-package-archive-8",
                        "final-external-review-package-archive-7",
                        "final-external-review-package-archive-6",
                        "final-external-review-package-archive-5",
                        "final-external-review-package-archive-4",
                        "final-external-review-package-archive-3"
                );
        assertThat(service.findArchive("final-external-review-package-archive-1")).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageVo readyPackage() {
        return new DemoFinalExternalReviewEvidencePackageVo(
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "task-2",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "final-acceptance-share-package-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "reviewer@example.com",
                "email",
                "2026-06-29T07:45:00Z",
                "FRESH",
                Instant.parse("2026-06-29T07:50:00Z"),
                Instant.parse("2026-06-29T08:00:00Z"),
                List.of(new DemoFinalExternalReviewEvidencePackageVo.Check(
                        "Frozen closeout archive",
                        DemoReadinessStatus.READY,
                        "Frozen closeout archive final-acceptance-completion-closeout-archive-1 is closed.",
                        "No action needed."
                )),
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of(
                        "Download final external-review evidence package.",
                        "Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1."
                ),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package\n"
        );
    }

    private static DemoFinalExternalReviewEvidencePackageVo waitingPackage() {
        DemoFinalExternalReviewEvidencePackageVo ready = readyPackage();
        return new DemoFinalExternalReviewEvidencePackageVo(
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                "PatchPilot final external-review evidence package is waiting for a frozen closeout archive.",
                "Archive the READY final acceptance completion closeout before sharing the final external-review package.",
                ready.finalAcceptanceSummaryStatus(),
                ready.finalAcceptanceShareFinalizationStatus(),
                ready.completionEvidenceBundleStatus(),
                ready.completionDeliveryFinalizationStatus(),
                ready.completionCloseoutStatus(),
                DemoReadinessStatus.NEEDS_ATTENTION,
                ready.latestTaskId(),
                ready.latestPullRequestUrl(),
                ready.finalAcceptanceSharePackageArchiveId(),
                ready.completionArchiveId(),
                ready.completionEvidenceDeliveryReceiptId(),
                null,
                ready.deliveryTarget(),
                ready.deliveryChannel(),
                ready.deliveredAt(),
                ready.deliveryReceiptFreshness(),
                null,
                ready.generatedAt(),
                ready.checks(),
                ready.evidenceNotes(),
                ready.downloadActions(),
                ready.sideEffectContract(),
                ready.markdownReport()
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-external-review-package-archive-" + nextId++;
        }
    }
}
