package io.patchpilot.backend.language.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(300)
public class NodeNpmLanguageAdapter implements LanguageAdapter {

    private final ObjectMapper objectMapper;

    public NodeNpmLanguageAdapter() {
        this(new ObjectMapper());
    }

    NodeNpmLanguageAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path packageJson = repositoryDir.toAbsolutePath().normalize().resolve("package.json");
        if (!Files.isRegularFile(packageJson)) {
            return unsupported("Unsupported repository: no package.json found");
        }
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(packageJson.toFile());
        } catch (IOException e) {
            return unsupported("Unsupported repository: package.json could not be parsed");
        }
        if (!StringUtils.hasText(rootNode.path("scripts").path("test").asText())) {
            return unsupported("Unsupported repository: package.json has no scripts.test command");
        }
        return LanguageDetectionResult.supported(
                "node",
                "npm",
                List.of("npm", "test"),
                "Detected npm project with test script"
        );
    }

    private static LanguageDetectionResult unsupported(String reason) {
        return LanguageDetectionResult.unsupported("node", "npm", reason);
    }
}
