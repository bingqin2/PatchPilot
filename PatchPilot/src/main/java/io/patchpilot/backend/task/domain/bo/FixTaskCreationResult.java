package io.patchpilot.backend.task.domain.bo;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;

public record FixTaskCreationResult(FixTaskVo task, boolean created) {
}
