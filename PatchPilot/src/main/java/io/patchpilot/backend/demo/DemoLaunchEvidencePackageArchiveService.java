package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.service.DemoLaunchEvidencePackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLaunchEvidencePackageArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoLaunchEvidencePackageVo> packageSupplier;
    private final DemoLaunchEvidencePackageArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoLaunchEvidencePackageArchiveService(
            DemoLaunchEvidencePackageService evidencePackageService,
            DemoLaunchEvidencePackageArchiveRepository archiveRepository
    ) {
        this(
                evidencePackageService::getPackage,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoLaunchEvidencePackageArchiveService(
            Supplier<DemoLaunchEvidencePackageVo> packageSupplier,
            DemoLaunchEvidencePackageArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.packageSupplier = packageSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoLaunchEvidencePackageArchiveVo archiveCurrentPackage() {
        DemoLaunchEvidencePackageVo evidencePackage = packageSupplier.get();
        DemoLaunchEvidencePackageArchiveVo archive = new DemoLaunchEvidencePackageArchiveVo(
                idSupplier.get(),
                evidencePackage.status(),
                evidencePackage.readyToShare(),
                evidencePackage.summary(),
                evidencePackage.sessionId(),
                evidencePackage.launchReadinessStatus(),
                evidencePackage.evidenceBundleStatus(),
                evidencePackage.handoffFinalizationStatus(),
                evidencePackage.finalHandoffReportPackageArchiveStatus(),
                evidencePackage.finalHandoffReportPackageArchiveReady(),
                evidencePackage.finalHandoffReportPackageArchiveId(),
                evidencePackage.finalHandoffReportPackageArchiveSummary(),
                evidencePackage.latestTaskId(),
                evidencePackage.latestPullRequestUrl(),
                evidencePackage.latestWebhookDeliveryId(),
                evidencePackage.evaluationRunId(),
                Instant.now(clock),
                evidencePackage.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<DemoLaunchEvidencePackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLaunchEvidencePackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
