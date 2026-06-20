package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.DefaultFixTaskMetricsService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskModelCallService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFixTaskMetricsServiceTests {

    private final FixTaskService fixTaskService = new InMemoryFixTaskService();
    private final FixTaskModelCallService fixTaskModelCallService = new InMemoryFixTaskModelCallService();
    private final FixTaskMetricsService fixTaskMetricsService = new DefaultFixTaskMetricsService(
            fixTaskService,
            fixTaskModelCallService
    );

    @Test
    void should_return_zero_summary_when_no_tasks_exist() {
        FixTaskMetricsSummaryVo summary = fixTaskMetricsService.summary();

        assertThat(summary.totalCount()).isZero();
        assertThat(summary.pendingCount()).isZero();
        assertThat(summary.runningCount()).isZero();
        assertThat(summary.runningTestsCount()).isZero();
        assertThat(summary.completedCount()).isZero();
        assertThat(summary.failedCount()).isZero();
        assertThat(summary.cancelledCount()).isZero();
        assertThat(summary.completionRate()).isZero();
        assertThat(summary.failureRate()).isZero();
        assertThat(summary.averageCompletionDurationMs()).isZero();
        assertThat(summary.totalModelTokens()).isZero();
        assertThat(summary.averageModelTokensPerCompletedTask()).isZero();
    }

    @Test
    void should_summarize_task_statuses_duration_and_model_tokens() {
        FixTaskMetricsService metricsService = new DefaultFixTaskMetricsService(
                new StaticFixTaskService(List.of(
                        task("completed", FixTaskStatus.COMPLETED,
                                Instant.parse("2026-06-20T01:00:00Z"),
                                Instant.parse("2026-06-20T01:00:10Z")),
                        task("failed", FixTaskStatus.FAILED,
                                Instant.parse("2026-06-20T01:01:00Z"),
                                null),
                        task("cancelled", FixTaskStatus.CANCELLED,
                                Instant.parse("2026-06-20T01:02:00Z"),
                                null),
                        task("running", FixTaskStatus.RUNNING,
                                Instant.parse("2026-06-20T01:03:00Z"),
                                null),
                        task("running-tests", FixTaskStatus.RUNNING_TESTS,
                                Instant.parse("2026-06-20T01:04:00Z"),
                                null),
                        task("pending", FixTaskStatus.PENDING,
                                Instant.parse("2026-06-20T01:05:00Z"),
                                null)
                )),
                new StaticFixTaskModelCallService(Map.of(
                        "completed", List.of(
                                modelCall("completed", 100, 50),
                                modelCall("completed", 40, 10)
                        ),
                        "failed", List.of(modelCall("failed", 20, 5))
                ))
        );

        FixTaskMetricsSummaryVo summary = metricsService.summary();

        assertThat(summary.totalCount()).isEqualTo(6);
        assertThat(summary.pendingCount()).isEqualTo(1);
        assertThat(summary.runningCount()).isEqualTo(1);
        assertThat(summary.runningTestsCount()).isEqualTo(1);
        assertThat(summary.completedCount()).isEqualTo(1);
        assertThat(summary.failedCount()).isEqualTo(1);
        assertThat(summary.cancelledCount()).isEqualTo(1);
        assertThat(summary.completionRate()).isEqualTo(1.0 / 6.0);
        assertThat(summary.failureRate()).isEqualTo(1.0 / 6.0);
        assertThat(summary.averageCompletionDurationMs()).isEqualTo(10000);
        assertThat(summary.totalModelTokens()).isEqualTo(225);
        assertThat(summary.averageModelTokensPerCompletedTask()).isEqualTo(200);
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

    private void recordModelCall(String taskId, int promptTokens, int completionTokens) {
        fixTaskModelCallService.recordModelCall(
                taskId,
                "openai",
                "gpt-5.5",
                "prompt",
                "response",
                promptTokens,
                completionTokens,
                true,
                null,
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:02Z")
        );
    }

    private static FixTaskVo task(String id, FixTaskStatus status, Instant createdAt, Instant completedAt) {
        return new FixTaskVo(
                id,
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                "delivery-" + id,
                98765,
                status,
                null,
                createdAt,
                completedAt == null ? null : "https://github.com/octocat/hello-world/pull/7",
                completedAt,
                completedAt == null ? createdAt : completedAt,
                null,
                null
        );
    }

    private static FixTaskModelCallVo modelCall(String taskId, int promptTokens, int completionTokens) {
        return new FixTaskModelCallVo(
                "model-call-" + taskId + "-" + promptTokens,
                taskId,
                "openai",
                "gpt-5.5",
                "prompt",
                "response",
                promptTokens,
                completionTokens,
                promptTokens + completionTokens,
                true,
                null,
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:02Z"),
                2000
        );
    }

    private record StaticFixTaskService(List<FixTaskVo> tasks) implements FixTaskService {

        @Override
        public FixTaskVo createFixTask(CreateFixTaskCommand command) {
            throw new UnsupportedOperationException();
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
            return tasks;
        }

        @Override
        public Optional<FixTaskVo> findTask(String id) {
            return tasks.stream()
                    .filter(task -> task.id().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<FixTaskVo> findTaskByDeliveryId(String deliveryId) {
            return Optional.empty();
        }

        @Override
        public Optional<FixTaskVo> findActiveTaskForIssue(String repositoryOwner, String repositoryName, long issueNumber) {
            return Optional.empty();
        }
    }

    private record StaticFixTaskModelCallService(
            Map<String, List<FixTaskModelCallVo>> modelCalls
    ) implements FixTaskModelCallService {

        @Override
        public FixTaskModelCallVo recordModelCall(
                String taskId,
                String provider,
                String model,
                String promptSummary,
                String responseSummary,
                int promptTokens,
                int completionTokens,
                boolean success,
                String errorMessage,
                Instant startedAt,
                Instant finishedAt
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<FixTaskModelCallVo> listModelCalls(String taskId) {
            return modelCalls.getOrDefault(taskId, List.of());
        }
    }
}
