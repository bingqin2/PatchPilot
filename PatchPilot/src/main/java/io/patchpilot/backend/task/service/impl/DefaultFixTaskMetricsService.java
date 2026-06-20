package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskMetricsService;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultFixTaskMetricsService implements FixTaskMetricsService {

    private final FixTaskService fixTaskService;
    private final FixTaskModelCallService fixTaskModelCallService;
    private final FixTaskTestRunService fixTaskTestRunService;

    @Override
    public FixTaskMetricsSummaryVo summary() {
        List<FixTaskVo> tasks = fixTaskService.listTasks();
        long totalCount = tasks.size();
        if (totalCount == 0) {
            return FixTaskMetricsSummaryVo.empty();
        }

        long pendingCount = countByStatus(tasks, FixTaskStatus.PENDING);
        long runningCount = countByStatus(tasks, FixTaskStatus.RUNNING);
        long runningTestsCount = countByStatus(tasks, FixTaskStatus.RUNNING_TESTS);
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

    private static long countByStatus(List<FixTaskVo> tasks, FixTaskStatus status) {
        return tasks.stream()
                .filter(task -> task.status() == status)
                .count();
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
}
