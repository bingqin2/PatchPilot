package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo> finalizationSupplier;
    private final DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService finalizationService,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository archiveRepository
    ) {
        this(
                finalizationService::getFinalizationGate,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService(
            Supplier<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo> finalizationSupplier,
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.finalizationSupplier = finalizationSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archiveCurrentFinalization() {
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo finalization = finalizationSupplier.get();
        if (!finalization.finalized() || finalization.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final external-review package delivery finalization is not ready");
        }
        DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive =
                DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert.fromFinalization(
                        idSupplier.get(),
                        finalization,
                        Instant.now(clock)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
