package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskFailureCauseSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskLatencySummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelUsageSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskMetricsService;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultFixTaskMetricsService implements FixTaskMetricsService {

    private static final List<String> FAILURE_CAUSE_ORDER = List.of(
            "MAVEN_TESTS",
            "GITHUB_AUTH",
            "MODEL_ERROR",
            "SANDBOX_REJECTION",
            "UNKNOWN"
    );

    private final FixTaskService fixTaskService;
    private final FixTaskModelCallService fixTaskModelCallService;
    private final FixTaskTestRunService fixTaskTestRunService;
    private final FixTaskToolCallService fixTaskToolCallService;
    private final AgentProperties agentProperties;

    @Override
    public FixTaskMetricsSummaryVo summary() {
        return summary(FixTaskListQuery.all());
    }

    @Override
    public FixTaskMetricsSummaryVo summary(FixTaskListQuery query) {
        List<FixTaskVo> tasks = fixTaskService.listTasks(query);
        long totalCount = tasks.size();
        if (totalCount == 0) {
            return FixTaskMetricsSummaryVo.empty();
        }

        long pendingCount = countByStatus(tasks, FixTaskStatus.PENDING);
        long runningCount = countByStatus(tasks, FixTaskStatus.RUNNING);
        long runningTestsCount = countByStatus(tasks, FixTaskStatus.RUNNING_TESTS);
        long pendingReviewCount = countByStatus(tasks, FixTaskStatus.PENDING_REVIEW);
        long completedCount = countByStatus(tasks, FixTaskStatus.COMPLETED);
        long failedCount = countByStatus(tasks, FixTaskStatus.FAILED);
        long cancelledCount = countByStatus(tasks, FixTaskStatus.CANCELLED);
        long averageCompletionDurationMs = averageCompletionDurationMs(tasks);
        long totalModelTokens = totalModelTokens(tasks);
        long averageModelTokensPerCompletedTask = completedCount == 0 ? 0
                : completedTaskModelTokens(tasks) / completedCount;
        TestRunMetrics testRunMetrics = testRunMetrics(tasks);

        return new FixTaskMetricsSummaryVo(
                totalCount,
                pendingCount,
                runningCount,
                runningTestsCount,
                pendingReviewCount,
                completedCount,
                failedCount,
                cancelledCount,
                (double) completedCount / totalCount,
                (double) failedCount / totalCount,
                averageCompletionDurationMs,
                totalModelTokens,
                averageModelTokensPerCompletedTask,
                testRunMetrics.testRunCount(),
                testRunMetrics.passedTestRunCount(),
                testRunMetrics.failedTestRunCount(),
                testRunMetrics.testPassRate()
        );
    }

    @Override
    public List<FixTaskFailureCauseSummaryVo> failureCauses() {
        return failureCauses(FixTaskListQuery.all());
    }

    @Override
    public List<FixTaskFailureCauseSummaryVo> failureCauses(FixTaskListQuery query) {
        Map<String, Long> counts = new LinkedHashMap<>();
        fixTaskService.listTasks(query).stream()
                .filter(task -> task.status() == FixTaskStatus.FAILED)
                .map(task -> classifyFailureCause(task.failureReason()))
                .forEach(cause -> counts.merge(cause, 1L, Long::sum));
        return FAILURE_CAUSE_ORDER.stream()
                .filter(cause -> counts.getOrDefault(cause, 0L) > 0)
                .map(cause -> new FixTaskFailureCauseSummaryVo(cause, counts.get(cause)))
                .toList();
    }

    @Override
    public FixTaskModelUsageSummaryVo modelUsage() {
        return modelUsage(FixTaskListQuery.all());
    }

    @Override
    public FixTaskModelUsageSummaryVo modelUsage(FixTaskListQuery query) {
        List<FixTaskModelCallVo> modelCalls = fixTaskService.listTasks(query).stream()
                .flatMap(task -> fixTaskModelCallService.listModelCalls(task.id()).stream())
                .toList();
        if (modelCalls.isEmpty()) {
            return FixTaskModelUsageSummaryVo.empty();
        }

        long totalPromptTokens = modelCalls.stream()
                .mapToLong(FixTaskModelCallVo::promptTokens)
                .sum();
        long totalCompletionTokens = modelCalls.stream()
                .mapToLong(FixTaskModelCallVo::completionTokens)
                .sum();
        long successfulCalls = modelCalls.stream()
                .filter(FixTaskModelCallVo::success)
                .count();
        long failedCalls = modelCalls.size() - successfulCalls;
        double estimatedCostUsd = totalPromptTokens * agentProperties.getCost().getPromptTokenUsd()
                + totalCompletionTokens * agentProperties.getCost().getCompletionTokenUsd();

        return new FixTaskModelUsageSummaryVo(
                totalPromptTokens,
                totalCompletionTokens,
                totalPromptTokens + totalCompletionTokens,
                successfulCalls,
                failedCalls,
                estimatedCostUsd
        );
    }

    @Override
    public FixTaskLatencySummaryVo latency() {
        return latency(FixTaskListQuery.all());
    }

    @Override
    public FixTaskLatencySummaryVo latency(FixTaskListQuery query) {
        List<FixTaskVo> tasks = fixTaskService.listTasks(query);
        if (tasks.isEmpty()) {
            return FixTaskLatencySummaryVo.empty();
        }

        DurationMetrics taskDurations = DurationMetrics.from(tasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED)
                .filter(task -> task.completedAt() != null)
                .mapToLong(task -> Duration.between(task.createdAt(), task.completedAt()).toMillis())
                .boxed()
                .toList());
        DurationMetrics modelDurations = DurationMetrics.from(tasks.stream()
                .flatMap(task -> fixTaskModelCallService.listModelCalls(task.id()).stream())
                .map(FixTaskModelCallVo::durationMs)
                .toList());
        DurationMetrics toolDurations = DurationMetrics.from(tasks.stream()
                .flatMap(task -> fixTaskToolCallService.listToolCalls(task.id()).stream())
                .map(FixTaskToolCallVo::durationMs)
                .toList());
        DurationMetrics testRunDurations = DurationMetrics.from(tasks.stream()
                .flatMap(task -> fixTaskTestRunService.listTestRuns(task.id()).stream())
                .map(FixTaskTestRunVo::durationMs)
                .toList());

        return new FixTaskLatencySummaryVo(
                taskDurations.count(),
                taskDurations.averageMs(),
                taskDurations.maxMs(),
                modelDurations.count(),
                modelDurations.averageMs(),
                modelDurations.maxMs(),
                toolDurations.count(),
                toolDurations.averageMs(),
                toolDurations.maxMs(),
                testRunDurations.count(),
                testRunDurations.averageMs(),
                testRunDurations.maxMs()
        );
    }

    private static long countByStatus(List<FixTaskVo> tasks, FixTaskStatus status) {
        return tasks.stream()
                .filter(task -> task.status() == status)
                .count();
    }

    private static String classifyFailureCause(String failureReason) {
        if (failureReason == null || failureReason.isBlank()) {
            return "UNKNOWN";
        }
        String normalizedReason = failureReason.toLowerCase(Locale.ROOT);
        if (normalizedReason.contains("maven") || normalizedReason.contains("test command") || normalizedReason.contains("tests failed")) {
            return "MAVEN_TESTS";
        }
        if (normalizedReason.contains("github") || normalizedReason.contains("token") || normalizedReason.contains("permission")
                || normalizedReason.contains("http 401") || normalizedReason.contains("http 403")
                || normalizedReason.contains("auth")) {
            return "GITHUB_AUTH";
        }
        if (normalizedReason.contains("model") || normalizedReason.contains("openai")) {
            return "MODEL_ERROR";
        }
        if (normalizedReason.contains("allowlist") || normalizedReason.contains("sandbox") || normalizedReason.contains("command rejected")) {
            return "SANDBOX_REJECTION";
        }
        return "UNKNOWN";
    }

    private static long averageCompletionDurationMs(List<FixTaskVo> tasks) {
        return (long) tasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED)
                .filter(task -> task.completedAt() != null)
                .mapToLong(task -> Duration.between(task.createdAt(), task.completedAt()).toMillis())
                .average()
                .orElse(0.0);
    }

    private long totalModelTokens(List<FixTaskVo> tasks) {
        return tasks.stream()
                .mapToLong(task -> modelTokens(task.id()))
                .sum();
    }

    private long completedTaskModelTokens(List<FixTaskVo> tasks) {
        return tasks.stream()
                .filter(task -> task.status() == FixTaskStatus.COMPLETED)
                .mapToLong(task -> modelTokens(task.id()))
                .sum();
    }

    private long modelTokens(String taskId) {
        return fixTaskModelCallService.listModelCalls(taskId).stream()
                .mapToLong(FixTaskModelCallVo::totalTokens)
                .sum();
    }

    private TestRunMetrics testRunMetrics(List<FixTaskVo> tasks) {
        List<FixTaskTestRunVo> testRuns = tasks.stream()
                .flatMap(task -> fixTaskTestRunService.listTestRuns(task.id()).stream())
                .toList();
        return TestRunMetrics.from(testRuns);
    }

    private record TestRunMetrics(
            long testRunCount,
            long passedTestRunCount,
            long failedTestRunCount,
            double testPassRate
    ) {

        private static TestRunMetrics from(List<FixTaskTestRunVo> testRuns) {
            long testRunCount = testRuns.size();
            if (testRunCount == 0) {
                return new TestRunMetrics(0, 0, 0, 0.0);
            }

            long passedTestRunCount = testRuns.stream()
                    .filter(testRun -> testRun.exitCode() == 0)
                    .count();
            long failedTestRunCount = testRunCount - passedTestRunCount;
            return new TestRunMetrics(
                    testRunCount,
                    passedTestRunCount,
                    failedTestRunCount,
                    (double) passedTestRunCount / testRunCount
            );
        }
    }

    private record DurationMetrics(
            long count,
            long averageMs,
            long maxMs
    ) {

        private static DurationMetrics from(List<Long> durations) {
            if (durations.isEmpty()) {
                return new DurationMetrics(0, 0, 0);
            }
            long count = durations.size();
            long averageMs = (long) durations.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            long maxMs = durations.stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0);
            return new DurationMetrics(count, averageMs, maxMs);
        }
    }
}
