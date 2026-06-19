package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.stereotype.Service;

@Service
public class DefaultTaskCancellationChecker implements TaskCancellationChecker {

    private final FixTaskService fixTaskService;

    public DefaultTaskCancellationChecker(FixTaskService fixTaskService) {
        this.fixTaskService = fixTaskService;
    }

    @Override
    public void throwIfCancelled(String taskId) {
        fixTaskService.findTask(taskId)
                .filter(task -> task.status() == FixTaskStatus.CANCELLED)
                .ifPresent(task -> {
                    throw new TaskCancellationException(taskId);
                });
    }
}
