package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalExternalReviewEvidencePackageArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalExternalReviewEvidencePackageVo> evidencePackageSupplier;
    private final DemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalExternalReviewEvidencePackageArchiveService(
            DemoFinalExternalReviewEvidencePackageService evidencePackageService,
            DemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository
    ) {
        this(
                evidencePackageService::getPackage,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalExternalReviewEvidencePackageArchiveService(
            Supplier<DemoFinalExternalReviewEvidencePackageVo> evidencePackageSupplier,
            DemoFinalExternalReviewEvidencePackageArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.evidencePackageSupplier = evidencePackageSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalExternalReviewEvidencePackageArchiveVo archiveCurrentPackage() {
        DemoFinalExternalReviewEvidencePackageVo evidencePackage = evidencePackageSupplier.get();
        if (!evidencePackage.readyForExternalReview() || evidencePackage.status() != DemoReadinessStatus.READY) {
            throw new IllegalStateException("final external-review evidence package is not ready");
        }
        DemoFinalExternalReviewEvidencePackageArchiveVo archive = new DemoFinalExternalReviewEvidencePackageArchiveVo(
                idSupplier.get(),
                evidencePackage.status(),
                evidencePackage.readyForExternalReview(),
                evidencePackage.summary(),
                evidencePackage.nextAction(),
                evidencePackage.latestTaskId(),
                evidencePackage.latestPullRequestUrl(),
                evidencePackage.finalAcceptanceSharePackageArchiveId(),
                evidencePackage.completionArchiveId(),
                evidencePackage.completionEvidenceDeliveryReceiptId(),
                evidencePackage.closeoutArchiveId(),
                evidencePackage.deliveryTarget(),
                evidencePackage.deliveryChannel(),
                evidencePackage.deliveredAt(),
                evidencePackage.deliveryReceiptFreshness(),
                evidencePackage.closeoutArchivedAt(),
                evidencePackage.evidenceNotes(),
                evidencePackage.downloadActions(),
                evidencePackage.sideEffectContract(),
                evidencePackage.markdownReport(),
                evidencePackage.generatedAt(),
                Instant.now(clock)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalExternalReviewEvidencePackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalExternalReviewEvidencePackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
