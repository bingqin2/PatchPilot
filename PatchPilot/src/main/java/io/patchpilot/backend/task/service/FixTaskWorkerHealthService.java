package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskWorkerHealthVo;

public interface FixTaskWorkerHealthService {

    FixTaskWorkerHealthVo getHealth();

    void recordPollStarted();

    void recordIdlePoll();

    void recordClaimed(FixTaskQueueItemVo queueItem);

    void recordCompleted(FixTaskQueueItemVo queueItem);

    void recordFailed(FixTaskQueueItemVo queueItem, String failureReason);

    void recordPollingFailed(String failureReason);
}
