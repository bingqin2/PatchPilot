package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskAuditSummaryVo;

import java.util.Optional;

public interface FixTaskAuditSummaryService {

    Optional<FixTaskAuditSummaryVo> summary(String taskId);
}
