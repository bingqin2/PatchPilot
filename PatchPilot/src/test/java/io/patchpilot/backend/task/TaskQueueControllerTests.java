package io.patchpilot.backend.task;

import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskQueueController.class)
class TaskQueueControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FixTaskQueueQueryService fixTaskQueueQueryService;

    @Test
    void should_list_queue_items_with_status_filter() throws Exception {
        when(fixTaskQueueQueryService.listItems(FixTaskQueueItemStatus.FAILED)).thenReturn(List.of(new FixTaskQueueItemVo(
                "queue-123",
                "task-123",
                FixTaskQueueItemStatus.FAILED,
                3,
                "worker failed",
                Instant.parse("2026-06-19T10:00:00Z"),
                null,
                Instant.parse("2026-06-19T09:59:00Z"),
                Instant.parse("2026-06-19T10:01:00Z")
        )));

        mockMvc.perform(get("/api/task-queue/items").param("status", "FAILED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("queue-123"))
                .andExpect(jsonPath("$.data[0].taskId").value("task-123"))
                .andExpect(jsonPath("$.data[0].status").value("FAILED"))
                .andExpect(jsonPath("$.data[0].attemptCount").value(3))
                .andExpect(jsonPath("$.data[0].lastError").value("worker failed"))
                .andExpect(jsonPath("$.data[0].availableAt").value("2026-06-19T10:00:00Z"))
                .andExpect(jsonPath("$.data[0].lockedAt").doesNotExist())
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-06-19T09:59:00Z"))
                .andExpect(jsonPath("$.data[0].updatedAt").value("2026-06-19T10:01:00Z"));
    }

    @Test
    void should_get_queue_summary() throws Exception {
        when(fixTaskQueueQueryService.summary()).thenReturn(new FixTaskQueueSummaryVo(
                10,
                4,
                3,
                1,
                2,
                3,
                1,
                0
        ));

        mockMvc.perform(get("/api/task-queue/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(10))
                .andExpect(jsonPath("$.data.pendingCount").value(4))
                .andExpect(jsonPath("$.data.availablePendingCount").value(3))
                .andExpect(jsonPath("$.data.delayedPendingCount").value(1))
                .andExpect(jsonPath("$.data.runningCount").value(2))
                .andExpect(jsonPath("$.data.completedCount").value(3))
                .andExpect(jsonPath("$.data.failedCount").value(1))
                .andExpect(jsonPath("$.data.cancelledCount").value(0));
    }
}
