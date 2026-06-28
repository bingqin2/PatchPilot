package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixTaskEvidencePackageAcceptanceCertificateArchiveServiceTests {

    @Test
    void should_archive_certified_task_evidence_acceptance_certificate() {
        FixTaskEvidencePackageAcceptanceCertificateArchiveService service =
                new FixTaskEvidencePackageAcceptanceCertificateArchiveService(
                        FixTaskEvidencePackageAcceptanceCertificateArchiveServiceTests::certificate,
                        new InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-28T07:35:00Z"), ZoneOffset.UTC),
                        () -> "task-evidence-certificate-archive-1"
                );

        FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive = service.archiveCurrentCertificate();

        assertThat(archive.id()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.certified()).isTrue();
        assertThat(archive.latestCloseoutArchiveId()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(archive.latestEvidenceArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-28T07:30:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-28T07:35:00Z"));
        assertThat(archive.report()).contains("# PatchPilot Task Evidence Acceptance Certificate");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("task-evidence-certificate-archive-1")).contains(archive);
    }

    @Test
    void should_reject_uncertified_certificate_archive() {
        FixTaskEvidencePackageAcceptanceCertificateArchiveService service =
                new FixTaskEvidencePackageAcceptanceCertificateArchiveService(
                        () -> new FixTaskEvidencePackageAcceptanceCertificateVo(
                                "NEEDS_ATTENTION",
                                false,
                                "Latest task evidence acceptance closeout archive is not accepted.",
                                "Resolve closeout blockers.",
                                1,
                                "task-evidence-closeout-archive-1",
                                "task-evidence-archive-1",
                                null,
                                "task-1",
                                "https://github.com/bingqin2/PatchPilot/pull/8",
                                null,
                                null,
                                "MISSING",
                                Instant.parse("2026-06-28T07:00:00Z"),
                                Instant.parse("2026-06-28T07:30:00Z"),
                                List.of("Archive a READY task evidence acceptance closeout before sharing a certificate."),
                                "# PatchPilot Task Evidence Acceptance Certificate"
                        ),
                        new InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-06-28T07:35:00Z"), ZoneOffset.UTC),
                        () -> "task-evidence-certificate-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentCertificate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Task evidence acceptance certificate must be certified before archiving");
        assertThat(service.listRecentArchives()).isEmpty();
    }

    private static FixTaskEvidencePackageAcceptanceCertificateVo certificate() {
        return new FixTaskEvidencePackageAcceptanceCertificateVo(
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
                List.of("Download task evidence acceptance certificate."),
                "# PatchPilot Task Evidence Acceptance Certificate"
        );
    }
}
