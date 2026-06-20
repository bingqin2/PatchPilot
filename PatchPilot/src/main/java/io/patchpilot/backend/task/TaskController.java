package io.patchpilot.backend.task;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
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

    @GetMapping
    public ApiResponse<List<FixTaskVo>> listTasks() {
        return ApiResponse.ok(fixTaskService.listTasks());
    }

    @GetMapping("/metrics/summary")
    public ApiResponse<FixTaskMetricsSummaryVo> getTaskMetricsSummary() {
        return ApiResponse.ok(fixTaskMetricsService.summary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FixTaskVo>> getTask(@PathVariable String id) {
        return fixTaskService.findTask(id)
                .map(task -> ResponseEntity.ok(ApiResponse.ok(task)))
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
}
