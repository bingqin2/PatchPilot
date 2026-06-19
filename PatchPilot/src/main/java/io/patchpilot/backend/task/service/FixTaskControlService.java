package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;

public interface FixTaskControlService {

    FixTaskVo cancelTask(String taskId);

    FixTaskVo retryTask(String taskId);
}
