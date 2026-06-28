package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageAcceptanceCertificateServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-28T07:30:00Z"), ZoneOffset.UTC);

    @Test
    void should_certify_latest_ready_accepted_closeout_archive() {
        InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository();
        repository.save(closeout("task-evidence-closeout-archive-1", "READY", true));
        FixTaskEvidencePackageAcceptanceCertificateService service =
                new FixTaskEvidencePackageAcceptanceCertificateService(repository, CLOCK);

        FixTaskEvidencePackageAcceptanceCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo("READY");
        assertThat(certificate.certified()).isTrue();
        assertThat(certificate.summary())
                .isEqualTo("Task evidence acceptance is certified from the latest accepted closeout archive.");
        assertThat(certificate.nextAction()).isEqualTo("Share the certificate and archived closeout report with reviewers.");
        assertThat(certificate.archiveCount()).isEqualTo(1);
        assertThat(certificate.latestCloseoutArchiveId()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(certificate.latestEvidenceArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(certificate.latestDeliveryReceiptId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(certificate.latestTaskId()).isEqualTo("task-1");
        assertThat(certificate.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(certificate.generatedAt()).isEqualTo(Instant.parse("2026-06-28T07:30:00Z"));
        assertThat(certificate.downloadActions()).containsExactly(
                "Download task evidence acceptance certificate.",
                "Download task evidence acceptance closeout archive task-evidence-closeout-archive-1.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/8 for review."
        );
        assertThat(certificate.markdownReport())
                .contains("# PatchPilot Task Evidence Acceptance Certificate")
                .contains("- Certified: `true`")
                .contains("- Closeout archive: `task-evidence-closeout-archive-1`")
                .contains("GET /api/tasks/evidence-packages/acceptance-certificate is read-only");
    }

    @Test
    void should_need_attention_when_latest_closeout_is_not_accepted() {
        InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository();
        repository.save(closeout("task-evidence-closeout-archive-1", "NEEDS_ATTENTION", false));
        FixTaskEvidencePackageAcceptanceCertificateService service =
                new FixTaskEvidencePackageAcceptanceCertificateService(repository, CLOCK);

        FixTaskEvidencePackageAcceptanceCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary()).isEqualTo("Latest task evidence acceptance closeout archive is not accepted.");
        assertThat(certificate.nextAction())
                .isEqualTo("Resolve closeout blockers, archive a READY accepted closeout, then download the certificate.");
        assertThat(certificate.latestCloseoutArchiveId()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(certificate.downloadActions()).containsExactly(
                "Archive a READY task evidence acceptance closeout before sharing a certificate."
        );
    }

    @Test
    void should_need_attention_when_no_closeout_archive_exists() {
        FixTaskEvidencePackageAcceptanceCertificateService service =
                new FixTaskEvidencePackageAcceptanceCertificateService(
                        new InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository(),
                        CLOCK
                );

        FixTaskEvidencePackageAcceptanceCertificateVo certificate = service.getCertificate();

        assertThat(certificate.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(certificate.certified()).isFalse();
        assertThat(certificate.summary())
                .isEqualTo("No task evidence acceptance closeout archive is available for certification.");
        assertThat(certificate.nextAction())
                .isEqualTo("Archive an accepted task evidence closeout before downloading the certificate.");
        assertThat(certificate.archiveCount()).isZero();
        assertThat(certificate.latestCloseoutArchiveId()).isNull();
        assertThat(certificate.deliveryReceiptFreshness()).isEqualTo("MISSING");
    }

    private static FixTaskEvidencePackageAcceptanceCloseoutArchiveVo closeout(
            String id,
            String status,
            boolean accepted
    ) {
        return new FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
                id,
                status,
                accepted,
                accepted
                        ? "Task evidence is finalized with a fresh delivery receipt for the current shareable archive."
                        : "Task evidence needs attention.",
                "task-evidence-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                accepted ? "task-evidence-delivery-receipt-1" : null,
                accepted ? "reviewer@example.com" : null,
                accepted ? "email" : null,
                accepted ? "FRESH" : "MISSING",
                Instant.parse("2026-06-28T07:00:00Z"),
                "# PatchPilot Task Evidence Acceptance Closeout Archive"
        );
    }
}
