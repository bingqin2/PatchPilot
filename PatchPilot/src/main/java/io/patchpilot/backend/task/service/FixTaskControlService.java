package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.ApproveReviewCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskRetryPreflightVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

public interface FixTaskControlService {

    FixTaskVo cancelTask(String taskId);

    FixTaskRetryPreflightVo retryPreflight(String taskId);

    FixTaskVo retryTask(String taskId);

    FixTaskVo approveReviewTask(String taskId, ApproveReviewCommand command);
}
