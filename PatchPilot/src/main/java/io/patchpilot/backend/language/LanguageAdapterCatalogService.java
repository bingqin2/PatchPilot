package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageAdapterCatalogService {

    private static final String SUPPORTED = "SUPPORTED";
    private static final List<SupportedLanguageAdapterVo> SUPPORTED_ADAPTERS = List.of(
            adapter(
                    "java",
                    "maven",
                    List.of("mvn", "test"),
                    List.of("pom.xml", "mvnw"),
                    "docs/demo-repositories/java-maven"
            ),
            adapter(
                    "java",
                    "gradle",
                    List.of("gradle", "test"),
                    List.of("build.gradle", "build.gradle.kts", "gradlew"),
                    "docs/demo-repositories/java-gradle"
            ),
            adapter(
                    "node",
                    "pnpm",
                    List.of("pnpm", "test"),
                    List.of("package.json", "pnpm-lock.yaml", "scripts.test"),
                    "docs/demo-repositories/node-pnpm"
            ),
            adapter(
                    "node",
                    "yarn",
                    List.of("yarn", "test"),
                    List.of("package.json", "yarn.lock", "scripts.test"),
                    "docs/demo-repositories/node-yarn"
            ),
            adapter(
                    "node",
                    "npm",
                    List.of("npm", "test"),
                    List.of("package.json", "scripts.test"),
                    "docs/demo-repositories/node-npm"
            ),
            adapter(
                    "python",
                    "poetry",
                    List.of("poetry", "run", "pytest"),
                    List.of("pyproject.toml", "[tool.poetry]", "pytest configuration or dependency"),
                    "docs/demo-repositories/python-poetry"
            ),
            adapter(
                    "python",
                    "uv",
                    List.of("uv", "run", "pytest"),
                    List.of("uv.lock", "pyproject.toml", "pytest configuration or dependency"),
                    "docs/demo-repositories/python-uv"
            ),
            adapter(
                    "python",
                    "pytest",
                    List.of("python3", "-m", "pytest"),
                    List.of("pytest.ini", "requirements.txt", "pyproject.toml"),
                    "docs/demo-repositories/python-pytest"
            )
    );

    public List<SupportedLanguageAdapterVo> listSupportedAdapters() {
        return SUPPORTED_ADAPTERS;
    }

    private static SupportedLanguageAdapterVo adapter(
            String language,
            String buildSystem,
            List<String> verificationCommand,
            List<String> detectionSignals,
            String demoFixturePath
    ) {
        return new SupportedLanguageAdapterVo(
                language,
                buildSystem,
                verificationCommand,
                detectionSignals,
                demoFixturePath,
                SUPPORTED
        );
    }
}
