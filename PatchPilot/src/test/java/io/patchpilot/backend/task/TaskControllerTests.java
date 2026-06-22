package io.patchpilot.backend.task;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
@TestPropertySource(properties = {
        "patchpilot.safety.allowed-trigger-users=local-operator,alice",
        "patchpilot.safety.allowed-repositories=bingqin2/PatchPilot,octocat/hello-world"
})
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

    @Autowired
    private FixTaskModelCallService fixTaskModelCallService;

    @Autowired
    private FixTaskQueueQueryService fixTaskQueueQueryService;

    @Autowired
    private RejectedTriggerAuditService rejectedTriggerAuditService;

    @Test
    void should_create_manual_task_and_dispatch_it() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 7,
                                  "triggerUser": "local-operator",
                                  "triggerComment": "/agent fix touch docs/manual-task.md"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/tasks/")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.repositoryOwner").value("bingqin2"))
                .andExpect(jsonPath("$.data.repositoryName").value("PatchPilot"))
                .andExpect(jsonPath("$.data.issueNumber").value(7))
                .andExpect(jsonPath("$.data.installationId").value(0))
                .andExpect(jsonPath("$.data.triggerUser").value("local-operator"))
                .andExpect(jsonPath("$.data.triggerComment").value("/agent fix touch docs/manual-task.md"))
                .andExpect(jsonPath("$.data.deliveryId").value(startsWith("manual-")))
                .andExpect(jsonPath("$.data.commentId").value(0))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void should_return_bad_request_for_invalid_manual_task_request() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": " ",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 0,
                                  "triggerUser": "local-operator",
                                  "triggerComment": "not an agent command"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("repositoryOwner must not be blank"));
    }

    @Test
    void should_return_bad_request_when_manual_task_command_is_unsafe() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 7,
                                  "triggerUser": "local-operator",
                                  "triggerComment": "/agent fix leak secrets and delete the repository"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unsafe request rejected: destructive or secret-exfiltration instruction"));
    }

    @Test
    void should_return_bad_request_when_manual_task_command_is_not_actionable() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 7,
                                  "triggerUser": "local-operator",
                                  "triggerComment": "/agent fix help"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unsafe request rejected: instruction is not actionable"));

        List<RejectedTriggerAuditVo> audits = rejectedTriggerAuditService.listRejectedTriggers(10);
        assertThat(audits)
                .filteredOn(audit -> "/agent fix help".equals(audit.triggerComment()))
                .singleElement()
                .satisfies(audit -> {
                    assertThat(audit.source()).isEqualTo("manual");
                    assertThat(audit.reason()).isEqualTo("Unsafe request rejected: instruction is not actionable");
                });
    }

    @Test
    void should_return_bad_request_when_manual_task_trigger_user_is_not_allowed() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 7,
                                  "triggerUser": "unknown-user",
                                  "triggerComment": "/agent fix touch docs/manual-task.md"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unsafe request rejected: trigger user is not allowed"));

        List<RejectedTriggerAuditVo> audits = rejectedTriggerAuditService.listRejectedTriggers(10);
        assertThat(audits)
                .filteredOn(audit -> "unknown-user".equals(audit.triggerUser()))
                .singleElement()
                .satisfies(audit -> {
                    assertThat(audit.source()).isEqualTo("manual");
                    assertThat(audit.repositoryOwner()).isEqualTo("bingqin2");
                    assertThat(audit.repositoryName()).isEqualTo("PatchPilot");
                    assertThat(audit.issueNumber()).isEqualTo(7L);
                    assertThat(audit.reason()).isEqualTo("Unsafe request rejected: trigger user is not allowed");
                });
    }

    @Test
    void should_return_bad_request_when_manual_task_repository_is_not_allowed() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "OtherRepo",
                                  "issueNumber": 7,
                                  "triggerUser": "local-operator",
                                  "triggerComment": "/agent fix touch docs/manual-task.md"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unsafe request rejected: repository is not allowed"));
    }

    @Test
    void should_return_conflict_when_manual_task_already_active_for_issue() throws Exception {
        createTask(new CreateFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                8,
                0,
                "local-operator",
                "/agent fix touch docs/existing-task.md",
                "delivery-manual-active",
                0
        ));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 8,
                                  "triggerUser": "local-operator",
                                  "triggerComment": "/agent fix touch docs/manual-task.md"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An active task already exists for this issue"));
    }

    @Test
    void should_list_tasks() throws Exception {
        createTask("delivery-list");

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.limit").value(50))
                .andExpect(jsonPath("$.data.offset").value(0))
                .andExpect(jsonPath("$.data.hasMore").value(false))
                .andExpect(jsonPath("$.data.items[0].id").value(not(nullValue())))
                .andExpect(jsonPath("$.data.items[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data.items[0].pullRequestUrl").value(nullValue()))
                .andExpect(jsonPath("$.data.items[0].completedAt").value(nullValue()))
                .andExpect(jsonPath("$.data.items[0].updatedAt").value(not(nullValue())))
                .andExpect(jsonPath("$.data.items[0].statusCommentId").value(nullValue()))
                .andExpect(jsonPath("$.data.items[0].statusCommentUrl").value(nullValue()));
    }

    @Test
    void should_return_task_list_pagination_metadata() throws Exception {
        FixTaskVo olderTask = createTask(command("pagination-owner", "pagination-repo", "delivery-page-older"));
        FixTaskVo newerTask = createTask(command("pagination-owner", "pagination-repo", "delivery-page-newer"));

        mockMvc.perform(get("/api/tasks")
                        .param("repositoryOwner", "pagination-owner")
                        .param("repositoryName", "pagination-repo")
                        .param("limit", "1")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(newerTask.id()))
                .andExpect(jsonPath("$.data.limit").value(1))
                .andExpect(jsonPath("$.data.offset").value(0))
                .andExpect(jsonPath("$.data.hasMore").value(true))
                .andExpect(jsonPath("$.data.total").value(2));

        mockMvc.perform(get("/api/tasks")
                        .param("repositoryOwner", "pagination-owner")
                        .param("repositoryName", "pagination-repo")
                        .param("limit", "1")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(olderTask.id()))
                .andExpect(jsonPath("$.data.limit").value(1))
                .andExpect(jsonPath("$.data.offset").value(1))
                .andExpect(jsonPath("$.data.hasMore").value(false))
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    void should_sort_tasks_oldest_first() throws Exception {
        FixTaskVo olderTask = createTask(command("sort-owner", "sort-repo", "delivery-sort-older"));
        FixTaskVo newerTask = createTask(command("sort-owner", "sort-repo", "delivery-sort-newer"));

        mockMvc.perform(get("/api/tasks")
                        .param("repositoryOwner", "sort-owner")
                        .param("repositoryName", "sort-repo")
                        .param("sort", "createdAtAsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(2))
                .andExpect(jsonPath("$.data.items[0].id").value(olderTask.id()))
                .andExpect(jsonPath("$.data.items[1].id").value(newerTask.id()));
    }

    @Test
    void should_filter_tasks_by_created_time_range() throws Exception {
        FixTaskVo olderTask = createTask(command("created-range-owner", "created-range-repo", "delivery-created-range-older"));
        FixTaskVo newerTask = createTask(command("created-range-owner", "created-range-repo", "delivery-created-range-newer"));

        mockMvc.perform(get("/api/tasks")
                        .param("repositoryOwner", "created-range-owner")
                        .param("repositoryName", "created-range-repo")
                        .param("createdAfter", olderTask.createdAt().plusNanos(1).toString())
                        .param("createdBefore", newerTask.createdAt().plusNanos(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(newerTask.id()))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void should_count_tasks_by_status_for_filtered_scope() throws Exception {
        FixTaskVo olderPendingTask = createTask(command(
                "status-count-owner",
                "status-count-repo",
                "delivery-status-count-pending-older"
        ));
        FixTaskVo completedTask = createTask(command(
                "status-count-owner",
                "status-count-repo",
                "delivery-status-count-completed"
        ));
        FixTaskVo failedTask = createTask(command(
                "status-count-owner",
                "status-count-repo",
                "delivery-status-count-failed"
        ));
        FixTaskVo otherRepositoryTask = createTask(command(
                "status-count-owner",
                "other-repo",
                "delivery-status-count-other"
        ));
        fixTaskService.markCompleted(completedTask.id(), "https://github.com/status-count-owner/status-count-repo/pull/1");
        fixTaskService.markFailed(failedTask.id(), "search target failed");
        fixTaskService.markFailed(otherRepositoryTask.id(), "search target failed");

        mockMvc.perform(get("/api/tasks/status-counts")
                        .param("query", "search target")
                        .param("repositoryOwner", "status-count-owner")
                        .param("repositoryName", "status-count-repo")
                        .param("createdAfter", olderPendingTask.createdAt().plusNanos(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.pendingCount").value(0))
                .andExpect(jsonPath("$.data.runningCount").value(0))
                .andExpect(jsonPath("$.data.runningTestsCount").value(0))
                .andExpect(jsonPath("$.data.pendingReviewCount").value(0))
                .andExpect(jsonPath("$.data.completedCount").value(0))
                .andExpect(jsonPath("$.data.failedCount").value(1))
                .andExpect(jsonPath("$.data.cancelledCount").value(0));
    }

    @Test
    void should_filter_and_count_pending_review_tasks() throws Exception {
        FixTaskVo reviewTask = createTask(command(
                "risk-owner",
                "risk-repo",
                "delivery-risk-review"
        ));
        FixTaskVo failedTask = createTask(command(
                "risk-owner",
                "risk-repo",
                "delivery-risk-failed"
        ));
        fixTaskService.markPendingReview(reviewTask.id(), "Generated diff rejected: sensitive path .env");
        fixTaskService.markFailed(failedTask.id(), "maven failed");

        mockMvc.perform(get("/api/tasks")
                        .param("status", "PENDING_REVIEW")
                        .param("repositoryOwner", "risk-owner")
                        .param("repositoryName", "risk-repo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(reviewTask.id()))
                .andExpect(jsonPath("$.data.items[0].status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.data.items[0].failureReason").value("Generated diff rejected: sensitive path .env"));

        mockMvc.perform(get("/api/tasks/status-counts")
                        .param("repositoryOwner", "risk-owner")
                        .param("repositoryName", "risk-repo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.pendingReviewCount").value(1))
                .andExpect(jsonPath("$.data.failedCount").value(1));
    }

    @Test
    void should_approve_pending_review_task() throws Exception {
        FixTaskVo reviewTask = createTask(command(
                "approve-owner",
                "approve-repo",
                "delivery-approve-review"
        ));
        fixTaskService.markPendingReview(reviewTask.id(), "Generated diff rejected: sensitive path .env");

        mockMvc.perform(post("/api/tasks/{id}/approve-review", reviewTask.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(reviewTask.id()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.failureReason").value(nullValue()))
                .andExpect(jsonPath("$.data.riskReviewApprovedAt").value(not(nullValue())));

        assertThat(fixTaskTimelineService.listEvents(reviewTask.id()))
                .extracting(event -> event.eventType())
                .contains(FixTaskTimelineEventType.REVIEW_APPROVED);
    }

    @Test
    void should_return_conflict_when_approving_non_pending_review_task() throws Exception {
        FixTaskVo task = createTask(command(
                "approve-owner",
                "approve-repo",
                "delivery-approve-conflict"
        ));

        mockMvc.perform(post("/api/tasks/{id}/approve-review", task.id()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only pending review tasks can be approved"));
    }

    @Test
    void should_filter_tasks_and_status_counts_by_adapter_metadata() throws Exception {
        FixTaskVo mavenTask = createTask(command(
                "adapter-owner",
                "adapter-repo",
                "delivery-adapter-maven"
        ));
        FixTaskVo npmTask = createTask(command(
                "adapter-owner",
                "adapter-repo",
                "delivery-adapter-npm"
        ));
        fixTaskService.recordAdapterMetadata(mavenTask.id(), "java", "maven", "./mvnw test");
        fixTaskService.recordAdapterMetadata(npmTask.id(), "node", "npm", "npm test");
        fixTaskService.markFailed(npmTask.id(), "npm test failed");

        mockMvc.perform(get("/api/tasks")
                        .param("repositoryOwner", "adapter-owner")
                        .param("repositoryName", "adapter-repo")
                        .param("language", "node")
                        .param("buildSystem", "npm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(npmTask.id()))
                .andExpect(jsonPath("$.data.items[0].language").value("node"))
                .andExpect(jsonPath("$.data.items[0].buildSystem").value("npm"))
                .andExpect(jsonPath("$.data.total").value(1));

        mockMvc.perform(get("/api/tasks/status-counts")
                        .param("repositoryOwner", "adapter-owner")
                        .param("repositoryName", "adapter-repo")
                        .param("language", "node")
                        .param("buildSystem", "npm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.failedCount").value(1))
                .andExpect(jsonPath("$.data.pendingCount").value(0));
    }

    @Test
    void should_return_bad_request_for_invalid_status_count_created_time_filter() throws Exception {
        mockMvc.perform(get("/api/tasks/status-counts").param("createdBefore", "not-an-instant"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("createdBefore must be an ISO-8601 instant"));
    }

    @Test
    void should_return_bad_request_for_invalid_created_time_filter() throws Exception {
        mockMvc.perform(get("/api/tasks").param("createdAfter", "not-an-instant"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("createdAfter must be an ISO-8601 instant"));
    }

    @Test
    void should_filter_tasks_by_status_repository_and_limit() throws Exception {
        FixTaskVo completedTask = createTask(command("octocat", "hello-world", "delivery-filter-completed"));
        FixTaskVo failedTask = createTask(command("octocat", "hello-world", "delivery-filter-failed"));
        FixTaskVo otherRepositoryTask = createTask(command("octocat", "other-repo", "delivery-filter-other"));
        fixTaskService.markCompleted(completedTask.id(), "https://github.com/octocat/hello-world/pull/7");
        fixTaskService.markFailed(failedTask.id(), "maven failed");
        fixTaskService.markFailed(otherRepositoryTask.id(), "maven failed");

        mockMvc.perform(get("/api/tasks")
                        .param("status", "FAILED")
                        .param("repositoryOwner", "octocat")
                        .param("repositoryName", "hello-world")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(failedTask.id()))
                .andExpect(jsonPath("$.data.items[0].status").value("FAILED"))
                .andExpect(jsonPath("$.data.items[0].repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data.items[0].repositoryName").value("hello-world"));
    }

    @Test
    void should_search_tasks_and_apply_offset_after_filtering() throws Exception {
        FixTaskVo olderMatchingTask = createTask(command("octocat", "hello-world", "delivery-search-older"));
        FixTaskVo newerMatchingTask = createTask(command("octocat", "hello-world", "delivery-search-newer"));
        FixTaskVo skippedTask = createTask(command("octocat", "hello-world", "delivery-search-skipped"));
        fixTaskService.markFailed(olderMatchingTask.id(), "maven failed because search target");
        fixTaskService.markFailed(newerMatchingTask.id(), "maven failed because search target");
        fixTaskService.markFailed(skippedTask.id(), "different failure");

        mockMvc.perform(get("/api/tasks")
                        .param("query", "search target")
                        .param("status", "FAILED")
                        .param("limit", "1")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(olderMatchingTask.id()))
                .andExpect(jsonPath("$.data.items[0].failureReason").value("maven failed because search target"));
    }

    @Test
    void should_return_bad_request_for_invalid_task_list_offset() throws Exception {
        mockMvc.perform(get("/api/tasks").param("offset", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("offset must be between 0 and 10000"));
    }

    @Test
    void should_return_bad_request_for_invalid_task_list_limit() throws Exception {
        mockMvc.perform(get("/api/tasks").param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }

    @Test
    void should_return_bad_request_for_invalid_task_list_status() throws Exception {
        mockMvc.perform(get("/api/tasks").param("status", "DONE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid task status: DONE"));
    }

    @Test
    void should_return_bad_request_for_invalid_task_list_sort() throws Exception {
        mockMvc.perform(get("/api/tasks").param("sort", "updatedAtDesc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("sort must be createdAtDesc or createdAtAsc"));
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
    void should_get_task_model_calls_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-model-calls");
        FixTaskModelCallVo modelCall = fixTaskModelCallService.recordModelCall(
                task.id(),
                "openai",
                "gpt-4.1-mini",
                "Fix calculator bug",
                "Changed Calculator#add",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:04Z")
        );

        mockMvc.perform(get("/api/tasks/{id}/model-calls", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(modelCall.id()))
                .andExpect(jsonPath("$.data[0].taskId").value(task.id()))
                .andExpect(jsonPath("$.data[0].provider").value("openai"))
                .andExpect(jsonPath("$.data[0].model").value("gpt-4.1-mini"))
                .andExpect(jsonPath("$.data[0].promptSummary").value("Fix calculator bug"))
                .andExpect(jsonPath("$.data[0].responseSummary").value("Changed Calculator#add"))
                .andExpect(jsonPath("$.data[0].promptTokens").value(120))
                .andExpect(jsonPath("$.data[0].completionTokens").value(80))
                .andExpect(jsonPath("$.data[0].totalTokens").value(200))
                .andExpect(jsonPath("$.data[0].success").value(true))
                .andExpect(jsonPath("$.data[0].errorMessage").value(nullValue()))
                .andExpect(jsonPath("$.data[0].startedAt").value("2026-06-20T01:00:00Z"))
                .andExpect(jsonPath("$.data[0].finishedAt").value("2026-06-20T01:00:04Z"))
                .andExpect(jsonPath("$.data[0].durationMs").value(4000));
    }

    @Test
    void should_return_404_for_missing_task_model_calls() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/model-calls", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_get_task_audit_summary_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-audit-summary");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.TASK_CREATED, "Task accepted");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.COMPLETED, "Task completed");
        fixTaskTestRunService.recordTestRun(
                task.id(),
                "./mvnw test",
                0,
                "tests passed",
                Instant.parse("2026-06-20T03:00:00Z"),
                Instant.parse("2026-06-20T03:00:07Z")
        );
        fixTaskToolCallService.recordToolCall(
                task.id(),
                "DiffTool",
                "repositoryDir=/tmp/workspace/repo",
                "diff ok",
                true,
                Instant.parse("2026-06-20T03:00:08Z"),
                Instant.parse("2026-06-20T03:00:09Z")
        );
        fixTaskModelCallService.recordModelCall(
                task.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Changed demo file",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T03:00:10Z"),
                Instant.parse("2026-06-20T03:00:14Z")
        );

        mockMvc.perform(get("/api/tasks/{id}/summary", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.task.id").value(task.id()))
                .andExpect(jsonPath("$.data.task.status").value("PENDING"))
                .andExpect(jsonPath("$.data.timelineEventCount").value(2))
                .andExpect(jsonPath("$.data.testRunCount").value(1))
                .andExpect(jsonPath("$.data.toolCallCount").value(1))
                .andExpect(jsonPath("$.data.modelCallCount").value(1))
                .andExpect(jsonPath("$.data.totalModelTokens").value(200))
                .andExpect(jsonPath("$.data.latestTimelineEvent.eventType").value("COMPLETED"))
                .andExpect(jsonPath("$.data.latestTimelineEvent.message").value("Task completed"))
                .andExpect(jsonPath("$.data.latestTestRunExitCode").value(0))
                .andExpect(jsonPath("$.data.latestTestRunDurationMs").value(7000));
    }

    @Test
    void should_get_task_detail_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-detail");
        fixTaskService.recordAdapterMetadata(
                task.id(),
                "java",
                "maven",
                "./mvnw test",
                "pom.xml detected with mvnw wrapper"
        );
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.TASK_CREATED, "Task accepted");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.COMPLETED, "Task completed");
        FixTaskTestRunVo testRun = fixTaskTestRunService.recordTestRun(
                task.id(),
                "./mvnw test",
                0,
                "tests passed",
                Instant.parse("2026-06-20T04:00:00Z"),
                Instant.parse("2026-06-20T04:00:06Z")
        );
        FixTaskToolCallVo toolCall = fixTaskToolCallService.recordToolCall(
                task.id(),
                "CommitTool",
                "repositoryDir=/tmp/workspace/repo",
                "committed",
                true,
                Instant.parse("2026-06-20T04:00:07Z"),
                Instant.parse("2026-06-20T04:00:08Z")
        );
        FixTaskModelCallVo modelCall = fixTaskModelCallService.recordModelCall(
                task.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Changed demo file",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T04:00:09Z"),
                Instant.parse("2026-06-20T04:00:13Z")
        );
        ((RecordingFixTaskQueueQueryService) fixTaskQueueQueryService).setQueueItems(List.of(
                new FixTaskQueueItemVo(
                        "queue-detail-latest",
                        task.id(),
                        FixTaskQueueItemStatus.FAILED,
                        3,
                        "maven tests failed",
                        Instant.parse("2026-06-20T04:02:00Z"),
                        Instant.parse("2026-06-20T04:01:00Z"),
                        Instant.parse("2026-06-20T04:00:00Z"),
                        Instant.parse("2026-06-20T04:03:00Z")
                ),
                new FixTaskQueueItemVo(
                        "queue-detail-older",
                        task.id(),
                        FixTaskQueueItemStatus.PENDING,
                        1,
                        null,
                        Instant.parse("2026-06-20T04:00:30Z"),
                        null,
                        Instant.parse("2026-06-20T04:00:00Z"),
                        Instant.parse("2026-06-20T04:00:30Z")
                )
        ));

        mockMvc.perform(get("/api/tasks/{id}/detail", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.summary.task.id").value(task.id()))
                .andExpect(jsonPath("$.data.summary.task.language").value("java"))
                .andExpect(jsonPath("$.data.summary.task.buildSystem").value("maven"))
                .andExpect(jsonPath("$.data.summary.task.verificationCommand").value("./mvnw test"))
                .andExpect(jsonPath("$.data.summary.task.adapterDetectionReason").value("pom.xml detected with mvnw wrapper"))
                .andExpect(jsonPath("$.data.summary.timelineEventCount").value(2))
                .andExpect(jsonPath("$.data.summary.testRunCount").value(1))
                .andExpect(jsonPath("$.data.summary.toolCallCount").value(1))
                .andExpect(jsonPath("$.data.summary.modelCallCount").value(1))
                .andExpect(jsonPath("$.data.summary.totalModelTokens").value(200))
                .andExpect(jsonPath("$.data.timeline.length()").value(2))
                .andExpect(jsonPath("$.data.timeline[0].message").value("Task accepted"))
                .andExpect(jsonPath("$.data.timeline[1].message").value("Task completed"))
                .andExpect(jsonPath("$.data.testRuns[0].id").value(testRun.id()))
                .andExpect(jsonPath("$.data.toolCalls[0].id").value(toolCall.id()))
                .andExpect(jsonPath("$.data.modelCalls[0].id").value(modelCall.id()))
                .andExpect(jsonPath("$.data.queueItem.id").value("queue-detail-latest"))
                .andExpect(jsonPath("$.data.queueItem.taskId").value(task.id()))
                .andExpect(jsonPath("$.data.queueItem.status").value("FAILED"))
                .andExpect(jsonPath("$.data.queueItem.attemptCount").value(3))
                .andExpect(jsonPath("$.data.queueItem.lastError").value("maven tests failed"))
                .andExpect(jsonPath("$.data.queueItems.length()").value(2))
                .andExpect(jsonPath("$.data.queueItems[0].id").value("queue-detail-latest"))
                .andExpect(jsonPath("$.data.queueItems[0].status").value("FAILED"))
                .andExpect(jsonPath("$.data.queueItems[1].id").value("queue-detail-older"))
                .andExpect(jsonPath("$.data.queueItems[1].status").value("PENDING"));
    }

    @Test
    void should_return_404_for_missing_task_detail() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/detail", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_get_task_report_by_task_id() throws Exception {
        FixTaskVo task = createTask("delivery-report");
        fixTaskService.recordAdapterMetadata(
                task.id(),
                "java",
                "maven",
                "./mvnw test",
                "pom.xml detected with mvnw wrapper"
        );
        fixTaskService.markFailed(task.id(), "maven tests failed");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.TASK_CREATED, "Task accepted");
        fixTaskTimelineService.recordEvent(task.id(), FixTaskTimelineEventType.FAILED, "Task failed");
        fixTaskTestRunService.recordTestRun(
                task.id(),
                "./mvnw test",
                1,
                "tests failed",
                Instant.parse("2026-06-20T05:00:00Z"),
                Instant.parse("2026-06-20T05:00:06Z")
        );
        fixTaskToolCallService.recordToolCall(
                task.id(),
                "CommitTool",
                "repositoryDir=/tmp/workspace/repo",
                "commit skipped",
                false,
                Instant.parse("2026-06-20T05:00:07Z"),
                Instant.parse("2026-06-20T05:00:08Z")
        );
        fixTaskModelCallService.recordModelCall(
                task.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Plan generated",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T05:00:09Z"),
                Instant.parse("2026-06-20T05:00:13Z")
        );
        ((RecordingFixTaskQueueQueryService) fixTaskQueueQueryService).setQueueItems(List.of(new FixTaskQueueItemVo(
                "queue-report",
                task.id(),
                FixTaskQueueItemStatus.FAILED,
                2,
                "worker failed",
                Instant.parse("2026-06-20T05:01:00Z"),
                Instant.parse("2026-06-20T05:00:30Z"),
                Instant.parse("2026-06-20T05:00:00Z"),
                Instant.parse("2026-06-20T05:02:00Z")
        )));

        mockMvc.perform(get("/api/tasks/{id}/report", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("# PatchPilot Task Report")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Task: `" + task.id() + "`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Status: `FAILED`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Failure: maven tests failed")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("## Adapter")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Language: `java`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Build system: `maven`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Verification: `./mvnw test`")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Detection reason: pom.xml detected with mvnw wrapper")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("## Queue")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- Latest: `FAILED`, attempt 2")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("## Test Runs")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- `./mvnw test` -> exit 1")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("## Tool Calls")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- `CommitTool` -> failed")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("## Model Calls")))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("- `gpt-5.5` -> success, 200 tokens")));
    }

    @Test
    void should_return_404_for_missing_task_report() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/report", "missing-task"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void should_return_404_for_missing_task_audit_summary() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}/summary", "missing-task"))
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
    void should_cancel_running_task() throws Exception {
        FixTaskVo task = createTask("delivery-cancel-running-http");
        fixTaskService.markRunning(task.id());

        mockMvc.perform(post("/api/tasks/{id}/cancel", task.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(task.id()))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void should_return_409_when_cancelling_terminal_task() throws Exception {
        FixTaskVo task = createTask("delivery-cancel-terminal-http");
        fixTaskService.markCompleted(task.id(), "https://github.com/octocat/hello-world/pull/7");

        mockMvc.perform(post("/api/tasks/{id}/cancel", task.id()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only active tasks can be cancelled"));
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

    @Test
    void should_get_task_metrics_summary() throws Exception {
        FixTaskVo completedTask = createTask("delivery-metrics-completed");
        FixTaskVo failedTask = createTask("delivery-metrics-failed");
        fixTaskService.markCompleted(completedTask.id(), "https://github.com/octocat/hello-world/pull/7");
        fixTaskService.markFailed(failedTask.id(), "maven failed");
        fixTaskModelCallService.recordModelCall(
                completedTask.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Changed demo file",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T02:00:00Z"),
                Instant.parse("2026-06-20T02:00:04Z")
        );
        fixTaskTestRunService.recordTestRun(
                completedTask.id(),
                "./mvnw test",
                0,
                "tests passed",
                Instant.parse("2026-06-20T02:00:05Z"),
                Instant.parse("2026-06-20T02:00:09Z")
        );
        fixTaskTestRunService.recordTestRun(
                failedTask.id(),
                "./mvnw test",
                1,
                "tests failed",
                Instant.parse("2026-06-20T02:00:10Z"),
                Instant.parse("2026-06-20T02:00:14Z")
        );

        mockMvc.perform(get("/api/tasks/metrics/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.completedCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.failedCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.completionRate").value(greaterThanOrEqualTo(0.0)))
                .andExpect(jsonPath("$.data.failureRate").value(greaterThanOrEqualTo(0.0)))
                .andExpect(jsonPath("$.data.averageCompletionDurationMs").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.totalModelTokens").value(greaterThanOrEqualTo(200)))
                .andExpect(jsonPath("$.data.averageModelTokensPerCompletedTask").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.testRunCount").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.passedTestRunCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.failedTestRunCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.testPassRate").value(greaterThanOrEqualTo(0.0)));
    }

    @Test
    void should_get_task_metrics_summary_for_adapter_scope() throws Exception {
        FixTaskVo mavenTask = createTask("delivery-metrics-adapter-maven");
        FixTaskVo npmTask = createTask("delivery-metrics-adapter-npm");
        fixTaskService.recordAdapterMetadata(mavenTask.id(), "java", "maven", "./mvnw test");
        fixTaskService.recordAdapterMetadata(npmTask.id(), "node", "npm", "npm test");
        fixTaskService.markCompleted(mavenTask.id(), "https://github.com/octocat/hello-world/pull/7");
        fixTaskService.markFailed(npmTask.id(), "npm failed");
        fixTaskModelCallService.recordModelCall(
                npmTask.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Changed package",
                30,
                20,
                true,
                null,
                Instant.parse("2026-06-20T02:00:00Z"),
                Instant.parse("2026-06-20T02:00:04Z")
        );

        mockMvc.perform(get("/api/tasks/metrics/summary")
                        .param("repositoryOwner", "octocat")
                        .param("repositoryName", "hello-world")
                        .param("language", "node")
                        .param("buildSystem", "npm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.completedCount").value(0))
                .andExpect(jsonPath("$.data.failedCount").value(1))
                .andExpect(jsonPath("$.data.totalModelTokens").value(50));
    }

    @Test
    void should_get_task_failure_cause_summary() throws Exception {
        FixTaskVo mavenFailure = createTask("delivery-failure-cause-maven");
        FixTaskVo githubFailure = createTask("delivery-failure-cause-github");
        createTask("delivery-failure-cause-pending");
        fixTaskService.markFailed(mavenFailure.id(), "maven tests failed: compilation error");
        fixTaskService.markFailed(githubFailure.id(), "GitHub token is required to create Pull Requests");

        mockMvc.perform(get("/api/tasks/metrics/failure-causes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data[0].cause").value("MAVEN_TESTS"))
                .andExpect(jsonPath("$.data[0].count").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[1].cause").value("GITHUB_AUTH"))
                .andExpect(jsonPath("$.data[1].count").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void should_get_task_model_usage_summary() throws Exception {
        FixTaskVo successfulTask = createTask("delivery-model-usage-success");
        FixTaskVo failedTask = createTask("delivery-model-usage-failed");
        fixTaskModelCallService.recordModelCall(
                successfulTask.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Changed demo file",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T02:00:00Z"),
                Instant.parse("2026-06-20T02:00:04Z")
        );
        fixTaskModelCallService.recordModelCall(
                failedTask.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Model failed",
                50,
                10,
                false,
                "model failed",
                Instant.parse("2026-06-20T02:00:05Z"),
                Instant.parse("2026-06-20T02:00:09Z")
        );

        mockMvc.perform(get("/api/tasks/metrics/model-usage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPromptTokens").value(greaterThanOrEqualTo(170)))
                .andExpect(jsonPath("$.data.totalCompletionTokens").value(greaterThanOrEqualTo(90)))
                .andExpect(jsonPath("$.data.totalTokens").value(greaterThanOrEqualTo(260)))
                .andExpect(jsonPath("$.data.successfulCalls").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.failedCalls").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.estimatedCostUsd").value(greaterThanOrEqualTo(0.0)));
    }

    @Test
    void should_get_task_latency_summary() throws Exception {
        FixTaskVo completedTask = createTask("delivery-latency-completed");
        FixTaskVo failedTask = createTask("delivery-latency-failed");
        fixTaskService.markCompleted(completedTask.id(), "https://github.com/octocat/hello-world/pull/7");
        fixTaskService.markFailed(failedTask.id(), "maven failed");
        fixTaskModelCallService.recordModelCall(
                completedTask.id(),
                "openai",
                "gpt-5.5",
                "Fix issue",
                "Changed demo file",
                120,
                80,
                true,
                null,
                Instant.parse("2026-06-20T02:00:00Z"),
                Instant.parse("2026-06-20T02:00:04Z")
        );
        fixTaskToolCallService.recordToolCall(
                completedTask.id(),
                "ReadFileTool",
                "docs/demo.md",
                "read file",
                true,
                Instant.parse("2026-06-20T02:00:05Z"),
                Instant.parse("2026-06-20T02:00:06Z")
        );
        fixTaskTestRunService.recordTestRun(
                failedTask.id(),
                "./mvnw test",
                1,
                "tests failed",
                Instant.parse("2026-06-20T02:00:10Z"),
                Instant.parse("2026-06-20T02:00:14Z")
        );

        mockMvc.perform(get("/api/tasks/metrics/latency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.completedTaskCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.averageTaskDurationMs").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.maxTaskDurationMs").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.modelCallCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.averageModelCallDurationMs").value(greaterThanOrEqualTo(4000)))
                .andExpect(jsonPath("$.data.maxModelCallDurationMs").value(greaterThanOrEqualTo(4000)))
                .andExpect(jsonPath("$.data.toolCallCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.averageToolCallDurationMs").value(greaterThanOrEqualTo(1000)))
                .andExpect(jsonPath("$.data.maxToolCallDurationMs").value(greaterThanOrEqualTo(1000)))
                .andExpect(jsonPath("$.data.testRunCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.averageTestRunDurationMs").value(greaterThanOrEqualTo(4000)))
                .andExpect(jsonPath("$.data.maxTestRunDurationMs").value(greaterThanOrEqualTo(4000)));
    }

    private FixTaskVo createTask(String deliveryId) {
        return createTask(command("octocat", "hello-world", deliveryId));
    }

    private FixTaskVo createTask(CreateFixTaskCommand command) {
        return fixTaskService.createFixTask(command);
    }

    private CreateFixTaskCommand command(String owner, String repositoryName, String deliveryId) {
        return new CreateFixTaskCommand(
                owner,
                repositoryName,
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        );
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class QueueQueryTestConfiguration {

        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        FixTaskQueueQueryService recordingFixTaskQueueQueryService() {
            return new RecordingFixTaskQueueQueryService();
        }

        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        FixTaskDispatcher recordingFixTaskDispatcher() {
            return taskId -> {
            };
        }
    }

    private static final class RecordingFixTaskQueueQueryService implements FixTaskQueueQueryService {

        private List<FixTaskQueueItemVo> queueItems = List.of();

        @Override
        public java.util.List<FixTaskQueueItemVo> listItems(FixTaskQueueItemStatus status) {
            return queueItems;
        }

        @Override
        public java.util.List<FixTaskQueueItemVo> listByTaskId(String taskId) {
            return queueItems.stream()
                    .filter(queueItem -> queueItem.taskId().equals(taskId))
                    .toList();
        }

        @Override
        public io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo summary() {
            return io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo.empty();
        }

        @Override
        public Optional<FixTaskQueueItemVo> findByTaskId(String taskId) {
            return listByTaskId(taskId).stream().findFirst();
        }

        private void setQueueItems(List<FixTaskQueueItemVo> queueItems) {
            this.queueItems = queueItems;
        }
    }
}
