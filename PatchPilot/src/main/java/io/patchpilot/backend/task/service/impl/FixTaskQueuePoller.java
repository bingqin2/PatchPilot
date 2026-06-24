package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.service.FixTaskWorkerHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class FixTaskQueuePoller {

    private final MyBatisFixTaskQueue fixTaskQueue;
    private final FixTaskWorker fixTaskWorker;
    private final FixTaskWorkerHealthService workerHealthService;

    @Scheduled(fixedDelayString = "${patchpilot.task.queue.poll-delay-ms:1000}")
    public void pollOnce() {
        workerHealthService.recordPollStarted();
        try {
            fixTaskQueue.recoverTimedOutRunningItems();
            fixTaskQueue.claimNext().ifPresentOrElse(this::execute, workerHealthService::recordIdlePoll);
        } catch (RuntimeException exception) {
            workerHealthService.recordPollingFailed(failureReason(exception));
            throw exception;
        }
    }

    private void execute(FixTaskQueueItemVo queueItem) {
        workerHealthService.recordClaimed(queueItem);
        try {
            fixTaskWorker.execute(queueItem.taskId());
            fixTaskQueue.markCompleted(queueItem.id());
            workerHealthService.recordCompleted(queueItem);
        } catch (RuntimeException exception) {
            String failureReason = failureReason(exception);
            fixTaskQueue.markFailed(queueItem.id(), failureReason);
            workerHealthService.recordFailed(queueItem, failureReason);
        }
    }

    private static String failureReason(RuntimeException exception) {
        if (!StringUtils.hasText(exception.getMessage())) {
            return exception.getClass().getSimpleName();
        }
        return exception.getMessage();
    }
}
