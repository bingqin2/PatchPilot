package io.patchpilot.backend.task;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FixTaskService fixTaskService;

    @Autowired
    private FixTaskTimelineService fixTaskTimelineService;

    @Autowired
    private FixTaskTestRunService fixTaskTestRunService;

    @Autowired
    private FixTaskToolCallService fixTaskToolCallService;

    @Test
    void should_list_tasks() throws Exception {
        createTask("delivery-list");

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[0].id").value(not(nullValue())))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data[0].pullRequestUrl").value(nullValue()))
                .andExpect(jsonPath("$.data[0].completedAt").value(nullValue()))
                .andExpect(jsonPath("$.data[0].updatedAt").value(not(nullValue())))
                .andExpect(jsonPath("$.data[0].statusCommentId").value(nullValue()))
                .andExpect(jsonPath("$.data[0].statusCommentUrl").value(nullValue()));
    }

    @Test
    void should_get_task_by_id() throws Exception {
        FixTaskVo task = createTask("delivery-get");

        mockMvc.perform(get("/api/tasks/{id}", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(task.id()))
                .andExpect(jsonPath("$.data.repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data.repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data.issueNumber").value(42))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.pullRequestUrl").value(nullValue()))
                .andExpect(jsonPath("$.data.completedAt").value(nullValue()))
                .andExpect(jsonPath("$.data.updatedAt").value(not(nullValue())))
                .andExpect(jsonPath("$.data.statusCommentId").value(nullValue()))
                .andExpect(jsonPath("$.data.statusCommentUrl").value(nullValue()));
    }

    @Test
    void should_return_404_for_missing_task() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void should_get_task_timeline_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-timeline");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.TASK_CREATED, "Task accepted");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.RUNNING, "Task is running");

        mockMvc.perform(get("/api/tasks/{id}/timeline", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].taskId").value(task.id()))
                .andExpect(jsonPath("$.data[0].eventType").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data[0].message").value("Task accepted"))
                .andExpect(jsonPath("$.data[0].createdAt").value(not(nullValue())))
                .andExpect(jsonPath("$.data[1].eventType").value("RUNNING"));
    }

    @Test
    void should_return_404_for_missing_task_timeline() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/timeline", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_get_task_test_runs_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-test-runs");
        FixTaskTestRunVo testRun = fixTaskTestRunService.recordTestRun(
                task.id(),
                "./mvnw test",
                1,
                "test failed",
                Instant.parse("2026-06-19T08:00:00Z"),
                Instant.parse("2026-06-19T08:00:05Z")
        );

        mockMvc.perform(get("/api/tasks/{id}/test-runs", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(testRun.id()))
                .andExpect(jsonPath("$.data[0].taskId").value(task.id()))
                .andExpect(jsonPath("$.data[0].command").value("./mvnw test"))
                .andExpect(jsonPath("$.data[0].exitCode").value(1))
                .andExpect(jsonPath("$.data[0].output").value("test failed"))
                .andExpect(jsonPath("$.data[0].startedAt").value("2026-06-19T08:00:00Z"))
                .andExpect(jsonPath("$.data[0].finishedAt").value("2026-06-19T08:00:05Z"))
                .andExpect(jsonPath("$.data[0].durationMs").value(5000));
    }

    @Test
    void should_return_404_for_missing_task_test_runs() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/test-runs", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_get_task_tool_calls_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-tool-calls");
        FixTaskToolCallVo toolCall = fixTaskToolCallService.recordToolCall(
                task.id(),
                "CommitTool",
                "repositoryDir=/tmp/workspace/repo, message=PatchPilot task task-123",
                "committed",
                true,
                Instant.parse("2026-06-19T09:00:00Z"),
                Instant.parse("2026-06-19T09:00:02Z")
        );

        mockMvc.perform(get("/api/tasks/{id}/tool-calls", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(toolCall.id()))
                .andExpect(jsonPath("$.data[0].taskId").value(task.id()))
                .andExpect(jsonPath("$.data[0].toolName").value("CommitTool"))
                .andExpect(jsonPath("$.data[0].inputSummary").value("repositoryDir=/tmp/workspace/repo, message=PatchPilot task task-123"))
                .andExpect(jsonPath("$.data[0].outputSummary").value("committed"))
                .andExpect(jsonPath("$.data[0].success").value(true))
                .andExpect(jsonPath("$.data[0].startedAt").value("2026-06-19T09:00:00Z"))
                .andExpect(jsonPath("$.data[0].finishedAt").value("2026-06-19T09:00:02Z"))
                .andExpect(jsonPath("$.data[0].durationMs").value(2000));
    }

    @Test
    void should_return_404_for_missing_task_tool_calls() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/tool-calls", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_cancel_pending_task() throws Exception {
        FixTaskVo task = createTask("delivery-cancel-http");

        mockMvc.perform(post("/api/tasks/{id}/cancel", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(task.id()))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.failureReason").value("Task cancelled by user request"));
    }

    @Test
    void should_return_404_when_cancelling_missing_task() throws Exception {
        mockMvc.perform(post("/api/tasks/{id}/cancel", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_return_409_when_cancelling_running_task() throws Exception {
        FixTaskVo task = createTask("delivery-cancel-running-http");
        fixTaskService.markRunning(task.id());

        mockMvc.perform(post("/api/tasks/{id}/cancel", task.id()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only pending tasks can be cancelled"));
    }

    @Test
    void should_retry_failed_task() throws Exception {
        FixTaskVo task = createTask("delivery-retry-http");
        fixTaskService.markFailed(task.id(), "executor failed");

        mockMvc.perform(post("/api/tasks/{id}/retry", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(task.id()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.failureReason").value(nullValue()));
    }

    @Test
    void should_return_404_when_retrying_missing_task() throws Exception {
        mockMvc.perform(post("/api/tasks/{id}/retry", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_return_409_when_retrying_active_task() throws Exception {
        FixTaskVo task = createTask("delivery-retry-active-http");

        mockMvc.perform(post("/api/tasks/{id}/retry", task.id()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only failed or cancelled tasks can be retried"));
    }

    @Test
    void should_list_task_queue_items() throws Exception {
        mockMvc.perform(get("/api/task-queue/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void should_get_task_queue_summary() throws Exception {
        mockMvc.perform(get("/api/task-queue/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(0))
                .andExpect(jsonPath("$.data.pendingCount").value(0))
                .andExpect(jsonPath("$.data.availablePendingCount").value(0))
                .andExpect(jsonPath("$.data.delayedPendingCount").value(0))
                .andExpect(jsonPath("$.data.runningCount").value(0))
                .andExpect(jsonPath("$.data.completedCount").value(0))
                .andExpect(jsonPath("$.data.failedCount").value(0));
    }

    private FixTaskVo createTask(String deliveryId) {
        return fixTaskService.createFixTask(new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        ));
    }
}
