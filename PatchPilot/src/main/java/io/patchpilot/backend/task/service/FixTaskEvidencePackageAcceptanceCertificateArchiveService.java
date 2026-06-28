package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class FixTaskEvidencePackageAcceptanceCertificateArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<FixTaskEvidencePackageAcceptanceCertificateVo> certificateSupplier;
    private final FixTaskEvidencePackageAcceptanceCertificateArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public FixTaskEvidencePackageAcceptanceCertificateArchiveService(
            FixTaskEvidencePackageAcceptanceCertificateService certificateService,
            FixTaskEvidencePackageAcceptanceCertificateArchiveRepository archiveRepository
    ) {
        this(
                certificateService::getCertificate,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    FixTaskEvidencePackageAcceptanceCertificateArchiveService(
            Supplier<FixTaskEvidencePackageAcceptanceCertificateVo> certificateSupplier,
            FixTaskEvidencePackageAcceptanceCertificateArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.certificateSupplier = certificateSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public FixTaskEvidencePackageAcceptanceCertificateArchiveVo archiveCurrentCertificate() {
        FixTaskEvidencePackageAcceptanceCertificateVo certificate = certificateSupplier.get();
        if (!certificate.certified()) {
            throw new IllegalStateException("Task evidence acceptance certificate must be certified before archiving");
        }
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive =
                new FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
                        idSupplier.get(),
                        certificate.status(),
                        certificate.certified(),
                        certificate.summary(),
                        certificate.nextAction(),
                        certificate.archiveCount(),
                        certificate.latestCloseoutArchiveId(),
                        certificate.latestEvidenceArchiveId(),
                        certificate.latestDeliveryReceiptId(),
                        certificate.latestTaskId(),
                        certificate.latestPullRequestUrl(),
                        certificate.latestDeliveryTarget(),
                        certificate.latestDeliveryChannel(),
                        certificate.deliveryReceiptFreshness(),
                        certificate.latestArchivedAt(),
                        certificate.generatedAt(),
                        Instant.now(clock),
                        certificate.downloadActions(),
                        certificate.markdownReport()
                );
        return archiveRepository.save(archive);
    }

    public List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
