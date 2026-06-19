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
        fixTaskService.markRunning(taskId);
        FixTaskExecutionResult executionResult;
        try {
            FixTaskVo runningTestsTask = fixTaskService.markRunningTests(taskId);
            executionResult = fixTaskExecutor.execute(runningTestsTask);
        } catch (RuntimeException exception) {
            String failureReason = failureReason(exception);
            FixTaskVo failedTask = fixTaskService.markFailed(taskId, failureReason);
            issueCommentTool.commentFailed(failedTask, failureReason);
            return;
        }
        FixTaskVo completedTask = fixTaskService.markCompleted(taskId);
        issueCommentTool.commentCompleted(completedTask, executionResult.pullRequestUrl());
    }

    private static String failureReason(RuntimeException exception) {
        if (exception.getMessage() == null || exception.getMessage().isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return exception.getMessage();
    }
}
