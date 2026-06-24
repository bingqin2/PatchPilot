package io.patchpilot.backend.task;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextComment;
import io.patchpilot.backend.task.domain.bo.ApproveReviewCommand;
import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.dto.ApproveReviewDto;
import io.patchpilot.backend.task.domain.dto.CreateFixTaskDto;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskSort;
import io.patchpilot.backend.task.domain.vo.FixTaskFailureCauseSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskAuditSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskFailureDiagnosisVo;
import io.patchpilot.backend.task.domain.vo.FixTaskGeneratedDiffVo;
import io.patchpilot.backend.task.domain.vo.FixTaskLatencySummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskDetailVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelUsageSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskPageVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskStatusCountsVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.IssueContextCommentVo;
import io.patchpilot.backend.task.domain.vo.IssueContextVo;
import io.patchpilot.backend.task.service.FixTaskAuditSummaryService;
import io.patchpilot.backend.task.service.FixTaskControlService;
import io.patchpilot.backend.task.service.FixTaskMetricsService;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.FixTaskPatchReviewService;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import io.patchpilot.backend.task.service.FixTaskReportFormatter;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import io.patchpilot.backend.task.service.ManualFixTaskService;
import io.patchpilot.backend.task.service.RepositorySupportGuidanceService;
import io.patchpilot.backend.task.service.TaskFailureFeedback;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final FixTaskService fixTaskService;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final FixTaskTestRunService fixTaskTestRunService;
    private final FixTaskToolCallService fixTaskToolCallService;
    private final FixTaskModelCallService fixTaskModelCallService;
    private final FixTaskPatchReviewService fixTaskPatchReviewService;
    private final FixTaskControlService fixTaskControlService;
    private final FixTaskMetricsService fixTaskMetricsService;
    private final FixTaskAuditSummaryService fixTaskAuditSummaryService;
    private final FixTaskQueueQueryService fixTaskQueueQueryService;
    private final FixTaskReportFormatter fixTaskReportFormatter;
    private final ManualFixTaskService manualFixTaskService;
    private final RepositorySupportGuidanceService repositorySupportGuidanceService;
    private final IssueContextService issueContextService;

    @PostMapping
    public ResponseEntity<ApiResponse<FixTaskVo>> createTask(@RequestBody CreateFixTaskDto request) {
        try {
            CreateManualFixTaskCommand command = manualTaskCommand(request);
            FixTaskVo task = manualFixTaskService.createManualTask(command);
            return ResponseEntity.created(URI.create("/api/tasks/" + task.id())).body(ApiResponse.ok(task));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(409).body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FixTaskPageVo>> listTasks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String buildSystem,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String sort
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(queryTasks(
                    query,
                    status,
                    repositoryOwner,
                    repositoryName,
                    language,
                    buildSystem,
                    createdAfter,
                    createdBefore,
                    limit,
                    offset,
                    sort
            )));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/status-counts")
    public ResponseEntity<ApiResponse<FixTaskStatusCountsVo>> getTaskStatusCounts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String buildSystem,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(countTasksByStatus(
                    query,
                    repositoryOwner,
                    repositoryName,
                    language,
                    buildSystem,
                    createdAfter,
                    createdBefore
            )));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/metrics/summary")
    public ResponseEntity<ApiResponse<FixTaskMetricsSummaryVo>> getTaskMetricsSummary(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String buildSystem,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskMetricsService.summary(metricsQuery(
                    query, repositoryOwner, repositoryName, language, buildSystem, createdAfter, createdBefore
            ))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/metrics/failure-causes")
    public ResponseEntity<ApiResponse<List<FixTaskFailureCauseSummaryVo>>> getTaskFailureCauseSummary(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String buildSystem,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskMetricsService.failureCauses(metricsQuery(
                    query, repositoryOwner, repositoryName, language, buildSystem, createdAfter, createdBefore
            ))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/metrics/model-usage")
    public ResponseEntity<ApiResponse<FixTaskModelUsageSummaryVo>> getTaskModelUsageSummary(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String buildSystem,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskMetricsService.modelUsage(metricsQuery(
                    query, repositoryOwner, repositoryName, language, buildSystem, createdAfter, createdBefore
            ))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/metrics/latency")
    public ResponseEntity<ApiResponse<FixTaskLatencySummaryVo>> getTaskLatencySummary(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String buildSystem,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskMetricsService.latency(metricsQuery(
                    query, repositoryOwner, repositoryName, language, buildSystem, createdAfter, createdBefore
            ))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FixTaskVo>> getTask(@PathVariable String id) {
        return fixTaskService.findTask(id)
                .map(task -> ResponseEntity.ok(ApiResponse.ok(task)))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.fail("Task not found")));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<FixTaskAuditSummaryVo>> getTaskAuditSummary(@PathVariable String id) {
        return fixTaskAuditSummaryService.summary(id)
                .map(summary -> ResponseEntity.ok(ApiResponse.ok(summary)))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.fail("Task not found")));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<FixTaskDetailVo>> getTaskDetail(@PathVariable String id) {
        return fixTaskAuditSummaryService.summary(id)
                .map(summary -> ResponseEntity.ok(ApiResponse.ok(buildTaskDetail(id, summary))))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.fail("Task not found")));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<ApiResponse<String>> getTaskReport(@PathVariable String id) {
        return fixTaskAuditSummaryService.summary(id)
                .map(summary -> ResponseEntity.ok(ApiResponse.ok(fixTaskReportFormatter.format(buildTaskDetail(id, summary)))))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.fail("Task not found")));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<ApiResponse<List<FixTaskTimelineEventVo>>> getTaskTimeline(@PathVariable String id) {
        if (fixTaskService.findTask(id).isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(fixTaskTimelineService.listEvents(id)));
    }

    @GetMapping("/{id}/test-runs")
    public ResponseEntity<ApiResponse<List<FixTaskTestRunVo>>> getTaskTestRuns(@PathVariable String id) {
        if (fixTaskService.findTask(id).isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(fixTaskTestRunService.listTestRuns(id)));
    }

    @GetMapping("/{id}/tool-calls")
    public ResponseEntity<ApiResponse<List<FixTaskToolCallVo>>> getTaskToolCalls(@PathVariable String id) {
        if (fixTaskService.findTask(id).isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(fixTaskToolCallService.listToolCalls(id)));
    }

    @GetMapping("/{id}/model-calls")
    public ResponseEntity<ApiResponse<List<FixTaskModelCallVo>>> getTaskModelCalls(@PathVariable String id) {
        if (fixTaskService.findTask(id).isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(fixTaskModelCallService.listModelCalls(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<FixTaskVo>> cancelTask(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskControlService.cancelTask(id)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(409).body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<FixTaskVo>> retryTask(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskControlService.retryTask(id)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(409).body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/{id}/approve-review")
    public ResponseEntity<ApiResponse<FixTaskVo>> approveReviewTask(
            @PathVariable String id,
            @RequestBody(required = false) ApproveReviewDto request
    ) {
        ApproveReviewCommand command;
        try {
            command = approveReviewCommand(request);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
        try {
            return ResponseEntity.ok(ApiResponse.ok(fixTaskControlService.approveReviewTask(id, command)));
        } catch (SecurityException exception) {
            return ResponseEntity.status(403).body(ApiResponse.fail(exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Task not found"));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(409).body(ApiResponse.fail(exception.getMessage()));
        }
    }

    private FixTaskPageVo queryTasks(
            String query,
            String status,
            String repositoryOwner,
            String repositoryName,
            String language,
            String buildSystem,
            String createdAfter,
            String createdBefore,
            Integer limit,
            Integer offset,
            String sort
    ) {
        FixTaskStatus parsedStatus = parseStatus(status);
        int parsedLimit = parseLimit(limit);
        int parsedOffset = parseOffset(offset);
        FixTaskSort parsedSort = parseSort(sort);
        String parsedQuery = blankToNull(query);
        String parsedRepositoryOwner = blankToNull(repositoryOwner);
        String parsedRepositoryName = blankToNull(repositoryName);
        String parsedLanguage = blankToNull(language);
        String parsedBuildSystem = blankToNull(buildSystem);
        Instant parsedCreatedAfter = parseInstant(createdAfter, "createdAfter");
        Instant parsedCreatedBefore = parseInstant(createdBefore, "createdBefore");
        FixTaskListQuery pageQuery = taskListQuery(
                parsedQuery,
                parsedStatus,
                parsedRepositoryOwner,
                parsedRepositoryName,
                parsedLanguage,
                parsedBuildSystem,
                parsedCreatedAfter,
                parsedCreatedBefore,
                parsedLimit + 1,
                parsedOffset,
                parsedSort
        );
        FixTaskListQuery countQuery = taskListQuery(
                parsedQuery,
                parsedStatus,
                parsedRepositoryOwner,
                parsedRepositoryName,
                parsedLanguage,
                parsedBuildSystem,
                parsedCreatedAfter,
                parsedCreatedBefore,
                Integer.MAX_VALUE,
                0,
                parsedSort
        );
        List<FixTaskVo> tasks = fixTaskService.listTasks(pageQuery);
        long total = fixTaskService.countTasks(countQuery);
        boolean hasMore = tasks.size() > parsedLimit;
        List<FixTaskVo> pageItems = hasMore ? tasks.subList(0, parsedLimit) : tasks;
        return new FixTaskPageVo(pageItems, parsedLimit, parsedOffset, hasMore, total);
    }

    private FixTaskStatusCountsVo countTasksByStatus(
            String query,
            String repositoryOwner,
            String repositoryName,
            String language,
            String buildSystem,
            String createdAfter,
            String createdBefore
    ) {
        String parsedQuery = blankToNull(query);
        String parsedRepositoryOwner = blankToNull(repositoryOwner);
        String parsedRepositoryName = blankToNull(repositoryName);
        String parsedLanguage = blankToNull(language);
        String parsedBuildSystem = blankToNull(buildSystem);
        Instant parsedCreatedAfter = parseInstant(createdAfter, "createdAfter");
        Instant parsedCreatedBefore = parseInstant(createdBefore, "createdBefore");
        return new FixTaskStatusCountsVo(
                countTasksWithStatus(parsedQuery, null, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.PENDING, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.RUNNING, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.RUNNING_TESTS, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.PENDING_REVIEW, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.COMPLETED, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.FAILED, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore),
                countTasksWithStatus(parsedQuery, FixTaskStatus.CANCELLED, parsedRepositoryOwner, parsedRepositoryName, parsedLanguage, parsedBuildSystem, parsedCreatedAfter, parsedCreatedBefore)
        );
    }

    private long countTasksWithStatus(
            String query,
            FixTaskStatus status,
            String repositoryOwner,
            String repositoryName,
            String language,
            String buildSystem,
            Instant createdAfter,
            Instant createdBefore
    ) {
        return fixTaskService.countTasks(taskListQuery(
                query,
                status,
                repositoryOwner,
                repositoryName,
                language,
                buildSystem,
                createdAfter,
                createdBefore,
                Integer.MAX_VALUE,
                0,
                FixTaskSort.CREATED_AT_DESC
        ));
    }

    private static FixTaskListQuery metricsQuery(
            String query,
            String repositoryOwner,
            String repositoryName,
            String language,
            String buildSystem,
            String createdAfter,
            String createdBefore
    ) {
        return taskListQuery(
                blankToNull(query),
                null,
                blankToNull(repositoryOwner),
                blankToNull(repositoryName),
                blankToNull(language),
                blankToNull(buildSystem),
                parseInstant(createdAfter, "createdAfter"),
                parseInstant(createdBefore, "createdBefore"),
                Integer.MAX_VALUE,
                0,
                FixTaskSort.CREATED_AT_DESC
        );
    }

    private static FixTaskListQuery taskListQuery(
            String query,
            FixTaskStatus status,
            String repositoryOwner,
            String repositoryName,
            String language,
            String buildSystem,
            Instant createdAfter,
            Instant createdBefore,
            int limit,
            int offset,
            FixTaskSort sort
    ) {
        return new FixTaskListQuery(
                query,
                status,
                repositoryOwner,
                repositoryName,
                language,
                buildSystem,
                createdAfter,
                createdBefore,
                limit,
                offset,
                sort
        );
    }

    private static CreateManualFixTaskCommand manualTaskCommand(CreateFixTaskDto request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        String repositoryOwner = requiredText(request.repositoryOwner(), "repositoryOwner must not be blank");
        String repositoryName = requiredText(request.repositoryName(), "repositoryName must not be blank");
        if (request.issueNumber() == null || request.issueNumber() < 1) {
            throw new IllegalArgumentException("issueNumber must be positive");
        }
        String triggerUser = requiredText(request.triggerUser(), "triggerUser must not be blank");
        String triggerComment = requiredText(request.triggerComment(), "triggerComment must not be blank");
        if (!triggerComment.equals("/agent fix") && !triggerComment.startsWith("/agent fix ")) {
            throw new IllegalArgumentException("triggerComment must start with /agent fix");
        }
        return new CreateManualFixTaskCommand(
                repositoryOwner,
                repositoryName,
                request.issueNumber(),
                triggerUser,
                triggerComment
        );
    }

    private static ApproveReviewCommand approveReviewCommand(ApproveReviewDto request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        String operator = requiredText(request.operator(), "operator must not be blank");
        String reason = requiredText(request.reason(), "reason must not be blank");
        return new ApproveReviewCommand(operator, reason);
    }

    private FixTaskDetailVo buildTaskDetail(String taskId, FixTaskAuditSummaryVo summary) {
        List<FixTaskQueueItemVo> queueItems = fixTaskQueueQueryService.listByTaskId(taskId);
        List<FixTaskToolCallVo> toolCalls = fixTaskToolCallService.listToolCalls(taskId);
        return new FixTaskDetailVo(
                summary,
                fixTaskTimelineService.listEvents(taskId),
                fixTaskTestRunService.listTestRuns(taskId),
                toolCalls,
                fixTaskModelCallService.listModelCalls(taskId),
                latestGeneratedDiff(toolCalls),
                fixTaskPatchReviewService.findLatestPatchReview(taskId).orElse(null),
                issueContext(summary.task()),
                failureDiagnosis(summary.task()),
                queueItems.stream().findFirst().orElse(null),
                queueItems,
                repositorySupportGuidanceService.guidanceFor(summary.task()).orElse(null)
        );
    }

    private static FixTaskFailureDiagnosisVo failureDiagnosis(FixTaskVo task) {
        if (task.status() != FixTaskStatus.FAILED || task.failureReason() == null || task.failureReason().isBlank()) {
            return null;
        }
        TaskFailureFeedback feedback = TaskFailureFeedback.from(task.failureReason());
        return new FixTaskFailureDiagnosisVo(
                feedback.category(),
                feedback.nextAction(),
                feedback.safeReason()
        );
    }

    private IssueContextVo issueContext(FixTaskVo task) {
        try {
            return toIssueContextVo(issueContextService.loadIssueContext(task));
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static IssueContextVo toIssueContextVo(GitHubIssueContext context) {
        return new IssueContextVo(
                context.title(),
                context.body(),
                context.url(),
                context.comments().stream()
                        .map(TaskController::toIssueContextCommentVo)
                        .toList()
        );
    }

    private static IssueContextCommentVo toIssueContextCommentVo(GitHubIssueContextComment comment) {
        return new IssueContextCommentVo(
                comment.id(),
                comment.author(),
                comment.body(),
                comment.createdAt(),
                comment.url()
        );
    }

    private static FixTaskGeneratedDiffVo latestGeneratedDiff(List<FixTaskToolCallVo> toolCalls) {
        return toolCalls.stream()
                .filter(call -> "DiffTool".equals(call.toolName()))
                .filter(FixTaskToolCallVo::success)
                .filter(call -> call.outputSummary() != null && !call.outputSummary().isBlank())
                .max((left, right) -> left.finishedAt().compareTo(right.finishedAt()))
                .map(call -> new FixTaskGeneratedDiffVo(call.id(), call.outputSummary(), call.finishedAt()))
                .orElse(null);
    }

    private static FixTaskStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return FixTaskStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid task status: " + status);
        }
    }

    private static int parseLimit(Integer limit) {
        if (limit == null) {
            return 50;
        }
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }
        return limit;
    }

    private static int parseOffset(Integer offset) {
        if (offset == null) {
            return 0;
        }
        if (offset < 0 || offset > 10000) {
            throw new IllegalArgumentException("offset must be between 0 and 10000");
        }
        return offset;
    }

    private static FixTaskSort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return FixTaskSort.CREATED_AT_DESC;
        }
        return FixTaskSort.fromApiValue(sort.trim());
    }

    private static Instant parseInstant(String value, String parameterName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(parameterName + " must be an ISO-8601 instant");
        }
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
