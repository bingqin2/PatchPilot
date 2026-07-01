package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLiveTriggerOutcomeCloseoutArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoLiveTriggerOutcomeCloseoutService closeoutService;
    private final DemoLiveTriggerOutcomeCloseoutArchiveRepository archiveRepository;
    private final Supplier<String> idSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveTriggerOutcomeCloseoutArchiveService(
            DemoLiveTriggerOutcomeCloseoutService closeoutService,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository archiveRepository
    ) {
        this(closeoutService, archiveRepository, () -> UUID.randomUUID().toString(), Instant::now);
    }

    DemoLiveTriggerOutcomeCloseoutArchiveService(
            DemoLiveTriggerOutcomeCloseoutService closeoutService,
            DemoLiveTriggerOutcomeCloseoutArchiveRepository archiveRepository,
            Supplier<String> idSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.closeoutService = closeoutService;
        this.archiveRepository = archiveRepository;
        this.idSupplier = idSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveTriggerOutcomeCloseoutArchiveVo archiveCloseout(DemoLiveTriggerOutcomeCloseoutCommand command) {
        DemoLiveTriggerOutcomeCloseoutVo closeout = closeoutService.createCloseout(command);
        DemoLiveTriggerOutcomeCloseoutArchiveVo archive = new DemoLiveTriggerOutcomeCloseoutArchiveVo(
                idSupplier.get(),
                closeout.status(),
                closeout.successful(),
                closeout.repository(),
                closeout.issueNumber(),
                closeout.issueUrl(),
                closeout.triggerUser(),
                closeout.triggerComment(),
                closeout.launchPackageArchiveId(),
                closeout.launchPackageStatus(),
                closeout.launchPackageArchivedAt(),
                closeout.taskId(),
                closeout.taskStatus(),
                closeout.failureReason(),
                closeout.taskCreatedAt(),
                closeout.taskUpdatedAt(),
                closeout.pullRequestUrl(),
                closeout.webhookDeliveryId(),
                closeout.webhookDeliveryStatus(),
                closeout.summary(),
                closeout.evidenceNotes(),
                closeout.nextActions(),
                archiveSideEffectContract(closeout.sideEffectContract()),
                closeout.generatedAt(),
                nowSupplier.get(),
                closeout.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<DemoLiveTriggerOutcomeCloseoutArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }

    private static String archiveSideEffectContract(String closeoutContract) {
        return "Archive creation writes only PatchPilot local archive records. " + closeoutContract;
    }
}
