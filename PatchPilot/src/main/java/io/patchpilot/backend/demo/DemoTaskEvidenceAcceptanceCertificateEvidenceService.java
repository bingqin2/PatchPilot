package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoTaskEvidenceAcceptanceCertificateEvidenceVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemoTaskEvidenceAcceptanceCertificateEvidenceService {

    private final FixTaskEvidencePackageAcceptanceCertificateArchiveRepository archiveRepository;

    public DemoTaskEvidenceAcceptanceCertificateEvidenceService(
            FixTaskEvidencePackageAcceptanceCertificateArchiveRepository archiveRepository
    ) {
        this.archiveRepository = archiveRepository;
    }

    public DemoTaskEvidenceAcceptanceCertificateEvidenceVo getEvidence() {
        return evidenceFromArchives(archiveRepository.listRecentArchives(20));
    }

    static DemoTaskEvidenceAcceptanceCertificateEvidenceVo evidenceFromArchives(
            List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> archives
    ) {
        FixTaskEvidencePackageAcceptanceCertificateArchiveVo latestArchive = archives.isEmpty() ? null : archives.get(0);
        if (latestArchive == null) {
            return new DemoTaskEvidenceAcceptanceCertificateEvidenceVo(
                    DemoReadinessStatus.NEEDS_ATTENTION,
                    false,
                    false,
                    "No task evidence acceptance certificate archive is available.",
                    "Archive a certified task evidence acceptance certificate after final task evidence closeout.",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("Archive a task evidence acceptance certificate before using the evidence bundle as task-level review proof.")
            );
        }

        boolean certified = "READY".equals(latestArchive.status()) && latestArchive.certified();
        DemoReadinessStatus status = certificateStatus(latestArchive);
        String nextAction = certified
                ? "Use the archived task evidence acceptance certificate as task-level review proof."
                : "Resolve task evidence acceptance certificate blockers, then archive a new certified certificate.";
        List<String> downloadActions = new ArrayList<>();
        downloadActions.add("Download task evidence acceptance certificate archive " + latestArchive.id() + ".");
        if (hasText(latestArchive.latestCloseoutArchiveId())) {
            downloadActions.add("Download linked task evidence acceptance closeout archive "
                    + latestArchive.latestCloseoutArchiveId() + ".");
        }
        if (hasText(latestArchive.latestDeliveryReceiptId())) {
            downloadActions.add("Download task evidence delivery receipt " + latestArchive.latestDeliveryReceiptId() + ".");
        }

        return new DemoTaskEvidenceAcceptanceCertificateEvidenceVo(
                status,
                true,
                certified,
                certificateSummary(latestArchive, certified),
                nextAction,
                archives.size(),
                latestArchive.id(),
                latestArchive.latestCloseoutArchiveId(),
                latestArchive.latestEvidenceArchiveId(),
                latestArchive.latestDeliveryReceiptId(),
                latestArchive.latestTaskId(),
                latestArchive.latestPullRequestUrl(),
                latestArchive.archivedAt(),
                List.copyOf(downloadActions)
        );
    }

    private static DemoReadinessStatus certificateStatus(FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive) {
        if ("BLOCKED".equals(archive.status())) {
            return DemoReadinessStatus.BLOCKED;
        }
        return "READY".equals(archive.status()) && archive.certified()
                ? DemoReadinessStatus.READY
                : DemoReadinessStatus.NEEDS_ATTENTION;
    }

    private static String certificateSummary(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive,
            boolean certified
    ) {
        if (certified) {
            return "Latest task evidence acceptance certificate archive is certified and ready.";
        }
        if ("BLOCKED".equals(archive.status())) {
            return "Latest task evidence acceptance certificate archive is blocked.";
        }
        return "Latest task evidence acceptance certificate archive is not certified yet.";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
