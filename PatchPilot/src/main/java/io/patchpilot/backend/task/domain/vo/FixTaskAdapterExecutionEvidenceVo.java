package io.patchpilot.backend.task.domain.vo;

import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;

import java.util.List;

public record FixTaskAdapterExecutionEvidenceVo(
        String status,
        String language,
        String buildSystem,
        String verificationCommand,
        String detectionReason,
        String operatorAction,
        String safetyNote,
        List<SupportedLanguageAdapterVo> supportedAdapters
) {

    public FixTaskAdapterExecutionEvidenceVo {
        supportedAdapters = List.copyOf(supportedAdapters);
    }
}
