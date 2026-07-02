package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveDemoCompletionCertificateArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoLiveDemoCompletionCertificateVo> certificateSupplier;
    private final DemoLiveDemoCompletionCertificateArchiveRepository archiveRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveDemoCompletionCertificateArchiveService(
            DemoLiveDemoCompletionCertificateService certificateService,
            DemoLiveDemoCompletionCertificateArchiveRepository archiveRepository
    ) {
        this(certificateService::getCertificate, archiveRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveDemoCompletionCertificateArchiveService(
            Supplier<DemoLiveDemoCompletionCertificateVo> certificateSupplier,
            DemoLiveDemoCompletionCertificateArchiveRepository archiveRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.certificateSupplier = certificateSupplier;
        this.archiveRepository = archiveRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveDemoCompletionCertificateArchiveVo archiveCurrentCertificate() {
        DemoLiveDemoCompletionCertificateVo certificate = certificateSupplier.get();
        if (!certificate.certified() || !"READY".equals(certificate.status())) {
            throw new IllegalStateException("Certified live demo completion certificate is required before archiving.");
        }
        DemoLiveDemoCompletionCertificateArchiveVo archive = new DemoLiveDemoCompletionCertificateArchiveVo(
                idSupplier.get(),
                certificate.status(),
                certificate.certified(),
                certificate.summary(),
                certificate.nextAction(),
                certificate.latestFinalizationArchiveId(),
                certificate.latestDeliveryReceiptId(),
                certificate.evidenceBundleArchiveId(),
                certificate.repository(),
                certificate.issueNumber(),
                certificate.issueUrl(),
                certificate.taskId(),
                certificate.taskStatus(),
                certificate.pullRequestUrl(),
                certificate.latestDeliveryTarget(),
                certificate.latestDeliveryChannel(),
                certificate.latestDeliveredAt(),
                certificate.deliveryReceiptFreshness(),
                certificate.latestFinalizationGeneratedAt(),
                certificate.latestFinalizationArchivedAt(),
                certificate.generatedAt(),
                nowSupplier.get(),
                certificate.downloadActions(),
                "Archive creation writes only PatchPilot local completion certificate archive records. "
                        + certificate.sideEffectContract(),
                certificate.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<DemoLiveDemoCompletionCertificateArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLiveDemoCompletionCertificateArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
