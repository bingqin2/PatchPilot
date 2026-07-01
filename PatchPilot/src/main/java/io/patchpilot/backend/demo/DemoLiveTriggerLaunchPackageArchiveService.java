package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveTriggerLaunchPackageArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoLiveTriggerLaunchPackageService launchPackageService;
    private final DemoLiveTriggerLaunchPackageArchiveRepository archiveRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveTriggerLaunchPackageArchiveService(
            DemoLiveTriggerLaunchPackageService launchPackageService,
            DemoLiveTriggerLaunchPackageArchiveRepository archiveRepository
    ) {
        this(launchPackageService, archiveRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveTriggerLaunchPackageArchiveService(
            DemoLiveTriggerLaunchPackageService launchPackageService,
            DemoLiveTriggerLaunchPackageArchiveRepository archiveRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.launchPackageService = launchPackageService;
        this.archiveRepository = archiveRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveTriggerLaunchPackageArchiveVo archivePackage(DemoLiveTriggerLaunchPackageCommand command) {
        DemoLiveTriggerLaunchPackageVo launchPackage = launchPackageService.createPackage(command);
        DemoLiveTriggerLaunchPackageArchiveVo archive = new DemoLiveTriggerLaunchPackageArchiveVo(
                idSupplier.get(),
                launchPackage.status(),
                launchPackage.readyToPost(),
                launchPackage.repository(),
                launchPackage.issueNumber(),
                launchPackage.issueUrl(),
                launchPackage.triggerUser(),
                launchPackage.triggerComment(),
                launchPackage.summary(),
                launchPackage.operatorHandoffArchiveId(),
                launchPackage.operatorHandoffArchiveReady(),
                launchPackage.operatorHandoffArchivedAt(),
                launchPackage.liveLaunchGateStatus(),
                launchPackage.liveLaunchGateReady(),
                launchPackage.evidenceNotes(),
                launchPackage.nextActions(),
                archiveSideEffectContract(launchPackage.sideEffectContract()),
                launchPackage.generatedAt(),
                nowSupplier.get(),
                launchPackage.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<DemoLiveTriggerLaunchPackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLiveTriggerLaunchPackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String archiveSideEffectContract(String packageContract) {
        return "Archive creation writes only PatchPilot local archive records. " + packageContract;
    }
}
