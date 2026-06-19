package io.patchpilot.backend.task;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task-queue")
public class TaskQueueController {

    private final FixTaskQueueQueryService fixTaskQueueQueryService;

    public TaskQueueController(FixTaskQueueQueryService fixTaskQueueQueryService) {
        this.fixTaskQueueQueryService = fixTaskQueueQueryService;
    }

    @GetMapping("/items")
    public ApiResponse<List<FixTaskQueueItemVo>> listItems(
            @RequestParam(required = false) FixTaskQueueItemStatus status
    ) {
        return ApiResponse.ok(fixTaskQueueQueryService.listItems(status));
    }

    @GetMapping("/summary")
    public ApiResponse<FixTaskQueueSummaryVo> summary() {
        return ApiResponse.ok(fixTaskQueueQueryService.summary());
    }
}
