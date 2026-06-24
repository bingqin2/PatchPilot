package io.patchpilot.backend.language.domain;

import java.util.List;

public record RepositoryPreflightVo(
        boolean supported,
        String language,
        String buildSystem,
        List<String> verificationCommand,
        String reason,
        String operatorAction,
        String repositoryPath,
        List<SupportedLanguageAdapterVo> supportedAdapters
) {
}
