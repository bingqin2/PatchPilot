package io.patchpilot.backend.task;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final FixTaskService fixTaskService;
    private final FixTaskTimelineService fixTaskTimelineService;

    public TaskController(FixTaskService fixTaskService, FixTaskTimelineService fixTaskTimelineService) {
        this.fixTaskService = fixTaskService;
        this.fixTaskTimelineService = fixTaskTimelineService;
    }

    @GetMapping
    public ApiResponse<List<FixTaskVo>> listTasks() {
        return ApiResponse.ok(fixTaskService.listTasks());
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
}
