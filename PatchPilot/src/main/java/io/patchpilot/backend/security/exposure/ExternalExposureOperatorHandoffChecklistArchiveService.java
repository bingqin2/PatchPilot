package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureOperatorHandoffChecklistArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ExternalExposureOperatorHandoffChecklistArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<ExternalExposureOperatorHandoffChecklistVo> checklistSupplier;
    private final ExternalExposureOperatorHandoffChecklistArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public ExternalExposureOperatorHandoffChecklistArchiveService(
            ExternalExposureOperatorHandoffChecklistService checklistService,
            ExternalExposureOperatorHandoffChecklistArchiveRepository archiveRepository
    ) {
        this(checklistService::getChecklist, archiveRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    ExternalExposureOperatorHandoffChecklistArchiveService(
            Supplier<ExternalExposureOperatorHandoffChecklistVo> checklistSupplier,
            ExternalExposureOperatorHandoffChecklistArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.checklistSupplier = checklistSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public ExternalExposureOperatorHandoffChecklistArchiveVo archiveCurrentChecklist() {
        ExternalExposureOperatorHandoffChecklistVo checklist = checklistSupplier.get();
        ExternalExposureOperatorHandoffChecklistArchiveVo archive =
                new ExternalExposureOperatorHandoffChecklistArchiveVo(
                        idSupplier.get(),
                        checklist.status(),
                        checklist.readyForNextLiveStep(),
                        checklist.summary(),
                        checklist.nextAction(),
                        checklist.repository(),
                        checklist.latestCloseoutArchiveId(),
                        checklist.latestSessionId(),
                        checklist.latestSessionStatus(),
                        checklist.publicUrl(),
                        checklist.webhookUrl(),
                        checklist.handoffStatus(),
                        checklist.archiveFreshness(),
                        checklist.livePublishStatus(),
                        checklist.livePublishReady(),
                        checklist.activeSessionCount(),
                        checklist.readyCount(),
                        checklist.needsAttentionCount(),
                        checklist.blockedCount(),
                        checklist.totalCount(),
                        checklist.nextActions(),
                        checklist.evidenceNotes(),
                        checklist.downloadActions(),
                        checklist.sideEffectContract(),
                        checklist.checks(),
                        checklist.generatedAt(),
                        Instant.now(clock),
                        checklist.markdownReport()
                );
        return archiveRepository.save(archive);
    }

    public List<ExternalExposureOperatorHandoffChecklistArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
