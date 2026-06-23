package io.patchpilot.backend.task.domain.vo;

import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;

import java.util.List;

public record RepositorySupportGuidanceVo(
        String status,
        String reason,
        String operatorAction,
        List<SupportedLanguageAdapterVo> supportedAdapters
) {

    public RepositorySupportGuidanceVo {
        supportedAdapters = List.copyOf(supportedAdapters);
    }
}
