package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepositoryTests {

    @Test
    void should_list_recent_archives_and_find_by_id() {
        InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository();
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo older =
                archive("task-evidence-certificate-archive-old", "2026-06-28T07:35:00Z");
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo newer =
                archive("task-evidence-certificate-archive-new", "2026-06-28T07:40:00Z");

        repository.save(older);
        repository.save(newer);

        assertThat(repository.listRecentArchives(1)).containsExactly(newer);
        assertThat(repository.listRecentArchives(20)).containsExactly(newer, older);
        assertThat(repository.findById("task-evidence-certificate-archive-old")).contains(older);
        assertThat(repository.findById("missing-certificate-archive")).isEmpty();
    }

    @Test
    void should_keep_only_twenty_recent_archives() {
        InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository();
        for (int index = 1; index <= 22; index++) {
            repository.save(archive("task-evidence-certificate-archive-" + index, "2026-06-28T07:35:00Z"));
        }

        assertThat(repository.listRecentArchives(25)).hasSize(20);
        assertThat(repository.findById("task-evidence-certificate-archive-1")).isEmpty();
    }

    static FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive(String id, String archivedAt) {
        return new FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
                id,
                "READY",
                true,
                "Task evidence acceptance is certified from the latest accepted closeout archive.",
                "Share the certificate and archived closeout report with reviewers.",
                1,
                "task-evidence-closeout-archive-1",
                "task-evidence-archive-1",
                "task-evidence-delivery-receipt-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                "reviewer@example.com",
                "email",
                "FRESH",
                Instant.parse("2026-06-28T07:00:00Z"),
                Instant.parse("2026-06-28T07:30:00Z"),
                Instant.parse(archivedAt),
                List.of("Download task evidence acceptance certificate."),
                "# PatchPilot Task Evidence Acceptance Certificate"
        );
    }
}
