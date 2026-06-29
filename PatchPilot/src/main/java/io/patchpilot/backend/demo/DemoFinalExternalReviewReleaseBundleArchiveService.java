package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewReleaseBundleArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalExternalReviewReleaseBundleVo> releaseBundleSupplier;
    private final DemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewReleaseBundleArchiveService(
            DemoFinalExternalReviewReleaseBundleService releaseBundleService,
            DemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository
    ) {
        this(releaseBundleService::getReleaseBundle, archiveRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    DemoFinalExternalReviewReleaseBundleArchiveService(
            Supplier<DemoFinalExternalReviewReleaseBundleVo> releaseBundleSupplier,
            DemoFinalExternalReviewReleaseBundleArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.releaseBundleSupplier = releaseBundleSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewReleaseBundleArchiveVo archiveCurrentReleaseBundle() {
        DemoFinalExternalReviewReleaseBundleVo releaseBundle = releaseBundleSupplier.get();
        if (!releaseBundle.releaseReady() || releaseBundle.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final external-review release bundle is not ready");
        }
        DemoFinalExternalReviewReleaseBundleArchiveVo archive =
                DemoFinalExternalReviewReleaseBundleArchiveConvert.fromReleaseBundle(
                        idSupplier.get(),
                        releaseBundle,
                        Instant.now(clock)
                );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalExternalReviewReleaseBundleArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalExternalReviewReleaseBundleArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
