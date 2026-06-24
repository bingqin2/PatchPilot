package io.patchpilot.backend.language;

import io.patchpilot.backend.language.config.RepositoryPreflightProperties;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.domain.RepositoryPreflightRequest;
import io.patchpilot.backend.language.domain.RepositoryPreflightVo;
import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryPreflightService {

    private static final String SUPPORTED_ACTION =
            "Repository is supported. PatchPilot can run the detected verification command after patch generation.";
    private static final String UNSUPPORTED_ACTION =
            "Add a LanguageAdapter for this repository shape before running /agent fix.";

    private final LanguageAdapterRegistry languageAdapterRegistry;
    private final LanguageAdapterCatalogService languageAdapterCatalogService;
    private final RepositoryPreflightProperties repositoryPreflightProperties;

    public RepositoryPreflightVo preflight(RepositoryPreflightRequest request) {
        String repositoryPath = normalizeInputPath(request);
        Path resolvedRepositoryPath = resolveRepositoryPath(repositoryPath);
        validateAllowedRoot(resolvedRepositoryPath);
        if (!Files.isDirectory(resolvedRepositoryPath)) {
            return unsupported(
                    repositoryPath,
                    "Repository path is not a directory: " + repositoryPath
            );
        }

        LanguageDetectionResult detectionResult = languageAdapterRegistry.detect(resolvedRepositoryPath);
        if (detectionResult.supported()) {
            return new RepositoryPreflightVo(
                    true,
                    detectionResult.language(),
                    detectionResult.buildSystem(),
                    detectionResult.verificationCommand(),
                    detectionResult.reason(),
                    SUPPORTED_ACTION,
                    repositoryPath,
                    List.of()
            );
        }
        return unsupported(repositoryPath, detectionResult.reason());
    }

    private RepositoryPreflightVo unsupported(String repositoryPath, String reason) {
        List<SupportedLanguageAdapterVo> supportedAdapters = languageAdapterCatalogService.listSupportedAdapters();
        return new RepositoryPreflightVo(
                false,
                "unknown",
                "unknown",
                List.of(),
                reason,
                UNSUPPORTED_ACTION,
                repositoryPath,
                supportedAdapters
        );
    }

    private String normalizeInputPath(RepositoryPreflightRequest request) {
        if (request == null || !StringUtils.hasText(request.repositoryPath())) {
            throw new IllegalArgumentException("repositoryPath is required");
        }
        return request.repositoryPath().trim();
    }

    private Path resolveRepositoryPath(String repositoryPath) {
        Path inputPath = Path.of(repositoryPath);
        if (inputPath.isAbsolute()) {
            return inputPath.toAbsolutePath().normalize();
        }

        Path currentDirectoryPath = inputPath.toAbsolutePath().normalize();
        if (Files.isDirectory(currentDirectoryPath)) {
            return currentDirectoryPath;
        }
        return Path.of("..").resolve(inputPath).toAbsolutePath().normalize();
    }

    private void validateAllowedRoot(Path resolvedRepositoryPath) {
        List<Path> allowedRootDirs = repositoryPreflightProperties.normalizedAllowedRootDirs();
        boolean allowed = allowedRootDirs.stream()
                .anyMatch(resolvedRepositoryPath::startsWith);
        if (!allowed) {
            throw new IllegalArgumentException("Repository preflight path is outside allowed roots");
        }
    }
}
