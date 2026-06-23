package io.patchpilot.backend.task.service;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelUsageSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.DefaultFixTaskMetricsService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskModelCallService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskTestRunService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFixTaskMetricsServiceTests {

    private final FixTaskService fixTaskService = new InMemoryFixTaskService();
    private final FixTaskModelCallService fixTaskModelCallService = new InMemoryFixTaskModelCallService();
    private final FixTaskTestRunService fixTaskTestRunService = new InMemoryFixTaskTestRunService();
    private final FixTaskMetricsService fixTaskMetricsService = new DefaultFixTaskMetricsService(
            fixTaskService,
            fixTaskModelCallService,
            fixTaskTestRunService,
            new StaticFixTaskToolCallService(Map.of()),
            new AgentProperties()
    );

    @Test
    void should_return_zero_summary_when_no_tasks_exist() {
        FixTaskMetricsSummaryVo summary = fixTaskMetricsService.summary();

        assertThat(summary.totalCount()).isZero();
        assertThat(summary.pendingCount()).isZero();
        assertThat(summary.runningCount()).isZero();
        assertThat(summary.runningTestsCount()).isZero();
        assertThat(summary.pendingReviewCount()).isZero();
        assertThat(summary.completedCount()).isZero();
        assertThat(summary.failedCount()).isZero();
        assertThat(summary.cancelledCount()).isZero();
        assertThat(summary.completionRate()).isZero();
        assertThat(summary.failureRate()).isZero();
        assertThat(summary.averageCompletionDurationMs()).isZero();
        assertThat(summary.totalModelTokens()).isZero();
        assertThat(summary.averageModelTokensPerCompletedTask()).isZero();
        assertThat(summary.testRunCount()).isZero();
        assertThat(summary.passedTestRunCount()).isZero();
        assertThat(summary.failedTestRunCount()).isZero();
        assertThat(summary.testPassRate()).isZero();
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
                        task("pending-review", FixTaskStatus.PENDING_REVIEW,
                                Instant.parse("2026-06-20T01:04:30Z"),
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
                )),
                new StaticFixTaskTestRunService(Map.of(
                        "completed", List.of(testRun("completed", 0)),
                        "failed", List.of(testRun("failed", 1), testRun("failed", 0))
                )),
                new StaticFixTaskToolCallService(Map.of()),
                new AgentProperties()
        );

        FixTaskMetricsSummaryVo summary = metricsService.summary();

        assertThat(summary.totalCount()).isEqualTo(7);
        assertThat(summary.pendingCount()).isEqualTo(1);
        assertThat(summary.runningCount()).isEqualTo(1);
        assertThat(summary.runningTestsCount()).isEqualTo(1);
        assertThat(summary.pendingReviewCount()).isEqualTo(1);
        assertThat(summary.completedCount()).isEqualTo(1);
        assertThat(summary.failedCount()).isEqualTo(1);
        assertThat(summary.cancelledCount()).isEqualTo(1);
        assertThat(summary.completionRate()).isEqualTo(1.0 / 7.0);
        assertThat(summary.failureRate()).isEqualTo(1.0 / 7.0);
        assertThat(summary.averageCompletionDurationMs()).isEqualTo(10000);
        assertThat(summary.totalModelTokens()).isEqualTo(225);
        assertThat(summary.averageModelTokensPerCompletedTask()).isEqualTo(200);
        assertThat(summary.testRunCount()).isEqualTo(3);
        assertThat(summary.passedTestRunCount()).isEqualTo(2);
        assertThat(summary.failedTestRunCount()).isEqualTo(1);
        assertThat(summary.testPassRate()).isEqualTo(2.0 / 3.0);
    }

    @Test
    void should_summarize_only_tasks_matching_query_scope() {
        FixTaskMetricsService metricsService = new DefaultFixTaskMetricsService(
                new StaticFixTaskService(List.of(
                        task("maven", FixTaskStatus.COMPLETED,
                                Instant.parse("2026-06-20T01:00:00Z"),
                                Instant.parse("2026-06-20T01:00:10Z"))
                                .withAdapterMetadata("java", "maven", "./mvnw test"),
                        task("npm", FixTaskStatus.FAILED,
                                Instant.parse("2026-06-20T01:01:00Z"),
                                null)
                                .withAdapterMetadata("node", "npm", "npm test")
                )),
                new StaticFixTaskModelCallService(Map.of(
                        "maven", List.of(modelCall("maven", 100, 50)),
                        "npm", List.of(modelCall("npm", 20, 5))
                )),
                new StaticFixTaskTestRunService(Map.of(
                        "maven", List.of(testRun("maven", 0)),
                        "npm", List.of(testRun("npm", 1))
                )),
                new StaticFixTaskToolCallService(Map.of()),
                new AgentProperties()
        );

        FixTaskMetricsSummaryVo summary = metricsService.summary(new FixTaskListQuery(
                null,
                null,
                "octocat",
                "hello-world",
                "node",
                "npm",
                100,
                0
        ));

        assertThat(summary.totalCount()).isEqualTo(1);
        assertThat(summary.completedCount()).isZero();
        assertThat(summary.failedCount()).isEqualTo(1);
        assertThat(summary.totalModelTokens()).isEqualTo(25);
        assertThat(summary.testRunCount()).isEqualTo(1);
        assertThat(summary.failedTestRunCount()).isEqualTo(1);
    }

    @Test
    void should_summarize_failed_tasks_by_failure_cause() {
        FixTaskMetricsService metricsService = new DefaultFixTaskMetricsService(
                new StaticFixTaskService(List.of(
                        failedTask("maven-tests", "maven tests failed: compilation error"),
                        failedTask("github-auth", "GitHub issue comment creation failed: HTTP 403"),
                        failedTask("model-error", "OpenAI-compatible model call failed: invalid API key"),
                        failedTask("patch-review", "Model patch review rejected generated edits: unrelated authentication change"),
                        failedTask("sandbox", "Command rejected by allowlist: rm -rf /tmp/repo"),
                        failedTask("unknown", "unexpected executor failure"),
                        task("completed", FixTaskStatus.COMPLETED,
                                Instant.parse("2026-06-20T01:06:00Z"),
                                Instant.parse("2026-06-20T01:06:10Z"))
                )),
                new StaticFixTaskModelCallService(Map.of()),
                new StaticFixTaskTestRunService(Map.of()),
                new StaticFixTaskToolCallService(Map.of()),
                new AgentProperties()
        );

        assertThat(metricsService.failureCauses())
                .extracting("cause", "count")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("MAVEN_TESTS", 1L),
                        org.assertj.core.groups.Tuple.tuple("GITHUB_AUTH", 1L),
                        org.assertj.core.groups.Tuple.tuple("MODEL_ERROR", 1L),
                        org.assertj.core.groups.Tuple.tuple("PATCH_REVIEW_REJECTION", 1L),
                        org.assertj.core.groups.Tuple.tuple("SANDBOX_REJECTION", 1L),
                        org.assertj.core.groups.Tuple.tuple("UNKNOWN", 1L)
                );
    }

    @Test
    void should_summarize_model_usage_and_estimated_cost() {
        AgentProperties properties = new AgentProperties();
        properties.getCost().setPromptTokenUsd(0.000001);
        properties.getCost().setCompletionTokenUsd(0.000002);
        FixTaskMetricsService metricsService = new DefaultFixTaskMetricsService(
                new StaticFixTaskService(List.of(
                        task("completed", FixTaskStatus.COMPLETED,
                                Instant.parse("2026-06-20T01:00:00Z"),
                                Instant.parse("2026-06-20T01:00:10Z")),
                        task("failed", FixTaskStatus.FAILED,
                                Instant.parse("2026-06-20T01:01:00Z"),
                                null)
                )),
                new StaticFixTaskModelCallService(Map.of(
                        "completed", List.of(
                                modelCall("completed", 1000, 500),
                                modelCall("completed", 200, 100, false)
                        ),
                        "failed", List.of(modelCall("failed", 300, 50))
                )),
                new StaticFixTaskTestRunService(Map.of()),
                new StaticFixTaskToolCallService(Map.of()),
                properties
        );

        FixTaskModelUsageSummaryVo usage = metricsService.modelUsage();

        assertThat(usage.totalPromptTokens()).isEqualTo(1500);
        assertThat(usage.totalCompletionTokens()).isEqualTo(650);
        assertThat(usage.totalTokens()).isEqualTo(2150);
        assertThat(usage.successfulCalls()).isEqualTo(2);
        assertThat(usage.failedCalls()).isEqualTo(1);
        assertThat(usage.estimatedCostUsd()).isEqualTo(0.0028);
    }

    @Test
    void should_summarize_latency_across_tasks_model_calls_tool_calls_and_test_runs() {
        FixTaskMetricsService metricsService = new DefaultFixTaskMetricsService(
                new StaticFixTaskService(List.of(
                        task("completed-fast", FixTaskStatus.COMPLETED,
                                Instant.parse("2026-06-20T01:00:00Z"),
                                Instant.parse("2026-06-20T01:00:10Z")),
                        task("completed-slow", FixTaskStatus.COMPLETED,
                                Instant.parse("2026-06-20T01:01:00Z"),
                                Instant.parse("2026-06-20T01:01:30Z")),
                        task("failed", FixTaskStatus.FAILED,
                                Instant.parse("2026-06-20T01:02:00Z"),
                                null)
                )),
                new StaticFixTaskModelCallService(Map.of(
                        "completed-fast", List.of(modelCall("completed-fast", 100, 50, true, 2000)),
                        "completed-slow", List.of(modelCall("completed-slow", 200, 100, true, 6000))
                )),
                new StaticFixTaskTestRunService(Map.of(
                        "completed-fast", List.of(testRun("completed-fast", 0, 4000)),
                        "failed", List.of(testRun("failed", 1, 10000))
                )),
                new StaticFixTaskToolCallService(Map.of(
                        "completed-fast", List.of(toolCall("completed-fast", "ReadFileTool", 1000)),
                        "completed-slow", List.of(toolCall("completed-slow", "WriteFileTool", 3000))
                )),
                new AgentProperties()
        );

        var latency = metricsService.latency();

        assertThat(latency.completedTaskCount()).isEqualTo(2);
        assertThat(latency.averageTaskDurationMs()).isEqualTo(20000);
        assertThat(latency.maxTaskDurationMs()).isEqualTo(30000);
        assertThat(latency.modelCallCount()).isEqualTo(2);
        assertThat(latency.averageModelCallDurationMs()).isEqualTo(4000);
        assertThat(latency.maxModelCallDurationMs()).isEqualTo(6000);
        assertThat(latency.toolCallCount()).isEqualTo(2);
        assertThat(latency.averageToolCallDurationMs()).isEqualTo(2000);
        assertThat(latency.maxToolCallDurationMs()).isEqualTo(3000);
        assertThat(latency.testRunCount()).isEqualTo(2);
        assertThat(latency.averageTestRunDurationMs()).isEqualTo(7000);
        assertThat(latency.maxTestRunDurationMs()).isEqualTo(10000);
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

    private static FixTaskVo failedTask(String id, String failureReason) {
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
                FixTaskStatus.FAILED,
                failureReason,
                Instant.parse("2026-06-20T01:00:00Z"),
                null,
                null,
                Instant.parse("2026-06-20T01:00:10Z"),
                null,
                null
        );
    }

    private static FixTaskModelCallVo modelCall(String taskId, int promptTokens, int completionTokens) {
        return modelCall(taskId, promptTokens, completionTokens, true, 2000);
    }

    private static FixTaskModelCallVo modelCall(String taskId, int promptTokens, int completionTokens, boolean success) {
        return modelCall(taskId, promptTokens, completionTokens, success, 2000);
    }

    private static FixTaskModelCallVo modelCall(String taskId, int promptTokens, int completionTokens, boolean success, long durationMs) {
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
                success,
                success ? null : "model failed",
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:00Z").plusMillis(durationMs),
                durationMs
        );
    }

    private static FixTaskTestRunVo testRun(String taskId, int exitCode) {
        return testRun(taskId, exitCode, 2000);
    }

    private static FixTaskTestRunVo testRun(String taskId, int exitCode, long durationMs) {
        return new FixTaskTestRunVo(
                "test-run-" + taskId + "-" + exitCode,
                taskId,
                "./mvnw test",
                exitCode,
                exitCode == 0 ? "tests passed" : "tests failed",
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:00Z").plusMillis(durationMs),
                durationMs
        );
    }

    private static FixTaskToolCallVo toolCall(String taskId, String toolName, long durationMs) {
        return new FixTaskToolCallVo(
                "tool-call-" + taskId + "-" + toolName,
                taskId,
                toolName,
                "input",
                "output",
                true,
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:00Z").plusMillis(durationMs),
                durationMs
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
        public List<FixTaskVo> listTasks(FixTaskListQuery query) {
            return tasks.stream()
                    .filter(task -> query.status() == null || task.status() == query.status())
                    .filter(task -> query.repositoryOwner() == null || task.repositoryOwner().equals(query.repositoryOwner()))
                    .filter(task -> query.repositoryName() == null || task.repositoryName().equals(query.repositoryName()))
                    .filter(task -> query.language() == null || query.language().equals(task.language()))
                    .filter(task -> query.buildSystem() == null || query.buildSystem().equals(task.buildSystem()))
                    .toList();
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

    private record StaticFixTaskTestRunService(
            Map<String, List<FixTaskTestRunVo>> testRuns
    ) implements FixTaskTestRunService {

        @Override
        public FixTaskTestRunVo recordTestRun(
                String taskId,
                String command,
                int exitCode,
                String output,
                Instant startedAt,
                Instant finishedAt
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<FixTaskTestRunVo> listTestRuns(String taskId) {
            return testRuns.getOrDefault(taskId, List.of());
        }
    }

    private record StaticFixTaskToolCallService(
            Map<String, List<FixTaskToolCallVo>> toolCalls
    ) implements FixTaskToolCallService {

        @Override
        public FixTaskToolCallVo recordToolCall(
                String taskId,
                String toolName,
                String inputSummary,
                String outputSummary,
                boolean success,
                Instant startedAt,
                Instant finishedAt
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<FixTaskToolCallVo> listToolCalls(String taskId) {
            return toolCalls.getOrDefault(taskId, List.of());
        }
    }
}
