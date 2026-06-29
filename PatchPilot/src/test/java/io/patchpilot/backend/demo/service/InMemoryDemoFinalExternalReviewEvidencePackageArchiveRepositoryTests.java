package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests {

    @Test
    void stores_recent_package_archives_newest_first_and_trims_old_entries() {
        InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository repository =
                new InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepository();

        for (int index = 1; index <= 22; index++) {
            repository.save(archive("archive-" + index, Instant.parse("2026-06-29T08:00:00Z").plusSeconds(index)));
        }

        assertThat(repository.listRecentArchives(5))
                .extracting(DemoFinalExternalReviewEvidencePackageArchiveVo::id)
                .containsExactly("archive-22", "archive-21", "archive-20", "archive-19", "archive-18");
        assertThat(repository.findById("archive-22")).isPresent();
        assertThat(repository.findById("archive-1")).isEmpty();
    }

    private static DemoFinalExternalReviewEvidencePackageArchiveVo archive(String id, Instant archivedAt) {
        return new DemoFinalExternalReviewEvidencePackageArchiveVo(
                id,
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final external-review evidence package is ready.",
                "Share this package with reviewers as the frozen external-review record.",
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
                List.of("Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed."),
                List.of("Download final external-review evidence package."),
                "GET /api/demo/final-external-review-evidence-package is read-only.",
                "# PatchPilot Final External Review Evidence Package",
                Instant.parse("2026-06-29T08:00:00Z"),
                archivedAt
        );
    }
}
