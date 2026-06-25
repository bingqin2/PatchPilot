package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageAdapterRuntimeReadinessVo;
import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageAdapterRuntimeReadinessService {

    private static final String READY = "READY";
    private static final String MISSING = "MISSING";

    private final LanguageAdapterCatalogService languageAdapterCatalogService;
    private final ExecutableAvailabilityChecker executableAvailabilityChecker;

    @Autowired
    public LanguageAdapterRuntimeReadinessService(
            LanguageAdapterCatalogService languageAdapterCatalogService,
            ExecutableAvailabilityChecker executableAvailabilityChecker
    ) {
        this.languageAdapterCatalogService = languageAdapterCatalogService;
        this.executableAvailabilityChecker = executableAvailabilityChecker;
    }

    LanguageAdapterRuntimeReadinessService(LanguageAdapterCatalogService languageAdapterCatalogService) {
        this(languageAdapterCatalogService, new PathExecutableAvailabilityChecker());
    }

    public List<LanguageAdapterRuntimeReadinessVo> listRuntimeReadiness() {
        return languageAdapterCatalogService.listSupportedAdapters().stream()
                .map(this::runtimeReadiness)
                .toList();
    }

    private LanguageAdapterRuntimeReadinessVo runtimeReadiness(SupportedLanguageAdapterVo adapter) {
        String executable = adapter.verificationCommand().isEmpty() ? "" : adapter.verificationCommand().get(0);
        boolean available = executableAvailabilityChecker.isAvailable(executable);
        String status = available ? READY : MISSING;
        return new LanguageAdapterRuntimeReadinessVo(
                adapter.language(),
                adapter.buildSystem(),
                executable,
                adapter.verificationCommand(),
                status,
                reason(executable, available)
        );
    }

    private static String reason(String executable, boolean available) {
        if (available) {
            return "Executable `" + executable + "` is available on PATH";
        }
        return "Executable `" + executable + "` is not available on PATH";
    }
}
