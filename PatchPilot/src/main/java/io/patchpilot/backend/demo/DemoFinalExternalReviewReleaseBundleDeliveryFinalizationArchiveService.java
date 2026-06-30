package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo> finalizationSupplier;
    private final DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationService finalizationService,
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository archiveRepository
    ) {
        this(
                finalizationService::getFinalizationGate,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService(
            Supplier<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo> finalizationSupplier,
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.finalizationSupplier = finalizationSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archiveCurrentFinalization() {
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationVo finalization = finalizationSupplier.get();
        if (!finalization.finalized() || finalization.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final external-review release bundle delivery finalization is not ready");
        }
        DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive =
                DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert.fromFinalization(
                        idSupplier.get(),
                        finalization,
                        Instant.now(clock)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
