package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceCompletionArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalAcceptanceShareFinalizationVo> finalizationSupplier;
    private final DemoFinalAcceptanceCompletionArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalAcceptanceCompletionArchiveService(
            DemoFinalAcceptanceShareFinalizationService finalizationService,
            DemoFinalAcceptanceCompletionArchiveRepository archiveRepository
    ) {
        this(
                finalizationService::getFinalizationGate,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalAcceptanceCompletionArchiveService(
            Supplier<DemoFinalAcceptanceShareFinalizationVo> finalizationSupplier,
            DemoFinalAcceptanceCompletionArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.finalizationSupplier = finalizationSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalAcceptanceCompletionArchiveVo archiveCurrentCompletion() {
        DemoFinalAcceptanceShareFinalizationVo finalization = finalizationSupplier.get();
        if (!finalization.finalized() || finalization.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final acceptance share finalization is not ready");
        }
        DemoFinalAcceptanceCompletionArchiveVo archive = new DemoFinalAcceptanceCompletionArchiveVo(
                idSupplier.get(),
                finalization.status(),
                finalization.finalized(),
                finalization.summary(),
                finalization.nextAction(),
                finalization.latestArchiveId(),
                finalization.latestTaskId(),
                finalization.latestDeliveryReceiptId(),
                finalization.latestDeliveryTarget(),
                finalization.latestDeliveryChannel(),
                finalization.latestDeliveredAt(),
                finalization.deliveryReceiptFreshness(),
                finalization.deliveryReceiptFresh(),
                finalization.deliveryReceiptFreshnessSummary(),
                finalization.evidenceNotes(),
                finalization.markdownReport(),
                finalization.generatedAt(),
                Instant.now(clock)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalAcceptanceCompletionArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalAcceptanceCompletionArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
