package io.patchpilot.backend.task;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskFailureCauseSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskAuditSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskLatencySummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskDetailVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelUsageSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskPageVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskAuditSummaryService;
import io.patchpilot.backend.task.service.FixTaskControlService;
import io.patchpilot.backend.task.service.FixTaskMetricsService;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final FixTaskControlService fixTaskControlService;
    private final FixTaskMetricsService fixTaskMetricsService;
    private final FixTaskAuditSummaryService fixTaskAuditSummaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<FixTaskPageVo>> listTasks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String repositoryOwner,
            @RequestParam(required = false) String repositoryName,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(queryTasks(query, status, repositoryOwner, repositoryName, limit, offset)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/metrics/summary")
    public ApiResponse<FixTaskMetricsSummaryVo> getTaskMetricsSummary() {
        return ApiResponse.ok(fixTaskMetricsService.summary());
    }

    @GetMapping("/metrics/failure-causes")
    public ApiResponse<List<FixTaskFailureCauseSummaryVo>> getTaskFailureCauseSummary() {
        return ApiResponse.ok(fixTaskMetricsService.failureCauses());
    }

    @GetMapping("/metrics/model-usage")
    public ApiResponse<FixTaskModelUsageSummaryVo> getTaskModelUsageSummary() {
        return ApiResponse.ok(fixTaskMetricsService.modelUsage());
    }

    @GetMapping("/metrics/latency")
    public ApiResponse<FixTaskLatencySummaryVo> getTaskLatencySummary() {
        return ApiResponse.ok(fixTaskMetricsService.latency());
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
                .map(summary -> ResponseEntity.ok(ApiResponse.ok(new FixTaskDetailVo(
                        summary,
                        fixTaskTimelineService.listEvents(id),
                        fixTaskTestRunService.listTestRuns(id),
                        fixTaskToolCallService.listToolCalls(id),
                        fixTaskModelCallService.listModelCalls(id)
                ))))
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

    private FixTaskPageVo queryTasks(
            String query,
            String status,
            String repositoryOwner,
            String repositoryName,
            Integer limit,
            Integer offset
    ) {
        FixTaskStatus parsedStatus = parseStatus(status);
        int parsedLimit = parseLimit(limit);
        int parsedOffset = parseOffset(offset);
        String parsedQuery = blankToNull(query);
        String parsedRepositoryOwner = blankToNull(repositoryOwner);
        String parsedRepositoryName = blankToNull(repositoryName);
        FixTaskListQuery pageQuery = new FixTaskListQuery(
                parsedQuery,
                parsedStatus,
                parsedRepositoryOwner,
                parsedRepositoryName,
                parsedLimit + 1,
                parsedOffset
        );
        FixTaskListQuery countQuery = new FixTaskListQuery(
                parsedQuery,
                parsedStatus,
                parsedRepositoryOwner,
                parsedRepositoryName,
                Integer.MAX_VALUE,
                0
        );
        List<FixTaskVo> tasks = fixTaskService.listTasks(pageQuery);
        long total = fixTaskService.countTasks(countQuery);
        boolean hasMore = tasks.size() > parsedLimit;
        List<FixTaskVo> pageItems = hasMore ? tasks.subList(0, parsedLimit) : tasks;
        return new FixTaskPageVo(pageItems, parsedLimit, parsedOffset, hasMore, total);
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

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
