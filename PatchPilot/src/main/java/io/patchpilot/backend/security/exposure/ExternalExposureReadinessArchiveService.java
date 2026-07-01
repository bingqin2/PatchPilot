package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureReadinessArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ExternalExposureReadinessArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<ExternalExposureReadinessVo> readinessSupplier;
    private final ExternalExposureReadinessArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public ExternalExposureReadinessArchiveService(
            ExternalExposureReadinessProvider readinessProvider,
            ExternalExposureReadinessArchiveRepository archiveRepository
    ) {
        this(readinessProvider::getReadiness, archiveRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    ExternalExposureReadinessArchiveService(
            Supplier<ExternalExposureReadinessVo> readinessSupplier,
            ExternalExposureReadinessArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.readinessSupplier = readinessSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public ExternalExposureReadinessArchiveVo archiveCurrentReadiness() {
        ExternalExposureReadinessVo readiness = readinessSupplier.get();
        ExternalExposureReadinessArchiveVo archive = new ExternalExposureReadinessArchiveVo(
                idSupplier.get(),
                readiness.status(),
                readiness.safeToExpose(),
                readiness.summary(),
                readiness.readyCount(),
                readiness.needsAttentionCount(),
                readiness.blockedCount(),
                readiness.totalCount(),
                Instant.now(clock),
                readiness.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<ExternalExposureReadinessArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<ExternalExposureReadinessArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
