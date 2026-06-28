package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateVo;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCertificateArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLaunchAcceptanceCertificateArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoLaunchAcceptanceCertificateVo> certificateSupplier;
    private final DemoLaunchAcceptanceCertificateArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoLaunchAcceptanceCertificateArchiveService(
            DemoLaunchAcceptanceCertificateService certificateService,
            DemoLaunchAcceptanceCertificateArchiveRepository archiveRepository
    ) {
        this(
                certificateService::getCertificate,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoLaunchAcceptanceCertificateArchiveService(
            Supplier<DemoLaunchAcceptanceCertificateVo> certificateSupplier,
            DemoLaunchAcceptanceCertificateArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.certificateSupplier = certificateSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoLaunchAcceptanceCertificateArchiveVo archiveCurrentCertificate() {
        DemoLaunchAcceptanceCertificateVo certificate = certificateSupplier.get();
        DemoLaunchAcceptanceCertificateArchiveVo archive = new DemoLaunchAcceptanceCertificateArchiveVo(
                idSupplier.get(),
                certificate.status(),
                certificate.certified(),
                certificate.summary(),
                certificate.nextAction(),
                certificate.archiveCount(),
                certificate.latestCloseoutArchiveId(),
                certificate.latestLaunchEvidenceArchiveId(),
                certificate.latestDeliveryReceiptId(),
                certificate.latestSessionId(),
                certificate.latestTaskId(),
                certificate.latestPullRequestUrl(),
                certificate.latestWebhookDeliveryId(),
                certificate.evaluationRunId(),
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

    public List<DemoLaunchAcceptanceCertificateArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLaunchAcceptanceCertificateArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
