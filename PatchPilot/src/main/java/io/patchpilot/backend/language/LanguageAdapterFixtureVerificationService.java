package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

@Service
public class LanguageAdapterFixtureVerificationService {

    private static final String PASS = "PASS";
    private static final String FAIL = "FAIL";

    private final Supplier<List<SupportedLanguageAdapterVo>> adapterCatalog;
    private final LanguageAdapterRegistry languageAdapterRegistry;

    @Autowired
    public LanguageAdapterFixtureVerificationService(
            LanguageAdapterCatalogService languageAdapterCatalogService,
            LanguageAdapterRegistry languageAdapterRegistry
    ) {
        this(languageAdapterCatalogService::listSupportedAdapters, languageAdapterRegistry);
    }

    LanguageAdapterFixtureVerificationService(
            Supplier<List<SupportedLanguageAdapterVo>> adapterCatalog,
            LanguageAdapterRegistry languageAdapterRegistry
    ) {
        this.adapterCatalog = adapterCatalog;
        this.languageAdapterRegistry = languageAdapterRegistry;
    }

    public List<LanguageAdapterFixtureVerificationVo> listFixtureVerifications() {
        return adapterCatalog.get().stream()
                .map(this::verifyFixture)
                .toList();
    }

    private LanguageAdapterFixtureVerificationVo verifyFixture(SupportedLanguageAdapterVo adapter) {
        Path fixturePath = resolveFixturePath(adapter.demoFixturePath());
        String fixtureName = fixtureName(adapter.demoFixturePath());
        if (!Files.isDirectory(fixturePath)) {
            return new LanguageAdapterFixtureVerificationVo(
                    fixtureName,
                    adapter.demoFixturePath(),
                    adapter.language(),
                    adapter.buildSystem(),
                    adapter.verificationCommand(),
                    "unknown",
                    "unknown",
                    List.of(),
                    "Unsupported repository: missing fixture path " + adapter.demoFixturePath(),
                    FAIL
            );
        }

        LanguageDetectionResult detection = languageAdapterRegistry.detect(fixturePath);
        boolean matches = detection.supported()
                && adapter.language().equals(detection.language())
                && adapter.buildSystem().equals(detection.buildSystem())
                && adapter.verificationCommand().equals(detection.verificationCommand());

        return new LanguageAdapterFixtureVerificationVo(
                fixtureName,
                adapter.demoFixturePath(),
                adapter.language(),
                adapter.buildSystem(),
                adapter.verificationCommand(),
                detection.language(),
                detection.buildSystem(),
                detection.verificationCommand(),
                detection.reason(),
                matches ? PASS : FAIL
        );
    }

    private static Path resolveFixturePath(String fixturePath) {
        Path rootRelativePath = Path.of(fixturePath);
        if (Files.exists(rootRelativePath)) {
            return rootRelativePath.toAbsolutePath().normalize();
        }
        return Path.of("..").resolve(fixturePath).toAbsolutePath().normalize();
    }

    private static String fixtureName(String fixturePath) {
        Path path = Path.of(fixturePath);
        Path fileName = path.getFileName();
        return fileName == null ? fixturePath : fileName.toString();
    }
}
