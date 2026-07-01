package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutVo;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskSort;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DemoLiveTriggerOutcomeCloseoutService {

    private static final int TASK_LOOKUP_LIMIT = 50;
    private static final String SIDE_EFFECT_CONTRACT =
            "read-only live trigger outcome closeout: this endpoint does not mutate GitHub, tasks, queues, repositories, or launch archives.";

    private final DemoLiveTriggerLaunchPackageArchiveRepository launchPackageArchiveRepository;
    private final FixTaskService fixTaskService;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public DemoLiveTriggerOutcomeCloseoutService(
            DemoLiveTriggerLaunchPackageArchiveRepository launchPackageArchiveRepository,
            FixTaskService fixTaskService
    ) {
        this(launchPackageArchiveRepository, fixTaskService, Instant::now);
    }

    DemoLiveTriggerOutcomeCloseoutService(
            DemoLiveTriggerLaunchPackageArchiveRepository launchPackageArchiveRepository,
            FixTaskService fixTaskService,
            Supplier<Instant> nowSupplier
    ) {
        this.launchPackageArchiveRepository = launchPackageArchiveRepository;
        this.fixTaskService = fixTaskService;
        this.nowSupplier = nowSupplier;
    }

    public DemoLiveTriggerOutcomeCloseoutVo createCloseout(DemoLiveTriggerOutcomeCloseoutCommand command) {
        Instant generatedAt = nowSupplier.get();
        Optional<DemoLiveTriggerLaunchPackageArchiveVo> archive = findLaunchPackageArchive(command);
        if (archive.isEmpty()) {
            return blockedWithoutArchive(command, generatedAt);
        }

        Optional<FixTaskVo> task = findMatchingTask(command);
        if (task.isEmpty()) {
            return blockedWithoutTask(command, archive.get(), generatedAt);
        }

        return closeoutForTask(command, archive.get(), task.get(), generatedAt);
    }

    private Optional<DemoLiveTriggerLaunchPackageArchiveVo> findLaunchPackageArchive(
            DemoLiveTriggerOutcomeCloseoutCommand command
    ) {
        if (hasText(command.launchPackageArchiveId())) {
            return launchPackageArchiveRepository.findById(command.launchPackageArchiveId().trim());
        }
        return launchPackageArchiveRepository.listRecentArchives(1).stream().findFirst();
    }

    private Optional<FixTaskVo> findMatchingTask(DemoLiveTriggerOutcomeCloseoutCommand command) {
        FixTaskListQuery query = new FixTaskListQuery(
                null,
                null,
                command.repositoryOwner(),
                command.repositoryName(),
                null,
                null,
                null,
                null,
                TASK_LOOKUP_LIMIT,
                0,
                FixTaskSort.CREATED_AT_DESC
        );
        return fixTaskService.listTasks(query).stream()
                .filter(task -> task.issueNumber() == command.issueNumber())
                .filter(task -> equalsText(task.triggerUser(), command.triggerUser()))
                .filter(task -> equalsText(task.triggerComment(), command.triggerComment()))
                .max(Comparator.comparing(FixTaskVo::createdAt));
    }

    private DemoLiveTriggerOutcomeCloseoutVo closeoutForTask(
            DemoLiveTriggerOutcomeCloseoutCommand command,
            DemoLiveTriggerLaunchPackageArchiveVo archive,
            FixTaskVo task,
            Instant generatedAt
    ) {
        boolean successful = task.status() == FixTaskStatus.COMPLETED && hasText(task.pullRequestUrl());
        String status = successful ? "READY" : "NEEDS_ATTENTION";
        String summary = successful
                ? "Live trigger created task %s and created Pull Request %s.".formatted(task.id(), task.pullRequestUrl())
                : "Live trigger created task %s but it ended as %s%s.".formatted(
                        task.id(),
                        task.status().name(),
                        hasText(task.failureReason()) ? ": " + task.failureReason() : ""
                );
        List<String> evidenceNotes = successful
                ? List.of(
                        "Launch package archive %s was used for this live trigger.".formatted(archive.id()),
                        "Webhook delivery %s created task %s.".formatted(nullSafe(task.deliveryId()), task.id()),
                        "Task %s completed with Pull Request %s.".formatted(task.id(), task.pullRequestUrl())
                )
                : List.of(
                        "Launch package archive %s was used for this live trigger.".formatted(archive.id()),
                        "Webhook delivery %s created task %s.".formatted(nullSafe(task.deliveryId()), task.id()),
                        "Task %s is %s%s.".formatted(
                                task.id(),
                                task.status().name(),
                                hasText(task.failureReason()) ? " with failure: " + task.failureReason() : ""
                        )
                );
        List<String> nextActions = successful
                ? List.of("Review and merge %s.".formatted(task.pullRequestUrl()))
                : List.of(
                        hasText(task.failureReason())
                                ? "Resolve the task failure: %s.".formatted(task.failureReason())
                                : "Inspect task %s execution records and wait or retry when safe.".formatted(task.id()),
                        "Use the task detail panel to inspect timeline, tool calls, model calls, and test runs."
                );

        return build(
                status,
                successful,
                command,
                archive.id(),
                archive.status(),
                archive.archivedAt(),
                task.id(),
                task.status().name(),
                task.failureReason(),
                task.createdAt(),
                task.updatedAt(),
                task.pullRequestUrl(),
                task.deliveryId(),
                "TASK_CREATED",
                summary,
                evidenceNotes,
                nextActions,
                generatedAt
        );
    }

    private DemoLiveTriggerOutcomeCloseoutVo blockedWithoutTask(
            DemoLiveTriggerOutcomeCloseoutCommand command,
            DemoLiveTriggerLaunchPackageArchiveVo archive,
            Instant generatedAt
    ) {
        return build(
                "BLOCKED",
                false,
                command,
                archive.id(),
                archive.status(),
                archive.archivedAt(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "No matching task was found for this exact live trigger after the launch package archive.",
                List.of(
                        "Launch package archive %s exists and was used as the expected trigger evidence.".formatted(archive.id()),
                        "No task matched repository %s/%s, issue #%d, user %s, and exact comment `%s`.".formatted(
                                command.repositoryOwner(),
                                command.repositoryName(),
                                command.issueNumber(),
                                command.triggerUser(),
                                command.triggerComment()
                        )
                ),
                List.of(
                        "Check GitHub webhook delivery for the exact issue comment and redeliver the event if GitHub did not reach PatchPilot.",
                        "Confirm the webhook secret, public tunnel URL, and backend `/api/github/webhook` route are still valid."
                ),
                generatedAt
        );
    }

    private DemoLiveTriggerOutcomeCloseoutVo blockedWithoutArchive(
            DemoLiveTriggerOutcomeCloseoutCommand command,
            Instant generatedAt
    ) {
        String requestedArchiveId = hasText(command.launchPackageArchiveId())
                ? command.launchPackageArchiveId().trim()
                : null;
        return build(
                "BLOCKED",
                false,
                command,
                requestedArchiveId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                requestedArchiveId == null
                        ? "No launch package archive is available for live trigger closeout."
                        : "No launch package archive %s was found for live trigger closeout.".formatted(requestedArchiveId),
                List.of("A live trigger outcome closeout requires a frozen launch package archive as the pre-trigger evidence."),
                List.of("Create or archive a live trigger launch package before posting the GitHub issue comment."),
                generatedAt
        );
    }

    private DemoLiveTriggerOutcomeCloseoutVo build(
            String status,
            boolean successful,
            DemoLiveTriggerOutcomeCloseoutCommand command,
            String launchPackageArchiveId,
            String launchPackageStatus,
            Instant launchPackageArchivedAt,
            String taskId,
            String taskStatus,
            String failureReason,
            Instant taskCreatedAt,
            Instant taskUpdatedAt,
            String pullRequestUrl,
            String webhookDeliveryId,
            String webhookDeliveryStatus,
            String summary,
            List<String> evidenceNotes,
            List<String> nextActions,
            Instant generatedAt
    ) {
        DemoLiveTriggerOutcomeCloseoutVo withoutReport = new DemoLiveTriggerOutcomeCloseoutVo(
                status,
                successful,
                command.repositoryOwner() + "/" + command.repositoryName(),
                command.issueNumber(),
                issueUrl(command),
                command.triggerUser(),
                command.triggerComment(),
                launchPackageArchiveId,
                launchPackageStatus,
                launchPackageArchivedAt,
                taskId,
                taskStatus,
                failureReason,
                taskCreatedAt,
                taskUpdatedAt,
                pullRequestUrl,
                webhookDeliveryId,
                webhookDeliveryStatus,
                summary,
                evidenceNotes,
                nextActions,
                SIDE_EFFECT_CONTRACT,
                generatedAt,
                null
        );
        return new DemoLiveTriggerOutcomeCloseoutVo(
                withoutReport.status(),
                withoutReport.successful(),
                withoutReport.repository(),
                withoutReport.issueNumber(),
                withoutReport.issueUrl(),
                withoutReport.triggerUser(),
                withoutReport.triggerComment(),
                withoutReport.launchPackageArchiveId(),
                withoutReport.launchPackageStatus(),
                withoutReport.launchPackageArchivedAt(),
                withoutReport.taskId(),
                withoutReport.taskStatus(),
                withoutReport.failureReason(),
                withoutReport.taskCreatedAt(),
                withoutReport.taskUpdatedAt(),
                withoutReport.pullRequestUrl(),
                withoutReport.webhookDeliveryId(),
                withoutReport.webhookDeliveryStatus(),
                withoutReport.summary(),
                withoutReport.evidenceNotes(),
                withoutReport.nextActions(),
                withoutReport.sideEffectContract(),
                withoutReport.generatedAt(),
                formatReport(withoutReport)
        );
    }

    private static String formatReport(DemoLiveTriggerOutcomeCloseoutVo closeout) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot Live Trigger Outcome Closeout\n\n")
                .append("- Status: `").append(closeout.status()).append("`\n")
                .append("- Successful: ").append(closeout.successful()).append('\n')
                .append("- Repository: ").append(closeout.repository()).append('\n')
                .append("- Issue: ").append(closeout.issueUrl()).append('\n')
                .append("- Trigger user: ").append(closeout.triggerUser()).append('\n')
                .append("- Trigger comment: `").append(closeout.triggerComment()).append("`\n");
        appendOptional(builder, "Launch package archive", closeout.launchPackageArchiveId());
        appendOptional(builder, "Launch package status", closeout.launchPackageStatus());
        appendOptional(builder, "Task", closeout.taskId());
        appendOptional(builder, "Task status", closeout.taskStatus());
        appendOptional(builder, "Failure reason", closeout.failureReason());
        appendOptional(builder, "Webhook delivery", closeout.webhookDeliveryId());
        appendOptional(builder, "Webhook delivery status", closeout.webhookDeliveryStatus());
        appendOptional(builder, "Pull Request", closeout.pullRequestUrl());
        builder.append("\n## Summary\n\n")
                .append(closeout.summary()).append("\n\n")
                .append("## Evidence\n\n");
        closeout.evidenceNotes().forEach(note -> builder.append("- ").append(note).append('\n'));
        builder.append("\n## Next Actions\n\n");
        closeout.nextActions().forEach(action -> builder.append("- ").append(action).append('\n'));
        builder.append("\n## Side Effect Contract\n\n")
                .append(closeout.sideEffectContract()).append('\n');
        return builder.toString();
    }

    private static void appendOptional(StringBuilder builder, String label, String value) {
        if (hasText(value)) {
            builder.append("- ").append(label).append(": ").append(value).append('\n');
        }
    }

    private static String issueUrl(DemoLiveTriggerOutcomeCloseoutCommand command) {
        return "https://github.com/%s/%s/issues/%d".formatted(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber()
        );
    }

    private static boolean equalsText(String left, String right) {
        return left != null && right != null && left.trim().equals(right.trim());
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String nullSafe(String value) {
        return hasText(value) ? value : "unknown";
    }
}
