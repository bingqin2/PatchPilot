package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepositoryTests {

    @Test
    void stores_recent_delivery_finalization_archives_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository repository =
                new InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(archive("archive-" + index, Instant.parse("2026-06-29T10:00:00Z").plusSeconds(index)));
        }

        assertThat(repository.listRecentArchives(5))
                .extracting(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo::id)
                .containsExactly("archive-22", "archive-21", "archive-20", "archive-19", "archive-18");
        assertThat(repository.findById("archive-22")).isPresent();
        assertThat(repository.findById("archive-1")).isEmpty();
    }

    static DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive(
            String id,
            Instant archivedAt
    ) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "Final external-review package delivery is finalized with a fresh package delivery receipt.",
                "Use the finalization report as proof that the frozen external-review package was delivered.",
                "final-external-review-package-archive-1",
                "final-external-review-package-delivery-receipt-1",
                "final-acceptance-completion-closeout-archive-1",
                "final-acceptance-completion-archive-1",
                "final-acceptance-completion-evidence-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "2026-06-29T09:25:00Z",
                "FRESH",
                true,
                "Latest package delivery receipt matches the current frozen final external-review package.",
                List.of(new DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo.Check(
                        "Frozen final external-review package",
                        DemoReadinessStatus.READY,
                        "Frozen final external-review package is ready.",
                        "No action needed."
                )),
                List.of("Frozen final external-review package final-external-review-package-archive-1 is ready."),
                List.of("Download final external-review package delivery finalization report."),
                "GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.",
                "# PatchPilot Final External Review Package Delivery Finalization",
                Instant.parse("2026-06-29T10:00:00Z"),
                archivedAt
        );
    }
}
