package io.patchpilot.backend.github.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.tool.IssueCommentTool;
import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueCommentException;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubWebhookServiceTests {

    @Test
    void should_return_duplicate_without_dispatch_when_delivery_already_exists_in_persistence() {
        ExistingDeliveryFixTaskService fixTaskService = new ExistingDeliveryFixTaskService();
        RecordingFixTaskDispatcher fixTaskDispatcher = new RecordingFixTaskDispatcher();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        GitHubWebhookService webhookService = new GitHubWebhookService(
                new ObjectMapper(),
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool
        );

        WebhookHandleResult result = webhookService.handle(
                "issue_comment",
                "delivery-existing",
                issueCommentPayload("/agent fix")
        );

        assertThat(result.status()).isEqualTo(WebhookHandleStatus.DUPLICATE_DELIVERY);
        assertThat(result.taskId()).isEqualTo("task-existing");
        assertThat(fixTaskDispatcher.dispatchCount()).isZero();
        assertThat(issueCommentTool.acceptedCount()).isZero();
    }

    @Test
    void should_create_status_comment_attach_it_and_dispatch_created_task() {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingFixTaskDispatcher fixTaskDispatcher = new RecordingFixTaskDispatcher();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        GitHubWebhookService webhookService = new GitHubWebhookService(
                new ObjectMapper(),
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool
        );

        WebhookHandleResult result = webhookService.handle(
                "issue_comment",
                "delivery-created-status-comment",
                issueCommentPayload("/agent fix")
        );

        FixTaskVo task = fixTaskService.findTask(result.taskId()).orElseThrow();
        assertThat(result.status()).isEqualTo(WebhookHandleStatus.TASK_CREATED);
        assertThat(issueCommentTool.acceptedTaskId()).isEqualTo(task.id());
        assertThat(task.statusCommentId()).isEqualTo(123L);
        assertThat(task.statusCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(fixTaskDispatcher.dispatchedTaskId()).isEqualTo(task.id());
    }

    @Test
    void should_dispatch_created_task_when_status_comment_creation_fails() {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingFixTaskDispatcher fixTaskDispatcher = new RecordingFixTaskDispatcher();
        FailingAcceptedIssueCommentTool issueCommentTool = new FailingAcceptedIssueCommentTool();
        GitHubWebhookService webhookService = new GitHubWebhookService(
                new ObjectMapper(),
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool
        );

        WebhookHandleResult result = webhookService.handle(
                "issue_comment",
                "delivery-comment-create-fails",
                issueCommentPayload("/agent fix")
        );

        FixTaskVo task = fixTaskService.findTask(result.taskId()).orElseThrow();
        assertThat(result.status()).isEqualTo(WebhookHandleStatus.TASK_CREATED);
        assertThat(issueCommentTool.acceptedCount()).isEqualTo(1);
        assertThat(task.statusCommentId()).isNull();
        assertThat(fixTaskDispatcher.dispatchedTaskId()).isEqualTo(task.id());
    }

    private static String issueCommentPayload(String commentBody) {
        return """
                {
                  "action": "created",
                  "installation": {
                    "id": 12345
                  },
                  "repository": {
                    "name": "hello-world",
                    "owner": {
                      "login": "octocat"
                    }
                  },
                  "issue": {
                    "number": 42
                  },
                  "comment": {
                    "id": 98765,
                    "body": "%s",
                    "user": {
                      "login": "alice"
                    }
                  }
                }
                """.formatted(commentBody);
    }

    private static final class ExistingDeliveryFixTaskService implements FixTaskService {

        @Override
        public FixTaskVo createFixTask(CreateFixTaskCommand command) {
            return existingTask(command.deliveryId());
        }

        @Override
        public FixTaskCreationResult createFixTaskIfAbsent(CreateFixTaskCommand command) {
            return new FixTaskCreationResult(existingTask(command.deliveryId()), false);
        }

        @Override
        public FixTaskVo markRunning(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FixTaskVo markRunningTests(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FixTaskVo markCompleted(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FixTaskVo markCompleted(String id, String pullRequestUrl) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FixTaskVo markFailed(String id, String failureReason) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FixTaskVo attachStatusComment(String id, long statusCommentId, String statusCommentUrl) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<FixTaskVo> listTasks() {
            return List.of();
        }

        @Override
        public Optional<FixTaskVo> findTask(String id) {
            return Optional.empty();
        }

        private FixTaskVo existingTask(String deliveryId) {
            return new FixTaskVo(
                    "task-existing",
                    "octocat",
                    "hello-world",
                    42,
                    12345,
                    "alice",
                    "/agent fix",
                    deliveryId,
                    98765,
                    FixTaskStatus.COMPLETED,
                    null,
                    Instant.parse("2026-06-19T01:02:03Z")
            );
        }
    }

    private static final class RecordingFixTaskDispatcher implements FixTaskDispatcher {

        private final AtomicInteger dispatchCount = new AtomicInteger();
        private final AtomicReference<String> dispatchedTaskId = new AtomicReference<>();

        @Override
        public void dispatch(String taskId) {
            dispatchCount.incrementAndGet();
            dispatchedTaskId.set(taskId);
        }

        private int dispatchCount() {
            return dispatchCount.get();
        }

        private String dispatchedTaskId() {
            return dispatchedTaskId.get();
        }
    }

    private static class RecordingIssueCommentTool extends IssueCommentTool {

        private final AtomicInteger acceptedCount = new AtomicInteger();
        private final AtomicReference<String> acceptedTaskId = new AtomicReference<>();

        private RecordingIssueCommentTool() {
            super(new GitHubIssueCommentClient(new GitHubProperties()) {
                @Override
                public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
                    return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
                }
            });
        }

        @Override
        public IssueCommentResult commentAccepted(FixTaskVo task) {
            acceptedCount.incrementAndGet();
            acceptedTaskId.set(task.id());
            return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        int acceptedCount() {
            return acceptedCount.get();
        }

        String acceptedTaskId() {
            return acceptedTaskId.get();
        }
    }

    private static final class FailingAcceptedIssueCommentTool extends RecordingIssueCommentTool {

        @Override
        public IssueCommentResult commentAccepted(FixTaskVo task) {
            super.commentAccepted(task);
            throw new GitHubIssueCommentException("comment failed");
        }
    }
}
