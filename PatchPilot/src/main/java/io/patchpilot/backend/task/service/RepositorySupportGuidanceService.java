package io.patchpilot.backend.task.service;

import io.patchpilot.backend.language.LanguageAdapterCatalogService;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.RepositorySupportGuidanceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepositorySupportGuidanceService {

    static final String UNSUPPORTED_STATUS = "UNSUPPORTED";
    static final String UNSUPPORTED_PREFIX = "Unsupported repository:";
    static final String OPERATOR_ACTION = "Add one supported project marker and deterministic test command, "
            + "then trigger /agent fix again. PatchPilot will not run arbitrary commands for unsupported repositories.";

    private final LanguageAdapterCatalogService languageAdapterCatalogService;

    public Optional<RepositorySupportGuidanceVo> guidanceFor(FixTaskVo task) {
        if (task.failureReason() == null || !task.failureReason().startsWith(UNSUPPORTED_PREFIX)) {
            return Optional.empty();
        }
        return Optional.of(new RepositorySupportGuidanceVo(
                UNSUPPORTED_STATUS,
                task.failureReason(),
                OPERATOR_ACTION,
                languageAdapterCatalogService.listSupportedAdapters()
        ));
    }
}
