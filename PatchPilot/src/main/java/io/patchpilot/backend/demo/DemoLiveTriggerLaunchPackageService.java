package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateCommand;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureOperatorHandoffChecklistArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class DemoLiveTriggerLaunchPackageService {

    private static final String STATUS_READY = "READY";
    private static final String STATUS_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String STATUS_BLOCKED = "BLOCKED";
    private static final String SIDE_EFFECT_CONTRACT = """
            Read-only live trigger launch package: this endpoint does not create tasks, does not enqueue work, \
            does not call the model directly, does not mutate Git, does not push branches, does not open Pull Requests, \
            does not write GitHub comments, does not archive records, and does not expose secrets.\
            """;

    private final Function<DemoLiveLaunchGateCommand, DemoLiveLaunchGateVo> liveLaunchGateSupplier;
    private final Supplier<Optional<ExternalExposureOperatorHandoffChecklistArchiveVo>> latestOperatorArchiveSupplier;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveTriggerLaunchPackageService(
            DemoLiveLaunchGateService liveLaunchGateService,
            ExternalExposureOperatorHandoffChecklistArchiveRepository operatorArchiveRepository
    ) {
        this(
                liveLaunchGateService::getGate,
                () -> operatorArchiveRepository.listRecentArchives(1).stream().findFirst(),
                Instant::now
        );
    }

    DemoLiveTriggerLaunchPackageService(
            Function<DemoLiveLaunchGateCommand, DemoLiveLaunchGateVo> liveLaunchGateSupplier,
            Supplier<Optional<ExternalExposureOperatorHandoffChecklistArchiveVo>> latestOperatorArchiveSupplier,
            Supplier<Instant> nowSupplier
    ) {
        this.liveLaunchGateSupplier = liveLaunchGateSupplier;
        this.latestOperatorArchiveSupplier = latestOperatorArchiveSupplier;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveTriggerLaunchPackageVo createPackage(DemoLiveTriggerLaunchPackageCommand command) {
        DemoLiveLaunchGateVo gate = liveLaunchGateSupplier.apply(toGateCommand(command));
        Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> archive = latestOperatorArchiveSupplier.get();
        String status = aggregateStatus(gate, archive);
        boolean readyToPost = STATUS_READY.equals(status);
        Instant generatedAt = nowSupplier.get();
        List<String> evidenceNotes = evidenceNotes(archive, gate);
        List<String> nextActions = nextActions(command, gate, archive, status);
        String summary = summary(status);
        String repository = command.repositoryOwner() + "/" + command.repositoryName();
        String issueUrl = issueUrl(repository, command.issueNumber());
        String archiveId = archive.map(ExternalExposureOperatorHandoffChecklistArchiveVo::id).orElse(null);
        boolean archiveReady = archive.map(ExternalExposureOperatorHandoffChecklistArchiveVo::readyForNextLiveStep).orElse(false);
        Instant archivedAt = archive.map(ExternalExposureOperatorHandoffChecklistArchiveVo::archivedAt).orElse(null);
        String markdownReport = markdownReport(
                status,
                summary,
                repository,
                issueUrl,
                command,
                archive,
                gate,
                evidenceNotes,
                nextActions,
                generatedAt
        );

        return new DemoLiveTriggerLaunchPackageVo(
                status,
                readyToPost,
                repository,
                command.issueNumber(),
                issueUrl,
                command.triggerUser(),
                command.triggerComment(),
                summary,
                archiveId,
                archiveReady,
                archivedAt,
                gate.status(),
                gate.readyToPost(),
                evidenceNotes,
                nextActions,
                SIDE_EFFECT_CONTRACT,
                gate,
                generatedAt,
                markdownReport
        );
    }

    private static DemoLiveLaunchGateCommand toGateCommand(DemoLiveTriggerLaunchPackageCommand command) {
        return new DemoLiveLaunchGateCommand(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser(),
                command.triggerComment()
        );
    }

    private static String aggregateStatus(
            DemoLiveLaunchGateVo gate,
            Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> archive
    ) {
        if (archive.isEmpty() || archive.filter(ExternalExposureOperatorHandoffChecklistArchiveVo::readyForNextLiveStep).isEmpty()) {
            return STATUS_BLOCKED;
        }
        if (STATUS_BLOCKED.equals(gate.status())) {
            return STATUS_BLOCKED;
        }
        if (!gate.readyToPost() || STATUS_NEEDS_ATTENTION.equals(gate.status())) {
            return STATUS_NEEDS_ATTENTION;
        }
        return STATUS_READY;
    }

    private static List<String> evidenceNotes(
            Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> archive,
            DemoLiveLaunchGateVo gate
    ) {
        List<String> notes = new ArrayList<>();
        if (archive.isEmpty()) {
            notes.add("Operator handoff archive is missing.");
        } else {
            ExternalExposureOperatorHandoffChecklistArchiveVo value = archive.get();
            if (value.readyForNextLiveStep()) {
                notes.add("Latest external exposure operator handoff archive " + value.id() + " is ready.");
            } else {
                notes.add("Latest external exposure operator handoff archive " + value.id() + " is not ready.");
            }
            notes.addAll(value.evidenceNotes());
        }
        notes.add("Live launch gate status is " + gate.status() + ".");
        return notes.stream().distinct().toList();
    }

    private static List<String> nextActions(
            DemoLiveTriggerLaunchPackageCommand command,
            DemoLiveLaunchGateVo gate,
            Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> archive,
            String status
    ) {
        if (STATUS_READY.equals(status)) {
            return List.of(
                    "Post `" + command.triggerComment() + "` on " + issueUrl(command.repositoryOwner() + "/" + command.repositoryName(), command.issueNumber()) + ".",
                    "After GitHub delivers the webhook, watch the task, Pull Request, and launch outcome tracker."
            );
        }
        List<String> actions = new ArrayList<>();
        if (archive.isEmpty() || archive.filter(ExternalExposureOperatorHandoffChecklistArchiveVo::readyForNextLiveStep).isEmpty()) {
            actions.add("Archive a ready external exposure operator handoff checklist, then rebuild this launch package.");
        }
        actions.addAll(gate.nextActions());
        if (!gate.readyToPost()) {
            actions.add("Rerun the live launch gate and resolve all launch package checks before posting.");
        }
        return actions.stream().distinct().toList();
    }

    private static String summary(String status) {
        return switch (status) {
            case STATUS_READY -> "PatchPilot is ready for the operator to post the live trigger.";
            case STATUS_BLOCKED -> "PatchPilot is blocked before posting the live trigger.";
            default -> "PatchPilot needs attention before posting the live trigger.";
        };
    }

    private static String issueUrl(String repository, long issueNumber) {
        return "https://github.com/" + repository + "/issues/" + issueNumber;
    }

    private static String markdownReport(
            String status,
            String summary,
            String repository,
            String issueUrl,
            DemoLiveTriggerLaunchPackageCommand command,
            Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> archive,
            DemoLiveLaunchGateVo gate,
            List<String> evidenceNotes,
            List<String> nextActions,
            Instant generatedAt
    ) {
        StringBuilder report = new StringBuilder();
        report.append("# PatchPilot Live Trigger Launch Package\n\n");
        report.append("- Status: `").append(status).append("`\n");
        report.append("- Summary: ").append(summary).append("\n");
        report.append("- Repository: `").append(repository).append("`\n");
        report.append("- Issue: ").append(issueUrl).append("\n");
        report.append("- Trigger user: `").append(command.triggerUser()).append("`\n");
        report.append("- Operator handoff archive: `")
                .append(archive.map(ExternalExposureOperatorHandoffChecklistArchiveVo::id).orElse("missing"))
                .append("`\n");
        report.append("- Live launch gate: `").append(gate.status()).append("`\n");
        report.append("- Generated at: `").append(generatedAt).append("`\n\n");
        report.append("## Exact GitHub Comment\n\n");
        report.append("`").append(command.triggerComment()).append("`\n\n");
        report.append("## Evidence Notes\n\n");
        for (String note : evidenceNotes) {
            report.append("- ").append(note).append("\n");
        }
        report.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            report.append("- ").append(action).append("\n");
        }
        report.append("\n## Side Effect Contract\n\n");
        report.append(SIDE_EFFECT_CONTRACT).append("\n");
        return report.toString();
    }
}
