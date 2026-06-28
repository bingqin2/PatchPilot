package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageVo;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceSharePackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalAcceptanceSharePackageArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalAcceptanceSharePackageVo> sharePackageSupplier;
    private final DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalAcceptanceSharePackageArchiveService(
            DemoFinalAcceptanceSharePackageService sharePackageService,
            DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository
    ) {
        this(
                sharePackageService::getSharePackage,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalAcceptanceSharePackageArchiveService(
            Supplier<DemoFinalAcceptanceSharePackageVo> sharePackageSupplier,
            DemoFinalAcceptanceSharePackageArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.sharePackageSupplier = sharePackageSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalAcceptanceSharePackageArchiveVo archiveCurrentSharePackage() {
        DemoFinalAcceptanceSharePackageVo sharePackage = sharePackageSupplier.get();
        DemoFinalAcceptanceSharePackageArchiveVo archive = new DemoFinalAcceptanceSharePackageArchiveVo(
                idSupplier.get(),
                sharePackage.status(),
                sharePackage.sendReady(),
                sharePackage.summary(),
                sharePackage.nextAction(),
                sharePackage.launchCertificateArchiveId(),
                sharePackage.taskCertificateArchiveId(),
                sharePackage.latestTaskId(),
                sharePackage.latestPullRequestUrl(),
                sharePackage.recommendedRecipients(),
                sharePackage.requiredAttachments(),
                sharePackage.preSendChecks(),
                sharePackage.messageSubject(),
                sharePackage.messageBody(),
                sharePackage.evidenceNotes(),
                sharePackage.sideEffectContract(),
                sharePackage.markdownReport(),
                sharePackage.generatedAt(),
                Instant.now(clock)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalAcceptanceSharePackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalAcceptanceSharePackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
