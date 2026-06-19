package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.agent.tool.IssueCommentTool;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.FixTaskExecutor;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFixTaskDispatcher implements FixTaskDispatcher {

    private final FixTaskService fixTaskService;
    private final FixTaskExecutor fixTaskExecutor;
    private final IssueCommentTool issueCommentTool;

    public AsyncFixTaskDispatcher(
            FixTaskService fixTaskService,
            FixTaskExecutor fixTaskExecutor,
            IssueCommentTool issueCommentTool
    ) {
        this.fixTaskService = fixTaskService;
        this.fixTaskExecutor = fixTaskExecutor;
        this.issueCommentTool = issueCommentTool;
    }

    @Override
    public void dispatch(String taskId) {
        CompletableFuture.runAsync(() -> executeTask(taskId));
    }

    private void executeTask(String taskId) {
        FixTaskVo runningTask = fixTaskService.markRunning(taskId);
        updateStatusComment(() -> issueCommentTool.updateRunning(runningTask));
        FixTaskExecutionResult executionResult;
        try {
            FixTaskVo runningTestsTask = fixTaskService.markRunningTests(taskId);
            updateStatusComment(() -> issueCommentTool.updateRunningTests(runningTestsTask));
            executionResult = fixTaskExecutor.execute(runningTestsTask);
        } catch (RuntimeException exception) {
            String failureReason = failureReason(exception);
            FixTaskVo failedTask = fixTaskService.markFailed(taskId, failureReason);
            updateStatusComment(() -> issueCommentTool.updateFailed(failedTask));
            return;
        }
        FixTaskVo completedTask = fixTaskService.markCompleted(taskId, executionResult.pullRequestUrl());
        updateStatusComment(() -> issueCommentTool.updateCompleted(completedTask));
    }

    private static void updateStatusComment(Runnable update) {
        try {
            update.run();
        } catch (RuntimeException exception) {
            // GitHub comment feedback must not change durable task status.
        }
    }

    private static String failureReason(RuntimeException exception) {
        if (exception.getMessage() == null || exception.getMessage().isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return exception.getMessage();
    }
}
