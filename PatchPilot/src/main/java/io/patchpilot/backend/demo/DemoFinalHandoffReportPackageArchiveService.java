package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageVo;
import io.patchpilot.backend.demo.service.DemoFinalHandoffReportPackageArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DemoFinalHandoffReportPackageArchiveService {

    private static final int MAX_ARCHIVES = 20;

    private final Supplier<DemoFinalHandoffReportPackageVo> reportPackageSupplier;
    private final DemoFinalHandoffReportPackageArchiveRepository archiveRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public DemoFinalHandoffReportPackageArchiveService(
            DemoFinalHandoffReportPackageService reportPackageService,
            DemoFinalHandoffReportPackageArchiveRepository archiveRepository
    ) {
        this(
                reportPackageService::getReportPackage,
                archiveRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    DemoFinalHandoffReportPackageArchiveService(
            Supplier<DemoFinalHandoffReportPackageVo> reportPackageSupplier,
            DemoFinalHandoffReportPackageArchiveRepository archiveRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.reportPackageSupplier = reportPackageSupplier;
        this.archiveRepository = archiveRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public DemoFinalHandoffReportPackageArchiveVo archiveCurrentReportPackage() {
        DemoFinalHandoffReportPackageVo reportPackage = reportPackageSupplier.get();
        DemoFinalHandoffReportPackageArchiveVo archive = new DemoFinalHandoffReportPackageArchiveVo(
                idSupplier.get(),
                reportPackage.status(),
                reportPackage.downloadReady(),
                reportPackage.summary(),
                reportPackage.nextAction(),
                reportPackage.latestArchiveId(),
                reportPackage.latestSessionId(),
                reportPackage.latestDeliveryReceiptId(),
                reportPackage.taskCertificateArchiveId(),
                reportPackage.taskCertificateReady(),
                reportPackage.readinessChecks(),
                reportPackage.requiredAttachments(),
                reportPackage.preSendChecks(),
                reportPackage.evidenceNotes(),
                reportPackage.sourceReports(),
                reportPackage.markdownReport(),
                reportPackage.generatedAt(),
                Instant.now(clock)
        );
        return archiveRepository.save(archive);
    }

    public List<DemoFinalHandoffReportPackageArchiveVo> listRecentArchives() {
        return archiveRepository.listRecentArchives(MAX_ARCHIVES);
    }

    public Optional<DemoFinalHandoffReportPackageArchiveVo> findArchive(String archiveId) {
        return archiveRepository.findById(archiveId);
    }
}
