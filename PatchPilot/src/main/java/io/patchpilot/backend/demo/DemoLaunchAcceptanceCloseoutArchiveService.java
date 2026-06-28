package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutVo;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCloseoutArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoLaunchAcceptanceCloseoutArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoLaunchAcceptanceCloseoutVo> closeoutSupplier;
    private final DemoLaunchAcceptanceCloseoutArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoLaunchAcceptanceCloseoutArchiveService(
            DemoLaunchAcceptanceCloseoutService closeoutService,
            DemoLaunchAcceptanceCloseoutArchiveRepository archiveRepository
    ) {
        this(
                closeoutService::getCloseout,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoLaunchAcceptanceCloseoutArchiveService(
            Supplier<DemoLaunchAcceptanceCloseoutVo> closeoutSupplier,
            DemoLaunchAcceptanceCloseoutArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.closeoutSupplier = closeoutSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoLaunchAcceptanceCloseoutArchiveVo archiveCurrentCloseout() {
        DemoLaunchAcceptanceCloseoutVo closeout = closeoutSupplier.get();
        DemoLaunchAcceptanceCloseoutArchiveVo archive = new DemoLaunchAcceptanceCloseoutArchiveVo(
                idSupplier.get(),
                closeout.status(),
                closeout.accepted(),
                closeout.summary(),
                closeout.sessionId(),
                closeout.latestTaskId(),
                closeout.latestPullRequestUrl(),
                closeout.latestWebhookDeliveryId(),
                closeout.evaluationRunId(),
                closeout.latestArchiveId(),
                closeout.latestDeliveryReceiptId(),
                closeout.latestDeliveryTarget(),
                closeout.latestDeliveryChannel(),
                closeout.deliveryReceiptFreshness(),
                Instant.now(clock),
                closeout.markdownReport()
        );
        return archiveRepository.save(archive);
    }

    public List<DemoLaunchAcceptanceCloseoutArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoLaunchAcceptanceCloseoutArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
