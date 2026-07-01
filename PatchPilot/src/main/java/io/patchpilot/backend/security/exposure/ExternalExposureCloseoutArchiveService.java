package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureCloseoutArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ExternalExposureCloseoutArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<ExternalExposureCloseoutVo> closeoutSupplier;
    private final ExternalExposureCloseoutArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public ExternalExposureCloseoutArchiveService(
            ExternalExposureCloseoutService closeoutService,
            ExternalExposureCloseoutArchiveRepository archiveRepository
    ) {
        this(closeoutService::getCloseout, archiveRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    ExternalExposureCloseoutArchiveService(
            Supplier<ExternalExposureCloseoutVo> closeoutSupplier,
            ExternalExposureCloseoutArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.closeoutSupplier = closeoutSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public ExternalExposureCloseoutArchiveVo archiveCurrentCloseout() {
        ExternalExposureCloseoutVo closeout = closeoutSupplier.get();
        ExternalExposureCloseoutArchiveVo archive = new ExternalExposureCloseoutArchiveVo(
                idSupplier.get(),
                closeout.status(),
                closeout.closeoutReady(),
                closeout.summary(),
                closeout.nextAction(),
                closeout.latestSessionId(),
                closeout.latestSessionStatus(),
                closeout.publicUrl(),
                closeout.webhookUrl(),
                closeout.purpose(),
                closeout.operator(),
                closeout.startedAt(),
                closeout.closedBy(),
                closeout.closedAt(),
                closeout.closeNotes(),
                closeout.linkedReadinessArchiveId(),
                closeout.handoffStatus(),
                closeout.archiveFreshness(),
                closeout.readyCount(),
                closeout.needsAttentionCount(),
                closeout.blockedCount(),
                closeout.totalCount(),
                closeout.nextActions(),
                closeout.evidenceNotes(),
                closeout.downloadActions(),
                closeout.sideEffectContract(),
                closeout.generatedAt(),
                Instant.now(clock),
                closeout.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<ExternalExposureCloseoutArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<ExternalExposureCloseoutArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
