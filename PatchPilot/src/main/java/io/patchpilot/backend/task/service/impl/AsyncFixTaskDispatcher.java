package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.FixTaskExecutor;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFixTaskDispatcher implements FixTaskDispatcher {

    private final FixTaskService fixTaskService;
    private final FixTaskExecutor fixTaskExecutor;

    public AsyncFixTaskDispatcher(FixTaskService fixTaskService, FixTaskExecutor fixTaskExecutor) {
        this.fixTaskService = fixTaskService;
        this.fixTaskExecutor = fixTaskExecutor;
    }

    @Override
    public void dispatch(String taskId) {
        CompletableFuture.runAsync(() -> executeTask(taskId));
    }

    private void executeTask(String taskId) {
        fixTaskService.markRunning(taskId);
        try {
            FixTaskVo runningTestsTask = fixTaskService.markRunningTests(taskId);
            fixTaskExecutor.execute(runningTestsTask);
            fixTaskService.markCompleted(taskId);
        } catch (RuntimeException exception) {
            fixTaskService.markFailed(taskId, failureReason(exception));
        }
    }

    private static String failureReason(RuntimeException exception) {
        if (exception.getMessage() == null || exception.getMessage().isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return exception.getMessage();
    }
}
