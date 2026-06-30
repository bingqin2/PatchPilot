package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo> certificateSupplier;
    private final DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateService certificateService,
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository archiveRepository
    ) {
        this(certificateService::getCertificate, archiveRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService(
            Supplier<DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo> certificateSupplier,
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.certificateSupplier = certificateSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archiveCurrentCertificate() {
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo certificate = certificateSupplier.get();
        if (!certificate.certified() || certificate.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException(
                    "final external-review release bundle delivery certificate is not certified"
            );
        }
        DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive =
                DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert.fromCertificate(
                        idSupplier.get(),
                        certificate,
                        Instant.now(clock)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
