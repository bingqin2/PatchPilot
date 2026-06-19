package io.patchpilot.backend.github.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubWebhookServiceTests {

    @Test
    void should_return_duplicate_without_dispatch_when_delivery_already_exists_in_persistence() {
        ExistingDeliveryFixTaskService fixTaskService = new ExistingDeliveryFixTaskService();
        RecordingFixTaskDispatcher fixTaskDispatcher = new RecordingFixTaskDispatcher();
        GitHubWebhookService webhookService = new GitHubWebhookService(
                new ObjectMapper(),
                fixTaskService,
                fixTaskDispatcher
        );

        WebhookHandleResult result = webhookService.handle(
                "issue_comment",
                "delivery-existing",
                issueCommentPayload("/agent fix")
        );

        assertThat(result.status()).isEqualTo(WebhookHandleStatus.DUPLICATE_DELIVERY);
        assertThat(result.taskId()).isEqualTo("task-existing");
        assertThat(fixTaskDispatcher.dispatchCount()).isZero();
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
        public FixTaskVo markFailed(String id, String failureReason) {
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

        @Override
        public void dispatch(String taskId) {
            dispatchCount.incrementAndGet();
        }

        private int dispatchCount() {
            return dispatchCount.get();
        }
    }
}
