package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile({"local", "docker"})
public class FixTaskQueuePoller {

    private final MyBatisFixTaskQueue fixTaskQueue;
    private final FixTaskWorker fixTaskWorker;

    public FixTaskQueuePoller(MyBatisFixTaskQueue fixTaskQueue, FixTaskWorker fixTaskWorker) {
        this.fixTaskQueue = fixTaskQueue;
        this.fixTaskWorker = fixTaskWorker;
    }

    @Scheduled(fixedDelayString = "${patchpilot.task.queue.poll-delay-ms:1000}")
    public void pollOnce() {
        fixTaskQueue.claimNext().ifPresent(this::execute);
    }

    private void execute(FixTaskQueueItemVo queueItem) {
        try {
            fixTaskWorker.execute(queueItem.taskId());
            fixTaskQueue.markCompleted(queueItem.id());
        } catch (RuntimeException exception) {
            fixTaskQueue.markFailed(queueItem.id(), failureReason(exception));
        }
    }

    private static String failureReason(RuntimeException exception) {
        if (exception.getMessage() == null || exception.getMessage().isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return exception.getMessage();
    }
}
