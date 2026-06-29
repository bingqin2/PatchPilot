package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewDeliveryCertificateArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewDeliveryCertificateArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewDeliveryCertificateArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalExternalReviewDeliveryCertificateVo> certificateSupplier;
    private final DemoFinalExternalReviewDeliveryCertificateArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewDeliveryCertificateArchiveService(
            DemoFinalExternalReviewDeliveryCertificateService certificateService,
            DemoFinalExternalReviewDeliveryCertificateArchiveRepository archiveRepository
    ) {
        this(certificateService::getCertificate, archiveRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    DemoFinalExternalReviewDeliveryCertificateArchiveService(
            Supplier<DemoFinalExternalReviewDeliveryCertificateVo> certificateSupplier,
            DemoFinalExternalReviewDeliveryCertificateArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.certificateSupplier = certificateSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewDeliveryCertificateArchiveVo archiveCurrentCertificate() {
        DemoFinalExternalReviewDeliveryCertificateVo certificate = certificateSupplier.get();
        if (!certificate.certified() || certificate.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final external-review delivery certificate is not certified");
        }
        DemoFinalExternalReviewDeliveryCertificateArchiveVo archive =
                DemoFinalExternalReviewDeliveryCertificateArchiveConvert.fromCertificate(
                        idSupplier.get(),
                        certificate,
                        Instant.now(clock)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalExternalReviewDeliveryCertificateArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalExternalReviewDeliveryCertificateArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
