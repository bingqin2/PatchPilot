package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceCompletionCloseoutArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalAcceptanceCompletionCloseoutVo> closeoutSupplier;
    private final DemoFinalAcceptanceCompletionCloseoutArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalAcceptanceCompletionCloseoutArchiveService(
            DemoFinalAcceptanceCompletionCloseoutService closeoutService,
            DemoFinalAcceptanceCompletionCloseoutArchiveRepository archiveRepository
    ) {
        this(
                closeoutService::getCloseout,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalAcceptanceCompletionCloseoutArchiveService(
            Supplier<DemoFinalAcceptanceCompletionCloseoutVo> closeoutSupplier,
            DemoFinalAcceptanceCompletionCloseoutArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.closeoutSupplier = closeoutSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalAcceptanceCompletionCloseoutArchiveVo archiveCurrentCloseout() {
        DemoFinalAcceptanceCompletionCloseoutVo closeout = closeoutSupplier.get();
        if (!closeout.closed() || closeout.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final acceptance completion closeout is not ready");
        }
        DemoFinalAcceptanceCompletionCloseoutArchiveVo archive = new DemoFinalAcceptanceCompletionCloseoutArchiveVo(
                idSupplier.get(),
                closeout.status(),
                closeout.closed(),
                closeout.summary(),
                closeout.nextAction(),
                closeout.latestTaskId(),
                closeout.latestPullRequestUrl(),
                closeout.latestSharePackageArchiveId(),
                closeout.latestCompletionArchiveId(),
                closeout.latestCompletionEvidenceDeliveryReceiptId(),
                closeout.latestDeliveryTarget(),
                closeout.latestDeliveryChannel(),
                closeout.latestDeliveredAt(),
                closeout.deliveryReceiptFreshness(),
                closeout.evidenceNotes(),
                closeout.downloadActions(),
                closeout.sideEffectContract(),
                closeout.markdownReport(),
                closeout.generatedAt(),
                Instant.now(clock)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalAcceptanceCompletionCloseoutArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalAcceptanceCompletionCloseoutArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
